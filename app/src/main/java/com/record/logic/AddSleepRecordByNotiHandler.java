package com.record.logic;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build.VERSION;
import com.record.conts.Consts;
import com.record.utils.DateTime;
import com.record.utils.LogUtils;
import com.record.utils.db.DbUtils;
import com.record.utils.noti.RemindNotification;
import com.umeng.analytics.onlineconfig.a;

public class AddSleepRecordByNotiHandler extends BaseLogic {
    public void addRecordByIntent(Context context, Intent intent) {
        if (intent != null) {
            try {
                new Recorder().addTimeSave(context, intent.getStringExtra("startTime"), intent.getStringExtra("endTime"), intent.getStringExtra("actId"), 0);
                clearNoti(context);
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
        }
    }

    public void notiAddSleep(Context context, String startTime, String endTime, String actid) {
        if (VERSION.SDK_INT >= 11) {
            ((NotificationManager) context.getSystemService("notification")).notify(3, new RemindNotification(context, startTime, endTime, actid).getRemindNofication());
        }
    }

    private void clearNoti(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(3);
    }

    public void calculateTake(Context context) {
        String lastSaveTime = "";
        Cursor cursor1 = DbUtils.getDb(context).rawQuery("select saveTime from t_screen_log where iscalc = 1 order by saveTime desc limit 1", null);
        if (cursor1.getCount() > 0) {
            cursor1.moveToNext();
            lastSaveTime = cursor1.getString(cursor1.getColumnIndex("saveTime"));
        }
        DbUtils.close(cursor1);
        String sql2 = "select Id,saveTime from t_screen_log where saveTime >= '" + lastSaveTime + "' order by saveTime";
        if (lastSaveTime == null || lastSaveTime.length() == 0) {
            sql2 = "select Id,saveTime from t_screen_log where iscalc is not 1 order by saveTime";
        }
        Cursor cursor2 = DbUtils.getDb(context).rawQuery(sql2, null);
        if (cursor2.getCount() > 2) {
            String lastTime = "";
            while (cursor2.moveToNext()) {
                int id = cursor2.getInt(cursor2.getColumnIndex("Id"));
                String savetime = cursor2.getString(cursor2.getColumnIndex("saveTime"));
                if (cursor2.isFirst()) {
                    lastTime = savetime;
                } else {
                    DbUtils.getDb(context).execSQL("UPDATE t_screen_log SET take = " + DateTime.cal_secBetween(lastTime, savetime) + ", iscalc = 1 WHERE Id = " + id);
                    lastTime = savetime;
                }
            }
        }
        DbUtils.close(cursor2);
    }

    public int getSysCloseScreenRange(Context context) {
        int sysCloseScreenRange = 0;
        Cursor cursor = DbUtils.getDb(context).rawQuery("select take,count(*) as times  from t_screen_log group by take  order by times desc limit 10", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int take = cursor.getInt(cursor.getColumnIndex("take"));
                LogUtils.log("take:" + take + "times" + cursor.getInt(cursor.getColumnIndex("times")));
                if (take % 5 == 0) {
                    sysCloseScreenRange = take;
                    break;
                }
            }
        }
        DbUtils.close(cursor);
        return sysCloseScreenRange;
    }

