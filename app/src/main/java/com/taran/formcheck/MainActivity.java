package com.taran.formcheck;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;

import com.taran.formcheck.analysis.LandmarkQualityGate;
import com.taran.formcheck.feedback.LoweringDurationRange;
import com.taran.formcheck.feedback.LoweringFeedbackAnalyzer;
import com.taran.formcheck.feedback.LoweringFeedbackOutcome;
import com.taran.formcheck.feedback.LoweringPracticeTarget;
import com.taran.formcheck.feedback.RecordingSuitability;
import com.taran.formcheck.pose.LoweringTimingAnalyzer;
import com.taran.formcheck.pose.PoseLandmarkerManager;
import com.taran.formcheck.pose.SquatAnalysisOutcome;
import com.taran.formcheck.pose.SquatAnalysisResult;
import com.taran.formcheck.pose.SquatRepetition;
import com.taran.formcheck.pose.SquatRepetitionDetector;
import com.taran.formcheck.pose.TimestampedPoseResult;
import com.taran.formcheck.pose.VideoAnalysisProgressListener;
import com.taran.formcheck.pose.VideoKneeAngleSequenceAnalyzer;
import com.taran.formcheck.pose.VideoPoseSequenceProcessor;
import com.taran.formcheck.pose.VideoSquatSequenceAnalyzer;
import com.taran.formcheck.video.VideoFrameDecoder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private static final long FRAME_INTERVAL_MILLISECONDS = 250;

    private static final double MINIMUM_LANDMARK_VISIBILITY = 0.5;

    private static final double MINIMUM_LANDMARK_PRESENCE = 0.5;

    private static final double STANDING_ANGLE_THRESHOLD = 160;

    private static final double BOTTOM_ANGLE_THRESHOLD = 120;

    private static final String TAG = "MainActivity";

    private final ExecutorService analysisExecutor = Executors.newSingleThreadExecutor();

    private Uri selectedVideoUri;
    private Future<?> analysisTask;
    private boolean analysisInProgress;
    private boolean analysisInterrupted;
    private boolean analysisComplete;
    private boolean analysisCancelled;
    private int analysisGeneration;

    private static final String STATE_VIDEO_URI = "selectedVideoUri";
    private static final String STATE_TARGET_POSITION = "targetPosition";
    private static final String STATE_RECORDING_CONFIRMED = "recordingConfirmed";
    private static final String STATE_ANALYSIS_INTERRUPTED = "analysisInterrupted";
    private static final String STATE_ANALYSIS_COMPLETE = "analysisComplete";
    private static final String STATE_ANALYSIS_CANCELLED = "analysisCancelled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button selectVideoButton = findViewById(R.id.selectVideoButton);
        TextView selectionStatusText = findViewById(R.id.selectionStatusText);
        Button analyzeVideoButton = findViewById(R.id.analyzeVideoButton);
        Button cancelAnalysisButton = findViewById(R.id.cancelAnalysisButton);
        TextView analysisResultText = findViewById(R.id.analysisResultText);
        Spinner loweringTargetSpinner = findViewById(R.id.loweringTargetSpinner);
        CheckBox recordingSuitabilityCheckBox = findViewById(R.id.recordingSuitabilityCheckBox);

        ActivityResultLauncher<String[]> videoPickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), inputUri -> {
            if (inputUri != null) {
                selectedVideoUri = inputUri;
                try {
                    getContentResolver().takePersistableUriPermission(inputUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                catch (SecurityException exception) {
                    Log.w(TAG, "Video provider did not allow persistent read access", exception);
                }
                analysisInterrupted = false;
                analysisCancelled = false;
                analysisComplete = false;
                selectionStatusText.setText(R.string.video_selected);
                analyzeVideoButton.setEnabled(true);
                analysisResultText.setText(R.string.analysis_not_started);
                recordingSuitabilityCheckBox.setChecked(false);
            }
        });

        if (savedInstanceState != null) {
            String savedVideoUri = savedInstanceState.getString(STATE_VIDEO_URI);
            if (savedVideoUri != null) {
                selectedVideoUri = Uri.parse(savedVideoUri);
            }
            loweringTargetSpinner.setSelection(savedInstanceState.getInt(STATE_TARGET_POSITION, 0));
            recordingSuitabilityCheckBox.setChecked(savedInstanceState.getBoolean(STATE_RECORDING_CONFIRMED, false));
            analysisInterrupted = savedInstanceState.getBoolean(STATE_ANALYSIS_INTERRUPTED, false);
            analysisComplete = savedInstanceState.getBoolean(STATE_ANALYSIS_COMPLETE, false);
            analysisCancelled = savedInstanceState.getBoolean(STATE_ANALYSIS_CANCELLED, false);
            updateIdleControls();
        }

        selectVideoButton.setOnClickListener(v -> videoPickerLauncher.launch(new String[]{"video/*"}));

        cancelAnalysisButton.setOnClickListener(v -> cancelAnalysis(false));

        analyzeVideoButton.setOnClickListener(v -> {
            Uri videoUri = selectedVideoUri;

            if (videoUri == null || analysisInProgress) {
                return;
            }

            int position = loweringTargetSpinner.getSelectedItemPosition();
            LoweringPracticeTarget target;

            if (position >= 1 && position <= 4) {
                target = new LoweringPracticeTarget(position * 1000, 500);
            }
            else {
                target = null;
            }

            RecordingSuitability suitability;

            if (recordingSuitabilityCheckBox.isChecked()) {
                suitability = RecordingSuitability.CONFIRMED_SIDE_VIEW;
            }
            else {
                suitability = RecordingSuitability.UNKNOWN;
            }

            analysisInProgress = true;
            analysisInterrupted = false;
            analysisCancelled = false;
            analysisComplete = false;
            int currentGeneration = ++analysisGeneration;

            cancelAnalysisButton.setVisibility(View.VISIBLE);
            analyzeVideoButton.setEnabled(false);
            selectVideoButton.setEnabled(false);
            analysisResultText.setText(R.string.analysis_in_progress);
            loweringTargetSpinner.setEnabled(false);
            recordingSuitabilityCheckBox.setEnabled(false);

            analysisTask = analysisExecutor.submit(() -> {
                try {
                    long startTime = System.nanoTime();

                    SquatAnalysisResult result = analyzeVideo(videoUri, ((int processedFrames, int totalFrames) -> runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed() || currentGeneration != analysisGeneration || !analysisInProgress) {
                            return;
                        }

                        analysisResultText.setText(getString(R.string.analysis_frame_progress, processedFrames, totalFrames));
                    })));

                    LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, target, suitability);
                    Log.d(TAG, "Lowering feedback outcome: " + outcome);

                    long endTime = System.nanoTime();

                    double timeElapsed = (double) (endTime - startTime) / 1000000000;

                    Log.d(TAG, "Time Elapsed: " + timeElapsed + " seconds");

                    if (result.getLoweringTiming().isPresent()) {
                        Log.d(TAG, "Estimated Start: " + result.getLoweringTiming().get().getLoweringStart() + " ms; Bottom Reached: " + result.getLoweringTiming().get().getBottomReached() + " ms; Duration: " + result.getLoweringTiming().get().getDurationMilliseconds() + " ms");
                    }
                    else {
                        Log.d(TAG, "Lowering timing unavailable");
                    }

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed() || currentGeneration != analysisGeneration || !analysisInProgress) {
                            return;
                        }

                        analysisInProgress = false;
                        analysisComplete = true;
                        analysisTask = null;
                        cancelAnalysisButton.setVisibility(View.GONE);
                        analysisResultText.setText(R.string.analysis_complete);
                        selectVideoButton.setEnabled(true);
                        analyzeVideoButton.setEnabled(true);
                        loweringTargetSpinner.setEnabled(true);
                        recordingSuitabilityCheckBox.setEnabled(true);

                        Intent intent = new Intent(this, ResultsActivity.class);

                        intent.putExtra(ResultsActivity.EXTRA_FEEDBACK_OUTCOME, outcome.name());
                        intent.putExtra(ResultsActivity.EXTRA_ANALYSIS_OUTCOME, result.getOutcome().name());
                        intent.putExtra(ResultsActivity.EXTRA_RECORDING_SUITABILITY, suitability.name());

                        if (result.getOutcome().equals(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED)) {
                            SquatRepetition repetition = result.getRepetition();

                            intent.putExtra(ResultsActivity.EXTRA_STANDING_START_MS, repetition.getStandingStartTimestamp());
                            intent.putExtra(ResultsActivity.EXTRA_LOWEST_ANGLE_MS, repetition.getLowestAngleTimestamp());
                            intent.putExtra(ResultsActivity.EXTRA_RETURN_TO_STANDING_MS, repetition.getReturnToStandingTimestamp());
                            intent.putExtra(ResultsActivity.EXTRA_STANDING_ANGLE_DEGREES, repetition.getStandingStartAngleDegrees());
                            intent.putExtra(ResultsActivity.EXTRA_LOWEST_ANGLE_DEGREES, repetition.getLowestAngleDegrees());
                            intent.putExtra(ResultsActivity.EXTRA_RETURN_ANGLE_DEGREES, repetition.getReturnToStandingAngleDegrees());
                        }

                        if (target != null) {
                            intent.putExtra(ResultsActivity.EXTRA_TARGET_DURATION_MS, target.getTargetDurationMS());
                            intent.putExtra(ResultsActivity.EXTRA_TARGET_TOLERANCE_MS, target.getComparisonToleranceMS());
                        }

                        if (result.getLoweringTiming().isPresent()) {
                            intent.putExtra(ResultsActivity.EXTRA_LOWERING_START_MS, result.getLoweringTiming().get().getLoweringStart());
                            intent.putExtra(ResultsActivity.EXTRA_LOWERING_BOTTOM_MS, result.getLoweringTiming().get().getBottomReached());
                        }

                        if (result.getLoweringTiming().isPresent()) {
                            Optional<LoweringDurationRange> loweringDurationRange = LoweringDurationRange.fromTiming(result.getLoweringTiming().get());

                            if (loweringDurationRange.isPresent()) {
                                intent.putExtra(ResultsActivity.EXTRA_MINIMUM_DURATION_MS, loweringDurationRange.get().getMinimumDurationMs());
                                intent.putExtra(ResultsActivity.EXTRA_MAXIMUM_DURATION_MS, loweringDurationRange.get().getMaximumDurationMs());
                            }
                        }

                        startActivity(intent);
                    });
                }
                catch (Exception exception) {
                    Log.e(TAG, "Video analysis failed", exception);

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed() || currentGeneration != analysisGeneration || !analysisInProgress) {
                            return;
                        }

                        analysisInProgress = false;
                        analysisTask = null;
                        cancelAnalysisButton.setVisibility(View.GONE);
                        analysisResultText.setText(R.string.analysis_failed);
                        selectVideoButton.setEnabled(true);
                        analyzeVideoButton.setEnabled(true);
                        loweringTargetSpinner.setEnabled(true);
                        recordingSuitabilityCheckBox.setEnabled(true);
                    });
                }
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private SquatAnalysisResult analyzeVideo(Uri videoUri, VideoAnalysisProgressListener progressListener) throws IOException {
        try (VideoFrameDecoder decoder = new VideoFrameDecoder(getApplicationContext(), videoUri);
             PoseLandmarkerManager manager = new PoseLandmarkerManager(getApplicationContext())) {
            List<TimestampedPoseResult> timestampedPoseResults = VideoPoseSequenceProcessor.processVideoPoseSequence(decoder, manager, FRAME_INTERVAL_MILLISECONDS, progressListener);

            LandmarkQualityGate gate = new LandmarkQualityGate(MINIMUM_LANDMARK_VISIBILITY, MINIMUM_LANDMARK_PRESENCE);
            VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
            SquatRepetitionDetector repetitionDetector = new SquatRepetitionDetector(STANDING_ANGLE_THRESHOLD, BOTTOM_ANGLE_THRESHOLD);
            VideoSquatSequenceAnalyzer squatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, repetitionDetector, new LoweringTimingAnalyzer(5, 3, 500));

            return squatSequenceAnalyzer.analyze(timestampedPoseResults);
        }

    }

    private void updateIdleControls() {
        findViewById(R.id.cancelAnalysisButton).setVisibility(View.GONE);
        findViewById(R.id.selectVideoButton).setEnabled(true);
        findViewById(R.id.analyzeVideoButton).setEnabled(selectedVideoUri != null);
        findViewById(R.id.loweringTargetSpinner).setEnabled(true);
        findViewById(R.id.recordingSuitabilityCheckBox).setEnabled(true);

        TextView selectionStatusText = findViewById(R.id.selectionStatusText);
        selectionStatusText.setText(selectedVideoUri == null ? R.string.no_video_selected : R.string.video_selected);
        TextView analysisResultText = findViewById(R.id.analysisResultText);
        if (analysisInterrupted) {
            analysisResultText.setText(R.string.analysis_interrupted);
        }
        else if (analysisCancelled) {
            analysisResultText.setText(R.string.analysis_cancelled);
        }
        else if (analysisComplete) {
            analysisResultText.setText(R.string.analysis_complete);
        }
        else {
            analysisResultText.setText(R.string.analysis_not_started);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (selectedVideoUri != null) {
            outState.putString(STATE_VIDEO_URI, selectedVideoUri.toString());
        }
        Spinner loweringTargetSpinner = findViewById(R.id.loweringTargetSpinner);
        CheckBox recordingSuitabilityCheckBox = findViewById(R.id.recordingSuitabilityCheckBox);
        outState.putInt(STATE_TARGET_POSITION, loweringTargetSpinner.getSelectedItemPosition());
        outState.putBoolean(STATE_RECORDING_CONFIRMED, recordingSuitabilityCheckBox.isChecked());
        outState.putBoolean(STATE_ANALYSIS_INTERRUPTED, analysisInProgress || analysisInterrupted);
        outState.putBoolean(STATE_ANALYSIS_COMPLETE, analysisComplete);
        outState.putBoolean(STATE_ANALYSIS_CANCELLED, analysisCancelled);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updateIdleControls();
    }

    private void cancelAnalysis(boolean interrupted) {
        if (analysisInProgress) {
            analysisGeneration++;
            analysisInProgress = false;
            analysisInterrupted = interrupted;
            analysisCancelled = !interrupted;
            analysisComplete = false;
            if (analysisTask != null) {
                analysisTask.cancel(true);
                analysisTask = null;
            }
            updateIdleControls();
        }
    }

    @Override
    protected void onStop() {
        cancelAnalysis(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        analysisExecutor.shutdownNow();
        super.onDestroy();
    }
}
