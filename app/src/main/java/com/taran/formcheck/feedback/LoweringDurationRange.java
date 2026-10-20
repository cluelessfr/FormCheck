package com.taran.formcheck.feedback;

public class LoweringDurationRange {
    private final long minimumDurationMs;
    private final long maximumDurationMs;

    public LoweringDurationRange(long minimumDurationMs, long maximumDurationMs) {
        if (!(minimumDurationMs > 0)) {
            throw new IllegalArgumentException("Minimum Duration must be positive");
        }

        if (!(maximumDurationMs >= minimumDurationMs)) {
            throw new IllegalArgumentException("Maximum Duration must be greater than or equal to the Minimum Duration");
        }

        this.minimumDurationMs = minimumDurationMs;
        this.maximumDurationMs = maximumDurationMs;
    }

    public long getMinimumDurationMs() {
        return minimumDurationMs;
    }

    public long getMaximumDurationMs() {
        return maximumDurationMs;
    }
}
