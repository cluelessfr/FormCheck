package com.taran.formcheck.feedback;

import com.taran.formcheck.pose.LoweringTiming;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class LoweringDurationRangeTest {
    @Test
    public void testNormalConversion() {
        LoweringTiming timing = new LoweringTiming(2000, 5000, 500);
        Optional<LoweringDurationRange> range = LoweringDurationRange.fromTiming(timing);

        Assert.assertTrue(range.isPresent());
        Assert.assertEquals(2500, range.get().getMinimumDurationMs());
        Assert.assertEquals(3500, range.get().getMaximumDurationMs());
    }

    @Test
    public void testMissingTiming() {
        Optional<LoweringDurationRange> range = LoweringDurationRange.fromTiming(null);

        Assert.assertFalse(range.isPresent());
    }

    @Test
    public void testNonstopLowerBound() {
        LoweringTiming timing = new LoweringTiming(0, 500, 500);
        Optional<LoweringDurationRange> range = LoweringDurationRange.fromTiming(timing);

        Assert.assertFalse(range.isPresent());
    }

    @Test
    public void testOverflow() {
        LoweringTiming timing = new LoweringTiming(0, Long.MAX_VALUE, 1);
        Optional<LoweringDurationRange> range = LoweringDurationRange.fromTiming(timing);

        Assert.assertFalse(range.isPresent());
    }
}
