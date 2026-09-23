package com.taran.formcheck.pose;

import java.util.List;
import java.util.Optional;

public final class SquatRepetitionDetector {
    private final double standingAngleThreshold;
    private final double bottomAngleThreshold;

    public SquatRepetitionDetector(double standingAngleThreshold, double bottomAngleThreshold) {
        if (!Double.isFinite(standingAngleThreshold) || !Double.isFinite(bottomAngleThreshold)) {
            throw new IllegalArgumentException("Both angle parameters must be finite");
        }

        if (!(0 <= standingAngleThreshold && standingAngleThreshold <= 180 && 0 <= bottomAngleThreshold && bottomAngleThreshold <= 180)) {
            throw new IllegalArgumentException("Both angle parameters must be within 0-180 degrees");
        }

        if (!(bottomAngleThreshold < standingAngleThreshold)) {
            throw new IllegalArgumentException("Bottom angle threshold must be less than the standing angle threshold");
        }

        this.standingAngleThreshold = standingAngleThreshold;
        this.bottomAngleThreshold = bottomAngleThreshold;
    }

    public Optional<SquatRepetition> detect(List<TimestampedKneeAngle> angles) {
        if (angles == null) {
            throw new IllegalArgumentException("TimestampedKneeAngles cannot be null");
        }

        if (angles.isEmpty()) {
            return Optional.empty();
        }

        Long standingStartTimestamp = null;
        double smallestAngle = Double.POSITIVE_INFINITY;
        long smallestAngleTimestamp = -1;
        boolean reachedBottom = false;

        for (TimestampedKneeAngle angle : angles) {
            double angleDegrees = angle.getAngleDegrees();
            long timestampMilliseconds = angle.getTimestampMilliseconds();

            if (standingStartTimestamp == null) {
                if (angleDegrees >= standingAngleThreshold) {
                    standingStartTimestamp = timestampMilliseconds;
                    smallestAngle = angleDegrees;
                    smallestAngleTimestamp = timestampMilliseconds;
                }

                continue;
            }

            if (!reachedBottom && angleDegrees >= standingAngleThreshold) {
                standingStartTimestamp = timestampMilliseconds;
                smallestAngle = angleDegrees;
                smallestAngleTimestamp = timestampMilliseconds;
                continue;
            }

            if (angleDegrees < smallestAngle) {
                smallestAngle = angleDegrees;
                smallestAngleTimestamp = timestampMilliseconds;
            }

            if (angleDegrees <= bottomAngleThreshold) {
                reachedBottom = true;
            }

            if (reachedBottom && angleDegrees >= standingAngleThreshold) {
                SquatRepetition repetition = new SquatRepetition(standingStartTimestamp, smallestAngleTimestamp, timestampMilliseconds);
                return Optional.of(repetition);
            }
        }

        return Optional.empty();
    }
}
