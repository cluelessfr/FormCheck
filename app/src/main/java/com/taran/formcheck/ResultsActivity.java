package com.taran.formcheck;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.taran.formcheck.feedback.LoweringFeedbackOutcome;
import com.taran.formcheck.feedback.RecordingSuitability;
import com.taran.formcheck.pose.SquatAnalysisOutcome;

public class ResultsActivity extends AppCompatActivity {
    public static final String EXTRA_FEEDBACK_OUTCOME = "com.taran.formcheck.extra.FEEDBACK_OUTCOME";
    public static final String EXTRA_ANALYSIS_OUTCOME = "com.taran.formcheck.extra.ANALYSIS_OUTCOME";
    public static final String EXTRA_STANDING_START_MS = "com.taran.formcheck.extra.STANDING_START_MS";
    public static final String EXTRA_LOWEST_ANGLE_MS = "com.taran.formcheck.extra.LOWEST_ANGLE_MS";
    public static final String EXTRA_RETURN_TO_STANDING_MS = "com.taran.formcheck.extra.RETURN_TO_STANDING_MS";
    public static final String EXTRA_TARGET_DURATION_MS = "com.taran.formcheck.extra.TARGET_DURATION_MS";
    public static final String EXTRA_TARGET_TOLERANCE_MS = "com.taran.formcheck.extra.TARGET_TOLERANCE_MS";
    public static final String EXTRA_RECORDING_SUITABILITY = "com.taran.formcheck.extra.RECORDING_SUITABILITY";
    public static final String EXTRA_LOWERING_START_MS = "com.taran.formcheck.extra.LOWERING_START_MS";
    public static final String EXTRA_LOWERING_BOTTOM_MS = "com.taran.formcheck.extra.LOWERING_BOTTOM_MS";
    public static final String EXTRA_MINIMUM_DURATION_MS = "com.taran.formcheck.extra.MINIMUM_DURATION_MS";
    public static final String EXTRA_MAXIMUM_DURATION_MS = "com.taran.formcheck.extra.MAXIMUM_DURATION_MS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        TextView loweringFeedbackText = findViewById(R.id.loweringFeedbackText);
        TextView detectionOutcomeText = findViewById(R.id.detectionOutcomeText);
        TextView sampledTextTimes = findViewById(R.id.sampledTimesText);
        TextView practiceTargetText = findViewById(R.id.practiceTargetText);
        TextView recordingStatusText = findViewById(R.id.recordingStatusText);
        TextView loweringTimingText = findViewById(R.id.loweringTimingText);
        TextView loweringDurationText = findViewById(R.id.loweringDurationText);

        String receivedOutcome = getIntent().getStringExtra(EXTRA_FEEDBACK_OUTCOME);
        String receivedAnalysisOutcome = getIntent().getStringExtra(EXTRA_ANALYSIS_OUTCOME);
        String receivedRecordingSuitability = getIntent().getStringExtra(EXTRA_RECORDING_SUITABILITY);

        long standingStartMs = getIntent().getLongExtra(EXTRA_STANDING_START_MS, -1);
        long lowestAngleMs = getIntent().getLongExtra(EXTRA_LOWEST_ANGLE_MS, -1);
        long returnToStandingMs = getIntent().getLongExtra(EXTRA_RETURN_TO_STANDING_MS, -1);
        long targetDurationMs = getIntent().getLongExtra(EXTRA_TARGET_DURATION_MS, -1);
        long targetToleranceMs = getIntent().getLongExtra(EXTRA_TARGET_TOLERANCE_MS, -1);
        long loweringStartMs = getIntent().getLongExtra(EXTRA_LOWERING_START_MS, -1);
        long loweringBottomMs = getIntent().getLongExtra(EXTRA_LOWERING_BOTTOM_MS, -1);
        long durationMs = loweringBottomMs - loweringStartMs;
        long minimumDurationMs = getIntent().getLongExtra(EXTRA_MINIMUM_DURATION_MS, -1);
        long maximumDurationMs = getIntent().getLongExtra(EXTRA_MAXIMUM_DURATION_MS, -1);

