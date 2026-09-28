package com.taran.formcheck.pose;

import java.util.List;

public class VideoSquatSequenceAnalyzer {
    private final VideoKneeAngleSequenceAnalyzer analyzer;
    private final SquatRepetitionDetector detector;

    public VideoSquatSequenceAnalyzer(VideoKneeAngleSequenceAnalyzer analyzer, SquatRepetitionDetector detector) {
        if (analyzer == null) {
            throw new IllegalArgumentException("VideoKneeAngleSequenceAnalyzer cannot be null");
        }

        if (detector == null) {
            throw new IllegalArgumentException("SquatRepetitionDetector cannot be null");
        }

        this.analyzer = analyzer;
        this.detector = detector;
    }

    public SquatAnalysisResult analyze(List<TimestampedPoseResult> results) {
        List<TimestampedKneeAngle> timestampedAngles = analyzer.analyze(results);
        SquatAnalysisResult analysisResult = detector.analyze(timestampedAngles);

        return analysisResult;
    }
}
