package com.record.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.record.bean.User;
import com.record.utils.db.DbUtils;

public class UserUtils {
    public static void isLoginUser(Context context) {
        Cursor cursor = DbUtils.getDb(context).rawQuery("select * from t_user where isLogin = 1", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String userName = cursor.getString(cursor.getColumnIndex("userName"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            int integral = cursor.getInt(cursor.getColumnIndex("integral"));
            String uid = cursor.getString(cursor.getColumnIndex("uid"));
            User user = User.getInstance();
            user.setUserId(id);
            user.setUserName(userName);
            user.setIntegral(integral);
            user.setNickname(nickname);
            user.setUid(uid);
        }
        DbUtils.close(cursor);
    }

    public static void initTryUser(Context context) {
        initLoginDb(context);
        String tableName = "t_user";
        Cursor cursor = DbUtils.getDb(context).rawQuery("select id from " + tableName + " where userName is '测试'", null);
        ContentValues values;
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            values = new ContentValues();
            values.put("isLogin", Integer.valueOf(1));
            DbUtils.getDb(context).update(tableName, values, " id = " + id, null);
        } else {
            values = new ContentValues();
            values.put("userName", "测试");
            values.put("password", Md5.toMd5("123456"));
            values.put("isLogin", Integer.valueOf(1));
            DbUtils.getDb(context).insert("t_user", null, values);
        }
        DbUtils.close(cursor);
        isLoginUser(context);
    }

    public static boolean isUserLogin(Context context) {
        if (DbUtils.getDb(context).rawQuery("select Id from t_user where isLogin = 1", null).getCount() > 0) {
            return true;
        }
        return false;
    }

    public static void initLoginDb(Context context) {
        ContentValues values = new ContentValues();
        values.put("isLogin", Integer.valueOf(0));
        DbUtils.getDb(context).update("t_user", values, "isLogin is ?", new String[]{"1"});
    }
}
