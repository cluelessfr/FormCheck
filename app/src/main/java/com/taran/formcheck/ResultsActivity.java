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
import com.taran.formcheck.pose.SquatAnalysisOutcome;

public class ResultsActivity extends AppCompatActivity {
    public static final String EXTRA_FEEDBACK_OUTCOME = "com.taran.formcheck.extra.FEEDBACK_OUTCOME";
    public static final String EXTRA_ANALYSIS_OUTCOME = "com.taran.formcheck.extra.ANALYSIS_OUTCOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        TextView loweringFeedbackText = findViewById(R.id.loweringFeedbackText);
        TextView detectionOutcomeText = findViewById(R.id.detectionOutcomeText);
        String receivedOutcome = getIntent().getStringExtra(EXTRA_FEEDBACK_OUTCOME);
        String receivedAnalysisOutcome = getIntent().getStringExtra(EXTRA_ANALYSIS_OUTCOME);
        int messageResource = R.string.lowering_feedback_unavailable;
        int detectionMessageResource = R.string.insufficient_evidence;

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

        loweringFeedbackText.setText(messageResource);
        detectionOutcomeText.setText(detectionMessageResource);

        MaterialToolbar resultsToolbar = findViewById(R.id.resultsToolbar);

        resultsToolbar.setNavigationOnClickListener(v -> this.finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}