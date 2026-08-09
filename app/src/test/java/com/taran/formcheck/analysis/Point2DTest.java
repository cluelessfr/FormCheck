package com.taran.formcheck.analysis;


import org.junit.Assert;
import org.junit.Test;

public class Point2DTest {
    Point2D point = new Point2D(2.4, 5.6);

    @Test
    public void testGetCoordinates() {
        Assert.assertEquals(2.4, point.getX(), 0.000001);
        Assert.assertEquals(5.6, point.getY(), 0.000001);
    }

}
