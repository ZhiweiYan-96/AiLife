package com.record.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.record.utils.db.DbUtils;

public class SetLabelPositionRunnable implements Runnable {
    static String TAG = "override";
    private Context context;

    public SetLabelPositionRunnable(Context context) {
        this.context = context;
        TAG = "override " + getClass().getName();
    }

    public void run() {
        try {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_sub_type where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 order by Id", null);
            if (cursor.getCount() > 0) {
                int i = 0;
                while (cursor.moveToNext()) {
                    ContentValues values = new ContentValues();
                    values.put("labelPosition", Integer.valueOf(i));
                    DbUtils.getDb(this.context).update("t_sub_type", values, "Id is " + cursor.getString(cursor.getColumnIndex("Id")), null);
                    i++;
                }
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
