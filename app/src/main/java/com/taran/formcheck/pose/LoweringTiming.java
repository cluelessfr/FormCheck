package com.taran.formcheck.pose;

public class LoweringTiming {
    private final long loweringStart;
    private final long bottomReached;

    public LoweringTiming(long loweringStart, long bottomReached) {
        if (loweringStart < 0) {
            throw new IllegalArgumentException("Lowering timestamp cannot be negative");
        }

        if (!(bottomReached > loweringStart)) {
            throw new IllegalArgumentException("Bottom timestamp must be greater than lowering timestamp");
        }

        this.loweringStart = loweringStart;
        this.bottomReached = bottomReached;
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
}
