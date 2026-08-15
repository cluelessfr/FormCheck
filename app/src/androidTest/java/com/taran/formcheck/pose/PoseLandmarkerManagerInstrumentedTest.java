package com.taran.formcheck.pose;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PoseLandmarkerManagerInstrumentedTest {
    @Test
    public void testCreateAndClosePoseLandmarker() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext);
        manager.close();
    }

    @Test
    public void testNullImage() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext);

        Assert.assertThrows(IllegalArgumentException.class, () -> manager.processDecodedFrame(null, 0));

        manager.close();
    }

    @Test
    public void testNegativeTimestamp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext);

        Bitmap map = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        MPImage image = new BitmapImageBuilder(map).build();

        Assert.assertThrows(IllegalArgumentException.class, () -> manager.processDecodedFrame(image, -1));

        image.close();
        manager.close();
    }

    @Test
    public void testValidFrameReturnsResult() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext);

        Bitmap map = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        MPImage image = new BitmapImageBuilder(map).build();

        PoseLandmarkerResult result = manager.processDecodedFrame(image, 0);

        Assert.assertNotNull(result);

        image.close();
        manager.close();
    }
}
