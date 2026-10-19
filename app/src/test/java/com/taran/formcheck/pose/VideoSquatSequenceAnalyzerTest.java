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

        Assert.assertThrows(IllegalArgumentException.class, () -> new VideoSquatSequenceAnalyzer(null, detector, new LoweringTimingAnalyzer(5, 3, 500)));
        Assert.assertThrows(IllegalArgumentException.class, () -> new VideoSquatSequenceAnalyzer(analyzer, null, new LoweringTimingAnalyzer(5, 3, 500)));
    }

    @Test
    public void testEmptySequenceReturnsInsufficientEvidence() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector, new LoweringTimingAnalyzer(5, 3, 500));
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
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector, new LoweringTimingAnalyzer(5, 3, 500));
        ArrayList<TimestampedPoseResult> timestampedPoseResults = new ArrayList<>();

        timestampedPoseResults.add(new TimestampedPoseResult(0, createPoseResult(170), 100, 100));
        timestampedPoseResults.add(new TimestampedPoseResult(1000, createPoseResult(145), 100, 100));
        timestampedPoseResults.add(new TimestampedPoseResult(2000, createPoseResult(110), 100, 100));
        timestampedPoseResults.add(new TimestampedPoseResult(3000, createPoseResult(140), 100, 100));
        timestampedPoseResults.add(new TimestampedPoseResult(4000, createPoseResult(170), 100, 100));

        SquatAnalysisResult result = videoSquatSequenceAnalyzer.analyze(timestampedPoseResults);

        Assert.assertEquals(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, result.getOutcome());
        Assert.assertNotNull(result.getRepetition());
        Assert.assertEquals(0, result.getRepetition().getStandingStartTimestamp());
        Assert.assertEquals(2000, result.getRepetition().getLowestAngleTimestamp());
        Assert.assertEquals(4000, result.getRepetition().getReturnToStandingTimestamp());
        Assert.assertTrue(result.getLoweringTiming().isEmpty());
    }

    @Test
    public void testNullSequenceThrowsException() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector, new LoweringTimingAnalyzer(5, 3, 500));

        Assert.assertThrows(IllegalArgumentException.class, () -> videoSquatSequenceAnalyzer.analyze(null));
    }

    @Test
    public void testIntegration() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        SquatRepetitionDetector detector = new SquatRepetitionDetector(160, 120);
        VideoSquatSequenceAnalyzer videoSquatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, detector, new LoweringTimingAnalyzer(5, 3, 500));
        List<TimestampedPoseResult> angles = new ArrayList<>();

        TimestampedPoseResult one = new TimestampedPoseResult(0, createPoseResult(170), 100, 100);
        TimestampedPoseResult two = new TimestampedPoseResult(250, createPoseResult(170), 100, 100);
        TimestampedPoseResult three = new TimestampedPoseResult(500, createPoseResult(168), 100, 100);
        TimestampedPoseResult four = new TimestampedPoseResult(750, createPoseResult(164), 100, 100);
        TimestampedPoseResult five = new TimestampedPoseResult(1000, createPoseResult(145), 100, 100);
        TimestampedPoseResult six = new TimestampedPoseResult(1250, createPoseResult(112), 100, 100);
        TimestampedPoseResult seven = new TimestampedPoseResult(1500, createPoseResult(110), 100, 100);
        TimestampedPoseResult eight = new TimestampedPoseResult(1750, createPoseResult(111), 100, 100);
        TimestampedPoseResult nine = new TimestampedPoseResult(2000, createPoseResult(140), 100, 100);
        TimestampedPoseResult ten = new TimestampedPoseResult(2250, createPoseResult(170), 100, 100);

        angles.add(one);
        angles.add(two);
        angles.add(three);
        angles.add(four);
        angles.add(five);
        angles.add(six);
        angles.add(seven);
        angles.add(eight);
        angles.add(nine);
        angles.add(ten);

        SquatAnalysisResult result = videoSquatSequenceAnalyzer.analyze(angles);

        Assert.assertEquals(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, result.getOutcome());
        Assert.assertTrue(result.getLoweringTiming().isPresent());

        LoweringTiming timing = result.getLoweringTiming().get();

        Assert.assertEquals(500, timing.getLoweringStart());
        Assert.assertEquals(1250, timing.getBottomReached());
        Assert.assertEquals(750, timing.getDurationMilliseconds());
    }
}
