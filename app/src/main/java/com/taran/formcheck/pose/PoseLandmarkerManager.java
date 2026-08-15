package com.taran.formcheck.pose;

import android.content.Context;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

public class PoseLandmarkerManager {

    private final PoseLandmarker poseLandmarker;

    public PoseLandmarkerManager(Context context) {
        BaseOptions baseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker_full.task")
                .setDelegate(Delegate.CPU)
                .build();



        PoseLandmarker.PoseLandmarkerOptions options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.VIDEO)
                .build();

        poseLandmarker = PoseLandmarker.createFromOptions(context, options);
    }

    public void close() {
        poseLandmarker.close();
    }

    public PoseLandmarkerResult processDecodedFrame(MPImage image, long timestampMs) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        if (timestampMs < 0) {
            throw new IllegalArgumentException(("Timestamp cannot be negative"));
        }

        return poseLandmarker.detectForVideo(image, timestampMs);
    }
}
