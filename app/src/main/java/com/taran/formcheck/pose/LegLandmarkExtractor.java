package com.taran.formcheck.pose;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;
import com.taran.formcheck.analysis.LandmarkObservation;

import java.util.List;
import java.util.Optional;

public final class LegLandmarkExtractor {
    private LegLandmarkExtractor() {
    }

    public static Optional<LegLandmarkObservations> extractForSide(PoseLandmarkerResult result, BodySide side) {
        if (result == null) {
            throw new IllegalArgumentException("PoseLandmarkerResult cannot be null");
        }

        if (side == null) {
            throw new IllegalArgumentException("BodySide cannot be null");
        }

        if (result.landmarks().isEmpty()) {
            return Optional.empty();
        }

        List<NormalizedLandmark> poseLandmarks = result.landmarks().get(0);

        if (poseLandmarks.size() <= PoseLandmarkIndices.RIGHT_ANKLE) {
            return Optional.empty();
        }

        int hipIndex;
        int kneeIndex;
        int ankleIndex;

        if (side == BodySide.LEFT) {
            hipIndex = PoseLandmarkIndices.LEFT_HIP;
            kneeIndex = PoseLandmarkIndices.LEFT_KNEE;
            ankleIndex = PoseLandmarkIndices.LEFT_ANKLE;
        }
        else {
            hipIndex = PoseLandmarkIndices.RIGHT_HIP;
            kneeIndex = PoseLandmarkIndices.RIGHT_KNEE;
            ankleIndex = PoseLandmarkIndices.RIGHT_ANKLE;
        }

        NormalizedLandmark normalizedHip = poseLandmarks.get(hipIndex);
        NormalizedLandmark normalizedKnee = poseLandmarks.get(kneeIndex);
        NormalizedLandmark normalizedAnkle = poseLandmarks.get(ankleIndex);

        LandmarkObservation hip = MediaPipeLandmarkConverter.convert(normalizedHip);
        LandmarkObservation knee = MediaPipeLandmarkConverter.convert(normalizedKnee);
        LandmarkObservation ankle = MediaPipeLandmarkConverter.convert(normalizedAnkle);

        LegLandmarkObservations observations = new LegLandmarkObservations(side, hip, knee, ankle);

        return Optional.of(observations);
    }
}
