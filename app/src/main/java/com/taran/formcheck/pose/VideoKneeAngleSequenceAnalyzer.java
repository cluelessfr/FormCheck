package com.taran.formcheck.pose;

import com.taran.formcheck.analysis.LandmarkQualityGate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VideoKneeAngleSequenceAnalyzer {
    private final PoseKneeAngleAnalyzer analyzer;

    public VideoKneeAngleSequenceAnalyzer(LandmarkQualityGate gate) {
        if (gate == null) {
            throw new IllegalArgumentException("QualityGate cannot be null");
        }

        analyzer = new PoseKneeAngleAnalyzer(gate);
    }

    public List<TimestampedKneeAngle> analyze(List<TimestampedPoseResult> timestampedPoses) {
        if (timestampedPoses == null) {
            throw new IllegalArgumentException("TimeStampedPoseResult cannot be null");
        }

        ArrayList<TimestampedKneeAngle> results = new ArrayList<>();

        for (TimestampedPoseResult poseResult : timestampedPoses) {
            Optional<TimestampedKneeAngle> analyzedPose = analyzer.analyze(poseResult);

            analyzedPose.ifPresent(results::add);
        }

        return results;
    }
}
