package com.taran.formcheck.feedback;

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
}
