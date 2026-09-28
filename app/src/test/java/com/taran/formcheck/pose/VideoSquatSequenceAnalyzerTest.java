package com.taran.formcheck.pose;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Landmark;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;
import com.taran.formcheck.analysis.LandmarkQualityGate;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VideoSquatSequenceAnalyzerTest {
    @Test
    public void testInvalidInputs() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);

        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);

        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);

        Assert.assertThrows(IllegalArgumentException.class, () -> new VideoSquatSequenceAnalyzer(null, detector));
        Assert.assertThrows(IllegalArgumentException.class, () -> new VideoSquatSequenceAnalyzer(analyzer, null));
    }

    @Test
    public void testEmptySequenceReturnsInsufficientEvidence() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector);
        SquatAnalysisResult result = videoSquatSequenceAnalyzer.analyze(new ArrayList<>());

        Assert.assertEquals(SquatAnalysisOutcome.INSUFFICIENT_EVIDENCE, result.getOutcome());
        Assert.assertNull(result.getRepetition());
    }

    private NormalizedLandmark createLandmark(float x, float y, float confidence) {
        NormalizedLandmark landmark = NormalizedLandmark.create(x, y, 0, Optional.of(confidence), Optional.of(confidence));

        return landmark;
    }

    private PoseLandmarkerResult createPoseResult(double angleDegrees) {
        ArrayList<NormalizedLandmark> normalizedLandmarks = new ArrayList<>();

        for (int i = 0; i <= PoseLandmarkIndices.RIGHT_ANKLE; i++) {
            normalizedLandmarks.add(i, createLandmark(0, 0, 0.2f));
        }

        double angleRadians = Math.toRadians(angleDegrees);
        float ankleX = (float) (0.5 + 0.25 * Math.cos(angleRadians));
        float ankleY = (float) (0.5 + 0.25 * Math.sin(angleRadians));

        normalizedLandmarks.set(PoseLandmarkIndices.LEFT_HIP, createLandmark(0.75f, 0.5f, 0.9f));
        normalizedLandmarks.set(PoseLandmarkIndices.LEFT_KNEE, createLandmark(0.5f, 0.5f, 0.9f));
        normalizedLandmarks.set(PoseLandmarkIndices.LEFT_ANKLE, createLandmark(ankleX, ankleY, 0.9f));

        List<List<NormalizedLandmark>> detectedPoses = new ArrayList<>();
        detectedPoses.add(normalizedLandmarks);

        return new PoseLandmarkerResult() {
            @Override
            public long timestampMs() {
                return 0;
            }

            @Override
            public List<List<NormalizedLandmark>> landmarks() {
                return detectedPoses;
            }

            @Override
            public List<List<Landmark>> worldLandmarks() {
                return Collections.emptyList();
            }

            @Override
            public Optional<List<MPImage>> segmentationMasks() {
                return Optional.empty();
            }
        };
    }

    @Test
    public void testCompleteSequenceReturnsDetectedRepetition() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector);
        ArrayList<TimestampedPoseResult> timestampedPoseResults = new ArrayList<>();

        timestampedPoseResults.add(new TimestampedPoseResult(0, createPoseResult(170)));
        timestampedPoseResults.add(new TimestampedPoseResult(1000, createPoseResult(145)));
        timestampedPoseResults.add(new TimestampedPoseResult(2000, createPoseResult(110)));
        timestampedPoseResults.add(new TimestampedPoseResult(3000, createPoseResult(140)));
        timestampedPoseResults.add(new TimestampedPoseResult(4000, createPoseResult(170)));

        SquatAnalysisResult result = videoSquatSequenceAnalyzer.analyze(timestampedPoseResults);

        Assert.assertEquals(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, result.getOutcome());
        Assert.assertNotNull(result.getRepetition());
        Assert.assertEquals(0, result.getRepetition().getStandingStartTimestamp());
        Assert.assertEquals(2000, result.getRepetition().getLowestAngleTimestamp());
        Assert.assertEquals(4000, result.getRepetition().getReturnToStandingTimestamp());
    }

    @Test
    public void testNullSequenceThrowsException() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector);

        Assert.assertThrows(IllegalArgumentException.class, () -> videoSquatSequenceAnalyzer.analyze(null));
    }
}
