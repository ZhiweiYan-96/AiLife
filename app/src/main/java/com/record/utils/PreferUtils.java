package com.record.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferUtils {
    public static int BIAS_REMIND_DAY = 7;
    private static SharedPreferences sp;

    public static SharedPreferences getSP(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return sp;
    }

    public static void putString(Context context, String key, String value) {
        getSP(context).edit().putString(key, value).commit();
    }

    public static void putInt(Context context, String key, int value) {
        getSP(context).edit().putInt(key, value).commit();
    }

    public static String getString(Context context, String key, String defaultV) {
        return getSP(context).getString(key, defaultV);
    }

    public static int getInt(Context context, String key, int defaultV) {
        return getSP(context).getInt(key, defaultV);
    }

    public static SharedPreferences getGoalSP(Context context) {
        return context.getSharedPreferences(Val.CONFIGURE_GOAL_REMINID_NAME, 0);
    }

    public static String getGoalBiasKey(int id) {
        return "bias" + id;
    }

    public static String getGoalOver13Key(int id) {
        return "correctionOver13" + id;
    }
}
