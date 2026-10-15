package com.taran.formcheck.pose;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

public class TimestampedPoseResult {
    private final long timestampMilliseconds;
    private final PoseLandmarkerResult result;
    private final int width;
    private final int height;

    public TimestampedPoseResult(long timestampMilliseconds, PoseLandmarkerResult result, int width, int height) {
        if (timestampMilliseconds < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }

        if (result == null) {
            throw new IllegalArgumentException("PoseLandmarkerResult cannot be null");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        this.timestampMilliseconds = timestampMilliseconds;
        this.result = result;
        this.width = width;
        this.height = height;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
    }

    public PoseLandmarkerResult getResult() {
        return result;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
