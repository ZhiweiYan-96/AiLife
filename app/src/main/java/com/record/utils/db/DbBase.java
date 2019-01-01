package com.record.utils.db;

import android.database.Cursor;

public class DbBase {
    public static String getStr(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    public static double getDou(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndex(column));
    }
}
