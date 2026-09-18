package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

public class TimestampedKneeAngleTest {
    @Test
    public void testValidObjectPreservesData() {
        TimestampedKneeAngle kneeAngle = new TimestampedKneeAngle(1000, BodySide.LEFT, 90);

        Assert.assertEquals(1000, kneeAngle.getTimestampMilliseconds());
        Assert.assertEquals(BodySide.LEFT, kneeAngle.getSide());
        Assert.assertEquals(90, kneeAngle.getAngleDegrees(), 0.000001);
    }

    @Test
    public void testNegativeTimestampThrowsError() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new TimestampedKneeAngle(-1000, BodySide.LEFT, 90));
    }

    @Test
    public void testNullSideThrowsError() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new TimestampedKneeAngle(1000, null, 90));
    }

    @Test
    public void testInvalidAnglesThrowsError() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new TimestampedKneeAngle(1000, BodySide.LEFT, -1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new TimestampedKneeAngle(1000, BodySide.LEFT, 181));
        Assert.assertThrows(IllegalArgumentException.class, () -> new TimestampedKneeAngle(1000, BodySide.LEFT, Double.POSITIVE_INFINITY));
        Assert.assertThrows(IllegalArgumentException.class, () -> new TimestampedKneeAngle(1000, BodySide.LEFT, Double.NaN));
    }

}
