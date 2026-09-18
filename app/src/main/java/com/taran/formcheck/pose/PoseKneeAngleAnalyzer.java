package com.taran.formcheck.pose;

import com.taran.formcheck.analysis.JointAngleAnalyzer;
import com.taran.formcheck.analysis.LandmarkQualityGate;

import java.util.Optional;
import java.util.OptionalDouble;

public class PoseKneeAngleAnalyzer {
    private final UsableLegSelector usableLegSelector;
    private final JointAngleAnalyzer angleAnalyzer;

    public PoseKneeAngleAnalyzer(LandmarkQualityGate qualityGate) {
        if (qualityGate == null) {
            throw new IllegalArgumentException("QualityGate cannot be null");
        }

        usableLegSelector = new UsableLegSelector(qualityGate);
        angleAnalyzer = new JointAngleAnalyzer(qualityGate);
    }

    public Optional<TimestampedKneeAngle> analyze(TimestampedPoseResult timestampedPose) {
        if (timestampedPose == null) {
            throw new IllegalArgumentException("TimestampedPoseResult cannot be null");
        }

        Optional<LegLandmarkObservations> observations = usableLegSelector.select(timestampedPose.getResult());

        if (observations.isEmpty()) {
            return Optional.empty();
        }

        OptionalDouble analyzedAngle = angleAnalyzer.analyzeAngle(observations.get().getHip(), observations.get().getKnee(), observations.get().getAnkle());

        if (analyzedAngle.isEmpty()) {
            return Optional.empty();
        }

        TimestampedKneeAngle kneeAngle = new TimestampedKneeAngle(timestampedPose.getTimestampMilliseconds(), observations.get().getSide(), analyzedAngle.getAsDouble());

        return Optional.of(kneeAngle);
    }
}
