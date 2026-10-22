package com.taran.formcheck.feedback;

import com.taran.formcheck.pose.SquatAnalysisOutcome;
import com.taran.formcheck.pose.SquatAnalysisResult;

import java.util.Optional;

public class LoweringFeedbackAnalyzer {
    public static LoweringFeedbackOutcome compare(long estimatedDurationMS, LoweringPracticeTarget target) {
        if (!(estimatedDurationMS > 0)) {
            throw new IllegalArgumentException("Estimated Duration must be positive");
        }

        if (target == null) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        long signedDifference = estimatedDurationMS - target.getTargetDurationMS();

        if (signedDifference < -target.getComparisonToleranceMS()) {
            return LoweringFeedbackOutcome.SHORTER_THAN_TARGET;
        }

        if (signedDifference > target.getComparisonToleranceMS()) {
            return LoweringFeedbackOutcome.LONGER_THAN_TARGET;
        }

        return LoweringFeedbackOutcome.NEAR_TARGET;
    }

    public static LoweringFeedbackOutcome compare(LoweringDurationRange range, LoweringPracticeTarget target) {
        if (range == null) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        if (target == null) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        LoweringFeedbackOutcome minimumClassification = compare(range.getMinimumDurationMs(), target);
        LoweringFeedbackOutcome maximumClassification = compare(range.getMaximumDurationMs(), target);

        if (minimumClassification == maximumClassification) {
            return maximumClassification;
        }

        return LoweringFeedbackOutcome.UNAVAILABLE;
    }

    public static LoweringFeedbackOutcome analyze(SquatAnalysisResult result, LoweringPracticeTarget target, RecordingSuitability suitability) {
        if (suitability != RecordingSuitability.CONFIRMED_SIDE_VIEW) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        if (result == null) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        if (target == null) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        if (result.getLoweringTiming().isEmpty() || result.getOutcome() != SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        Optional<LoweringDurationRange> range = LoweringDurationRange.fromTiming(result.getLoweringTiming().get());

        if (range.isEmpty()) {
            return LoweringFeedbackOutcome.UNAVAILABLE;
        }

        return compare(range.get(), target);
    }
}
