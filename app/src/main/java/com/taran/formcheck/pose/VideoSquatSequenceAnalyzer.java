package com.taran.formcheck.pose;

import java.util.List;

public class VideoSquatSequenceAnalyzer {
    private final VideoKneeAngleSequenceAnalyzer analyzer;
    private final SquatRepetitionDetector detector;
    private final LoweringTimingAnalyzer loweringTimingAnalyzer;

    public VideoSquatSequenceAnalyzer(VideoKneeAngleSequenceAnalyzer analyzer, SquatRepetitionDetector detector, LoweringTimingAnalyzer loweringTimingAnalyzer) {
        if (analyzer == null) {
            throw new IllegalArgumentException("VideoKneeAngleSequenceAnalyzer cannot be null");
        }

        if (detector == null) {
            throw new IllegalArgumentException("SquatRepetitionDetector cannot be null");
        }

        if (loweringTimingAnalyzer == null) {
            throw new IllegalArgumentException("LoweringTiming cannot be null");
        }

        this.analyzer = analyzer;
        this.detector = detector;
        this.loweringTimingAnalyzer = loweringTimingAnalyzer;
    }

    public SquatAnalysisResult analyze(List<TimestampedPoseResult> results) {
        List<TimestampedKneeAngle> timestampedAngles = analyzer.analyze(results);
        SquatAnalysisResult analysisResult = detector.analyze(timestampedAngles);

        if (analysisResult.getOutcome() == SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE) {
            return analysisResult;
        }

        SquatRepetition repetition = analysisResult.getRepetition();

        int standingStartIndex = -1;
        int returnToStandingIndex = -1;

        return analysisResult;
    }
}
