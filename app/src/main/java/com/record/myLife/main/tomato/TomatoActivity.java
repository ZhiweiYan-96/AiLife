package com.record.myLife.main.tomato;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.NoSubmitTomato;
import com.record.myLife.BaseApplication;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.TomatosView;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.TimerService;
import com.record.utils.DateTime;
import com.record.utils.GeneralUtils;
import com.record.utils.LogUtils;
import com.record.utils.PreferUtils;
import com.record.utils.RemindUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogEdit;
import com.record.utils.dialog.DialogEdit.OnClickListener;
import com.record.utils.dialog.DialogUtils;
import com.record.utils.sound.Sound;
import com.record.utils.tomato.TomatoController;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.Calendar;

public class TomatoActivity extends BaseActivity {
    public static final int SUTDY_TIME_MAX_VALUE = 60;
    public static final int SUTDY_TIME_MIN_VALUE = 1;
    static String TAG = "override";
    public static final int TOMATO_TYPE_REST = 2;
    public static final int TOMATO_TYPE_STUDY = 1;
    static SharedPreferences sp = null;
    AnimationController animationController;
    AlertDialog closeDialog = null;
    Context context;
    OnClickListener custonStudyTimeListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which, EditText et) {
            if (et != null) {
                try {
                    double number = Double.parseDouble(et.getText().toString());
                    if (number > 60.0d) {
                        ToastUtils.toastShort(TomatoActivity.this.context, TomatoActivity.this.getString(R.string.str_part_cant_more_than) + 60 + TomatoActivity.this.getString(R.string.str_minute));
                    } else if (number < 1.0d) {
                        ToastUtils.toastShort(TomatoActivity.this.context, TomatoActivity.this.getString(R.string.str_part_cant_less_than) + 1 + TomatoActivity.this.getString(R.string.str_minute));
                    } else {
                        PreferUtils.putInt(TomatoActivity.this.context, Val.CONFIGURE_TOMATO_STUDY_TIME, (int) number);
                        TomatoActivity.this.onResume();
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    ToastUtils.toastShort(TomatoActivity.this.context, TomatoActivity.this.getString(R.string.str_please_input_valid_number));
                }
            }
        }
    };
    ImageView iv_big_line;
    ImageView iv_rest_bg;
    ImageView iv_small_line;
    ImageView iv_tomato_help;
    ImageView iv_tomato_stop_counter;
    ImageView iv_tomato_sun_light;
    String mCustomRingtone = null;
    View.OnClickListener myClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_tomato_help) {
                TomatoActivity.this.showSetTimeDialog_v2();
                MobclickAgent.onEvent(TomatoActivity.this.getApplicationContext(), "today_tomato_click_select_a_type_btn");
            } else if (id == R.id.rl_tomato_big_circle_inside) {
                TomatoActivity.this.clickBigCircle();
            } else if (id == R.id.iv_tomato_stop_counter) {
                TomatoActivity.this.showStopCounterDialog();
                MobclickAgent.onEvent(TomatoActivity.this.getApplicationContext(), "today_tomato_click_close_touru_btn");
            } else if (id == R.id.iv_rest_bg) {
                TomatoActivity.this.clickSmallCircle();
            } else if (id == R.id.iv_tomato_sun_light) {
                TomatoActivity.this.switchSound();
                MobclickAgent.onEvent(TomatoActivity.this.getApplicationContext(), "today_tomato_click_activate_voice_btn");
            } else if (id == R.id.iv_unhandler_tomato) {
                UnhandlerTomatoActivity.startActivity(TomatoActivity.this.context);
            }
        }
    };
    BroadcastReceiver mybroReceiver;
    String[] restTimeArr = null;
    int[] restTimeArrInt = new int[]{5, 10, 15};
    RelativeLayout rl_tomato_big_circle;
    RelativeLayout rl_tomato_big_circle_inside;
    RelativeLayout rl_tomato_body;
    TomatosView rl_tomato_progress;
    TextView rl_tomato_size;
    RelativeLayout rl_tomato_small_circle;
    String[] setStrArr = null;
    String[] studyTimeArr = null;
    int[] studyTimeArrInt = new int[]{25, 30, 35, 40, 45, 50};
    TextView tv_tomato_big_circle_name;
    TextView tv_tomato_big_circle_time;
    TextView tv_tomato_small_circle_rest;
    UiComponent uiComponent;
    WakeLock wakeLock;

    class UiComponent {
        ImageView iv_unhandler_tomato;

        UiComponent() {
        }
    }

    class mybroad extends BroadcastReceiver {
        mybroad() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Val.INTENT_ACTION_REMIND_TOMOTO_STOP.equals(action)) {
                TomatoActivity.this.stopCounter(false);
            } else if (Val.ACTION_TOMOTO_COUNTING.equals(action)) {
                TomatoActivity.this.tv_tomato_big_circle_time.setText(intent.getStringExtra(TomatoController.TOMATO_DISPLAY_TIME));
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomato);
        init();
        initView();
    }

    private void updateUiTomato() {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and startTime >= '" + DateTime.getDateStringZero() + "' and isRecord  = 3 and isEnd = 1 and actType = 11", null);
        int count = cursor.getCount();
        if (count > 0) {
            this.rl_tomato_progress.setTomatoSize(count);
            this.rl_tomato_progress.setVisibility(0);
            this.rl_tomato_size.setText(count + "");
        }
        DbUtils.close(cursor);
    }

    private void init() {
        this.context = getParent();
        TAG += getClass().getSimpleName();
        this.animationController = new AnimationController();
        if (TimerService.getInstance() == null) {
            startService(new Intent(this.context, TimerService.class));
        }
        getWakeLock();
    }

    private WakeLock getWakeLock() {
        if (this.wakeLock == null) {
            this.wakeLock = ((PowerManager) getSystemService("power")).newWakeLock(536870922, "WakeLockActivity");
        }
        return this.wakeLock;
    }

    private void initView() {
        this.uiComponent = new UiComponent();
        setUicomponent(this.uiComponent);
        this.tv_tomato_big_circle_time = (TextView) findViewById(R.id.tv_tomato_big_circle_time);
        this.tv_tomato_big_circle_name = (TextView) findViewById(R.id.tv_tomato_big_circle_name);
        this.tv_tomato_small_circle_rest = (TextView) findViewById(R.id.tv_tomato_small_circle_rest);
        this.rl_tomato_size = (TextView) findViewById(R.id.rl_tomato_size);
        this.iv_tomato_help = (ImageView) findViewById(R.id.iv_tomato_help);
        this.iv_tomato_stop_counter = (ImageView) findViewById(R.id.iv_tomato_stop_counter);
        this.rl_tomato_big_circle = (RelativeLayout) findViewById(R.id.rl_tomato_big_circle);
        this.rl_tomato_small_circle = (RelativeLayout) findViewById(R.id.rl_tomato_small_circle);
        this.iv_small_line = (ImageView) findViewById(R.id.iv_small_line);
        this.iv_tomato_sun_light = (ImageView) findViewById(R.id.iv_tomato_sun_light);
        this.iv_rest_bg = (ImageView) findViewById(R.id.iv_rest_bg);
        this.rl_tomato_big_circle_inside = (RelativeLayout) findViewById(R.id.rl_tomato_big_circle_inside);
        this.iv_big_line = (ImageView) findViewById(R.id.iv_big_line);
        this.rl_tomato_progress = (TomatosView) findViewById(R.id.rl_tomato_progress);
        this.rl_tomato_big_circle_inside.setOnClickListener(this.myClickListener);
        this.iv_tomato_stop_counter.setOnClickListener(this.myClickListener);
        this.iv_tomato_sun_light.setOnClickListener(this.myClickListener);
        this.iv_tomato_help.setOnClickListener(this.myClickListener);
        this.iv_rest_bg.setOnClickListener(this.myClickListener);
        this.uiComponent.iv_unhandler_tomato.setOnClickListener(this.myClickListener);
        if (this.mybroReceiver == null) {
            this.mybroReceiver = new mybroad();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Val.INTENT_ACTION_REMIND_TOMOTO_STOP);
            filter.addAction(Val.ACTION_TOMOTO_COUNTING);
            registerReceiver(this.mybroReceiver, filter);
        }
    }

    private void setUicomponent(UiComponent uiComponent) {
        uiComponent.iv_unhandler_tomato = (ImageView) findViewById(R.id.iv_unhandler_tomato);
    }

    private void updateUiNoCounting() {
        int defaultMin = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25);
        this.iv_tomato_stop_counter.setVisibility(8);
        this.tv_tomato_big_circle_time.setText(getString(R.string.str_study));
        this.tv_tomato_big_circle_time.setVisibility(0);
        this.tv_tomato_big_circle_name.setText(defaultMin + "min");
        this.tv_tomato_big_circle_name.setVisibility(0);
        this.rl_tomato_big_circle_inside.setBackgroundResource(R.drawable.x_circle_blue_1);
        this.iv_big_line.setBackgroundColor(getResources().getColor(R.color.bg_blue1));
        this.rl_tomato_small_circle.setVisibility(0);
        this.tv_tomato_small_circle_rest.setText(getString(R.string.str_rest));
        this.iv_rest_bg.setImageResource(R.drawable.x_circle_yellow_1);
        this.iv_small_line.setVisibility(0);
        this.iv_small_line.setBackgroundColor(getResources().getColor(R.color.bg_yellow1));
    }

    private void updateUiToCountingStudy() {
        this.tv_tomato_big_circle_time.setVisibility(0);
        this.tv_tomato_big_circle_name.setText(getString(R.string.str_study));
        this.tv_tomato_big_circle_name.setVisibility(0);
        this.iv_tomato_stop_counter.setVisibility(0);
        this.iv_big_line.setBackgroundColor(getResources().getColor(R.color.bg_blue1));
        this.rl_tomato_big_circle_inside.setBackgroundResource(R.drawable.x_circle_blue_1);
        this.rl_tomato_small_circle.setVisibility(8);
        this.tv_tomato_small_circle_rest.setText(getString(R.string.str_rest));
        this.iv_rest_bg.setImageResource(R.drawable.x_circle_yellow_1);
        this.iv_small_line.setVisibility(0);
        this.iv_small_line.setBackgroundColor(getResources().getColor(R.color.bg_yellow1));
    }

    private void updateUiToSubmit() {
        this.iv_tomato_stop_counter.setVisibility(8);
        this.tv_tomato_big_circle_time.setText(getString(R.string.str_submit));
        this.tv_tomato_big_circle_time.setVisibility(0);
        this.tv_tomato_big_circle_name.setVisibility(8);
        this.tv_tomato_big_circle_name.setText("");
        this.rl_tomato_big_circle_inside.setBackgroundResource(R.drawable.x_circle_green_1);
        this.iv_big_line.setBackgroundColor(getResources().getColor(R.color.bg_green1));
        this.tv_tomato_small_circle_rest.setText(getString(R.string.str_give_up));
        this.rl_tomato_small_circle.setVisibility(0);
        this.iv_rest_bg.setImageResource(R.drawable.x_circle_blue_1);
        this.iv_small_line.setVisibility(0);
        this.iv_small_line.setBackgroundColor(getResources().getColor(R.color.bg_blue1));
    }

    private void updateUiToCountingRest() {
        this.tv_tomato_big_circle_time.setVisibility(0);
        this.tv_tomato_big_circle_name.setText(getString(R.string.str_Rest));
        this.tv_tomato_big_circle_name.setVisibility(0);
        this.iv_tomato_stop_counter.setVisibility(0);
        this.iv_big_line.setBackgroundColor(getResources().getColor(R.color.bg_yellow1));
        this.rl_tomato_big_circle_inside.setBackgroundResource(R.drawable.x_circle_yellow_1);
        this.rl_tomato_small_circle.setVisibility(8);
        this.tv_tomato_small_circle_rest.setText(getString(R.string.str_rest));
        this.iv_rest_bg.setImageResource(R.drawable.x_circle_yellow_1);
        this.iv_small_line.setVisibility(0);
        this.iv_small_line.setBackgroundColor(getResources().getColor(R.color.bg_yellow1));
    }

    private void updateUiToCountingStudyWithAnim() {
        this.tv_tomato_big_circle_name.setText(getString(R.string.str_study));
        this.animationController.fadeIn(this.tv_tomato_big_circle_time, 300, 0);
        this.animationController.fadeIn(this.tv_tomato_big_circle_name, 500, 0);
        this.animationController.fadeIn(this.iv_tomato_stop_counter, 500, 0);
        this.animationController.slideFadeOut_down(this.rl_tomato_small_circle, 500, 0);
        logfile("updateUiToCountingStudyWithAnim-开始学习，更新界面");
        updateUiToCountingStudy();
    }

    private void updateUiToCountingRestWithAnim() {
        this.tv_tomato_big_circle_name.setText(getString(R.string.str_Rest));
        this.animationController.fadeIn(this.tv_tomato_big_circle_time, 300, 0);
        this.animationController.fadeIn(this.tv_tomato_big_circle_name, 500, 0);
        this.animationController.fadeIn(this.iv_tomato_stop_counter, 500, 0);
        this.animationController.fadeOut(this.rl_tomato_small_circle, 500, 0);
        updateUiToCountingRest();
    }

    private void updateUiToStopCounterWithAnim() {
        this.animationController.fadeIn(this.tv_tomato_big_circle_time, 300, 0);
        this.animationController.fadeOut(this.iv_tomato_stop_counter, 500, 0);
        this.animationController.fadeIn(this.tv_tomato_big_circle_name, 500, 0);
        this.animationController.slideFadeIn_up(this.rl_tomato_small_circle, 500, 0);
        updateUiNoCounting();
    }

    private void switchSound() {
        if (getSp(this.context).getInt(Val.CONFIGURE_TOMATO_IS_RING, 1) > 0) {
            PreferUtils.putInt(this.context, Val.CONFIGURE_TOMATO_IS_RING, 0);
        } else {
            PreferUtils.putInt(this.context, Val.CONFIGURE_TOMATO_IS_RING, 1);
        }
        updateUiSound();
    }

    private void clickSmallCircle() {
        NoSubmitTomato noSubmitTomato = getNoSubmitTomatoBean(this.context);
        if (noSubmitTomato == null || noSubmitTomato.getType() <= 0) {
            if (getTomatoStartTime(this.context).length() == 0) {
                updateUiToCountingRestWithAnim();
                startCounter(Val.CONFIGURE_TOMATO_REST_TIME, getSp(this.context).getInt(Val.CONFIGURE_TOMATO_REST_TIME, 5) * 60, 2, true, 0, Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT);
            }
            closeStopDialogIfExist();
            MobclickAgent.onEvent(getApplicationContext(), "today_tomato_click_activate_rest_btn");
            return;
        }
        stopCounter(true);
        MobclickAgent.onEvent(getApplicationContext(), "today_tomato_click_activate_rest_btn_giveup_tomato");
    }

    private void closeStopDialogIfExist() {
        try {
            if (this.closeDialog != null && this.closeDialog.isShowing()) {
                this.closeDialog.dismiss();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void showStopCounterDialog() {
        CharSequence message = getString(R.string.str_is_give_up_tomato);
        String btnStr = "提交";
        int type = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_TYPE, 1);
        if (type == 1) {
            if (isSubmitTomatoAhead()) {
                return;
            }
        } else if (type == 2) {
            message = getString(R.string.str_is_give_up_rest);
        }
        try {
            this.closeDialog = new Builder(this.context).setTitle((int) R.string.str_prompt).setMessage(message).setPositiveButton((int) R.string.str_give_up, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    TomatoActivity.this.stopCounter(true);
                    dialog.cancel();
                }
            }).setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create();
            this.closeDialog.show();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public boolean isSubmitTomatoAhead() {
        try {
            if (getSp(this.context).getInt(Val.CONFIGURE_TOMATO_TYPE, 1) == 1) {
                String time = BaseApplication.getInstance().getPreferenceConfig().getString(Val.CONFIGURE_TOMATO_START_TIME, "");
                int studyLength = BaseApplication.getInstance().getPreferenceConfig().getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 0);
                if (time != null && time.length() > 0 && studyLength > 0) {
                    Calendar calendar = DateTime.pars2Calender(time);
                    calendar.add(12, studyLength);
                    if (DateTime.compare_time(calendar, Calendar.getInstance()) <= 0) {
                        showAheadSubmitDialog();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return false;
    }

    private void showAheadSubmitDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage((CharSequence) "干得不错！您已完成一个番茄时间，您可以提前提交这个番茄！\n\n提示：若临时做了别的事情，您也可选择放弃这个番茄。").setPositiveButton(getString(R.string.str_submit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                RemindTomatoActivity.startActivity(TomatoActivity.this.context, Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT, TomatoActivity.getTomatoStartTime(TomatoActivity.this.context), (double) (DateTime.cal_secBetween(TomatoActivity.getTomatoStartTime(TomatoActivity.this.context), DateTime.getTimeString()) / 60));
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNeutralButton(getString(R.string.str_give_up), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TomatoActivity.this.stopCounter(true);
                dialog.cancel();
            }
        }).create().show();
    }

    public static void resetPre(Context context) {
        RemindUtils.cancelTomatoRemindAll(context);
        getSp(context).edit().putString(Val.CONFIGURE_TOMATO_START_TIME, "").commit();
        getSp(context).edit().putInt(Val.CONFIGURE_TOMATO_TYPE, 0).commit();
        getSp(context).edit().putInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0).commit();
    }

    private void stopCounter(boolean isRestPre) {
        sendBroadcast(new Intent(Val.ACTION_TOMOTO_STOP));
        if (isRestPre) {
            resetPre(this.context);
        }
        updateUiToStopCounterWithAnim();
    }

    private void clickBigCircle() {
        logfile("clickBigCircle-点击");
        NoSubmitTomato noSubmitTomato = getNoSubmitTomatoBean(this.context);
        if (noSubmitTomato == null || noSubmitTomato.getType() <= 0) {
            if (getTomatoStartTime(this.context).length() == 0) {
                if (getSp(this.context).getInt(Val.CONFIGURE_TOMATO_IS_RING, 1) > 0 && new Sound(this.context).isMute()) {
                    ToastUtils.toastShort(this.context, "当前系统为静音状态，提醒声音无法播放哦！");
                }
                logfile("clickBigCircle-没有倒计时");
                updateUiToCountingStudyWithAnim();
                boolean z = true;
                startCounter(Val.CONFIGURE_TOMATO_STUDY_TIME, getSp(this.context).getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25) * 60, 1, z, 0, Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT);
            } else {
                logfile("clickBigCircle-正在倒计时");
            }
            closeStopDialogIfExist();
            MobclickAgent.onEvent(getApplicationContext(), "today_tomato_click_activate_touru_btn");
            return;
        }
        RemindTomatoActivity.startActivityLastTomato(this.context, noSubmitTomato.getTypeAction(), noSubmitTomato.getStartTime(), (double) noSubmitTomato.getTotalMin(), 1);
        MobclickAgent.onEvent(getApplicationContext(), "today_tomato_click_activate_touru_btn_submit_tomato");
        logfile("提交未提交番茄");
    }

    private void startCounter(String preName, int totalSecond, int type, boolean isNormalStart, int counter, String action) {
        if (isNormalStart) {
            logfile("startCounter-开始计时");
            setPre(this.context, totalSecond, type, action);
            Intent it = new Intent(Val.ACTION_TOMOTO_START);
            it.putExtra(TomatoController.TOMATO_TOTAL, totalSecond);
            it.putExtra(TomatoController.TOMATO_PASS_SEC, counter);
            this.context.sendBroadcast(it);
            logfile("startCounter-开始计时发送广播,totalSecond:" + totalSecond + ",counter" + counter);
            return;
        }
        logfile("startCounter-继承计时发送广播,totalSecond:" + totalSecond + ",counter" + counter);
        TomatoController.getTomatoController(this.context).startTomato(totalSecond, counter);
    }

    public static void setPre(Context context, int totalSecond, int type, String action) {
        getSp(context).edit().putString(Val.CONFIGURE_TOMATO_START_TIME, DateTime.getTimeString()).commit();
        getSp(context).edit().putInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0).commit();
        getSp(context).edit().putInt(Val.CONFIGURE_TOMATO_TYPE, type).commit();
    }

    public static String getTomatoStartTime(Context context) {
        return getSp(context).getString(Val.CONFIGURE_TOMATO_START_TIME, "");
    }

    private void showSetRestTimeDialog() {
        TomatoController tomatoController = TomatoController.getTomatoController(this.context);
        if (tomatoController.isCounting() && tomatoController.getTomatoType() == 2) {
            ToastUtils.toastShort(this.context, getString(R.string.str_stop_tomato_before_setting_time));
        } else {
            new Builder(this.context).setTitle(getString(R.string.str_choose)).setItems(getRestTimeArr(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PreferUtils.getSP(TomatoActivity.this.context).edit().putInt(Val.CONFIGURE_TOMATO_REST_TIME, TomatoActivity.this.restTimeArrInt[which]).commit();
                    dialog.cancel();
                }
            }).setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
    }

    private String[] getRestTimeArr() {
        if (this.restTimeArr == null) {
            String min = getString(R.string.str_minute);
            this.restTimeArr = new String[]{"5" + min, "10" + min, "15" + min};
        }
        return this.restTimeArr;
    }

    private void showSetStudyTimeDialog() {
        if (TomatoController.getTomatoController(this.context).isCounting()) {
            ToastUtils.toastShort(this.context, getString(R.string.str_stop_tomato_before_setting_time));
        } else {
            new Builder(this.context).setTitle(getString(R.string.str_choose)).setItems(getLearnTimeArr(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (TomatoActivity.this.getString(R.string.str_custom).equals(TomatoActivity.this.getLearnTimeArr()[which])) {
                        new DialogEdit(TomatoActivity.this.context, TomatoActivity.this.getString(R.string.str_input), TomatoActivity.this.getString(R.string.str_unit_min), 2, TomatoActivity.this.custonStudyTimeListener).show();
                    } else {
                        PreferUtils.putInt(TomatoActivity.this.context, Val.CONFIGURE_TOMATO_STUDY_TIME, TomatoActivity.this.studyTimeArrInt[which]);
                        TomatoActivity.this.onResume();
                    }
                    dialog.cancel();
                }
            }).setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
    }

    private String[] getLearnTimeArr() {
        if (this.studyTimeArr == null) {
            String min = getString(R.string.str_minute);
            String hour = getString(R.string.str_hour);
            this.studyTimeArr = new String[]{"25" + min, "30" + min, "35" + min, "40" + min, "45" + min, "50" + min, getString(R.string.str_custom)};
        }
        return this.studyTimeArr;
    }

    private String[] getSetStrArr() {
        if (this.setStrArr == null) {
            this.setStrArr = new String[]{getString(R.string.str_learn_time), getString(R.string.str_rest_time), getString(R.string.str_remind_rest_ring), getString(R.string.str_remind_learn_ring), getString(R.string.str_reset_tomato), getString(R.string.str_help)};
        }
        return this.setStrArr;
    }

    private void showSetTimeDialog_v2() {
        try {
            new Builder(this.context).setTitle(getString(R.string.str_choose_type)).setItems(getSetStrArr(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        TomatoActivity.this.showSetStudyTimeDialog();
                        MobclickAgent.onEvent(TomatoActivity.this.context, "today_tomato_setting_select_study_time");
                    } else if (which == 1) {
                        TomatoActivity.this.showSetRestTimeDialog();
                        MobclickAgent.onEvent(TomatoActivity.this.context, "today_tomato_setting_select_rest_time");
                    } else if (which == 2) {
                        TomatoActivity.this.doPickRingtone(19);
                        MobclickAgent.onEvent(TomatoActivity.this.context, "today_tomato_setting_study_ring");
                    } else if (which == 3) {
                        TomatoActivity.this.doPickRingtone(20);
                        MobclickAgent.onEvent(TomatoActivity.this.context, "today_tomato_setting_rest_ring");
                    } else if (which == 4) {
                        TomatoActivity.this.showResetTomatoDialog();
                        MobclickAgent.onEvent(TomatoActivity.this.context, "today_tomato_setting_reset_tomato");
                    } else if (which == 5) {
                        DialogUtils.showPrompt(TomatoActivity.this.context, TomatoActivity.this.getString(R.string.str_tomato_prompt));
                        MobclickAgent.onEvent(TomatoActivity.this.context, "today_tomato_setting_help");
                    }
                    dialog.cancel();
                }
            }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        } catch (Exception e) {
            DbUtils.exceptionHandler(this.context, e);
        }
    }

    protected void showResetTomatoDialog() {
        new Builder(this.context).setTitle((int) R.string.str_is_reset_tomato).setMessage((int) R.string.str_reset_tomato_prompt).setPositiveButton(getString(R.string.str_reset), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sp = TomatoActivity.getSp(TomatoActivity.this.context);
                sp.edit().putString(Val.CONFIGURE_TOMATO_LEARN_TIME_OUT_RINGTONE, "").commit();
                sp.edit().putString(Val.CONFIGURE_TOMATO_REST_TIME_OUT_RINGTONE, "").commit();
                sp.edit().putInt(Val.CONFIGURE_TOMATO_REST_TIME, 5).commit();
                sp.edit().putInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25).commit();
                GeneralUtils.toastShort(TomatoActivity.this.context, TomatoActivity.this.getString(R.string.str_reset_successfully));
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showSetTimeDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_choose_type)).setPositiveButton(getString(R.string.str_study), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TomatoActivity.this.showSetStudyTimeDialog();
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_Rest), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TomatoActivity.this.showSetRestTimeDialog();
                dialog.cancel();
            }
        }).create().show();
    }

    private void doPickRingtone(int action) {
        Uri ringtoneUri;
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", true);
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", false);
        if (action == 19) {
            this.mCustomRingtone = getSp(this.context).getString(Val.CONFIGURE_TOMATO_LEARN_TIME_OUT_RINGTONE, "");
        } else {
            this.mCustomRingtone = getSp(this.context).getString(Val.CONFIGURE_TOMATO_REST_TIME_OUT_RINGTONE, "");
        }
        if (this.mCustomRingtone == null || this.mCustomRingtone.length() <= 0) {
            ringtoneUri = RingtoneManager.getDefaultUri(2);
        } else {
            ringtoneUri = Uri.parse(this.mCustomRingtone);
        }
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", ringtoneUri);
        startActivityForResult(intent, action);
    }

    private static SharedPreferences getSp(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return sp;
    }

    public static NoSubmitTomato getNoSubmitTomatoBean(Context context) {
        NoSubmitTomato noSubmitTomato = null;
        String startTime = getSp(context).getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        if (startTime == null || startTime.length() == 0) {
            return null;
        }
        String now = DateTime.getTimeString();
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
        if ((type == 2 || type == 1) && hadPassSec >= totalTime) {
            noSubmitTomato = new NoSubmitTomato(type, startTime, endTime, defaultSec, delaySec);
        }
        return noSubmitTomato;
    }

    private void isContinue() {
        if (getNoSubmitTomatoBean(this.context) != null) {
            updateUiToSubmit();
            return;
        }
        String startTime = getSp(this.context).getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        log("isContinue番茄开时间:" + startTime);
        if (startTime == null || startTime.length() <= 0) {
            stopCounter(false);
            updateUiNoCounting();
            return;
        }
        try {
            int totalTime;
            int hadPass = DateTime.cal_secBetween(startTime, DateTime.getTimeString());
            int type = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_TYPE, 0);
            int defaultMin;
            int delaySec;
            if (type == 2) {
                defaultMin = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_REST_TIME, 5);
                delaySec = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
                totalTime = (defaultMin * 60) + delaySec;
                log("isContinue番茄开时间:" + startTime + "，defaultMin：" + defaultMin + "," + delaySec + "," + type);
            } else if (type == 1) {
                defaultMin = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25);
                delaySec = getSp(this.context).getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
                totalTime = (defaultMin * 60) + delaySec;
                log("isContinue番茄开时间:" + startTime + "1defaultMin：" + defaultMin + "" + delaySec + "" + type);
            } else {
                stopCounter(true);
                return;
            }
            if (hadPass >= totalTime) {
                if (type == 1) {
                    saveTomato();
                }
                stopCounter(true);
            } else if (type == 2) {
                updateUiToCountingRest();
                startCounter(Val.CONFIGURE_TOMATO_REST_TIME, totalTime, 2, false, hadPass, "");
            } else if (type == 1) {
                updateUiToCountingStudy();
                startCounter(Val.CONFIGURE_TOMATO_STUDY_TIME, totalTime, 1, false, hadPass, "");
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            stopCounter(true);
        }
    }

    private void updateUiSound() {
        if (getSp(this.context).getInt(Val.CONFIGURE_TOMATO_IS_RING, 1) == 0) {
            this.iv_tomato_sun_light.setImageResource(R.drawable.ic_sound_off);
        } else {
            this.iv_tomato_sun_light.setImageResource(R.drawable.ic_sound_on);
        }
    }

    private void saveTomato() {
        int delaySecond = sp.getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
        int studyMinute = sp.getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25);
        String start = sp.getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        if (start != null && start.length() > 0) {
            Cursor cursor = DbUtils.getDb(this).rawQuery("select Id from t_unhandler_tomato where startTime = '" + start + "'", null);
            if (cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("startTime", start);
                values.put("length", Double.valueOf((((double) delaySecond) / 60.0d) + ((double) studyMinute)));
                values.put(a.a, Integer.valueOf(1));
                DbUtils.getDb(this).insert("t_unhandler_tomato", null, values);
            }
            DbUtils.close(cursor);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            switch (requestCode) {
                case 19:
                    handleRingtonePicked((Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI"), 19);
                    return;
                case 20:
                    handleRingtonePicked((Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI"), 20);
                    return;
                default:
                    return;
            }
        }
    }

    private void handleRingtonePicked(Uri pickedUri, int type) {
        if (pickedUri == null || RingtoneManager.isDefault(pickedUri)) {
            this.mCustomRingtone = null;
        } else {
            this.mCustomRingtone = pickedUri.toString();
        }
        if (type == 19) {
            getSp(this.context).edit().putString(Val.CONFIGURE_TOMATO_LEARN_TIME_OUT_RINGTONE, this.mCustomRingtone).commit();
        } else {
            getSp(this.context).edit().putString(Val.CONFIGURE_TOMATO_REST_TIME_OUT_RINGTONE, this.mCustomRingtone).commit();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        try {
            isContinue();
            updateUiTomato();
            showUnhandlerTomato();
            updateUiSound();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void showUnhandlerTomato() {
        if (DbUtils.queryUnhandlerTomatoCount(this) > 0) {
            this.uiComponent.iv_unhandler_tomato.setVisibility(0);
        } else {
            this.uiComponent.iv_unhandler_tomato.setVisibility(8);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void onDestroy() {
        try {
            if (this.mybroReceiver != null) {
                unregisterReceiver(this.mybroReceiver);
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        try {
            if (this.wakeLock != null && this.wakeLock.isHeld()) {
                this.wakeLock.release();
                this.wakeLock = null;
            }
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
        }
        super.onDestroy();
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }

    public static void logfile(String str) {
        LogUtils.logfile(TomatoActivity.class.getSimpleName() + str);
    }
}
