package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void testDetectMethodValidInputs() {
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle standingStart = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle descending = new TimestampedKneeAngle(1000, BodySide.LEFT, 145);
        TimestampedKneeAngle bottom = new TimestampedKneeAngle(2000, BodySide.LEFT, 110);
        TimestampedKneeAngle ascending = new TimestampedKneeAngle(3000, BodySide.LEFT, 140);
        TimestampedKneeAngle returnToStanding = new TimestampedKneeAngle(4000, BodySide.LEFT, 170);

        angles.add(standingStart);
        angles.add(descending);
        angles.add(bottom);
        angles.add(ascending);
        angles.add(returnToStanding);

        Optional<SquatRepetition> detectedRepetition = detector.detect(angles);

        Assert.assertTrue(detectedRepetition.isPresent());
        Assert.assertEquals(0, detectedRepetition.get().getStandingStartTimestamp());
        Assert.assertEquals(2000, detectedRepetition.get().getLowestAngleTimestamp());
        Assert.assertEquals(4000, detectedRepetition.get().getReturnToStandingTimestamp());
    }

    @Test
    public void testDetectMethodInvalidInputs() {
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        Assert.assertThrows(IllegalArgumentException.class, () -> detector.detect(null));
        Assert.assertEquals(Optional.empty(), detector.detect(new ArrayList<>()));
    }

    @Test
    public void testDetectMethodNoStandingAngle() {
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle standingStart = new TimestampedKneeAngle(0, BodySide.LEFT, 150);
        TimestampedKneeAngle descending = new TimestampedKneeAngle(1000, BodySide.LEFT, 145);
        TimestampedKneeAngle bottom = new TimestampedKneeAngle(2000, BodySide.LEFT, 110);
        TimestampedKneeAngle ascending = new TimestampedKneeAngle(3000, BodySide.LEFT, 140);
        TimestampedKneeAngle returnToStanding = new TimestampedKneeAngle(4000, BodySide.LEFT, 150);

        angles.add(standingStart);
        angles.add(descending);
        angles.add(bottom);
        angles.add(ascending);
        angles.add(returnToStanding);

        Assert.assertEquals(Optional.empty(), detector.detect(angles));
    }

    @Test
    public void testDetectMethodNoBottomAngle() {
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle standingStart = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle descending = new TimestampedKneeAngle(1000, BodySide.LEFT, 145);
        TimestampedKneeAngle bottom = new TimestampedKneeAngle(2000, BodySide.LEFT, 130);
        TimestampedKneeAngle ascending = new TimestampedKneeAngle(3000, BodySide.LEFT, 140);
        TimestampedKneeAngle returnToStanding = new TimestampedKneeAngle(4000, BodySide.LEFT, 170);

        angles.add(standingStart);
        angles.add(descending);
        angles.add(bottom);
        angles.add(ascending);
        angles.add(returnToStanding);

        Assert.assertEquals(Optional.empty(), detector.detect(angles));
    }

    @Test
    public void testDetectMethodNoReturnStandingAngle() {
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        List<TimestampedKneeAngle> angles = new ArrayList<>();

        TimestampedKneeAngle standingStart = new TimestampedKneeAngle(0, BodySide.LEFT, 170);
        TimestampedKneeAngle descending = new TimestampedKneeAngle(1000, BodySide.LEFT, 145);
        TimestampedKneeAngle bottom = new TimestampedKneeAngle(2000, BodySide.LEFT, 110);
        TimestampedKneeAngle ascending = new TimestampedKneeAngle(3000, BodySide.LEFT, 140);
        TimestampedKneeAngle returnToStanding = new TimestampedKneeAngle(4000, BodySide.LEFT, 150);

        angles.add(standingStart);
        angles.add(descending);
        angles.add(bottom);
        angles.add(ascending);
        angles.add(returnToStanding);

        Assert.assertEquals(Optional.empty(), detector.detect(angles));
    }
}
