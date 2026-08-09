package com.taran.formcheck.analysis;

import org.junit.Assert;
import org.junit.Test;

public class JointAngleCalculatorTest {
    @Test
    public void testRightAngle() {
        Point2D firstEndpoint = new Point2D(0, 1);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(1, 0);

        double angle = JointAngleCalculator.calculateAngleDegrees(firstEndpoint, vertex, secondEndpoint);

        Assert.assertEquals(90, angle, 0.000001);
    }

    @Test
    public void testStraightAngle() {
        Point2D firstEndpoint = new Point2D(0, 1);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(0, -1);

        double angle = JointAngleCalculator.calculateAngleDegrees(firstEndpoint, vertex, secondEndpoint);

        Assert.assertEquals(180, angle, 0.000001);
    }

    @Test
    public void testUndefinedAngle() {
        Point2D firstEndpoint = new Point2D(0, 0);
        Point2D vertex = new Point2D(0, 0);
        Point2D secondEndpoint = new Point2D(0, -1);

        Assert.assertThrows(IllegalArgumentException.class, () -> JointAngleCalculator.calculateAngleDegrees(firstEndpoint, vertex, secondEndpoint));
    }
}
