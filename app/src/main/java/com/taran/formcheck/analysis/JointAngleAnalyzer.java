package com.taran.formcheck.analysis;

import java.util.OptionalDouble;

public class JointAngleAnalyzer {
    private final LandmarkQualityGate qualityGate;

    public JointAngleAnalyzer(LandmarkQualityGate qualityGate) {
        if (qualityGate == null) {
            throw new IllegalArgumentException("The quality gate cannot be null");
        }

        this.qualityGate = qualityGate;
    }

    public OptionalDouble analyzeAngle(LandmarkObservation firstEndpoint, LandmarkObservation vertex, LandmarkObservation secondEndpoint) {
        if (!qualityGate.isUsable(firstEndpoint) || !qualityGate.isUsable(vertex) || !qualityGate.isUsable(secondEndpoint)) {
            return OptionalDouble.empty();
        }

        Point2D firstEndpointPoint = firstEndpoint.getPosition();
        Point2D vertexPoint = vertex.getPosition();
        Point2D secondEndpointPoint = secondEndpoint.getPosition();

        double jointAngle = JointAngleCalculator.calculateAngleDegrees(firstEndpointPoint, vertexPoint, secondEndpointPoint);

        return OptionalDouble.of(jointAngle);
    }
}
