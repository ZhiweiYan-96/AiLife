package com.record.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.record.bean.Act;
import com.record.bean.ActUI;
import com.record.bean.User;
import com.record.myLife.main.TodayActivity;
import com.record.service.AutoBackupRunnable;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.MyNotification;
import com.record.utils.RemindUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.sun.mail.imap.IMAPStore;
import java.util.Date;

public class ITodayReceiver extends BroadcastReceiver {
    public static boolean isRunning = false;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        log("ITodayReceiver:" + action);
        try {
            if (Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(action)) {
                log("收到广播Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER");
                new MyNotification(context).initRetrospectNoti();
            } else if (Val.INTENT_ACTION_AUTO_BACK_UP_DATA.equals(action)) {
                log("收到广播Val.INTENT_ACTION_AUTO_BACK_UP_DATA");
                new Thread(new AutoBackupRunnable(context)).start();
            } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                log("收到广播,开机！");
                try {
                    RemindUtils.quickSetRetroSpection(context);
                    RemindUtils.quicksetAutoBackup(context);
                } catch (Exception e) {
                    DbUtils.exceptionHandler(context, e);
                }
            } else if (Val.INTENT_ACTION_ALARM_SEND.equals(action)) {
                log("收到广播Val.INTENT_ACTION_ALARM_SEND");
                isRunning = true;
                if (Val.actCount == 0) {
                    isContinue(context);
                }
                if (TodayActivity.onResume && TodayActivity.uiMap != null) {
                    ((ActUI) TodayActivity.uiMap.get(Act.getInstance().getId() + "")).getTv_temp_show_actName().setText(DateTime.calculateTime2((long) Val.actCount));
                }
                ContentValues values;
                if (Val.isInsertDb) {
                    log("插入数据！！！");
                    values = new ContentValues();
                    values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
                    values.put("actId", Integer.valueOf(Act.getInstance().getId()));
                    values.put("startTime", DateTime.getTimeString());
                    values.put("take", Integer.valueOf(Val.actCount));
                    values.put("stoptime", DateTime.getTimeString());
                    Val.actItemsId = (int) DbUtils.getDb(context).insert("t_act_item", null, values);
                    Val.isInsertDb = false;
                } else {
                    values = new ContentValues();
                    values.put("take", Integer.valueOf(Val.actCount));
                    values.put("stoptime", DateTime.getTimeString());
                    values.put("actId", Integer.valueOf(Act.getInstance().getId()));
                    DbUtils.getDb(context).update("t_act_item", values, " Id is ? ", new String[]{"" + Val.actItemsId});
                    log("更新数据！" + Val.actCount);
                }
                Val.actCount++;
            } else if (Val.INTENT_ACTION_WEIBO_SEND_STATUS.equals(action)) {
                log("收到广播Val.INTENT_ACTION_ALARM_SEND");
                GeneralHelper.toastShort(context, intent.getStringExtra("msg"));
            } else if (Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(action)) {
                log("收到广播Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER");
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                log("激活屏幕！");
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                log("关闭屏幕");
            } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                log("ACTION_USER_PRESENT");
            } else if ("com.record.timerService.close".equals(action)) {
                log("关闭服务");
            }
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
        }
    }

    private void isContinue(Context context) {
        Cursor cursor = DbUtils.getDb2(context).rawQuery("Select * from t_act_item where userId is ? and isEnd is not 1 order by startTime desc", new String[]{DbUtils.queryUserId(context) + ""});
        log("内存被释放！");
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String actId = cursor.getString(cursor.getColumnIndex("actId"));
            String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
            int take = cursor.getInt(cursor.getColumnIndex("take"));
            String nowStr = DateTime.getTimeString();
            log("id:" + id + ",actId:" + actId + ",take:" + take + ",startTime:" + startTime + ",stopTime:" + nowStr);
            try {
                Date start = DateTime.Calendar2Date(DateTime.pars2Calender(startTime));
                int start2 = (int) start.getTime();
                int tmp = (((int) DateTime.Calendar2Date(DateTime.pars2Calender(nowStr)).getTime()) - start2) / IMAPStore.RESPONSE;
                if (tmp > 0) {
                    take = tmp;
                }
                log("上次到" + take);
                Val.actCount = take;
                Val.actItemsId = Integer.parseInt(id);
                DbUtils.getActAndSet_v2(context, actId);
                Val.isInsertDb = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DbUtils.close(cursor);
    }

    public void log(String str) {
        Log.i("override ItodayRecerver", ":" + str);
    }
}
