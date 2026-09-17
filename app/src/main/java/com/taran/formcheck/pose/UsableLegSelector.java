package com.taran.formcheck.pose;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;
import com.taran.formcheck.analysis.LandmarkQualityGate;

import java.util.Optional;

public class UsableLegSelector {
    private final LandmarkQualityGate qualityGate;

    public UsableLegSelector(LandmarkQualityGate qualityGate) {
        if (qualityGate == null) {
            throw new IllegalArgumentException("QualityGate cannot be null");
        }

        this.qualityGate = qualityGate;
    }

    private boolean isUsable(LegLandmarkObservations leg) {
        if (leg == null) {
            return false;
        }
        return qualityGate.isUsable(leg.getHip()) && qualityGate.isUsable(leg.getKnee()) && qualityGate.isUsable(leg.getAnkle());
    }

    private double confidenceScore(LegLandmarkObservations leg) {
        double minimumValue = leg.getHip().getVisibility();
        minimumValue = Math.min(minimumValue, leg.getHip().getPresence());
        minimumValue = Math.min(minimumValue, leg.getKnee().getVisibility());
        minimumValue = Math.min(minimumValue, leg.getKnee().getPresence());
        minimumValue = Math.min(minimumValue, leg.getAnkle().getVisibility());
        minimumValue = Math.min(minimumValue, leg.getAnkle().getPresence());

        return minimumValue;
    }

    public Optional<LegLandmarkObservations> select(PoseLandmarkerResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }

        Optional<LegLandmarkObservations> leftLeg = LegLandmarkExtractor.extractForSide(result, BodySide.LEFT);
        Optional<LegLandmarkObservations> rightLeg = LegLandmarkExtractor.extractForSide(result, BodySide.RIGHT);

        boolean leftLegIsUsable = leftLeg.isPresent() && isUsable(leftLeg.get());
        boolean rightLegIsUsable = rightLeg.isPresent() && isUsable(rightLeg.get());

        if (!leftLegIsUsable && !rightLegIsUsable) {
            return Optional.empty();
        }

        if (leftLegIsUsable && !rightLegIsUsable) {
            return leftLeg;
        }

        if (!leftLegIsUsable) {
            return rightLeg;
        }

        if (confidenceScore(leftLeg.get()) >= confidenceScore(rightLeg.get())) {
            return leftLeg;
        }

        return rightLeg;
    }
}
