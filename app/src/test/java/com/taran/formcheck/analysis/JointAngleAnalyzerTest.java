package com.taran.formcheck.analysis;

import org.junit.Assert;
import org.junit.Test;

import java.util.OptionalDouble;

public class JointAngleAnalyzerTest {
    @Test
    public void testValidAngle() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);

        Point2D firstEndpoint = new Point2D(1, 0);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(0, 1);

        LandmarkObservation firstEndpointObservation = new LandmarkObservation(firstEndpoint, 1, 1);
        LandmarkObservation vertexObservation = new LandmarkObservation(vertex, 1, 1);
        LandmarkObservation secondEndpointObservation = new LandmarkObservation(secondEndpoint, 1, 1);

        JointAngleAnalyzer analyzer = new JointAngleAnalyzer(gate);

        OptionalDouble result = analyzer.analyzeAngle(firstEndpointObservation, vertexObservation, secondEndpointObservation);

        Assert.assertTrue(result.isPresent());

        double doubleResult = result.getAsDouble();

        Assert.assertEquals(90, doubleResult, 0.000001);
    }

    @Test
    public void testLowQualityLandmark() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);

        Point2D firstEndpoint = new Point2D(1, 0);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(0, 1);

        LandmarkObservation firstEndpointObservation = new LandmarkObservation(firstEndpoint, 0, 1);
        LandmarkObservation vertexObservation = new LandmarkObservation(vertex, 1, 1);
        LandmarkObservation secondEndpointObservation = new LandmarkObservation(secondEndpoint, 1, 1);

        JointAngleAnalyzer analyzer = new JointAngleAnalyzer(gate);

        OptionalDouble result = analyzer.analyzeAngle(firstEndpointObservation, vertexObservation, secondEndpointObservation);

        Assert.assertFalse(result.isPresent());

        LandmarkObservation firstEndpointObservation1 = new LandmarkObservation(firstEndpoint, 1, 1);
        LandmarkObservation vertexObservation1 = new LandmarkObservation(vertex, 1, 0);
        LandmarkObservation secondEndpointObservation1 = new LandmarkObservation(secondEndpoint, 1, 1);

        OptionalDouble result1 = analyzer.analyzeAngle(firstEndpointObservation1, vertexObservation1, secondEndpointObservation1);

        Assert.assertFalse(result1.isPresent());

        LandmarkObservation firstEndpointObservation2 = new LandmarkObservation(firstEndpoint, 1, 1);
        LandmarkObservation vertexObservation2 = new LandmarkObservation(vertex, 1, 1);
        LandmarkObservation secondEndpointObservation2 = new LandmarkObservation(secondEndpoint, 0, 1);

        OptionalDouble result2 = analyzer.analyzeAngle(firstEndpointObservation2, vertexObservation2, secondEndpointObservation2);

        Assert.assertFalse(result2.isPresent());
    }

    @Test
    public void testNullLandmark() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);

        Point2D firstEndpoint = new Point2D(1, 0);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(0, 1);

        LandmarkObservation vertexObservation = new LandmarkObservation(vertex, 1, 1);
        LandmarkObservation secondEndpointObservation = new LandmarkObservation(secondEndpoint, 1, 1);

        JointAngleAnalyzer analyzer = new JointAngleAnalyzer(gate);

        OptionalDouble result = analyzer.analyzeAngle(null, vertexObservation, secondEndpointObservation);

        Assert.assertFalse(result.isPresent());

        LandmarkObservation firstEndpointObservation1 = new LandmarkObservation(firstEndpoint, 1, 1);
        LandmarkObservation secondEndpointObservation1 = new LandmarkObservation(secondEndpoint, 1, 1);

        OptionalDouble result1 = analyzer.analyzeAngle(firstEndpointObservation1, null, secondEndpointObservation1);

        Assert.assertFalse(result1.isPresent());

        LandmarkObservation firstEndpointObservation2 = new LandmarkObservation(firstEndpoint, 1, 1);
        LandmarkObservation vertexObservation2 = new LandmarkObservation(vertex, 1, 1);

        OptionalDouble result2 = analyzer.analyzeAngle(firstEndpointObservation2, vertexObservation2, null);

        Assert.assertFalse(result2.isPresent());
    }

    @Test
    public void testNullGate() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new JointAngleAnalyzer(null));
    }

    @Test
    public void testUndefinedGeometry() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);

        Point2D firstEndpoint = new Point2D(0, 0);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(0, 1);

        LandmarkObservation firstEndpointObservation = new LandmarkObservation(firstEndpoint, 1, 1);
        LandmarkObservation vertexObservation = new LandmarkObservation(vertex, 1, 1);
        LandmarkObservation secondEndpointObservation = new LandmarkObservation(secondEndpoint, 1, 1);

        JointAngleAnalyzer analyzer = new JointAngleAnalyzer(gate);

        Assert.assertThrows(IllegalArgumentException.class, () -> analyzer.analyzeAngle(firstEndpointObservation, vertexObservation, secondEndpointObservation));
    }
}
