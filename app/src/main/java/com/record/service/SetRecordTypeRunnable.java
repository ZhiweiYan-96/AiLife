package com.record.service;

import android.content.ContentValues;
import android.content.Context;
import com.record.utils.db.DbUtils;

public class SetRecordTypeRunnable implements Runnable {
    Context context;

    public SetRecordTypeRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            ContentValues values = new ContentValues();
            values.put("isRecord", Integer.valueOf(1));
            DbUtils.getDb(this.context).update("t_act_item", values, " userId is ? and id > 0", new String[]{DbUtils.queryUserId(this.context)});
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }
}
