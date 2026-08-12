package com.taran.formcheck.analysis;


import org.junit.Assert;
import org.junit.Test;

public class Point2DTest {
    @Test
    public void testGetCoordinates() {
        Point2D point = new Point2D(2.4, 5.6);

        Assert.assertEquals(2.4, point.getX(), 0.000001);
        Assert.assertEquals(5.6, point.getY(), 0.000001);
    }

    @Test
    public void testNonFiniteCoordinates() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new Point2D(Double.NaN, 5.6));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Point2D(2.4, Double.NaN));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Point2D(Double.POSITIVE_INFINITY, 5.6));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Point2D(2.4, Double.NEGATIVE_INFINITY));
    }
}
