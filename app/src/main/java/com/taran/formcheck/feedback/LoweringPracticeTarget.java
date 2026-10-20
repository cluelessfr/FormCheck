package com.taran.formcheck.feedback;

public class LoweringPracticeTarget {
    private final long targetDurationMS;
    private final long comparisonToleranceMS;

    public LoweringPracticeTarget(long targetDurationMS, long comparisonToleranceMS) {
        if (!(targetDurationMS > 0)) {
            throw new IllegalArgumentException("Target Duration must be positive");
        }

        if (comparisonToleranceMS < 0) {
            throw new IllegalArgumentException("Comparison Tolerance must be nonnegative");
        }

        if (comparisonToleranceMS >= targetDurationMS) {
            throw new IllegalArgumentException("Target Duration must be greater than Comparison Tolerance");
        }

        this.targetDurationMS = targetDurationMS;
        this.comparisonToleranceMS = comparisonToleranceMS;
    }

    public long getTargetDurationMS() {
        return targetDurationMS;
    }

    public long getComparisonToleranceMS() {
        return comparisonToleranceMS;
    }
}
