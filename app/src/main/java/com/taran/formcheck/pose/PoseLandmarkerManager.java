package com.taran.formcheck.pose;

import android.content.Context;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;

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
}
