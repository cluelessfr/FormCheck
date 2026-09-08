package com.taran.formcheck.video;

import android.content.Context;
import android.net.Uri;
import android.media.MediaMetadataRetriever;
import java.io.IOException;


public class VideoFrameDecoder implements AutoCloseable {

    private final MediaMetadataRetriever retriever;

    public VideoFrameDecoder(Context context, Uri uri) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }

        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
    }

    @Override
    public void close() throws IOException {
        retriever.release();
    }

    public long getDurationMilliseconds() {
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        if (duration == null) {
            throw new IllegalStateException("Video duration is unavailable");
        }

        return Long.parseLong(duration);
    }
}
