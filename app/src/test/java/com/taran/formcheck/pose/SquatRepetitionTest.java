package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

public class SquatRepetitionTest {
    @Test
    public void testValidInputs() {
        SquatRepetition repetition = new SquatRepetition(new TimestampedKneeAngle(1000, BodySide.LEFT, 171), new TimestampedKneeAngle(2000, BodySide.LEFT, 108), new TimestampedKneeAngle(4000, BodySide.LEFT, 168));
        Assert.assertEquals(1000, repetition.getStandingStartTimestamp());
        Assert.assertEquals(2000, repetition.getLowestAngleTimestamp());
        Assert.assertEquals(4000, repetition.getReturnToStandingTimestamp());
        Assert.assertEquals(171, repetition.getStandingStartAngleDegrees(), 0);
        Assert.assertEquals(108, repetition.getLowestAngleDegrees(), 0);
        Assert.assertEquals(168, repetition.getReturnToStandingAngleDegrees(), 0);
    }

    @Test
    public void testInvalidInputs() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(new TimestampedKneeAngle(-1, BodySide.LEFT, 171), new TimestampedKneeAngle(1000, BodySide.LEFT, 108), new TimestampedKneeAngle(2000, BodySide.LEFT, 168)));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(new TimestampedKneeAngle(1000, BodySide.LEFT, 171), new TimestampedKneeAngle(1000, BodySide.LEFT, 108), new TimestampedKneeAngle(4000, BodySide.LEFT, 168)));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(new TimestampedKneeAngle(1000, BodySide.LEFT, 171), new TimestampedKneeAngle(4000, BodySide.LEFT, 108), new TimestampedKneeAngle(4000, BodySide.LEFT, 168)));
    }

    @Test
    public void testMissingSamples() {
        TimestampedKneeAngle standing = new TimestampedKneeAngle(0, BodySide.LEFT, 171);
        TimestampedKneeAngle bottom = new TimestampedKneeAngle(1000, BodySide.LEFT, 108);
        TimestampedKneeAngle returned = new TimestampedKneeAngle(2000, BodySide.LEFT, 168);

        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(null, bottom, returned));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(standing, null, returned));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(standing, bottom, null));
    }

    @Test
    public void testMixedBodySides() {
        TimestampedKneeAngle standing = new TimestampedKneeAngle(0, BodySide.LEFT, 171);
        TimestampedKneeAngle bottom = new TimestampedKneeAngle(1000, BodySide.RIGHT, 108);
        TimestampedKneeAngle returned = new TimestampedKneeAngle(2000, BodySide.LEFT, 168);

        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(standing, bottom, returned));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(standing, new TimestampedKneeAngle(1000, BodySide.LEFT, 108), new TimestampedKneeAngle(2000, BodySide.RIGHT, 168)));
    }
}
