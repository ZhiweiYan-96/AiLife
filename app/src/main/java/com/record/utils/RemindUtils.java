package com.record.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.alibaba.fastjson.asm.Opcodes;
import com.record.myLife.main.tomato.RemindTomatoActivity;
import com.record.myLife.settings.remind.IntervalRemindActivity;
import com.record.myLife.settings.remind.RemindInvestActivity;
import com.record.myLife.settings.remind.RemindRestActivity;
import com.record.service.AutoBackupService;
import com.record.service.RemindControlService;
import com.record.utils.db.DbUtils;
import java.util.Calendar;

public class RemindUtils {
    private static final int milSecond = 1000;
    static SharedPreferences sp;

    public static void setRemindTomatoTimeOut(Context context, int triggerSecond, String action) {
        ((AlarmManager) context.getSystemService("alarm")).set(0, System.currentTimeMillis() + ((long) (triggerSecond * 1000)), getTomatoRemindPi(context, action));
        log("开启休息提醒！triggerSecond:" + triggerSecond + ",action:" + action + ":" + 2);
    }

    public static PendingIntent getTomatoRemindPi(Context context, String action) {
        Intent it = new Intent(context, RemindTomatoActivity.class);
        it.setAction(action);
        it.setFlags(536870912);
        return PendingIntent.getActivity(context, 1, it, 134217728);
    }

    public static void cancelTomatoRemindAll(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        alarmManager.cancel(getTomatoRemindPi(context, Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT));
        alarmManager.cancel(getTomatoRemindPi(context, Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT));
    }

