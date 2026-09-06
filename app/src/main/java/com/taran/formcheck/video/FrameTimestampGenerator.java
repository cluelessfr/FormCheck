package com.taran.formcheck.video;

import java.util.ArrayList;
import java.util.List;

public class FrameTimestampGenerator {
    public static List<Long> generateTimestamps(long durationMilliseconds, long samplingInterval) {
        if (durationMilliseconds <= 0 || samplingInterval <= 0) {
            throw new IllegalArgumentException("Duration and interval cannot be 0 or negative");
        }

        ArrayList<Long> timestamps = new ArrayList<>();
        long currentValue = 0;

        while (currentValue < durationMilliseconds) {
            timestamps.add(currentValue);
            currentValue += samplingInterval;
        }

        return timestamps;
    }
}
