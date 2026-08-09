package com.taran.formcheck.analysis;


public class JointAngleCalculator {
    public static double calculateAngleDegrees(Point2D firstEndpoint, Point2D vertex, Point2D secondEndpoint) {
        double vertexToFirstEndpointX = firstEndpoint.getX() - vertex.getX();
        double vertexToFirstEndpointY = firstEndpoint.getY() - vertex.getY();

        double vertexToSecondEndpointX = secondEndpoint.getX() - vertex.getX();
        double vertexToSecondEndpointY = secondEndpoint.getY() - vertex.getY();

        double dotProduct = (vertexToFirstEndpointX * vertexToSecondEndpointX) + (vertexToFirstEndpointY * vertexToSecondEndpointY);

        double vertexToFirstEndpointMagnitude = Math.sqrt(Math.pow(vertexToFirstEndpointX, 2) + Math.pow(vertexToFirstEndpointY, 2));
        double vertexToSecondEndpointMagnitude = Math.sqrt(Math.pow(vertexToSecondEndpointX, 2) + Math.pow(vertexToSecondEndpointY, 2));

        if (vertexToFirstEndpointMagnitude == 0 || vertexToSecondEndpointMagnitude == 0) {
            throw new IllegalArgumentException("Angle is undefined when endpoint and vertex are identical");
        }

        double angleCosine = Math.max(-1, Math.min(1, dotProduct / (vertexToFirstEndpointMagnitude * vertexToSecondEndpointMagnitude)));

        double angleRadians = Math.acos(angleCosine);
        double degrees = Math.toDegrees(angleRadians);

        return degrees;
    }
}
