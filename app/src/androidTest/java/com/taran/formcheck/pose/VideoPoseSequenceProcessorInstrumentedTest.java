package com.taran.formcheck.pose;

import android.content.Context;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.taran.formcheck.video.VideoFrameDecoder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class VideoPoseSequenceProcessorInstrumentedTest {
    @Test
    public void testProcessesFramesInTimestampOrder() throws IOException {
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
            List<TimestampedPoseResult> results = VideoPoseSequenceProcessor.processVideoPoseSequence(decoder, manager, 1000);
            Assert.assertEquals(3, results.size());
            Assert.assertEquals(0, results.get(0).getTimestampMilliseconds());
            Assert.assertEquals(1000, results.get(1).getTimestampMilliseconds());
            Assert.assertEquals(2000, results.get(2).getTimestampMilliseconds());
            Assert.assertNotNull(results.get(0).getResult());
            Assert.assertNotNull(results.get(1).getResult());
            Assert.assertNotNull(results.get(2).getResult());
        }

        finally {
            cachedVideoFile.delete();
        }
    }

    @Test
    public void testVideoWithPersonProcessesFramesInTimestampOrder() throws IOException {
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File cachedVideoFile = new File(appContext.getCacheDir(), "squat-demonstration.mp4");

        try (InputStream inputStream = testContext.getAssets().open("squat-demonstration.mp4");
             FileOutputStream outputStream = new FileOutputStream(cachedVideoFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        Uri uri = Uri.fromFile(cachedVideoFile);

        try (VideoFrameDecoder decoder = new VideoFrameDecoder(appContext, uri);
             PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext)) {
            List<TimestampedPoseResult> results = VideoPoseSequenceProcessor.processVideoPoseSequence(decoder, manager, 1000);

            for (TimestampedPoseResult result : results) {
                System.out.println("Timestamp: " + result.getTimestampMilliseconds() + "; Number of people: " + result.getResult().landmarks().size());
            }

            Assert.assertEquals(8, results.size());
            Assert.assertEquals(1, results.get(0).getResult().landmarks().size());
        }

        finally {
            cachedVideoFile.delete();
        }
    }
}
