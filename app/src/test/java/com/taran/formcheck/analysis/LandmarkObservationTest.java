package com.taran.formcheck.analysis;

import org.junit.Assert;
import org.junit.Test;

public class LandmarkObservationTest {
    @Test
    public void testValidObservation() {
        Point2D point = new Point2D(0, 1);
        LandmarkObservation landmark = new LandmarkObservation(point, 0.75, 0.5);

        Assert.assertSame(point, landmark.getPosition());
        Assert.assertEquals(0.75, landmark.getVisibility(), 0.000001);
        Assert.assertEquals(0.5, landmark.getPresence(), 0.000001);
    }

    @Test
    public void testNullPosition() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkObservation(null, 0.75, 0.45));
    }

    @Test
    public void testOutOfRangeScores() {
        Point2D point = new Point2D(0, 1);
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkObservation(point, -1, 0.4));
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkObservation(point, 1, 3));
    }

    @Test
    public void testNonFiniteScores() {
        Point2D point = new Point2D(0, 1);
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkObservation(point, Double.NaN, 0.4));
        Assert.assertThrows(IllegalArgumentException.class, () -> new LandmarkObservation(point, 0.3, Double.POSITIVE_INFINITY));
    }
}
