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

public class PoseKneeAngleAnalyzerTest {
    @Test
    public void testNullQualityGate() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new PoseKneeAngleAnalyzer(null));
    }

    @Test
    public void testNullTimestampedPose() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0, 0);
        PoseKneeAngleAnalyzer analyzer = new PoseKneeAngleAnalyzer(gate);

        Assert.assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(null));
    }

    @Test
    public void testNoDetectedPoseReturnsEmpty() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        PoseKneeAngleAnalyzer analyzer = new PoseKneeAngleAnalyzer(gate);
        PoseLandmarkerResult result = new PoseLandmarkerResult() {
            @Override
            public long timestampMs() {
                return 0;
            }

            @Override
            public List<List<NormalizedLandmark>> landmarks() {
                return Collections.emptyList();
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

        TimestampedPoseResult timestampedPoseResult = new TimestampedPoseResult(1000, result);

        Assert.assertEquals(Optional.empty(), analyzer.analyze(timestampedPoseResult));
    }

    private NormalizedLandmark createLandmark(float x, float y, float confidence) {
        NormalizedLandmark landmark = NormalizedLandmark.create(x, y, 0, Optional.of(confidence), Optional.of(confidence));

        return landmark;
    }

    private PoseLandmarkerResult createPoseResult() {
        ArrayList<NormalizedLandmark> normalizedLandmarks = new ArrayList<>();

        for (int i = 0; i <= PoseLandmarkIndices.RIGHT_ANKLE; i++) {
            normalizedLandmarks.add(i, createLandmark(0, 0, 0.2f));
        }

        normalizedLandmarks.set(PoseLandmarkIndices.LEFT_HIP, createLandmark(1, 0, 0.9f));
        normalizedLandmarks.set(PoseLandmarkIndices.LEFT_KNEE, createLandmark(0, 0, 0.9f));
        normalizedLandmarks.set(PoseLandmarkIndices.LEFT_ANKLE, createLandmark(0, 1, 0.9f));

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
    public void testValidPoseReturnsTimestampedKneeAngle() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        PoseKneeAngleAnalyzer analyzer = new PoseKneeAngleAnalyzer(gate);
        PoseLandmarkerResult poseLandmarkerResult = createPoseResult();
        TimestampedPoseResult result = new TimestampedPoseResult(1000, poseLandmarkerResult);
        Optional<TimestampedKneeAngle> timestampedKneeAngle = analyzer.analyze(result);

        Assert.assertTrue(timestampedKneeAngle.isPresent());
        TimestampedKneeAngle kneeAngle = timestampedKneeAngle.get();
        Assert.assertEquals(1000, kneeAngle.getTimestampMilliseconds());
        Assert.assertEquals(BodySide.LEFT, kneeAngle.getSide());
        Assert.assertEquals(90, kneeAngle.getAngleDegrees(), 0.000001);
    }
}
