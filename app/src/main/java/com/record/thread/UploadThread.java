package com.record.thread;

import android.content.Context;
import android.os.Handler;

public class UploadThread {
    static Thread uploadThread;

    private UploadThread() {
    }

    public static Thread getInstance(Context context, Handler handler) {
        if (uploadThread == null || !uploadThread.isAlive()) {
            uploadThread = new Thread(new UploadRunnable(context, handler));
        }
        return uploadThread;
    }

    public static Thread getInstance(Context context) {
        return getInstance(context, null);
    }
}
