package com.taran.formcheck.pose;

import java.util.List;
import java.util.Optional;

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

        for (int i = 0; i < timestampedAngles.size(); i++) {
            long timestamp = timestampedAngles.get(i).getTimestampMilliseconds();

            if (timestamp == repetition.getStandingStartTimestamp()) {
                standingStartIndex = i;
            }

            if (timestamp == repetition.getReturnToStandingTimestamp()) {
                returnToStandingIndex = i;
            }
        }

        if (standingStartIndex == -1 || returnToStandingIndex == -1) {
            return analysisResult;
        }

        int candidateStartIndex = standingStartIndex;

        while (candidateStartIndex > 0) {
            TimestampedKneeAngle current = timestampedAngles.get(candidateStartIndex);
            TimestampedKneeAngle previous = timestampedAngles.get(candidateStartIndex - 1);

            if (previous.getAngleDegrees() < detector.getStandingAngleThreshold()) {
                break;
            }

            if (previous.getSide() != current.getSide()) {
                break;
            }

            if (current.getTimestampMilliseconds() - previous.getTimestampMilliseconds() > loweringTimingAnalyzer.getMaximumGapMilliseconds()) {
                break;
            }

            candidateStartIndex -= 1;
        }

        List<TimestampedKneeAngle> angles = timestampedAngles.subList(candidateStartIndex, returnToStandingIndex+1);
        Optional<LoweringTiming> timings = loweringTimingAnalyzer.analyze(angles);

        if (timings.isEmpty()) {
            return analysisResult;
        }

        return new SquatAnalysisResult(analysisResult.getOutcome(), repetition, timings.get());
    }
}
