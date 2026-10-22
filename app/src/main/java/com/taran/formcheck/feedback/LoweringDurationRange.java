package com.taran.formcheck.feedback;

import com.taran.formcheck.pose.LoweringTiming;

import java.util.Optional;

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

    public static Optional<LoweringDurationRange> fromTiming(LoweringTiming loweringTiming) {
        if (loweringTiming == null) {
            return Optional.empty();
        }

        long duration = loweringTiming.getDurationMilliseconds();
        long allowance = loweringTiming.getBoundarySamplingAllowanceMS();

        if (allowance >= duration) {
            return Optional.empty();
        }

        if (allowance > Long.MAX_VALUE - duration) {
            return Optional.empty();
        }

        return Optional.of(new LoweringDurationRange(duration - allowance, duration + allowance));
    }
}
