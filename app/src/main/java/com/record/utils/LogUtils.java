package com.record.utils;

import android.util.Log;
import com.record.myLife.other.DebugActivity;
import com.record.utils.db.DbUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class LogUtils {
    public static void log(String str) {
        Log.i("override", str);
    }

    public static void logfile(String str) {
        if (DebugActivity.debug_save_logfile_for_tomato_bool) {
            String logName = DateTime.getDateString() + "_remind.txt";
            String dir = "itodayss";
            str = "[" + DateTime.getTimeString() + "]:" + str;
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FileUtils.creatSDFile(logName, dir), true));
                bufferedWriter.append(str);
                bufferedWriter.newLine();
                bufferedWriter.close();
                return;
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                return;
            }
        }
        log(str);
    }
}
