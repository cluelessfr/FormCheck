package com.taran.formcheck.pose;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;
import com.taran.formcheck.video.FrameTimestampGenerator;
import com.taran.formcheck.video.VideoFrameDecoder;

import java.util.ArrayList;
import java.util.List;

public class VideoPoseSequenceProcessor {
    public static List<TimestampedPoseResult> processVideoPoseSequence(VideoFrameDecoder decoder, PoseLandmarkerManager manager, long intervalMilliseconds) {
        if (decoder == null) {
            throw new IllegalArgumentException("VideoFrameDecoder cannot be null");
        }

        if (manager == null) {
            throw new IllegalArgumentException("PoseLandmarkerManager cannot be null");
        }

        List<Long> timestamps = FrameTimestampGenerator.generateTimestamps(decoder.getDurationMilliseconds(), intervalMilliseconds);

        List<TimestampedPoseResult> results = new ArrayList<>();

        for (Long timestamp:timestamps) {
            PoseLandmarkerResult result = VideoFramePoseProcessor.processPoseFrame(decoder, manager, timestamp);
            TimestampedPoseResult timestampedPoseResult = new TimestampedPoseResult(timestamp, result);
            results.add(timestampedPoseResult);
        }

        return results;
    }
}
