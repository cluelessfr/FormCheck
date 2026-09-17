package com.taran.formcheck.pose;

import com.taran.formcheck.analysis.LandmarkObservation;

public class LegLandmarkObservations {
    private final BodySide side;
    private final LandmarkObservation hip;
    private final LandmarkObservation knee;
    private final LandmarkObservation ankle;

    public LegLandmarkObservations(BodySide side, LandmarkObservation hip, LandmarkObservation knee, LandmarkObservation ankle) {
        if (side == null) {
            throw new IllegalArgumentException("Side cannot be null");
        }
        if (hip == null) {
            throw new IllegalArgumentException("Hip cannot be null");
        }
        if (knee == null) {
            throw new IllegalArgumentException("Knee cannot be null");
        }
        if (ankle == null) {
            throw new IllegalArgumentException("Ankle cannot be null");
        }

        this.side = side;
        this.hip = hip;
        this.knee = knee;
        this.ankle = ankle;
    }

    public BodySide getSide() {
        return side;
    }

    public LandmarkObservation getHip() {
        return hip;
    }

    public LandmarkObservation getKnee() {
        return knee;
    }

    public LandmarkObservation getAnkle() {
        return ankle;
    }
}
