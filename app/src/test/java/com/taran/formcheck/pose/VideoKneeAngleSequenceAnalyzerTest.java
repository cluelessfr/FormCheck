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

public class VideoKneeAngleSequenceAnalyzerTest {
    @Test
    public void testNullQualityGate() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new VideoKneeAngleSequenceAnalyzer(null));
    }

    @Test
    public void testNullPoseList() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        Assert.assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(null));
    }

    @Test
    public void testEmptyPoseList() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        List<TimestampedPoseResult> emptyList = new ArrayList<>();
        Assert.assertEquals(new ArrayList<>(), analyzer.analyze(emptyList));
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

    private PoseLandmarkerResult createEmptyPoseResult() {
        return new PoseLandmarkerResult() {
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
    }

    @Test
    public void testMixedSequenceSkipsEmptyPose() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
        PoseLandmarkerResult validResult1 = createPoseResult();
        PoseLandmarkerResult invalidResult = createEmptyPoseResult();
        PoseLandmarkerResult validResult2 = createPoseResult();

        TimestampedPoseResult timestampedValidResult1 = new TimestampedPoseResult(0, validResult1);
        TimestampedPoseResult timestampedInvalidPoseResult = new TimestampedPoseResult(1000, invalidResult);
        TimestampedPoseResult timestampedValidPoseResult2 = new TimestampedPoseResult(2000, validResult2);

        List<TimestampedPoseResult> results = new ArrayList<>();
        results.add(timestampedValidResult1);
        results.add(timestampedInvalidPoseResult);
        results.add(timestampedValidPoseResult2);

        List<TimestampedKneeAngle> timestampedResults = analyzer.analyze(results);

        Assert.assertEquals(2, timestampedResults.size());
        Assert.assertEquals(0, timestampedResults.get(0).getTimestampMilliseconds());
        Assert.assertEquals(2000, timestampedResults.get(1).getTimestampMilliseconds());
        Assert.assertEquals(90, timestampedResults.get(0).getAngleDegrees(), 0.000001);
        Assert.assertEquals(90, timestampedResults.get(1).getAngleDegrees(), 0.000001);
        Assert.assertEquals(BodySide.LEFT, timestampedResults.get(0).getSide());
        Assert.assertEquals(BodySide.LEFT, timestampedResults.get(1).getSide());
    }
}
