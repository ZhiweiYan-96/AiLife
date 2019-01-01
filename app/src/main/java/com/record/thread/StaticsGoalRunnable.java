package com.record.thread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;

public class StaticsGoalRunnable extends DbBase implements Runnable {
    static String TAG = "override";
    private Context context;

    public StaticsGoalRunnable(Context context) {
        this.context = context;
        TAG = "override " + getClass().getSimpleName();
    }

    public void run() {
        try {
            DbUtils.staticsGoalAll(this.context);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void updateDb(String id) {
        Cursor cur = DbUtils.getDb2(this.context).rawQuery("select * from t_act_item  where userId is ? and actId is " + id, new String[]{DbUtils.queryUserId(this.context)});
        if (cur.getCount() > 0) {
            int hadSpend = 0;
            while (cur.moveToNext()) {
                hadSpend += cur.getInt(cur.getColumnIndex("take"));
            }
            ContentValues values = new ContentValues();
            values.put("hadSpend", Integer.valueOf(hadSpend));
            DbUtils.getDb(this.context).update("t_act", values, " Id is ?", new String[]{id});
        }
        DbUtils.close(cur);
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