    public static void setRemindInterval(Context context) {
        if (getsp(context).getInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 0) > 0) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
            int interval = getsp(context).getInt(Val.CONFIGURE_REMIND_INTERVAL_VAL, 30);
            Calendar c = Calendar.getInstance();
            if (interval == 15) {
                c = DateTime.getNextquarter();
            } else if (interval == 30) {
                c = DateTime.getNextHalf();
            } else if (interval == 60 || interval == Opcodes.GETFIELD) {
                c = DateTime.getNextHour();
            }
            alarmManager.setRepeating(0, c.getTimeInMillis(), (long) ((interval * 60) * 1000), getRemindIntervalPi(context));
        }
    }

    public static void cancelRemindInterval(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getRemindIntervalPi(context));
        log("取消间隔提醒！");
    }

    public static PendingIntent getRemindIntervalPi(Context context) {
        Intent it = new Intent(context, RemindControlService.class);
        it.setAction(Val.INTENT_ACTION_REMIND_INTERVAL);
        return PendingIntent.getService(context, 1, it, 134217728);
    }

    public static PendingIntent getRemindIntervalPi_Old(Context context) {
        Intent it = new Intent(context, IntervalRemindActivity.class);
        it.setFlags(805306368);
        return PendingIntent.getActivity(context, 1, it, 134217728);
    }

    public static void setRemindInvest(Context context, int triggerMin) {
        ((AlarmManager) context.getSystemService("alarm")).set(0, System.currentTimeMillis() + ((long) ((triggerMin * 60) * 1000)), getRemindInvestPi(context));
        log("开启学习提醒！");
    }

    public static void cancleRemindInvest(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getRemindInvestPi(context));
        log("取消学习提醒！");
    }

    public static PendingIntent getRemindInvestPi(Context context) {
        Intent it = new Intent(context, RemindInvestActivity.class);
        it.setFlags(268435456);
        return PendingIntent.getActivity(context, 1, it, 134217728);
    }

    public static void quickSetRemindRest2(Context context) {
        if (!isRemindRest(context)) {
            log("设置休息提醒：当前休息提醒关闭，不提醒！");
        } else if (DbUtils.queryIsGoalCounting(context, 10, 11)) {
            log("设置休息提醒：当前有目标在计时");
            int triTime = getsp(context).getInt(Val.CONFIGURE_REMIND_REST_LEARN_TIME, 45);
            GeneralUtils.toastShort(context, "检测到您正在学习中，将在" + triTime + "分钟后提醒休息哦！");
            setRemindRest(context, triTime);
        } else {
            log("设置休息提醒：当前无目标在计时..");
        }
    }

    public static void quickSetRemindRest(Context context) {
        if (!isRemindRest(context)) {
            log("设置休息提醒：当前休息提醒关闭，不提醒！");
        } else if (DbUtils.queryIsGoalCounting(context, 10, 11)) {
            log("设置休息提醒：当前有目标在计时");
            int triTime = getsp(context).getInt(Val.CONFIGURE_REMIND_REST_LEARN_TIME, 45);
            String startTime = getsp(context).getString(Val.CONFIGURE_REMIND_REST_START_TIME, "");
            long now = System.currentTimeMillis();
            log("设置休息提醒开始时间：startTime" + startTime + ",学习" + triTime + "分钟");
            if (startTime == null || startTime.length() <= 0) {
                log("设置休息提醒：now：" + now + ",triTime:" + triTime);
                getsp(context).edit().putString(Val.CONFIGURE_REMIND_REST_START_TIME, DateTime.getTimeString()).commit();
                setRemindRest2(context, now, triTime);
                return;
            }
            long now2 = DateTime.pars2Calender(startTime).getTime().getTime();
            Calendar cal = DateTime.pars2Calender(startTime);
            cal.set(12, cal.get(12) + triTime);
            log("设置休息提醒：now：" + now + ",cal.getTime().getTime():" + cal.getTime().getTime());
            if (now < cal.getTime().getTime()) {
                setRemindRest2(context, now2, triTime);
                return;
            }
            getsp(context).edit().putString(Val.CONFIGURE_REMIND_REST_START_TIME, DateTime.getTimeString()).commit();
            setRemindRest2(context, now, triTime);
        } else {
            log("设置休息提醒：当前无目标在计时..");
        }
    }

    public static boolean isRemindRest(Context context) {
        if (getsp(context).getInt(Val.CONFIGURE_IS_REMIND_REST, 0) > 0) {
            return true;
        }
        return false;
    }

    public static void setRemindRest(Context context, int triggerMin) {
        ((AlarmManager) context.getSystemService("alarm")).set(0, System.currentTimeMillis() + ((long) ((triggerMin * 60) * 1000)), getRemindRestPi(context));
        log("开启休息提醒！");
    }

    private static void setRemindRest2(Context context, long now, int triggerSecod) {
        ((AlarmManager) context.getSystemService("alarm")).set(0, ((long) ((triggerSecod * 60) * 1000)) + now, getRemindRestPi(context));
        log("开启休息提醒！");
    }

    public static void cancelRemindRest(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getRemindRestPi(context));
        log("取消休息提醒！");
    }

    public static PendingIntent getRemindRestPi(Context context) {
        Intent it = new Intent(context, RemindRestActivity.class);
        it.setFlags(268435456);
        return PendingIntent.getActivity(context, 1, it, 134217728);
    }

    public static void quicksetAutoBackup(Context context) {
        if (getsp(context).getInt(Val.CONFIGURE_IS_AUTO_BACKUP_DATA, 0) > 0) {
            setAutoBackup(context, "22:10:00");
        }
        log("开启每天自动备份");
    }

    public static void setAutoBackup(Context context, String time) {
        String date = DateTime.getTimeString();
        String endTime = DateTime.getDateString() + " " + time;
        int triggerAtTime = DateTime.cal_secBetween(date, endTime);
        log("每天自动备份1：" + endTime + ",,triggerAtTime" + triggerAtTime);
        if (triggerAtTime <= 0) {
            endTime = DateTime.beforeNDays2Str(1) + " " + time;
            triggerAtTime = DateTime.cal_secBetween(date, endTime);
            log("每天自动备份2：" + endTime + ",,triggerAtTime" + triggerAtTime);
        }
        ((AlarmManager) context.getSystemService("alarm")).setRepeating(0, System.currentTimeMillis() + ((long) (triggerAtTime * 1000)), 86400000, getAutoBackupPendingIntent(context));
        log("每天自动备份开启！");
    }

    public static void cancelAutoBackup(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getAutoBackupPendingIntent(context));
        log("取消每天自动备份！");
    }

    public static PendingIntent getAutoBackupPendingIntent(Context context) {
        Intent it = new Intent(context, AutoBackupService.class);
        it.setFlags(268435456);
        it.putExtra("fromAutoBackup", 1);
        return PendingIntent.getService(context, 1, it, 134217728);
    }

    public static void quickSetRemindMorningVoice(Context context) {
        if (getsp(context).getInt(Val.CONFIGURE_IS_REMIND_ADD_MONING_NOTE, 0) > 0) {
            setRetroSpection(context, sp.getString(Val.CONFIGURE_REMIND_MORNING_VOICE_TIME, Val.CONFIGURE_REMIND_MORNING_VOICE_TIME_DEFAULT) + ":00", Val.INTENT_ACTION_NOTI_MORNING_VOICE);
        }
    }

    public static void quickSetRetroSpection(Context context) {
        if (getsp(context).getInt(Val.CONFIGURE_IS_REMIND_ADD_NOTE, 1) > 0) {
            setRetroSpection(context, sp.getString(Val.CONFIGURE_REMIND_ADD_NOTE_TIME, "22:00") + ":00", Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER);
        }
    }

    public static void setRetroSpection(Context context, String time, String action) {
        String date = DateTime.getTimeString();
        String endTime = DateTime.getDateString() + " " + time;
        int triggerAtTime = DateTime.cal_secBetween(date, endTime);
        log(action + "提醒信息1：" + endTime + ",,triggerAtTime" + triggerAtTime);
        if (triggerAtTime <= 0) {
            endTime = DateTime.beforeNDays2Str(1) + " " + time;
            triggerAtTime = DateTime.cal_secBetween(date, endTime);
            log(action + "提醒信息2：" + endTime + ",,triggerAtTime" + triggerAtTime);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        alarmManager.setRepeating(0, System.currentTimeMillis() + ((long) (triggerAtTime * 1000)), 86400000, getAddNoteRemindPit_v2(context, action));
        log("每日回顾提醒开启！");
        alarmManager.cancel(getAddNoteRemindPit(context));
    }

    public static void cancelRetroSpection(Context context, String action) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getAddNoteRemindPit_v2(context, action));
        log("取消每日回顾提醒！");
    }

    public static PendingIntent getAddNoteRemindPit(Context context) {
        return PendingIntent.getBroadcast(context, 1, new Intent(Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER), 134217728);
    }

    public static PendingIntent getAddNoteRemindPit_v2(Context context, String action) {
        Intent it = new Intent(context, RemindControlService.class);
        it.setAction(action);
        return PendingIntent.getService(context, 1, it, 134217728);
    }

    public static void setUpdateWidgetUI(Context context) {
        AlarmManager manger = (AlarmManager) context.getSystemService("alarm");
        int interal = context.getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_UPDATE_WIDGETS_INTERAL, Val.DEFAULT_UPDATE_WIDGET_INTERVAL);
        manger.setRepeating(1, System.currentTimeMillis() + 3000, (long) ((interal * 60) * 1000), getWidgetPendingIntent(context));
        log("设置更新widget闹钟  interal:" + interal);
    }

    public static void cancelUpdateWidgetUI(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getWidgetPendingIntent(context));
    }

    public static PendingIntent getWidgetPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, 1, new Intent(Val.INTENT_ACTION_WIDGET_UPDATE_BAR_UI), 134217728);
    }

    private static SharedPreferences getsp(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 2);
        }
        return sp;
    }

    public static void log(String str) {
        Log.i("override Login", ":" + str);
    }
}
