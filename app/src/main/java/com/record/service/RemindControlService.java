package com.record.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import com.record.myLife.R;
import com.record.utils.MyNotification;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import java.util.Calendar;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class RemindControlService extends Service {
    static String TAG = "override";
    String action;
    Context context;
    boolean isStopSelf = true;
    MediaPlayer media;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this;
        this.action = intent.getAction();
        if (this.action != null) {
            if (Val.INTENT_ACTION_REMIND_INTERVAL.equals(this.action)) {
                remindInterval();
            } else if (Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(this.action)) {
                new MyNotification(this.context).initRetrospectNoti();
            } else if (Val.INTENT_ACTION_NOTI_MORNING_VOICE.equals(this.action)) {
                new MyNotification(this.context).initMorningVoiceNoti();
            }
        }
        if (this.isStopSelf) {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void remindInterval() {
        try {
            if (getSp().getInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 0) != 0) {
                Calendar c = Calendar.getInstance();
                if (!getSp().getString(Val.CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR, "0,1,2,3,4,5,6,7,23").contains(c.get(HOUR_OF_DAY) + "")) {
                    SharedPreferences sp = getSp();
                    int isShake = sp.getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1);
                    int isSound = sp.getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1);
                    log("间隔提醒isShake" + isShake + ",isSound:" + isSound + ",isShowDialog:" + sp.getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG, 0));
                    if (isSound > 0) {
                        this.isStopSelf = false;
                    }
                    if (c.get(MINUTE) == 0) {
                        if (isSound > 0) {
                            playTwoSound();
                        }
                        if (isShake > 0) {
                            vibrateTwice();
                            return;
                        }
                        return;
                    }
                    if (isSound > 0) {
                        playOneSound();
                    }
                    if (isShake > 0) {
                        vibrateOne();
                    }
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(this.context, e);
        }
    }

    private void playTwoSound() {
//        this.media = MediaPlayer.create(this.context, R.raw.itodayss_strike);
        this.media.start();
        this.media.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                RemindControlService.this.stopSelf();
            }
        });
    }

    private void playOneSound() {
//        this.media = MediaPlayer.create(this.context, R.raw.itodayss_strike_one);
        this.media.start();
        this.media.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                RemindControlService.this.stopSelf();
            }
        });
    }

    private void vibrateOne() {
        ((Vibrator) getSystemService("vibrator")).vibrate(new long[]{100, 200, 500}, -1);
    }

    private void vibrateTwice() {
        ((Vibrator) getSystemService("vibrator")).vibrate(new long[]{100, 200, 500, 200, 100}, -1);
    }

    public SharedPreferences getSp() {
        return PreferUtils.getSP(this.context);
    }

    public void onCreate() {
        super.onCreate();
        TAG += getClass().getSimpleName();
        this.context = this;
    }

    public void onDestroy() {
        log("服务onDestroy");
        try {
            if (this.media != null) {
                this.media.release();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(this.context, e);
        }
        super.onDestroy();
    }

    private static void log(String str) {
        Log.i("override ", ":" + str);
    }
}
