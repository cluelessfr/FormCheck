package com.taran.formcheck.pose;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

public class TimestampedPoseResult {
    private final long timestampMilliseconds;
    private final PoseLandmarkerResult result;

    public TimestampedPoseResult(long timestampMilliseconds, PoseLandmarkerResult result) {
        if (timestampMilliseconds < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }

        if (result == null) {
            throw new IllegalArgumentException("PoseLandmarkerResult cannot be null");
        }

        this.timestampMilliseconds = timestampMilliseconds;
        this.result = result;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
    }

    public PoseLandmarkerResult getResult() {
        return result;
    }
}
