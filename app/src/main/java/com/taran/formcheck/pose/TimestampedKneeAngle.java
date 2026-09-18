package com.taran.formcheck.pose;

public final class TimestampedKneeAngle {
    private final long timestampMilliseconds;
    private final BodySide side;
    private final double angleDegrees;
    public TimestampedKneeAngle(long timestampMilliseconds, BodySide side, double angleDegrees) {
        if (timestampMilliseconds < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }

        if (side == null) {
            throw new IllegalArgumentException("BodySide cannot be null");
        }

        if (!Double.isFinite(angleDegrees)) {
            throw new IllegalArgumentException("Angle must be finite");
        }

        if (angleDegrees < 0 || angleDegrees > 180) {
            throw new IllegalArgumentException("Angle must be between 0 and 180 degrees");
        }

        this.timestampMilliseconds = timestampMilliseconds;
        this.side = side;
        this.angleDegrees = angleDegrees;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
    }

    public BodySide getSide() {
        return side;
    }

    public double getAngleDegrees() {
        return angleDegrees;
    }
}
