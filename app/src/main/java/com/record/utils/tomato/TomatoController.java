package com.record.utils.tomato;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager.WakeLock;
import com.record.bean.NoSubmitTomato;
import com.record.myLife.R;
import com.record.utils.DateTime;
import com.record.utils.LogUtils;
import com.record.utils.PowerMangerUtils;
import com.record.utils.PreferUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.log.MyLog;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TomatoController {
    static String TAG = "override";
    public static String TOMATO_DISPLAY_TIME = "TIME";
    public static String TOMATO_LEFT_SEC = "LEFT_SEC ";
    public static String TOMATO_PASS_SEC = "PASS_SEC ";
    public static String TOMATO_TOTAL = "TOTAL ";
    public static final int TOMATO_TYPE_REST = 2;
    public static final int TOMATO_TYPE_STUDY = 1;
    private static Context context;
    private static TomatoController controller = null;
    private static WakeLock wakeLock;
    int counter = 0;
    private String[] restPromptArr = null;
    private SharedPreferences sp = null;
    private String[] studyPromptArr = null;
    Timer tomatoTimer = null;
    int totalCounter = 1500;

    class myTimerTask extends TimerTask {
        myTimerTask() {
        }

        public void run() {
            int left = TomatoController.this.totalCounter - TomatoController.this.counter;
            if (left <= 0) {
                TomatoController.logfile("myTimerTask-番茄倒计为0 left:" + left + ",totalCounter " + TomatoController.this.totalCounter + ",counter" + TomatoController.this.counter);
                left = 0;
                TomatoController.this.counter = 0;
                TomatoController.context.sendBroadcast(new Intent(Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT));
                TomatoController.logfile("myTimerTask-番茄倒计为0 sendBroadcast intent:INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT");
                PowerMangerUtils.releasePrivateWakeLock(TomatoController.context);
                TomatoController.logfile("myTimerTask-番茄倒计时结束");
                cancel();
            } else {
                if (TomatoController.wakeLock == null) {
                    TomatoController.this.log(" wakeLock is null!");
                }
                if (TomatoController.wakeLock == null || TomatoController.wakeLock.isHeld()) {
                    TomatoController.logfile1("isHeld:" + TomatoController.wakeLock.isHeld());
                } else {
                    TomatoController.wakeLock.acquire();
                    TomatoController.logfile("myTimerTask- wakeLock acquire!");
                }
            }
            TomatoController.logfile1(left + "");
            TomatoController.this.sendMsgUpdateCounterTime(TomatoController.this.totalCounter, left, DateTime.calculateTime7((long) left));
            TomatoController tomatoController = TomatoController.this;
            tomatoController.counter++;
        }
    }

    private void sendMsgUpdateCounterTime(int total, int left, String time) {
        Intent it = new Intent(Val.ACTION_TOMOTO_COUNTING);
        it.putExtra(TOMATO_DISPLAY_TIME, time);
        it.putExtra(TOMATO_TOTAL, total);
        it.putExtra(TOMATO_LEFT_SEC, left);
        context.sendBroadcast(it);
    }

    public void startTomato(int totalCounter, int pass) {
        logfile("startTomato-totalSecond:" + totalCounter + ",pass" + pass);
        if (this.tomatoTimer != null) {
            logfile("startTomato-tomatoTimer.cancel");
            this.tomatoTimer.cancel();
            this.tomatoTimer = null;
        }
        this.totalCounter = totalCounter;
        this.counter = pass;
        if (this.tomatoTimer == null) {
            this.tomatoTimer = new Timer();
            logfile("startTomato-tomatoTimer.schedule");
            this.tomatoTimer.schedule(new myTimerTask(), 0, 1000);
            if (pass <= 0) {
                toastWhenStart();
            }
        }
    }

    private void toastWhenStart() {
        String prompt;
        if (1 == getTomatoType()) {
            prompt = getStudyPrompt();
            if (prompt != null && prompt.length() > 0) {
                ToastUtils.toastShort(context, prompt);
            }
        } else if (2 == getTomatoType()) {
            prompt = getRestPrompt();
            if (prompt != null && prompt.length() > 0) {
                ToastUtils.toastShort(context, prompt);
            }
        }
    }

    public void endTomato() {
        if (this.tomatoTimer != null) {
            this.tomatoTimer.cancel();
            this.tomatoTimer = null;
        }
        if (context != null) {
            PowerMangerUtils.releasePrivateWakeLock(context);
        }
    }

    public boolean isCounting() {
        int left = this.totalCounter - this.counter;
        if (this.tomatoTimer == null || left <= 0) {
            return false;
        }
        return true;
    }

    public int getLeftSec() {
        return this.totalCounter - this.counter;
    }

    public int getTomatoType() {
        return PreferUtils.getInt(context, Val.CONFIGURE_TOMATO_TYPE, 0);
    }

    private TomatoController() {
    }

    public static TomatoController getTomatoController(Context context1) {
        if (context1 != null) {
            context = context1;
        }
        if (controller == null) {
            controller = new TomatoController();
            wakeLock = PowerMangerUtils.getPowerManager(context1);
        }
        return controller;
    }

    private String getStudyPrompt() {
        if (this.studyPromptArr == null) {
            this.studyPromptArr = new String[]{context.getString(R.string.str_tomato_start_prompt1), context.getString(R.string.str_tomato_start_prompt2), context.getString(R.string.str_tomato_start_prompt3), context.getString(R.string.str_tomato_start_prompt4), context.getString(R.string.str_tomato_start_prompt4)};
        }
        String prompt = context.getString(R.string.str_tomato_start_prompt);
        try {
            int ran = (int) (Math.random() * 10.0d);
            if (ran >= 5) {
                ran = 9 - ran;
            }
            return this.studyPromptArr[ran];
        } catch (Exception e) {
            e.printStackTrace();
            return prompt;
        }
    }

    private String getRestPrompt() {
        if (this.restPromptArr == null) {
            this.restPromptArr = new String[]{context.getString(R.string.str_tomato_rest_start_prompt1), context.getString(R.string.str_tomato_rest_start_prompt2), context.getString(R.string.str_tomato_rest_start_prompt3), context.getString(R.string.str_tomato_rest_start_prompt4), context.getString(R.string.str_tomato_rest_start_prompt5), context.getString(R.string.str_tomato_rest_start_prompt6), context.getString(R.string.str_tomato_rest_start_prompt7), context.getString(R.string.str_tomato_rest_start_prompt8), context.getString(R.string.str_tomato_rest_start_prompt9), context.getString(R.string.str_tomato_rest_start_prompt9)};
        }
        String prompt = context.getString(R.string.str_tomato_rest_start_prompt);
        try {
            return this.restPromptArr[(int) (Math.random() * 10.0d)];
        } catch (Exception e) {
            e.printStackTrace();
            return prompt;
        }
    }

    public void log(String str) {
        MyLog.d(getClass().getSimpleName(), str);
    }

    public NoSubmitTomato getNoSubmitTomatoBean(Context context) {
        NoSubmitTomato noSubmitTomato = null;
        String startTime = getSp(context).getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        if (startTime == null || startTime.length() == 0) {
            return null;
        }
        int hadPassSec = DateTime.cal_secBetween(startTime, DateTime.getTimeString());
        int type = getSp(context).getInt(Val.CONFIGURE_TOMATO_TYPE, 0);
        int totalTime = 0;
        int defaultSec = 0;
        int delaySec = 0;
        int defaultMin;
        if (type == 2) {
            defaultMin = getSp(context).getInt(Val.CONFIGURE_TOMATO_REST_TIME, 5);
            defaultSec = defaultMin * 60;
            delaySec = getSp(context).getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
            totalTime = (defaultMin * 60) + delaySec;
            log("isContinue番茄开时间:" + startTime + "，defaultMin：" + defaultMin + "" + delaySec + "" + type);
        } else if (type == 1) {
            defaultMin = getSp(context).getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25);
            defaultSec = defaultMin * 60;
            delaySec = getSp(context).getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
            totalTime = (defaultMin * 60) + delaySec;
            log("isContinue番茄开时间:" + startTime + "1defaultMin：" + defaultMin + "" + delaySec + "" + type);
        }
        Calendar c = DateTime.pars2Calender(startTime);
        c.add(13, totalTime);
        String endTime = DateTime.formatTime(c);
        if ((type == 2 || type == 1) && hadPassSec - totalTime >= -1) {
            noSubmitTomato = new NoSubmitTomato(type, startTime, endTime, defaultSec, delaySec);
        }
        return noSubmitTomato;
    }

    private SharedPreferences getSp(Context context) {
        if (this.sp == null) {
            this.sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return this.sp;
    }

    public static void logfile(String str) {
        LogUtils.logfile(TomatoController.class.getSimpleName() + ":" + str);
    }

    public static void logfile1(String str) {
        LogUtils.logfile(":" + str);
    }
}
