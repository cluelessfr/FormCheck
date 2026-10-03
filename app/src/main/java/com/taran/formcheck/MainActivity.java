package com.taran.formcheck;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.taran.formcheck.analysis.LandmarkQualityGate;
import com.taran.formcheck.pose.PoseLandmarkerManager;
import com.taran.formcheck.pose.SquatAnalysisOutcome;
import com.taran.formcheck.pose.SquatAnalysisResult;
import com.taran.formcheck.pose.SquatRepetition;
import com.taran.formcheck.pose.SquatRepetitionDetector;
import com.taran.formcheck.pose.TimestampedPoseResult;
import com.taran.formcheck.pose.VideoKneeAngleSequenceAnalyzer;
import com.taran.formcheck.pose.VideoPoseSequenceProcessor;
import com.taran.formcheck.pose.VideoSquatSequenceAnalyzer;
import com.taran.formcheck.video.VideoFrameDecoder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    private static final long FRAME_INTERVAL_MILLISECONDS = 250;

    private static final double MINIMUM_LANDMARK_VISIBILITY = 0.5;

    private static final double MINIMUM_LANDMARK_PRESENCE = 0.5;

    private static final double STANDING_ANGLE_THRESHOLD = 160;

    private static final double BOTTOM_ANGLE_THRESHOLD = 120;

    private static final String TAG = "MainActivity";

    private final ExecutorService analysisExecutor = Executors.newSingleThreadExecutor();

    private Uri selectedVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button selectVideoButton = findViewById(R.id.selectVideoButton);
        TextView selectionStatusText = findViewById(R.id.selectionStatusText);
        Button analyzeVideoButton = findViewById(R.id.analyzeVideoButton);
        TextView analysisResultText = findViewById(R.id.analysisResultText);
        TextView repetitionDetailsText = findViewById(R.id.repetitionDetailsText);

        ActivityResultLauncher<String[]> videoPickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), inputUri -> {
            if (inputUri != null) {
                selectedVideoUri = inputUri;
                selectionStatusText.setText(R.string.video_selected);
                analyzeVideoButton.setEnabled(true);
                analysisResultText.setText(R.string.analysis_not_started);
                repetitionDetailsText.setText("");
            }
        });

        selectVideoButton.setOnClickListener(v -> videoPickerLauncher.launch(new String[]{"video/*"}));

        analyzeVideoButton.setOnClickListener(v -> {
            Uri videoUri = selectedVideoUri;

            if (videoUri == null) {
                return;
            }

            analyzeVideoButton.setEnabled(false);
            selectVideoButton.setEnabled(false);
            analysisResultText.setText(R.string.analysis_in_progress);
            repetitionDetailsText.setText("");

            analysisExecutor.submit(() -> {
                try {
                    long startTime = System.nanoTime();

                    SquatAnalysisResult result = analyzeVideo(videoUri);

                    long endTime = System.nanoTime();

                    double timeElapsed = (double) (endTime - startTime) / 1000000000;

                    Log.d(TAG, "Time Elapsed: " + timeElapsed + " seconds");

                    int analysisResult;
                    String repetitionDetails;

                    if (result.getOutcome() == SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED) {
                        analysisResult = R.string.complete_repetition_detected;
                        SquatRepetition repetition = result.getRepetition();
                        double standingStartSeconds = (double) repetition.getStandingStartTimestamp() / 1000;
                        double lowestAngleSeconds = (double) repetition.getLowestAngleTimestamp() / 1000;
                        double returnToStandingSeconds = (double) repetition.getReturnToStandingTimestamp() / 1000;
                        repetitionDetails = getString(R.string.repetition_timing, standingStartSeconds, lowestAngleSeconds, returnToStandingSeconds);
                    }
                    else {
                        analysisResult = R.string.insufficient_evidence;
                        repetitionDetails = "";
                    }

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        analysisResultText.setText(analysisResult);
                        repetitionDetailsText.setText(repetitionDetails);
                        selectVideoButton.setEnabled(true);
                        analyzeVideoButton.setEnabled(true);
                    });
                }
                catch (Exception exception) {
                    Log.e(TAG, "Video analysis failed", exception);

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        analysisResultText.setText(R.string.analysis_failed);
                        selectVideoButton.setEnabled(true);
                        analyzeVideoButton.setEnabled(true);
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

    private SquatAnalysisResult analyzeVideo(Uri videoUri) throws IOException {
        try (VideoFrameDecoder decoder = new VideoFrameDecoder(getApplicationContext(), videoUri);
             PoseLandmarkerManager manager = new PoseLandmarkerManager(getApplicationContext())) {
            List<TimestampedPoseResult> timestampedPoseResults = VideoPoseSequenceProcessor.processVideoPoseSequence(decoder, manager, FRAME_INTERVAL_MILLISECONDS);

            LandmarkQualityGate gate = new LandmarkQualityGate(MINIMUM_LANDMARK_VISIBILITY, MINIMUM_LANDMARK_PRESENCE);
            VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
            SquatRepetitionDetector repetitionDetector = new SquatRepetitionDetector(STANDING_ANGLE_THRESHOLD, BOTTOM_ANGLE_THRESHOLD);
            VideoSquatSequenceAnalyzer squatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, repetitionDetector);

            return squatSequenceAnalyzer.analyze(timestampedPoseResults);
        }

    }

    @Override
    protected void onDestroy() {
        analysisExecutor.shutdownNow();
        super.onDestroy();
    }
}