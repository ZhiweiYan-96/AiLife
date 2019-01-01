package com.record.thread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.record.myLife.R;
import com.record.utils.DateTime;
import com.record.utils.PushInitUtils;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;
import com.record.utils.net.EmailUtils;

public class SendEmialRunnable extends DbBase implements Runnable {
    static String TAG = "override";
    private Context context;

    public SendEmialRunnable(Context context) {
        this.context = context;
        TAG = "override " + getClass().getSimpleName();
    }

    public void run() {
        try {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_feed_back where isSended is not 1 ", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String Id = DbBase.getStr(cursor, "Id");
                    String content = DbBase.getStr(cursor, PushInitUtils.RESPONSE_CONTENT);
                    String contact = DbBase.getStr(cursor, "contact");
                    String ti = DbBase.getStr(cursor, "contact");
                    String sendTime = DbBase.getStr(cursor, "sendTime");
                    String str = "";
                    if (content.length() > 100) {
                        str = content.substring(0, 90);
                    } else {
                        str = content;
                    }
                    boolean falg = EmailUtils.send(this.context.getResources().getString(R.string.app_name) + this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionName + " 反馈:" + contact + " 内容：" + str, content + ",发送时间：" + sendTime + DateTime.getDayOfWeek(sendTime));
                    log("发送反馈成功：" + content);
                    if (falg) {
                        ContentValues values = new ContentValues();
                        values.put("isSended", Integer.valueOf(1));
                        DbUtils.getDb(this.context).update("t_feed_back", values, "Id is " + Id, null);
                    }
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
