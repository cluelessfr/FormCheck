package com.taran.formcheck.pose;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.taran.formcheck.analysis.LandmarkObservation;
import com.taran.formcheck.analysis.Point2D;

public final class MediaPipeLandmarkConverter {
    private MediaPipeLandmarkConverter() {
    }

    public static LandmarkObservation convert(NormalizedLandmark landmark, int width, int height) {
        if (landmark == null) {
            throw new IllegalArgumentException("Landmark cannot be null");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        Point2D point = new Point2D(landmark.x() * width, landmark.y() * height);

        double visibility;
        double presence;

        if (landmark.visibility().isPresent()) {
            visibility = landmark.visibility().get();
        }
        else {
            visibility = 0.0;
        }

        if (landmark.presence().isPresent()) {
            presence = landmark.presence().get();
        }
        else {
            presence = 0.0;
        }

        return new LandmarkObservation(point, visibility, presence);
    }

}
