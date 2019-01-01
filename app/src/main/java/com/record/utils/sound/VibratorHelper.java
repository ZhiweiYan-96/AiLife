package com.record.utils.sound;

import android.content.Context;
import android.os.Vibrator;

public class VibratorHelper {
    static VibratorHelper helper;
    public static Vibrator vibrator;

    private VibratorHelper() {
    }

    public static VibratorHelper getInstance(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService("vibrator");
        }
        if (helper == null) {
            helper = new VibratorHelper();
        }
        return helper;
    }

    public void vibrateTwice() {
        vibrator.vibrate(new long[]{100, 500, 400, 500, 1000, 500, 400, 500}, -1);
    }

    public void cancel() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
