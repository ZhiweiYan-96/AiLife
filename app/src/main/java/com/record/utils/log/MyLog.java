package com.record.utils.log;

import android.util.Log;
import com.record.utils.DateTime;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyLog {
    private static String MYLOGFILEName = "itodayss_log.txt";
    public static String MYLOG_PATH_SDCARD_DIR = "/sdcard/itodayss";
    public static Boolean MYLOG_SWITCH = Boolean.valueOf(true);
    private static char MYLOG_TYPE = 'v';
    public static Boolean MYLOG_WRITE_TO_FILE = Boolean.valueOf(false);
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 7;
    private static SimpleDateFormat logfile = new SimpleDateFormat(DateTime.DATE_FORMAT_LINE);
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void w(String tag, Object msg) {
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) {
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        log(tag, text, 'i');
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    private static void log(String tag, String msg, char level) {
        if (MYLOG_SWITCH.booleanValue()) {
            if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.e(tag, msg);
            } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.w(tag, msg);
            } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.d(tag, msg);
            } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.i(tag, msg);
            } else {
                Log.v(tag, msg);
            }
            if (MYLOG_WRITE_TO_FILE.booleanValue()) {
                writeLogtoFile(String.valueOf(level), tag, msg);
            }
        }
    }

    private static void writeLogtoFile(String mylogtype, String tag, String text) {
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype + "    " + tag + "    " + text;
        try {
            FileWriter filerWriter = new FileWriter(new File(MYLOG_PATH_SDCARD_DIR, needWriteFiel + MYLOGFILEName), true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delFile() {
        File file = new File(MYLOG_PATH_SDCARD_DIR, logfile.format(getDateBefore()) + MYLOGFILEName);
        if (file.exists()) {
            file.delete();
        }
    }

    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(5, now.get(5) - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
}
