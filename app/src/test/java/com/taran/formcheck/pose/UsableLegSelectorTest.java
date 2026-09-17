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

public class UsableLegSelectorTest {
    @Test
    public void testNullQualityGate() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new UsableLegSelector(null));
    }

    @Test
    public void testNullResult() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        UsableLegSelector selector = new UsableLegSelector(gate);

        Assert.assertThrows(IllegalArgumentException.class, () -> selector.select(null));
    }

    @Test
    public void testNoDetectedPose() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        UsableLegSelector selector = new UsableLegSelector(gate);
        PoseLandmarkerResult result = createEmptyPoseResult();

        Optional<LegLandmarkObservations> selectedLeg = selector.select(result);

        Assert.assertFalse(selectedLeg.isPresent());
    }

    @Test
    public void testNoUsableLeg() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        UsableLegSelector selector = new UsableLegSelector(gate);
        PoseLandmarkerResult result = createPoseResult(0.4f, 0.3f);

        Optional<LegLandmarkObservations> selectedLeg = selector.select(result);

        Assert.assertFalse(selectedLeg.isPresent());
    }

    @Test
    public void testSelectsOnlyUsableLeg() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        UsableLegSelector selector = new UsableLegSelector(gate);
        PoseLandmarkerResult result = createPoseResult(0.4f, 0.8f);

        Optional<LegLandmarkObservations> selectedLeg = selector.select(result);

        Assert.assertTrue(selectedLeg.isPresent());
        Assert.assertEquals(BodySide.RIGHT, selectedLeg.get().getSide());
    }

    @Test
    public void testSelectsLegWithHigherConfidence() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        UsableLegSelector selector = new UsableLegSelector(gate);
        PoseLandmarkerResult result = createPoseResult(0.7f, 0.9f);

        Optional<LegLandmarkObservations> selectedLeg = selector.select(result);

        Assert.assertTrue(selectedLeg.isPresent());
        Assert.assertEquals(BodySide.RIGHT, selectedLeg.get().getSide());
    }

    @Test
    public void testEqualConfidenceSelectsLeftLeg() {
        LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
        UsableLegSelector selector = new UsableLegSelector(gate);
        PoseLandmarkerResult result = createPoseResult(0.8f, 0.8f);

        Optional<LegLandmarkObservations> selectedLeg = selector.select(result);

        Assert.assertTrue(selectedLeg.isPresent());
        Assert.assertEquals(BodySide.LEFT, selectedLeg.get().getSide());
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

    private PoseLandmarkerResult createPoseResult(float leftConfidence, float rightConfidence) {
        List<NormalizedLandmark> poseLandmarks = new ArrayList<>();

        for (int index = 0; index <= PoseLandmarkIndices.RIGHT_ANKLE; index++) {
            poseLandmarks.add(createLandmark(1));
        }

        poseLandmarks.set(PoseLandmarkIndices.LEFT_HIP, createLandmark(leftConfidence));
        poseLandmarks.set(PoseLandmarkIndices.LEFT_KNEE, createLandmark(leftConfidence));
        poseLandmarks.set(PoseLandmarkIndices.LEFT_ANKLE, createLandmark(leftConfidence));
        poseLandmarks.set(PoseLandmarkIndices.RIGHT_HIP, createLandmark(rightConfidence));
        poseLandmarks.set(PoseLandmarkIndices.RIGHT_KNEE, createLandmark(rightConfidence));
        poseLandmarks.set(PoseLandmarkIndices.RIGHT_ANKLE, createLandmark(rightConfidence));

        List<List<NormalizedLandmark>> detectedPoses = new ArrayList<>();
        detectedPoses.add(poseLandmarks);

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

    private NormalizedLandmark createLandmark(float confidence) {
        return NormalizedLandmark.create(0, 0, 0, Optional.of(confidence), Optional.of(confidence));
    }
}
