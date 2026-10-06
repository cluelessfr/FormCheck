package com.taran.formcheck.pose;

import java.util.List;
import java.util.Optional;

public final class SquatRepetitionDetector {
    private final double standingAngleThreshold;
    private final double bottomAngleThreshold;
    private final long maximumGapMilliseconds;

    private static final long DEFAULT_MAXIMUM_GAP_MILLISECONDS = 1000;


    public SquatRepetitionDetector(double standingAngleThreshold, double bottomAngleThreshold) {
        this(standingAngleThreshold, bottomAngleThreshold, DEFAULT_MAXIMUM_GAP_MILLISECONDS);
    }

    public SquatRepetitionDetector(double standingAngleThreshold, double bottomAngleThreshold, long maximumGapMilliseconds) {
        if (!Double.isFinite(standingAngleThreshold) || !Double.isFinite(bottomAngleThreshold)) {
            throw new IllegalArgumentException("Both angle parameters must be finite");
        }

        if (!(0 <= standingAngleThreshold && standingAngleThreshold <= 180 && 0 <= bottomAngleThreshold && bottomAngleThreshold <= 180)) {
            throw new IllegalArgumentException("Both angle parameters must be within 0-180 degrees");
        }

        if (!(bottomAngleThreshold < standingAngleThreshold)) {
            throw new IllegalArgumentException("Bottom angle threshold must be less than the standing angle threshold");
        }

        if (maximumGapMilliseconds <= 0) {
            throw new IllegalArgumentException("The maximum gap must be greater than 0");
        }

        this.maximumGapMilliseconds = maximumGapMilliseconds;

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
        Long previousTimestampMilliseconds = null;
        double smallestAngle = Double.POSITIVE_INFINITY;
        long smallestAngleTimestamp = -1;
        boolean reachedBottom = false;

        for (TimestampedKneeAngle angle : angles) {
            double angleDegrees = angle.getAngleDegrees();
            long timestampMilliseconds = angle.getTimestampMilliseconds();

            if (previousTimestampMilliseconds != null && timestampMilliseconds - previousTimestampMilliseconds > maximumGapMilliseconds) {
                standingStartTimestamp = null;
                smallestAngle = Double.POSITIVE_INFINITY;
                smallestAngleTimestamp = -1;
                reachedBottom = false;
            }

            previousTimestampMilliseconds = timestampMilliseconds;

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

    public SquatAnalysisResult analyze(List<TimestampedKneeAngle> angles) {
        Optional<SquatRepetition> repetition = detect(angles);

        if (repetition.isPresent()) {
            return new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition.get());
        }
        else {
            return new SquatAnalysisResult(SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE, null);
        }
    }
}
