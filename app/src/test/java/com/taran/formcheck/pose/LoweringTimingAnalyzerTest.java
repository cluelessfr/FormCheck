package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoweringTimingAnalyzerTest {
    @Test
    public void testLoweringFollowedByBottomPause() {
        LoweringTimingAnalyzer analyzer = new LoweringTimingAnalyzer(5, 3, 500);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle one = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle two = new TimestampedKneeAngle(250, BodySide.LEFT, 170);
        TimestampedKneeAngle three = new TimestampedKneeAngle(500, BodySide.LEFT, 168);
        TimestampedKneeAngle four = new TimestampedKneeAngle(750, BodySide.LEFT, 160);
        TimestampedKneeAngle five = new TimestampedKneeAngle(1000, BodySide.LEFT, 145);
        TimestampedKneeAngle six = new TimestampedKneeAngle(1250, BodySide.LEFT, 112);
        TimestampedKneeAngle seven = new TimestampedKneeAngle(1500, BodySide.LEFT, 110);
        TimestampedKneeAngle eight = new TimestampedKneeAngle(1750, BodySide.LEFT, 111);
        TimestampedKneeAngle nine = new TimestampedKneeAngle(2000, BodySide.LEFT, 140);
        TimestampedKneeAngle ten = new TimestampedKneeAngle(2250, BodySide.LEFT, 170);

        angles.add(one);
        angles.add(two);
        angles.add(three);
        angles.add(four);
        angles.add(five);
        angles.add(six);
        angles.add(seven);
        angles.add(eight);
        angles.add(nine);
        angles.add(ten);

        Optional<LoweringTiming> result = analyzer.analyze(angles);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(500, result.get().getLoweringStart());
        Assert.assertEquals(1250, result.get().getBottomReached());
        Assert.assertEquals(750, result.get().getDurationMilliseconds());
    }

    @Test
    public void testOneIsolatedAngleDip() {
        LoweringTimingAnalyzer analyzer = new LoweringTimingAnalyzer(5, 3, 500);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle one = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle two = new TimestampedKneeAngle(250, BodySide.LEFT, 160);
        TimestampedKneeAngle three = new TimestampedKneeAngle(500, BodySide.LEFT, 170);
        TimestampedKneeAngle four = new TimestampedKneeAngle(750, BodySide.LEFT, 170);
        TimestampedKneeAngle five = new TimestampedKneeAngle(1000, BodySide.LEFT, 170);


        angles.add(one);
        angles.add(two);
        angles.add(three);
        angles.add(four);
        angles.add(five);


        Optional<LoweringTiming> result = analyzer.analyze(angles);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testGap() {
        LoweringTimingAnalyzer analyzer = new LoweringTimingAnalyzer(5, 3, 500);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle one = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle two = new TimestampedKneeAngle(250, BodySide.LEFT, 160);
        TimestampedKneeAngle three = new TimestampedKneeAngle(1000, BodySide.LEFT, 145);
        TimestampedKneeAngle four = new TimestampedKneeAngle(1250, BodySide.LEFT, 112);
        TimestampedKneeAngle five = new TimestampedKneeAngle(1500, BodySide.LEFT, 140);
        TimestampedKneeAngle six = new TimestampedKneeAngle(1750, BodySide.LEFT, 170);

        angles.add(one);
        angles.add(two);
        angles.add(three);
        angles.add(four);
        angles.add(five);
        angles.add(six);

        Optional<LoweringTiming> result = analyzer.analyze(angles);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testLegSwitch() {
        LoweringTimingAnalyzer analyzer = new LoweringTimingAnalyzer(5, 3, 500);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle one = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle two = new TimestampedKneeAngle(250, BodySide.LEFT, 160);
        TimestampedKneeAngle three = new TimestampedKneeAngle(500, BodySide.RIGHT, 145);
        TimestampedKneeAngle four = new TimestampedKneeAngle(750, BodySide.RIGHT, 112);
        TimestampedKneeAngle five = new TimestampedKneeAngle(1000, BodySide.RIGHT, 140);
        TimestampedKneeAngle six = new TimestampedKneeAngle(1250, BodySide.RIGHT, 170);

        angles.add(one);
        angles.add(two);
        angles.add(three);
        angles.add(four);
        angles.add(five);
        angles.add(six);

        Optional<LoweringTiming> result = analyzer.analyze(angles);

        Assert.assertFalse(result.isPresent());
    }
}
