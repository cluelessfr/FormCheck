package com.taran.formcheck.analysis;

public class LandmarkQualityGate {
    private final double minimumVisibility;
    private final double minimumPresence;

    public LandmarkQualityGate(double minimumVisibility, double minimumPresence) {
        if (minimumVisibility < 0 || minimumVisibility > 1 || minimumPresence  < 0 || minimumPresence > 1 || !Double.isFinite(minimumVisibility) || !Double.isFinite(minimumPresence)) {
            throw new IllegalArgumentException("Visibility and Presence must both be between 0 and 1 (inclusive)");
        }

        this.minimumVisibility = minimumVisibility;
        this.minimumPresence = minimumPresence;
    }

    public boolean isUsable(LandmarkObservation observation) {
        if (observation == null) {
            return false;
        }

        double visibility = observation.getVisibility();
        double presence = observation.getPresence();

        return visibility >= minimumVisibility && presence >= minimumPresence;
    }
}
