package com.taran.formcheck.pose;

import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.MPImage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class BitmapImageConverterInstrumentedTest {
    @Test
    public void testBitmapConvertsToImage() {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(4, 3, config);

        MPImage image = BitmapImageConverter.convertBitmapToImage(bitmap);

        try {
            Assert.assertNotNull(image);
            Assert.assertEquals(4, image.getWidth());
            Assert.assertEquals(3, image.getHeight());
        }

        finally {
            image.close();
        }
    }

    @Test
    public void testNullBitmapThrowsError() {
        Assert.assertThrows(IllegalArgumentException.class, () -> BitmapImageConverter.convertBitmapToImage(null));
    }
}
