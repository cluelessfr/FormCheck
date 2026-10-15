package com.taran.formcheck.pose;

import java.util.List;
import java.util.Optional;

public class LoweringTimingAnalyzer {
    private final double angleChange;
    private final double minimumDistance;
    private final long maximumGap;

    public LoweringTimingAnalyzer(double angleChange, double minimumDistance, long maximumGap) {
        if (!Double.isFinite(angleChange) || !(angleChange > 0) || !(angleChange < 180)) {
            throw new IllegalArgumentException("Angle change must be finite, greater than 0, and less than 180 degrees");
        }

        if (!Double.isFinite(minimumDistance) || !(minimumDistance > 0) || !(minimumDistance < 180)) {
            throw new IllegalArgumentException("Minimum distance must be finite, greater than 0, and less than 180 degrees");
        }

        if (maximumGap <= 0) {
            throw new IllegalArgumentException("Maximum gap must be positive");
        }

        this.angleChange = angleChange;
        this.minimumDistance = minimumDistance;
        this.maximumGap = maximumGap;
    }

    private boolean isContinuous(List<TimestampedKneeAngle> angles) {
        if (angles == null) {
            throw new IllegalArgumentException("List cannot be null");
        }


        if (angles.size() < 3) {
            return false;
        }

        Long previousTimestampMilliseconds = null;
        BodySide previousSide = null;

        for (TimestampedKneeAngle angle : angles) {
            if (angle == null) {
                throw new IllegalArgumentException("Angle entries cannot be null");
            }

            long timestampMilliseconds = angle.getTimestampMilliseconds();

            if (previousTimestampMilliseconds != null && timestampMilliseconds <= previousTimestampMilliseconds) {
                throw new IllegalArgumentException("Current timestamp must be greater than previous timestamp");
            }

            if (previousSide != null && previousSide != angle.getSide()) {
                return false;
            }

            if (previousTimestampMilliseconds != null && timestampMilliseconds - previousTimestampMilliseconds > maximumGap) {
                return false;
            }

            previousTimestampMilliseconds = timestampMilliseconds;
            previousSide = angle.getSide();
        }

        return true;
    }

    private int findBottomIndex(List<TimestampedKneeAngle> angles) {
        double smallestAngle = Double.POSITIVE_INFINITY;

        for (TimestampedKneeAngle angle : angles) {
            if (angle.getAngleDegrees() < smallestAngle) {
                smallestAngle = angle.getAngleDegrees();
            }
        }

        for (int i = 0; i < angles.size(); i++) {
            if (Math.abs(angles.get(i).getAngleDegrees() - smallestAngle) <= minimumDistance) {
                return i;
            }
        }

        return -1;
    }

    private int findLoweringStartIndex(List<TimestampedKneeAngle> angles, int bottomIndex) {
        double highestEarlierAngle = angles.get(0).getAngleDegrees();

        for (int i = 1; i+1 <= bottomIndex; i++) {
            double currentAngle = angles.get(i).getAngleDegrees();
            double nextAngle = angles.get(i+1).getAngleDegrees();

            double firstDecrease = highestEarlierAngle - currentAngle;
            double secondDecrease = highestEarlierAngle - nextAngle;

            if (firstDecrease >= angleChange && secondDecrease >= angleChange) {
                return i-1;
            }

            highestEarlierAngle = Math.max(highestEarlierAngle, currentAngle);
        }

        return -1;
    }

    public Optional<LoweringTiming> analyze(List<TimestampedKneeAngle> angles) {
        if (!isContinuous(angles)) {
            return Optional.empty();
        }

        int bottomIndex = findBottomIndex(angles);

        if (bottomIndex == -1 || bottomIndex == 0 || bottomIndex == angles.size() - 1) {
            return Optional.empty();
        }

        int loweringStartIndex = findLoweringStartIndex(angles, bottomIndex);

        if (loweringStartIndex == -1) {
            return Optional.empty();
        }

        long loweringStartTimestamp = angles.get(loweringStartIndex).getTimestampMilliseconds();
        long bottomTimestamp = angles.get(bottomIndex).getTimestampMilliseconds();

        LoweringTiming loweringTiming = new LoweringTiming(loweringStartTimestamp, bottomTimestamp);

        return Optional.of(loweringTiming);
    }
}
