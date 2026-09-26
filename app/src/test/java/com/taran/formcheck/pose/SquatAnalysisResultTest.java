package com.taran.formcheck.pose;

import org.junit.Assert;
import org.junit.Test;

public class SquatAnalysisResultTest {
    @Test
    public void testValidInputs() {
        SquatRepetition repetition = new SquatRepetition(0, 1000, 20000);
        SquatAnalysisResult result = new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, repetition);
        Assert.assertEquals(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, result.getOutcome());
        Assert.assertEquals(repetition, result.getRepetition());

        SquatAnalysisResult result1 = new SquatAnalysisResult(SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE, null);
        Assert.assertEquals(SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE, result1.getOutcome());
        Assert.assertNull(result1.getRepetition());
    }

    @Test
    public void testInvalidInputs() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatAnalysisResult(null, null));
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatAnalysisResult(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, null));

        SquatRepetition repetition = new SquatRepetition(0, 1000, 20000);
        Assert.assertThrows(IllegalArgumentException.class, () -> new SquatAnalysisResult(SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE, repetition));
    }
}
