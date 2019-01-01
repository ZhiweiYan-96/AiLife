package com.record.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.record.myLife.base.BottomActivity;
import com.record.utils.db.DbUtils;
import java.util.List;
import java.util.Locale;

public class AndroidUtils {
    public static boolean isBackground2(Context context) {
        try {
            List<RunningTaskInfo> tasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
            if (!(tasks.isEmpty() || ((RunningTaskInfo) tasks.get(0)).topActivity.getPackageName().equals(context.getPackageName()))) {
                return true;
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
        }
        return false;
    }

    public static String getDeviceId(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
            return "";
        }
    }

    public static String[] getLanguageArr() {
        return new String[]{Val.CONFIGURE_LANGUAGE_DEFAULT, "tw", "English"};
    }

    public static void changeAppLanguage(Resources resources, String lan) {
        String[] arr1 = getLanguageArr();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (arr1[0].equalsIgnoreCase(lan)) {
            config.locale = Locale.CHINA;
        } else if (arr1[1].equalsIgnoreCase(lan)) {
            config.locale = Locale.TAIWAN;
        } else if (arr1[2].equalsIgnoreCase(lan)) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
    }

    public static void restart(Context context) {
        Intent it = new Intent(context, BottomActivity.class);
        it.addFlags(67108864);
        it.addFlags(268435456);
        context.startActivity(it);
    }
}
