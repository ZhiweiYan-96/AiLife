package com.record.utils;

import android.content.Context;
import android.util.AttributeSet;

public class AttrsUtils {
    static String android = "http://schemas.android.com/apk/res/android";
    static String myAttrs = "http://schemas.android.com/apk/res/com.weicheche.android";

    public static String getStringFromSystemAttrs(Context context, AttributeSet attrs, String name) {
        String hintId = attrs.getAttributeValue(android, name);
        if (hintId == null || hintId.length() <= 0 || hintId.equals("null") || !hintId.matches("@[0-9]*")) {
            return hintId;
        }
        return context.getResources().getString(Integer.parseInt(hintId.replace("@", "")));
    }

    public static String getStringFromCustomAttrs(Context context, AttributeSet attrs, String name) {
        String hintId = attrs.getAttributeValue(myAttrs, name);
        if (hintId == null || hintId.length() <= 0 || hintId.equals("null") || !hintId.matches("@[0-9]*")) {
            return hintId;
        }
        return context.getResources().getString(Integer.parseInt(hintId.replace("@", "")));
    }

    public static String getAttrs(Context context, AttributeSet attrs, String name) {
        return attrs.getAttributeValue(android, name);
    }
}
