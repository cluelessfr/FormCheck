package com.taran.formcheck.pose;

import com.taran.formcheck.video.FrameTimestampGenerator;
import com.taran.formcheck.video.VideoFrameDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

public class VideoPoseSequenceProcessor {
    public static List<TimestampedPoseResult> processVideoPoseSequence(VideoFrameDecoder decoder, PoseLandmarkerManager manager, long intervalMilliseconds) {
        return processVideoPoseSequence(decoder, manager, intervalMilliseconds, null);
    }

    public static List<TimestampedPoseResult> processVideoPoseSequence(VideoFrameDecoder decoder, PoseLandmarkerManager manager, long intervalMilliseconds, VideoAnalysisProgressListener progressListener) {
        if (decoder == null) {
            throw new IllegalArgumentException("VideoFrameDecoder cannot be null");
        }

        if (manager == null) {
            throw new IllegalArgumentException("PoseLandmarkerManager cannot be null");
        }

        List<Long> timestamps = FrameTimestampGenerator.generateTimestamps(decoder.getDurationMilliseconds(), intervalMilliseconds);

        List<TimestampedPoseResult> results = new ArrayList<>();

        for (Long timestamp:timestamps) {
            if (Thread.currentThread().isInterrupted()) {
                throw new CancellationException("Video analysis was interrupted");
            }
            TimestampedPoseResult result = VideoFramePoseProcessor.processPoseFrame(decoder, manager, timestamp);
            results.add(result);

            if (progressListener != null) {
                progressListener.onProgress(results.size(), timestamps.size());
            }
        }

        return results;
    }
}
