package com.taran.formcheck.pose;

public final class SquatRepetition {
    private final long standingStartTimestamp;
    private final long lowestAngleTimestamp;
    private final long returnToStandingTimestamp;

    public SquatRepetition(long standingStartTimestamp, long lowestAngleTimestamp, long returnToStandingTimestamp) {
        if (!(0 <= standingStartTimestamp && standingStartTimestamp < lowestAngleTimestamp && lowestAngleTimestamp < returnToStandingTimestamp)) {
            throw new IllegalArgumentException("All angle timestamps must be nonnegative and in increasing order");
        }

        this.standingStartTimestamp = standingStartTimestamp;
        this.lowestAngleTimestamp = lowestAngleTimestamp;
        this.returnToStandingTimestamp = returnToStandingTimestamp;
    }

    public long getStandingStartTimestamp() {
        return standingStartTimestamp;
    }

    public long getLowestAngleTimestamp() {
        return lowestAngleTimestamp;
    }

    public long getReturnToStandingTimestamp() {
        return returnToStandingTimestamp;
    }
}
