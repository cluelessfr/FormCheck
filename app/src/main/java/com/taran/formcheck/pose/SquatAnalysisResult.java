package com.taran.formcheck.pose;

public class SquatAnalysisResult {
    private final SquatAnalysisOutcome outcome;
    private final SquatRepetition repetition;

    public SquatAnalysisResult(SquatAnalysisOutcome outcome, SquatRepetition repetition) {
        if (outcome == null) {
            throw new IllegalArgumentException("SquatAnalysisOutcome cannot be null");
        }

        if (outcome == SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED && repetition == null) {
            throw new IllegalArgumentException("Complete repetition outcome requires a repetition");
        }

        if (outcome == SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE && repetition != null) {
            throw new IllegalArgumentException("Insufficient evidence outcome cannot contain a repetition");
        }

        this.outcome = outcome;
        this.repetition = repetition;
    }

    public SquatAnalysisOutcome getOutcome() {
        return outcome;
    }

    public SquatRepetition getRepetition() {
        return repetition;
    }
}
