package com.record.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.record.utils.FormatUtils;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;

public class SetActItemsTypeRunnable implements Runnable {
    Context context;

    public SetActItemsTypeRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            int id;
            ContentValues values;
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id,type from t_act where " + DbUtils.getWhereUserId(this.context), null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    int type = cursor.getInt(cursor.getColumnIndex(a.a));
                    values = new ContentValues();
                    values.put("actType", Integer.valueOf(type));
                    DbUtils.getDb(this.context).update("t_act_item", values, " actId is ? ", new String[]{"" + id});
                }
            }
            DbUtils.close(cursor);
            cursor = DbUtils.getDb(this.context).rawQuery("select id,invest from t_allocation where " + DbUtils.getWhereUserId(this.context), null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    int invest = cursor.getInt(cursor.getColumnIndex("invest"));
                    if (invest > 0) {
                        values = new ContentValues();
                        values.put("earnMoney", FormatUtils.format_1fra(((double) invest) / 1800.0d));
                        DbUtils.getDb(this.context).update("t_allocation", values, "id is ?", new String[]{id + ""});
                    }
                }
                log("更新完毕！");
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private static void log(String str) {
        Log.i("override SetActItemsType", ":" + str);
    }
}