        int messageResource = R.string.lowering_feedback_unavailable;
        int detectionMessageResource = R.string.insufficient_evidence;
        int recordingMessageResource = R.string.recording_status_unconfirmed;

        if (LoweringFeedbackOutcome.SHORTER_THAN_TARGET.name().equals(receivedOutcome)) {
            messageResource = R.string.lowering_feedback_shorter;
        }
        else if (LoweringFeedbackOutcome.NEAR_TARGET.name().equals(receivedOutcome)) {
            messageResource = R.string.lowering_feedback_near;
        }
        else if (LoweringFeedbackOutcome.LONGER_THAN_TARGET.name().equals(receivedOutcome)) {
            messageResource = R.string.lowering_feedback_longer;
        }

        if (SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED.name().equals(receivedAnalysisOutcome)) {
            detectionMessageResource = R.string.complete_repetition_detected;
        }

        if (SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED.name().equals(receivedAnalysisOutcome) && standingStartMs >= 0 && lowestAngleMs > standingStartMs && returnToStandingMs > lowestAngleMs) {
            double standingStartSeconds = standingStartMs/1000.0;
            double lowestAngleSeconds = lowestAngleMs/1000.0;
            double returnToStandingSeconds = returnToStandingMs/1000.0;


            String analysisResults = getString(R.string.repetition_timing, standingStartSeconds, lowestAngleSeconds, returnToStandingSeconds);

            sampledTextTimes.setText(analysisResults);
        }
        else {
            sampledTextTimes.setText(R.string.sampled_times_unavailable);
        }

        if (targetToleranceMs >= 0 && targetToleranceMs < targetDurationMs) {
            double targetDurationSeconds = targetDurationMs/1000.0;
            double targetToleranceSeconds = targetToleranceMs/1000.0;

            String practiceTarget = getString(R.string.practice_target_details, targetDurationSeconds, targetToleranceSeconds);

            practiceTargetText.setText(practiceTarget);
        }
        else {
            practiceTargetText.setText(R.string.practice_target_unavailable);
        }

        if (RecordingSuitability.CONFIRMED_SIDE_VIEW.name().equals(receivedRecordingSuitability)) {
            recordingMessageResource = R.string.recording_status_confirmed;
        }
        else if (RecordingSuitability.UNSUPPORTED_VIEW.name().equals(receivedRecordingSuitability)){
            recordingMessageResource = R.string.recording_status_unsupported;
        }

        if (!(loweringStartMs < 0) && loweringBottomMs > loweringStartMs) {
            double loweringStartSeconds = loweringStartMs/1000.0;
            double loweringBottomSeconds = loweringBottomMs/1000.0;
            double durationSeconds = durationMs/1000.0;

            String loweringTimings = getString(R.string.lowering_timing_valid, loweringStartSeconds, loweringBottomSeconds, durationSeconds);

            loweringTimingText.setText(loweringTimings);
        }
        else {
            loweringTimingText.setText(R.string.lowering_timing_invalid);
        }

        if (minimumDurationMs > 0 && maximumDurationMs >= minimumDurationMs) {
            double minimumDurationSeconds = minimumDurationMs/1000.0;
            double maximumDurationSeconds = maximumDurationMs/1000.0;

            String loweringDuration = getString(R.string.lowering_duration_valid, minimumDurationSeconds, maximumDurationSeconds);

            loweringDurationText.setText(loweringDuration);
        }
        else {
            loweringDurationText.setText(R.string.lowering_duration_invalid);
        }

        loweringFeedbackText.setText(messageResource);
        detectionOutcomeText.setText(detectionMessageResource);
        recordingStatusText.setText(recordingMessageResource);

        MaterialToolbar resultsToolbar = findViewById(R.id.resultsToolbar);

        resultsToolbar.setNavigationOnClickListener(v -> this.finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}