    public String[] getLastSleepTimeArr(Context context) {
        int sysCloseScreenRange;
        calculateTake(context);
        if (Consts.SYSTEM_CLOSE_SCREEN_TIME == 0) {
            Consts.SYSTEM_CLOSE_SCREEN_TIME = getSysCloseScreenRange(context);
            sysCloseScreenRange = Consts.SYSTEM_CLOSE_SCREEN_TIME;
        } else {
            sysCloseScreenRange = Consts.SYSTEM_CLOSE_SCREEN_TIME;
        }
        int sysCloseScreenRange2 = sysCloseScreenRange + 1;
        String yesterdayTime = DateTime.beforeNDays2Str2(-1);
        String yesterday = DateTime.beforeNDays2Str(-1);
        String today = DateTime.getDateString();
        String minTime = yesterday + " 18:00:00";
        String maxTime = today + " 14:00:00";
        Cursor cursor3 = DbUtils.getDb(context).rawQuery("select * from t_screen_log where saveTime > '" + yesterdayTime + "'  order by take desc limit 5", null);
        if (cursor3.getCount() > 0) {
            while (cursor3.moveToNext()) {
                String savetime = cursor3.getString(cursor3.getColumnIndex("saveTime"));
                int type = cursor3.getInt(cursor3.getColumnIndex(a.a));
                int take = cursor3.getInt(cursor3.getColumnIndex("take"));
                int morethan = DateTime.compare_date(savetime, minTime);
                int lessthan = DateTime.compare_date(maxTime, savetime);
                if (morethan == 1 && lessthan == 1) {
                    int type6;
                    String savetime6;
                    int take6;
                    String startTime = "";
                    String endTime = savetime;
                    Cursor cursor6 = DbUtils.getDb(context).rawQuery("select * from t_screen_log where saveTime < '" + savetime + "'  order by saveTime desc limit 30", null);
                    if (cursor6.getCount() > 0) {
                        while (cursor6.moveToNext()) {
                            type6 = cursor6.getInt(cursor6.getColumnIndex(a.a));
                            if (type6 != 1) {
                                savetime6 = cursor6.getString(cursor6.getColumnIndex("saveTime"));
                                take6 = cursor6.getInt(cursor6.getColumnIndex("take"));
                                if (sysCloseScreenRange > 0) {
                                    if (!(type6 == 2 && (take6 == sysCloseScreenRange2 || take6 == sysCloseScreenRange))) {
                                        startTime = savetime6;
                                        if (!cursor6.moveToNext()) {
                                            break;
                                        }
                                        type6 = cursor6.getInt(cursor6.getColumnIndex(a.a));
                                        if (type6 != 1) {
                                            take6 = cursor6.getInt(cursor6.getColumnIndex("take"));
                                            if (type6 == 2) {
                                                if (take6 != sysCloseScreenRange2) {
                                                    if (take6 != sysCloseScreenRange) {
                                                        break;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                startTime = savetime6;
                                break;
                            }
                        }
                    }
                    DbUtils.close(cursor6);
                    if (sysCloseScreenRange > 0) {
                        Cursor cursor7 = DbUtils.getDb(context).rawQuery("select * from t_screen_log where saveTime > '" + savetime + "'  order by saveTime limit 30", null);
                        String templastType1Time = "";
                        if (cursor7.getCount() > 0) {
                            while (cursor7.moveToNext()) {
                                savetime6 = cursor7.getString(cursor7.getColumnIndex("saveTime"));
                                type6 = cursor7.getInt(cursor7.getColumnIndex(a.a));
                                if (type6 == 1) {
                                    templastType1Time = savetime6;
                                } else {
                                    take6 = cursor7.getInt(cursor7.getColumnIndex("take"));
                                    if (sysCloseScreenRange <= 0) {
                                        break;
                                    } else if (!(type6 == 2 && (take6 == sysCloseScreenRange2 || take6 == sysCloseScreenRange))) {
                                        if (templastType1Time != null && templastType1Time.length() > 0) {
                                            endTime = templastType1Time;
                                        }
                                    }
                                }
                            }
                        }
                        DbUtils.close(cursor7);
                    }
                    if (startTime != null && endTime != null && startTime.length() > 0 && endTime.length() > 0) {
                        return new String[]{startTime, endTime};
                    }
                }
            }
        }
        DbUtils.close(cursor3);
        return null;
    }

    public boolean isShowNoti(Context context) {
        calculateTake(context);
        int sysCloseScreenRange = 0;
        if (Consts.SYSTEM_CLOSE_SCREEN_TIME == 0) {
            Consts.SYSTEM_CLOSE_SCREEN_TIME = getSysCloseScreenRange(context);
            sysCloseScreenRange = Consts.SYSTEM_CLOSE_SCREEN_TIME;
        }
        Cursor cursor4 = DbUtils.getDb(context).rawQuery("select Id from t_screen_log where take > " + sysCloseScreenRange + " and type = 2 and  savetime > '" + DateTime.getDateString() + " 04:00:00'  limit 1", null);
        if (cursor4.getCount() > 0) {
            DbUtils.close(cursor4);
            return true;
        }
        DbUtils.close(cursor4);
        return false;
    }
}
