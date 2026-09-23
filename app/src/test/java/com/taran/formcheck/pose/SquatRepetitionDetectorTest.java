package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

public class SquatRepetitionDetectorTest {
    @Test
    public void testValidInputs() {
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
    }

    @Test
    public void testInvalidInputs() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetitionDetector(Double.NaN, 120));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetitionDetector(190, 120));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatRepetitionDetector(50, 50));
    }
}
