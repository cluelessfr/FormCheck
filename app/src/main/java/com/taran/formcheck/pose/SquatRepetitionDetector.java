package com.taran.formcheck.pose;

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
}
