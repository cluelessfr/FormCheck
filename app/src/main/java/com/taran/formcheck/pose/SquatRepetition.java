package com.taran.formcheck.pose;

public final class SquatRepetition {
    private final TimestampedKneeAngle standingStart;
    private final TimestampedKneeAngle lowestAngle;
    private final TimestampedKneeAngle returnToStanding;

    public SquatRepetition(TimestampedKneeAngle standingStart, TimestampedKneeAngle lowestAngle, TimestampedKneeAngle returnToStanding) {
        if (standingStart == null || lowestAngle == null || returnToStanding == null) {
            throw new IllegalArgumentException("Angle samples cannot be null");
        }

        if (!(standingStart.getTimestampMilliseconds() < lowestAngle.getTimestampMilliseconds() && lowestAngle.getTimestampMilliseconds() < returnToStanding.getTimestampMilliseconds())) {
            throw new IllegalArgumentException("All angle timestamps must be nonnegative and in increasing order");
        }

        if (standingStart.getSide() != lowestAngle.getSide() || lowestAngle.getSide() != returnToStanding.getSide()) {
            throw new IllegalArgumentException("All angle samples must use the same body side");
        }

        this.standingStart = standingStart;
        this.lowestAngle = lowestAngle;
        this.returnToStanding = returnToStanding;
    }

    public long getStandingStartTimestamp() {
        return standingStart.getTimestampMilliseconds();
    }

    public long getLowestAngleTimestamp() {
        return lowestAngle.getTimestampMilliseconds();
    }

    public long getReturnToStandingTimestamp() {
        return returnToStanding.getTimestampMilliseconds();
    }

    public double getStandingStartAngleDegrees() {
        return standingStart.getAngleDegrees();
    }

    public double getLowestAngleDegrees() {
        return lowestAngle.getAngleDegrees();
    }

    public double getReturnToStandingAngleDegrees() {
        return returnToStanding.getAngleDegrees();
    }
}
