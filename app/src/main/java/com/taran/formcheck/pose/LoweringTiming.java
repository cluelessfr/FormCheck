package com.taran.formcheck.pose;

public class LoweringTiming {
    private final long loweringStart;
    private final long bottomReached;
    private final long boundarySamplingAllowanceMS;

    public LoweringTiming(long loweringStart, long bottomReached, long boundarySamplingAllowanceMS) {
        if (loweringStart < 0) {
            throw new IllegalArgumentException("Lowering timestamp cannot be negative");
        }

        if (!(bottomReached > loweringStart)) {
            throw new IllegalArgumentException("Bottom timestamp must be greater than lowering timestamp");
        }

        if (!(boundarySamplingAllowanceMS > 0)) {
            throw new IllegalArgumentException("Boundary Sampling Allowance must be positive");
        }

        this.loweringStart = loweringStart;
        this.bottomReached = bottomReached;
        this.boundarySamplingAllowanceMS = boundarySamplingAllowanceMS;
    }

    public long getLoweringStart() {
        return loweringStart;
    }

    public long getBottomReached() {
        return bottomReached;
    }

    public long getDurationMilliseconds() {
        return bottomReached - loweringStart;
    }

    public long getBoundarySamplingAllowanceMS() {
        return boundarySamplingAllowanceMS;
    }
}
