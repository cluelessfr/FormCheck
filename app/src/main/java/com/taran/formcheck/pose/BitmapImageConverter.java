package com.taran.formcheck.pose;

import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;

public class BitmapImageConverter {
    public static MPImage convertBitmapToImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        MPImage image = new BitmapImageBuilder(bitmap).build();

        return image;
    }
}
