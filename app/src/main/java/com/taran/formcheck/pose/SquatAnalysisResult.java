package com.taran.formcheck.pose;

import java.util.Optional;

public class SquatAnalysisResult {
    private final SquatAnalysisOutcome outcome;
    private final SquatRepetition repetition;
    private final LoweringTiming loweringTiming;

    public SquatAnalysisResult(SquatAnalysisOutcome outcome, SquatRepetition repetition) {
        this(outcome, repetition, null);
    }

    public SquatAnalysisResult(SquatAnalysisOutcome outcome, SquatRepetition repetition, LoweringTiming loweringTiming) {
        if (outcome == null) {
            throw new IllegalArgumentException("SquatAnalysisOutcome cannot be null");
        }

        if (outcome == SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED && repetition == null) {
            throw new IllegalArgumentException("Complete repetition outcome requires a repetition");
        }

        if (outcome == SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE && (repetition != null || loweringTiming != null)) {
            throw new IllegalArgumentException("Insufficient evidence outcome cannot contain a repetition");
        }

        this.outcome = outcome;
        this.repetition = repetition;
        this.loweringTiming = loweringTiming;
    }

    public SquatAnalysisOutcome getOutcome() {
        return outcome;
    }

    public SquatRepetition getRepetition() {
        return repetition;
    }

    public Optional<LoweringTiming> getLoweringTiming() {
        if (loweringTiming == null) {
            return Optional.empty();
        }

        return Optional.of(loweringTiming);
    }
}
