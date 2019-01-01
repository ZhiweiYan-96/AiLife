package com.record.thread;

import android.content.Context;
import android.util.Log;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;

public class TemRunnable extends DbBase implements Runnable {
    static String TAG = "override";
    private Context context;

    public TemRunnable(Context context) {
        this.context = context;
        TAG = "override " + getClass().getSimpleName();
    }

    public void run() {
    }

    public static boolean isUidExist(Context context, int id) {
        if (DbUtils.queryUserUidByUserId(context, id) > 0) {
            return true;
        }
        return false;
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
