package com.taran.formcheck.feedback;

import org.junit.Assert;
import org.junit.Test;

public class LoweringFeedbackAnalyzerTest {
    @Test
    public void testFeedback() {
        LoweringPracticeTarget target = new LoweringPracticeTarget(3000, 500);

        Assert.assertEquals(LoweringFeedbackOutcome.SHORTER_THAN_TARGET, LoweringFeedbackAnalyzer.compare(2499, target));
        Assert.assertEquals(LoweringFeedbackOutcome.NEAR_TARGET, LoweringFeedbackAnalyzer.compare(2500, target));
        Assert.assertEquals(LoweringFeedbackOutcome.NEAR_TARGET, LoweringFeedbackAnalyzer.compare(3000, target));
        Assert.assertEquals(LoweringFeedbackOutcome.NEAR_TARGET, LoweringFeedbackAnalyzer.compare(3500, target));
        Assert.assertEquals(LoweringFeedbackOutcome.LONGER_THAN_TARGET, LoweringFeedbackAnalyzer.compare(3501, target));
        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, LoweringFeedbackAnalyzer.compare(3000, null));
        Assert.assertThrows(IllegalArgumentException.class, () -> LoweringFeedbackAnalyzer.compare(0, target));
        Assert.assertThrows(IllegalArgumentException.class, () -> LoweringFeedbackAnalyzer.compare(-3000, target));
    }

    @Test
    public void testRangeComparison() {
        LoweringPracticeTarget target = new LoweringPracticeTarget(3000, 500);

        Assert.assertEquals(LoweringFeedbackOutcome.SHORTER_THAN_TARGET, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(2000, 2499), target));
        Assert.assertEquals(LoweringFeedbackOutcome.NEAR_TARGET, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(2500, 3500), target));
        Assert.assertEquals(LoweringFeedbackOutcome.LONGER_THAN_TARGET, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(3501, 4000), target));
        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(2400, 2600), target));
        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(3400, 3600), target));
        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(2000, 4000), target));
        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, LoweringFeedbackAnalyzer.compare(new LoweringDurationRange(2500, 3500), null));
        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, LoweringFeedbackAnalyzer.compare(null, target));
    }
}
