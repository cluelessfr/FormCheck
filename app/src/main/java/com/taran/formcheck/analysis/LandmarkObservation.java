package com.taran.formcheck.analysis;

public class LandmarkObservation {
    private final Point2D position;
    private final double visibility;
    private final double presence;

    public LandmarkObservation(Point2D position, double visibility, double presence) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        if (visibility < 0 || visibility > 1 || presence  < 0 || presence > 1 || !Double.isFinite(visibility) || !Double.isFinite(presence)) {
            throw new IllegalArgumentException("Visibility and Presence must both be between 0 and 1 (inclusive)");
        }

        this.position = position;
        this.visibility = visibility;
        this.presence = presence;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getPresence() {
        return presence;
    }
}
