package com.taran.formcheck.pose;

import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;
import com.taran.formcheck.video.VideoFrameDecoder;

public class VideoFramePoseProcessor {
    public static TimestampedPoseResult processPoseFrame(VideoFrameDecoder decoder, PoseLandmarkerManager manager, long timestampMilliseconds) {
        if (decoder == null) {
            throw new IllegalArgumentException("VideoFrameDecoder cannot be null");
        }
        else if (manager == null) {
            throw new IllegalArgumentException("PoseLandmarkerManager cannot be null");
        }

        Bitmap bitmap = decoder.getFrameAtMilliseconds(timestampMilliseconds);
        MPImage image = BitmapImageConverter.convertBitmapToImage(bitmap);

        try {
            PoseLandmarkerResult result = manager.processDecodedFrame(image, timestampMilliseconds);

            TimestampedPoseResult timestampedPoseResult = new TimestampedPoseResult(timestampMilliseconds, result, bitmap.getWidth(), bitmap.getHeight());

            return timestampedPoseResult;
        }

        finally {
            image.close();
        }
    }
}
