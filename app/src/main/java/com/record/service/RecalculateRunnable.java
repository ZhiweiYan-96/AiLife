package com.record.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.record.utils.DateTime;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import java.util.ArrayList;

public class RecalculateRunnable implements Runnable {
    Context context;

    public RecalculateRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            Log.i("override RecalculateRunnable", "重新计算开始！");
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select * from t_act_item where isEnd is 1 and " + DbUtils.getWhereUserId(this.context) + " order by startTime", null);
            ArrayList<String> dateList = new ArrayList();
            String tempDate = "";
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                    Log.i("override RecalculateRunnable", "重新计算：" + startTime);
                    int take = cursor.getInt(cursor.getColumnIndex("take"));
                    int calTake = DateTime.cal_secBetween(startTime, cursor.getString(cursor.getColumnIndex("stopTime")));
                    if ((take < 0 || take != calTake) && calTake > 0) {
                        ContentValues values = new ContentValues();
                        values.put("take", Integer.valueOf(calTake));
                        DbUtils.getDb(this.context).update("t_act_item", values, "id is " + id, null);
                        String startDate = startTime.substring(0, startTime.indexOf(" "));
                        if (!startDate.equals(tempDate)) {
                            tempDate = startDate;
                            dateList.add(tempDate);
                        }
                    }
                }
            }
            Log.i("override RecalculateRunnable", "计算完毕！");
            DbUtils.close(cursor);
            this.context.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_IS_RECALCULATE_TAKE, 1).commit();
            new Thread(new RecalculateAllocationRunnable(this.context, dateList)).start();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }
}
