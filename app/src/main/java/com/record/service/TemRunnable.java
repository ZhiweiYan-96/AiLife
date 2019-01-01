package com.record.service;

import android.content.Context;
import android.util.Log;

public class TemRunnable implements Runnable {
    static String TAG = "override";
    private Context context;

    public TemRunnable(Context context) {
        this.context = context;
        TAG = "override " + getClass().getName();
    }

    public void run() {
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
