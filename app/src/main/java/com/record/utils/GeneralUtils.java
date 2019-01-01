package com.record.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class GeneralUtils {
    public static final boolean D = true;
    public static final String TAG = "AutoTag";

    public static boolean isNumeric(String str) {
        int i = str.length();
        int chr;
        do {
            i--;
            if (i >= 0) {
                chr = str.charAt(i);
                if (chr < 48) {
                    break;
                }
            } else {
                return true;
            }
        } while (chr <= 57);
        return false;
    }

    public static String getExceptionString(Exception e) {
        String errorString = e.toString() + "\r\n";
        StackTraceElement[] stackArr = e.getStackTrace();
        for (int i = 0; i < stackArr.length; i++) {
            errorString = errorString + "STACK-->: " + stackArr[i].toString();
            if (i + 1 != stackArr.length) {
                errorString = errorString + "\r\n";
            }
        }
        return errorString;
    }

    public static void i(String msg) {
        Log.i("AutoTag", msg);
    }

    public static void d(String msg) {
        Log.d("AutoTag", msg);
    }

    public static void e(String msg, Exception e) {
        Log.e("AutoTag", msg, e);
    }

    public static void w(String msg) {
        Log.w("AutoTag", msg);
    }

    public static void v(String msg) {
        Log.v("AutoTag", msg);
    }

    public static void toastShort(Context context, String msg) {
        Toast.makeText(context, msg, 0).show();
    }

    public static void toastShort(Context context, int rId) {
        Toast.makeText(context, rId, 0).show();
    }

    public static void toastLong(Context context, String msg) {
        Toast.makeText(context, msg, 1).show();
    }

    public static void toastLong(Context context, int rId) {
        Toast.makeText(context, rId, 1).show();
    }
}
