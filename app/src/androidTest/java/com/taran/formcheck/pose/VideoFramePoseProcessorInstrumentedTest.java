package com.taran.formcheck.pose;

import android.content.Context;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;
import com.taran.formcheck.video.VideoFrameDecoder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(AndroidJUnit4.class)
public class VideoFramePoseProcessorInstrumentedTest {
    @Test
    public void testFrameDecoding() throws IOException {
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File cachedVideoFile = new File(appContext.getCacheDir(), "test_video.mp4");

        try (InputStream inputStream = testContext.getAssets().open("test_video.mp4");
             FileOutputStream outputStream = new FileOutputStream(cachedVideoFile);) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        Uri uri = Uri.fromFile(cachedVideoFile);

        try (VideoFrameDecoder decoder = new VideoFrameDecoder(appContext, uri);
             PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext)) {
            PoseLandmarkerResult result = VideoFramePoseProcessor.processPoseFrame(decoder, manager, 1000);
            Assert.assertNotNull(result);
        }

        finally {
            cachedVideoFile.delete();
        }
    }
}
