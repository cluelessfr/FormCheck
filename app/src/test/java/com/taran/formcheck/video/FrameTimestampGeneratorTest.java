package com.taran.formcheck.video;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FrameTimestampGeneratorTest {
    @Test
    public void testNormalInputReturnsExpectedSequence() {
        List<Long> expectedTimestamps = new ArrayList<>();
        expectedTimestamps.add(0L);
        expectedTimestamps.add(400L);
        expectedTimestamps.add(800L);

        Assert.assertEquals(expectedTimestamps, FrameTimestampGenerator.generateTimestamps(1000, 400));
    }

    @Test
    public void testGreaterIntervalReturnsZero() {
        List<Long> expectedTimestamps = new ArrayList<>();
        expectedTimestamps.add(0L);

        Assert.assertEquals(expectedTimestamps, FrameTimestampGenerator.generateTimestamps(300, 500));
    }

    @Test
    public void testInvalidDuration() {
        Assert.assertThrows(IllegalArgumentException.class, () -> FrameTimestampGenerator.generateTimestamps(0, 500));
    }

    @Test
    public void testInvalidInterval() {
        Assert.assertThrows(IllegalArgumentException.class, () -> FrameTimestampGenerator.generateTimestamps(300, 0));
    }
}
