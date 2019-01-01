package com.record.utils.db.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;

public class BaseDbService extends DbBase {
    public static SQLiteDatabase getDb(Context context) {
        return DbUtils.getDb(context);
    }

    public static void log(String str) {
        Log.i("override BaseDbService", ":" + str);
    }
}
