package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

public class SquatRepetitionTest {
    @Test
    public void testValidInputs() {
        SquatRepetition repetition = new SquatRepetition(1000, 2000, 4000);
        Assert.assertEquals(1000, repetition.getStandingStartTimestamp());
        Assert.assertEquals(2000, repetition.getLowestAngleTimestamp());
        Assert.assertEquals(4000, repetition.getReturnToStandingTimestamp());
    }

    @Test
    public void testInvalidInputs() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(-1, 1000, 2000));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(1000, 1000, 4000));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetition(1000, 4000, 4000));
    }
}
