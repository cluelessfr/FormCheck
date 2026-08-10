package com.taran.formcheck.analysis;

import org.junit.Assert;
import org.junit.Test;

public class LandmarkQualityGateTest {
    @Test
    public void testUsableObservations() {
        Point2D point = new Point2D(1, 0);
        LandmarkObservation observation = new LandmarkObservation(point, 0.5, 0.5);

        LandmarkQualityGate gate = new LandmarkQualityGate(0.4, 0.4);
        LandmarkQualityGate gate1 = new LandmarkQualityGate(0.5, 0.5);

        Assert.assertTrue(gate.isUsable(observation));
        Assert.assertTrue(gate1.isUsable(observation));
    }

    @Test
    public void testBelowThresholdObservations() {
        Point2D point = new Point2D(1, 0);
        LandmarkObservation observation = new LandmarkObservation(point, 0.45, 0.5);
        LandmarkObservation observation1 = new LandmarkObservation(point, 0.5, 0.3);

        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.4);

        Assert.assertFalse(gate.isUsable(observation));
        Assert.assertFalse(gate.isUsable(observation1));
    }

    @Test
    public void testMissingObservations() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.4);

        Assert.assertFalse(gate.isUsable(null));
    }

    @Test
    public void testInvalidGateThresholds() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkQualityGate(-0.3, 0.4));
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkQualityGate(0.3, 1.3));
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkQualityGate(Double.NaN, 0.4));
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkQualityGate(0.3, Double.POSITIVE_INFINITY));
    }
}
