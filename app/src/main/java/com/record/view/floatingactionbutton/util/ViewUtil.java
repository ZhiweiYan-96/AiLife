package com.record.view.floatingactionbutton.util;

import android.os.Build.VERSION;
import android.view.View;

public class ViewUtil {
    public static void typeSoftWare(View view) {
        if (VERSION.SDK_INT > 11) {
            view.setLayerType(1, null);
        }
    }
}
