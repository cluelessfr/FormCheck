package com.taran.formcheck.feedback;

import com.taran.formcheck.pose.LoweringTiming;
import com.taran.formcheck.pose.SquatAnalysisOutcome;
import com.taran.formcheck.pose.SquatAnalysisResult;
import com.taran.formcheck.pose.SquatRepetition;

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

    @Test
    public void testSuccessfulCase() {
        SquatRepetition repetition = new SquatRepetition(0, 3000, 4000);
        LoweringTiming timing = new LoweringTiming(0, 3000, 500);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition, timing);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, new LoweringPracticeTarget(3000, 500), RecordingSuitability.CONFIRMED_SIDE_VIEW);

        Assert.assertEquals(LoweringFeedbackOutcome.NEAR_TARGET, outcome);
    }

    @Test
    public void testNullTiming() {
        SquatRepetition repetition = new SquatRepetition(0, 3000, 4000);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition, null);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, new LoweringPracticeTarget(3000, 500), RecordingSuitability.CONFIRMED_SIDE_VIEW);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }

    @Test
    public void testInsufficientEvidence() {
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE, null, null);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, new LoweringPracticeTarget(3000, 500), RecordingSuitability.CONFIRMED_SIDE_VIEW);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }

    @Test
    public void testNullResult() {
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(null, new LoweringPracticeTarget(3000, 500), RecordingSuitability.CONFIRMED_SIDE_VIEW);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }

    @Test
    public void testNullTarget() {
        SquatRepetition repetition = new SquatRepetition(0, 3000, 4000);
        LoweringTiming timing = new LoweringTiming(0, 3000, 500);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition, timing);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, null, RecordingSuitability.CONFIRMED_SIDE_VIEW);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }

    @Test
    public void testSuccessfulCaseWithUnsupportedSuitability() {
        SquatRepetition repetition = new SquatRepetition(0, 3000, 4000);
        LoweringTiming timing = new LoweringTiming(0, 3000, 500);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition, timing);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, new LoweringPracticeTarget(3000, 500), RecordingSuitability.UNSUPPORTED_VIEW);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }

    @Test
    public void testSuccessfulCaseWithUnknownSuitability() {
        SquatRepetition repetition = new SquatRepetition(0, 3000, 4000);
        LoweringTiming timing = new LoweringTiming(0, 3000, 500);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition, timing);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, new LoweringPracticeTarget(3000, 500), RecordingSuitability.UNKNOWN);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }

    @Test
    public void testSuccessfulCaseWithNullSuitability() {
        SquatRepetition repetition = new SquatRepetition(0, 3000, 4000);
        LoweringTiming timing = new LoweringTiming(0, 3000, 500);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition, timing);
        LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, new LoweringPracticeTarget(3000, 500), null);

        Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
    }
}
