package com.record.myLife.settings.remind;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import com.alibaba.fastjson.asm.Opcodes;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.GeneralUtils;
import com.record.utils.PreferUtils;
import com.record.utils.RemindUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.TreeSet;

public class SetRemindActivity extends BaseActivity {
    Button btn_set_back;
    Button btn_set_morning;
    Button btn_set_remind_interval;
    Button btn_set_remind_interval_led;
    Button btn_set_remind_interval_prompt_dialog;
    Button btn_set_remind_interval_rest_clock;
    Button btn_set_remind_interval_shake;
    Button btn_set_remind_interval_sound;
    Button btn_set_remind_interval_time;
    Button btn_set_remind_learn_time;
    Button btn_set_remind_morning_pre;
    Button btn_set_remind_morning_time;
    Button btn_set_remind_rest;
    Button btn_set_remind_rest_all;
    Button btn_set_remind_rest_class_over;
    Button btn_set_remind_rest_class_start;
    Button btn_set_remind_rest_time;
    Button btn_set_remind_retrospection;
    Button btn_set_remind_retrospection_value;
    Button btn_set_summarize;
    Button btn_set_update_ui_interval;
    Context context;
    AnimationController controller;
    String[] hours24;
    boolean[] hours24Boolean;
    TreeSet<Integer> hours24Set;
    int[] intervaItemsVal = new int[]{15, 30, 60, Opcodes.GETFIELD};
    ImageView iv_set_remind_add_sleep_record;
    ImageView iv_set_remind_interval;
    ImageView iv_set_remind_morning;
    ImageView iv_set_remind_rest;
    ImageView iv_set_remind_rest_all;
    ImageView iv_set_remind_retrospection;
    String mCustomRingtone = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            final int[] restIntArr;
            if (id == R.id.iv_set_remind_add_sleep_record) {
                SetRemindActivity.this.switchAddSleepRecord();
            } else if (id == R.id.btn_set_remind_retrospection) {
                GeneralUtils.toastLong(SetRemindActivity.this.context, "每天在指定的时间提醒您进行总结！");
            } else if (id == R.id.btn_set_remind_retrospection_value) {
                SetRemindActivity.this.showSelectRemindTimeDialog(Val.CONFIGURE_REMIND_ADD_NOTE_TIME, "22:00");
            } else if (id == R.id.iv_set_remind_retrospection) {
                if (SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_IS_REMIND_ADD_NOTE, 1) > 0) {
                    RemindUtils.cancelRetroSpection(SetRemindActivity.this.context, Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER);
                    SetRemindActivity.this.iv_set_remind_retrospection.setImageResource(R.drawable.ic_off_v2);
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_ADD_NOTE, 0).commit();
                    SetRemindActivity.this.btn_set_remind_retrospection_value.setVisibility(8);
                    SetRemindActivity.this.btn_set_summarize.setVisibility(8);
                    GeneralUtils.toastShort(SetRemindActivity.this.context, "关闭 每日回顾 提醒！");
                    return;
                }
                String time = SetRemindActivity.this.getsp().getString(Val.CONFIGURE_REMIND_ADD_NOTE_TIME, "22:00");
                SetRemindActivity.this.iv_set_remind_retrospection.setImageResource(R.drawable.ic_on_v2);
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_ADD_NOTE, 1).commit();
                SetRemindActivity.this.btn_set_remind_retrospection_value.setVisibility(0);
                SetRemindActivity.this.btn_set_summarize.setVisibility(0);
                RemindUtils.quickSetRetroSpection(SetRemindActivity.this.context);
                GeneralUtils.toastShort(SetRemindActivity.this.context, "开启 每日回顾 提醒!");
            } else if (id == R.id.iv_set_remind_rest) {
                if (SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_IS_REMIND_REST, 0) > 0) {
                    RemindUtils.cancelRemindRest(SetRemindActivity.this.context);
                    RemindUtils.cancleRemindInvest(SetRemindActivity.this.context);
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_REST, 0).commit();
                    GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_close) + SetRemindActivity.this.getString(R.string.str_remind_rest));
                    SetRemindActivity.this.updateUIRestOnOrOff(0);
                    return;
                }
                GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_open) + SetRemindActivity.this.getString(R.string.str_remind_rest));
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_REST, 1).commit();
                RemindUtils.quickSetRemindRest2(SetRemindActivity.this.context);
                SetRemindActivity.this.updateUIRestOnOrOff(1);
            } else if (id == R.id.iv_set_remind_rest_all) {
                if (SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_IS_REMIND_REST_WHOLE, 1) > 0) {
                    SetRemindActivity.this.iv_set_remind_rest_all.setImageResource(R.drawable.ic_off_v2);
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_REST_WHOLE, 0).commit();
                    GeneralUtils.toastShort(SetRemindActivity.this.context, "关闭 全局！");
                    return;
                }
                SetRemindActivity.this.iv_set_remind_rest_all.setImageResource(R.drawable.ic_on_v2);
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_REST_WHOLE, 1).commit();
                GeneralUtils.toastShort(SetRemindActivity.this.context, "开启 全局！");
            } else if (id == R.id.btn_set_back) {
                SetRemindActivity.this.finish();
                SetRemindActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.btn_set_remind_rest) {
                GeneralUtils.toastLong(SetRemindActivity.this.context, "开启后，会隔一定时间提醒您休息。");
            } else if (id == R.id.btn_set_remind_rest_time) {
                restIntArr = new int[]{5, 10};
                new Builder(SetRemindActivity.this.context).setTitle((CharSequence) "休息").setNeutralButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setItems(new String[]{"5分钟", "10分钟"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int temp = restIntArr[which];
                        if (temp != SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_REMIND_REST_REST_TIME, 5)) {
                            SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_REST_TIME, temp).commit();
                            GeneralUtils.toastShort(SetRemindActivity.this.context, "每次休息" + temp + "分钟！");
                        }
                        SetRemindActivity.this.btn_set_remind_rest_time.setText(SetRemindActivity.this.getResources().getString(R.string.str_Rest) + ":" + temp + SetRemindActivity.this.getResources().getString(R.string.str_minute_short));
                        dialog.cancel();
                    }
                }).create().show();
            } else if (id == R.id.btn_set_remind_learn_time) {
                restIntArr = new int[]{25, 30, 35, 40, 45, 50};
                new Builder(SetRemindActivity.this.context).setTitle((CharSequence) "学习").setNeutralButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setItems(new String[]{"25分钟", "30分钟", "35分钟", "40分钟", "45分钟", "50分钟"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int temp = restIntArr[which];
                        if (temp != SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_REMIND_REST_LEARN_TIME, 45)) {
                            SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_LEARN_TIME, temp).commit();
                            GeneralUtils.toastShort(SetRemindActivity.this.context, "每次学习" + temp + "分钟！");
                            RemindUtils.quickSetRemindRest2(SetRemindActivity.this.context);
                        }
                        SetRemindActivity.this.btn_set_remind_learn_time.setText(SetRemindActivity.this.getResources().getString(R.string.str_study) + ":" + temp + SetRemindActivity.this.getResources().getString(R.string.str_minute_short));
                        dialog.cancel();
                    }
                }).create().show();
            } else if (id == R.id.iv_set_remind_morning) {
                if (SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_IS_REMIND_ADD_MONING_NOTE, 0) > 0) {
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_ADD_MONING_NOTE, 0).commit();
                    RemindUtils.cancelRetroSpection(SetRemindActivity.this.context, Val.INTENT_ACTION_NOTI_MORNING_VOICE);
                    SetRemindActivity.this.updateMorningVoiceUi();
                    GeneralUtils.toastShort(SetRemindActivity.this.context, "晨音提醒关闭！");
                    return;
                }
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_ADD_MONING_NOTE, 1).commit();
                SetRemindActivity.this.updateMorningVoiceUi();
                RemindUtils.quickSetRemindMorningVoice(SetRemindActivity.this.context);
                GeneralUtils.toastShort(SetRemindActivity.this.context, "晨音提醒开启！");
            } else if (id == R.id.btn_set_remind_morning_time) {
                SetRemindActivity.this.showSelectRemindTimeDialog(Val.CONFIGURE_REMIND_MORNING_VOICE_TIME, Val.CONFIGURE_REMIND_MORNING_VOICE_TIME_DEFAULT);
            } else if (id == R.id.btn_set_remind_morning_pre) {
                GeneralUtils.toastLong(SetRemindActivity.this.context, "开启后，在您起床时，提醒您记录当时想法！");
            } else if (id == R.id.btn_set_update_ui_interval) {
                SetRemindActivity.this.showSetUpdateTime();
            } else if (id == R.id.rl_set_remind_rest_all) {
            } else {
                if (id == R.id.iv_set_remind_interval) {
                    SetRemindActivity.this.switchRemindInterval();
                } else if (id == R.id.btn_set_remind_interval_shake) {
                    SetRemindActivity.this.showChooseIntervalShakeDialog();
                } else if (id == R.id.btn_set_remind_interval_sound) {
                    SetRemindActivity.this.showChooseIntervalSoundDialog();
                } else if (id == R.id.btn_set_remind_interval_time) {
                    SetRemindActivity.this.showChooseIntervalTimeDialog();
                } else if (id == R.id.btn_set_remind_interval) {
                    GeneralUtils.toastLong(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_shake_or_sound_promt));
                } else if (id == R.id.btn_set_remind_interval_rest_clock) {
                    SetRemindActivity.this.showChooseNoRingDialog();
                } else if (id == R.id.btn_set_remind_interval_led) {
                    SetRemindActivity.this.showChooseIntervalLEDDialog();
                } else if (id == R.id.btn_set_summarize) {
                    SetRemindActivity.this.setSummarizePromptDialog(Val.CONFIGURE_SUMMARIZE_PROMPT);
                } else if (id == R.id.btn_set_morning) {
                    SetRemindActivity.this.setSummarizePromptDialog(Val.CONFIGURE_MORNING_VOICE_PROMPT);
                } else if (id == R.id.btn_set_remind_rest_all) {
                    GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getStr(R.string.str_remind_rest_globe_prompt));
                } else if (R.id.btn_set_remind_interval_prompt_dialog == id) {
                    SetRemindActivity.this.changeIntervalShowDialog();
                } else if (R.id.btn_set_remind_rest_class_start == id) {
                    SetRemindActivity.this.changeClassStartRing(22);
                } else if (R.id.btn_set_remind_rest_class_over == id) {
                    SetRemindActivity.this.changeClassStartRing(23);
                }
            }
        }
    };
    RelativeLayout rl_set_remind_interval_items;
    RelativeLayout rl_set_remind_rest;
    RelativeLayout rl_set_remind_rest_all;
    RelativeLayout rl_set_remind_rest_sound;
    RelativeLayout rl_set_remind_rest_time;
    SharedPreferences sp;
    EditText template_edit_text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remind);
        this.context = this;
        this.sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        this.controller = new AnimationController();
        SystemBarTintManager.setMIUIbar(this);
        this.btn_set_remind_retrospection = (Button) findViewById(R.id.btn_set_remind_retrospection);
        this.btn_set_remind_learn_time = (Button) findViewById(R.id.btn_set_remind_learn_time);
        this.btn_set_remind_rest_time = (Button) findViewById(R.id.btn_set_remind_rest_time);
        this.rl_set_remind_rest_all = (RelativeLayout) findViewById(R.id.rl_set_remind_rest_all);
        this.iv_set_remind_retrospection = (ImageView) findViewById(R.id.iv_set_remind_retrospection);
        this.iv_set_remind_rest = (ImageView) findViewById(R.id.iv_set_remind_rest);
        this.iv_set_remind_rest_all = (ImageView) findViewById(R.id.iv_set_remind_rest_all);
        this.rl_set_remind_rest_time = (RelativeLayout) findViewById(R.id.rl_set_remind_rest_time);
        this.rl_set_remind_rest_sound = (RelativeLayout) findViewById(R.id.rl_set_remind_rest_sound);
        this.btn_set_remind_rest_class_over = (Button) findViewById(R.id.btn_set_remind_rest_class_over);
        this.btn_set_remind_rest_class_start = (Button) findViewById(R.id.btn_set_remind_rest_class_start);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_set_remind_retrospection_value = (Button) findViewById(R.id.btn_set_remind_retrospection_value);
        this.btn_set_remind_rest = (Button) findViewById(R.id.btn_set_remind_rest);
        this.btn_set_update_ui_interval = (Button) findViewById(R.id.btn_set_update_ui_interval);
        this.btn_set_remind_morning_pre = (Button) findViewById(R.id.btn_set_remind_morning_pre);
        this.btn_set_remind_interval = (Button) findViewById(R.id.btn_set_remind_interval);
        this.btn_set_remind_interval_shake = (Button) findViewById(R.id.btn_set_remind_interval_shake);
        this.btn_set_remind_interval_sound = (Button) findViewById(R.id.btn_set_remind_interval_sound);
        this.btn_set_remind_interval_time = (Button) findViewById(R.id.btn_set_remind_interval_time);
        this.btn_set_remind_interval_rest_clock = (Button) findViewById(R.id.btn_set_remind_interval_rest_clock);
        this.btn_set_remind_interval_led = (Button) findViewById(R.id.btn_set_remind_interval_led);
        this.btn_set_summarize = (Button) findViewById(R.id.btn_set_summarize);
        this.btn_set_morning = (Button) findViewById(R.id.btn_set_morning);
        this.btn_set_remind_morning_time = (Button) findViewById(R.id.btn_set_remind_morning_time);
        this.btn_set_remind_rest_all = (Button) findViewById(R.id.btn_set_remind_rest_all);
        this.btn_set_remind_interval_prompt_dialog = (Button) findViewById(R.id.btn_set_remind_interval_prompt_dialog);
        this.iv_set_remind_morning = (ImageView) findViewById(R.id.iv_set_remind_morning);
        this.iv_set_remind_interval = (ImageView) findViewById(R.id.iv_set_remind_interval);
        this.rl_set_remind_interval_items = (RelativeLayout) findViewById(R.id.rl_set_remind_interval_items);
        this.rl_set_remind_rest = (RelativeLayout) findViewById(R.id.rl_set_remind_rest);
        this.iv_set_remind_add_sleep_record = (ImageView) findViewById(R.id.iv_set_remind_add_sleep_record);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.btn_set_remind_retrospection.setOnClickListener(this.myClickListener);
        this.iv_set_remind_retrospection.setOnClickListener(this.myClickListener);
        this.iv_set_remind_rest.setOnClickListener(this.myClickListener);
        this.btn_set_remind_rest_time.setOnClickListener(this.myClickListener);
        this.btn_set_remind_learn_time.setOnClickListener(this.myClickListener);
        this.btn_set_remind_retrospection_value.setOnClickListener(this.myClickListener);
        this.btn_set_remind_rest.setOnClickListener(this.myClickListener);
        this.iv_set_remind_morning.setOnClickListener(this.myClickListener);
        this.btn_set_remind_morning_pre.setOnClickListener(this.myClickListener);
        this.rl_set_remind_rest_all.setOnClickListener(this.myClickListener);
        this.iv_set_remind_rest_all.setOnClickListener(this.myClickListener);
        this.btn_set_update_ui_interval.setOnClickListener(this.myClickListener);
        this.iv_set_remind_interval.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval_time.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval_sound.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval_shake.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval_rest_clock.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval_led.setOnClickListener(this.myClickListener);
        this.btn_set_summarize.setOnClickListener(this.myClickListener);
        this.btn_set_morning.setOnClickListener(this.myClickListener);
        this.btn_set_remind_rest_all.setOnClickListener(this.myClickListener);
        this.btn_set_remind_interval_prompt_dialog.setOnClickListener(this.myClickListener);
        this.btn_set_remind_rest_class_over.setOnClickListener(this.myClickListener);
        this.btn_set_remind_rest_class_start.setOnClickListener(this.myClickListener);
        this.btn_set_remind_morning_time.setOnClickListener(this.myClickListener);
        this.iv_set_remind_add_sleep_record.setOnClickListener(this.myClickListener);
        initSetUI();
        try {
            getSharedPreferences(Val.CONFIGURE_NAME_DOT, 2).edit().putInt(Val.CONFIGURE_SET_REMIND_DOT, 5).commit();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void initSetUI() {
        String str;
        updateAddSleepRecordUi();
        updateMorningVoiceUi();
        updateUIWithSwithInterval(getsp().getInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 0));
        int CONFIGURE_REMIND_INTERVAL_VAL = getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_VAL, 30);
        if (CONFIGURE_REMIND_INTERVAL_VAL >= 60) {
            this.btn_set_remind_interval_time.setText(getString(R.string.str_interval) + ":" + (CONFIGURE_REMIND_INTERVAL_VAL / 60) + getString(R.string.str_hour));
        } else {
            this.btn_set_remind_interval_time.setText(getString(R.string.str_interval) + ":" + CONFIGURE_REMIND_INTERVAL_VAL + getString(R.string.str_minute));
        }
        if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1) > 0) {
            this.btn_set_remind_interval_shake.setText(getString(R.string.str_shake) + ":" + getString(R.string.str_on));
        } else {
            this.btn_set_remind_interval_shake.setText(getString(R.string.str_shake) + ":" + getString(R.string.str_off));
        }
        if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1) > 0) {
            this.btn_set_remind_interval_sound.setText(getString(R.string.str_sound2) + ":" + getString(R.string.str_on));
        } else {
            this.btn_set_remind_interval_sound.setText(getString(R.string.str_sound2) + ":" + getString(R.string.str_off));
        }
        String noRingHour = getsp().getString(Val.CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR, "23,0,1,2,3,4,5,6,7");
        if (noRingHour == null || noRingHour.length() <= 0) {
            this.btn_set_remind_interval_rest_clock.setText(getString(R.string.str_none));
        } else {
            this.btn_set_remind_interval_rest_clock.setText(getString(R.string.str_rest_range) + ":" + noRingHour);
        }
        if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG, 0) > 0) {
            this.btn_set_remind_interval_prompt_dialog.setText(getString(R.string.str_interval_show_dialog) + ":" + getString(R.string.str_on));
        } else {
            this.btn_set_remind_interval_prompt_dialog.setText(getString(R.string.str_interval_show_dialog) + ":" + getString(R.string.str_off));
        }
        if (getsp().getInt(Val.CONFIGURE_IS_REMIND_ADD_NOTE, 1) > 0) {
            this.iv_set_remind_retrospection.setImageResource(R.drawable.ic_on_v2);
            this.btn_set_remind_retrospection_value.setVisibility(0);
            this.btn_set_summarize.setVisibility(0);
        } else {
            this.iv_set_remind_retrospection.setImageResource(R.drawable.ic_off_v2);
            this.btn_set_remind_retrospection_value.setVisibility(8);
            this.btn_set_summarize.setVisibility(8);
        }
        this.btn_set_remind_retrospection_value.setText(getResources().getString(R.string.str_remind_time) + ":" + getsp().getString(Val.CONFIGURE_REMIND_ADD_NOTE_TIME, "22:00"));
        updateUIRestOnOrOff(getsp().getInt(Val.CONFIGURE_IS_REMIND_REST, 0));
        if (getsp().getInt(Val.CONFIGURE_IS_REMIND_REST_WHOLE, 1) > 0) {
            this.iv_set_remind_rest_all.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_remind_rest_all.setImageResource(R.drawable.ic_off_v2);
        }
        this.btn_set_remind_learn_time.setText(getResources().getString(R.string.str_study) + ":" + getsp().getInt(Val.CONFIGURE_REMIND_REST_LEARN_TIME, 45) + getResources().getString(R.string.str_minute_short));
        this.btn_set_remind_rest_time.setText(getResources().getString(R.string.str_Rest) + ":" + getsp().getInt(Val.CONFIGURE_REMIND_REST_REST_TIME, 5) + getResources().getString(R.string.str_minute_short));
        String ON = getStr(R.string.str_on);
        String OFF = getStr(R.string.str_off);
        int CONFIGURE_REMIND_REST_CLASS_START = getsp().getInt(Val.CONFIGURE_REMIND_REST_CLASS_START, 0);
        StringBuilder append = new StringBuilder().append(getResources().getString(R.string.str_class_start_ring)).append(":");
        if (CONFIGURE_REMIND_REST_CLASS_START > 0) {
            str = ON;
        } else {
            str = OFF;
        }
        this.btn_set_remind_rest_class_start.setText(append.append(str).toString());
        int CONFIGURE_REMIND_REST_CLASS_OVER = getsp().getInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 0);
        StringBuilder append2 = new StringBuilder().append(getResources().getString(R.string.str_class_over_ring)).append(":");
        if (CONFIGURE_REMIND_REST_CLASS_OVER <= 0) {
            ON = OFF;
        }
        this.btn_set_remind_rest_class_over.setText(append2.append(ON).toString());
        int widgets = getsp().getInt(Val.CONFIGURE_UPDATE_WIDGETS_INTERAL, 5);
        if (widgets / 60 > 0) {
            this.btn_set_update_ui_interval.setText(getStr(R.string.str_update_ui_interval) + ":" + (widgets / 60) + getStr(R.string.str_hour));
        } else {
            this.btn_set_update_ui_interval.setText(getStr(R.string.str_update_ui_interval) + ":" + widgets + getStr(R.string.str_minute));
        }
    }

    private void updateMorningVoiceUi() {
        int REMIND_ADD_MONING_NOTE = getsp().getInt(Val.CONFIGURE_IS_REMIND_ADD_MONING_NOTE, 0);
        this.btn_set_remind_morning_time.setText(getStr(R.string.str_remind_time) + ":" + getsp().getString(Val.CONFIGURE_REMIND_MORNING_VOICE_TIME, Val.CONFIGURE_REMIND_MORNING_VOICE_TIME_DEFAULT));
        if (REMIND_ADD_MONING_NOTE > 0) {
            this.iv_set_remind_morning.setImageResource(R.drawable.ic_on_v2);
            this.btn_set_morning.setVisibility(0);
            this.btn_set_remind_morning_time.setVisibility(0);
            return;
        }
        this.iv_set_remind_morning.setImageResource(R.drawable.ic_off_v2);
        this.btn_set_morning.setVisibility(8);
        this.btn_set_remind_morning_time.setVisibility(8);
    }

    private void updateAddSleepRecordUi() {
        if (getsp().getInt(Val.CONFIGURE_IS_NOTI_ADD_SLEEP_DATE, 1) > 0) {
            this.iv_set_remind_add_sleep_record.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_remind_add_sleep_record.setImageResource(R.drawable.ic_off_v2);
        }
    }

    private void updateUIRestOnOrOff(int REMIND_RES) {
        if (REMIND_RES > 0) {
            this.iv_set_remind_rest.setImageResource(R.drawable.ic_on_v2);
            this.rl_set_remind_rest_time.setVisibility(0);
            this.rl_set_remind_rest_sound.setVisibility(0);
            this.rl_set_remind_rest_all.setVisibility(0);
            return;
        }
        this.iv_set_remind_rest.setImageResource(R.drawable.ic_off_v2);
        this.rl_set_remind_rest_time.setVisibility(8);
        this.rl_set_remind_rest_all.setVisibility(8);
        this.rl_set_remind_rest_sound.setVisibility(8);
    }

    private void updateUIWithSwithInterval(int CONFIGURE_IS_REMIND_INTERVAL) {
        if (CONFIGURE_IS_REMIND_INTERVAL > 0) {
            this.iv_set_remind_interval.setImageResource(R.drawable.ic_on_v2);
            this.btn_set_remind_interval_time.setVisibility(0);
            this.rl_set_remind_interval_items.setVisibility(0);
            this.btn_set_remind_interval_rest_clock.setVisibility(0);
            this.btn_set_remind_interval_prompt_dialog.setVisibility(0);
        } else {
            this.iv_set_remind_interval.setImageResource(R.drawable.ic_off_v2);
            this.btn_set_remind_interval_time.setVisibility(8);
            this.rl_set_remind_interval_items.setVisibility(8);
            this.btn_set_remind_interval_rest_clock.setVisibility(8);
            this.btn_set_remind_interval_prompt_dialog.setVisibility(8);
        }
        this.btn_set_remind_interval_prompt_dialog.setVisibility(8);
    }

    private void switchAddSleepRecord() {
        if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_IS_NOTI_ADD_SLEEP_DATE, 1) > 0) {
            PreferUtils.putInt(this.context, Val.CONFIGURE_IS_NOTI_ADD_SLEEP_DATE, 0);
        } else {
            PreferUtils.putInt(this.context, Val.CONFIGURE_IS_NOTI_ADD_SLEEP_DATE, 1);
        }
        updateAddSleepRecordUi();
    }

    private void showSelectRemindTimeDialog(final String selectTimeFlag, String defualtTime) {
        String time = getsp().getString(selectTimeFlag, defualtTime);
        int index = time.indexOf(":");
        String hourStr = time.substring(0, index);
        String minStr = time.substring(index + 1, time.length());
        final int hour = Integer.parseInt(hourStr);
        final int min = Integer.parseInt(minStr);
        new TimePickerDialog(this.context, new OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay != hour || min != minute) {
                    String remindTime = String.format("%02d", new Object[]{Integer.valueOf(hourOfDay)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(minute)});
                    SetRemindActivity.this.getsp().edit().putString(selectTimeFlag, remindTime).commit();
                    if (Val.CONFIGURE_REMIND_ADD_NOTE_TIME.equals(selectTimeFlag)) {
                        RemindUtils.quickSetRetroSpection(SetRemindActivity.this.context);
                        SetRemindActivity.this.btn_set_remind_retrospection_value.setText("提醒时间:" + remindTime);
                    } else if (Val.CONFIGURE_REMIND_MORNING_VOICE_TIME.equals(selectTimeFlag)) {
                        SetRemindActivity.this.updateMorningVoiceUi();
                        RemindUtils.quickSetRemindMorningVoice(SetRemindActivity.this.context);
                    }
                }
            }
        }, Integer.parseInt(hourStr), Integer.parseInt(minStr), true).show();
    }

    private void changeClassStartRing(final int type) {
        new Builder(this.context).setTitle(getString(R.string.str_choose)).setItems(new String[]{getString(R.string.str_open), getString(R.string.str_close), getString(R.string.str_set_ring), getString(R.string.str_set_default_ring)}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (type == 22) {
                    if (which == 0) {
                        SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_CLASS_START, 1).commit();
                        SetRemindActivity.this.btn_set_remind_rest_class_start.setText(SetRemindActivity.this.getStr(R.string.str_class_start_ring) + ":" + SetRemindActivity.this.getStr(R.string.str_on));
                    } else if (which == 1) {
                        SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_CLASS_START, 0).commit();
                        SetRemindActivity.this.btn_set_remind_rest_class_start.setText(SetRemindActivity.this.getStr(R.string.str_class_start_ring) + ":" + SetRemindActivity.this.getStr(R.string.str_off));
                    } else if (which == 2) {
                        SetRemindActivity.this.doPickRingtone(22);
                    } else if (which == 3) {
                        SetRemindActivity.this.getsp().edit().putString(Val.CONFIGURE_REMIND_REST_CLASS_START_RING, "").commit();
                        GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_set_successful));
                    }
                } else if (which == 0) {
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 1).commit();
                    SetRemindActivity.this.btn_set_remind_rest_class_over.setText(SetRemindActivity.this.getStr(R.string.str_class_over_ring) + ":" + SetRemindActivity.this.getStr(R.string.str_on));
                } else if (which == 1) {
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 0).commit();
                    SetRemindActivity.this.btn_set_remind_rest_class_over.setText(SetRemindActivity.this.getStr(R.string.str_class_over_ring) + ":" + SetRemindActivity.this.getStr(R.string.str_off));
                } else if (which == 2) {
                    SetRemindActivity.this.doPickRingtone(23);
                } else if (which == 3) {
                    SetRemindActivity.this.getsp().edit().putString(Val.CONFIGURE_REMIND_REST_CLASS_OVER_RING, "").commit();
                    GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_set_successful));
                }
                dialog.cancel();
            }
        }).create().show();
    }

    private void changeClassOverRing() {
        if (getsp().getInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 0) > 0) {
            getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 0).commit();
            this.btn_set_remind_rest_class_over.setText(getStr(R.string.str_class_over_ring) + ":" + getStr(R.string.str_off));
            return;
        }
        getsp().edit().putInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 1).commit();
        this.btn_set_remind_rest_class_over.setText(getStr(R.string.str_class_over_ring) + ":" + getStr(R.string.str_on));
    }

    private void doPickRingtone(int action) {
        Uri ringtoneUri;
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", true);
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", false);
        if (action == 22) {
            this.mCustomRingtone = getsp().getString(Val.CONFIGURE_REMIND_REST_CLASS_START_RING, "");
        } else {
            this.mCustomRingtone = getsp().getString(Val.CONFIGURE_REMIND_REST_CLASS_OVER_RING, "");
        }
        if (this.mCustomRingtone == null || this.mCustomRingtone.length() <= 0) {
            ringtoneUri = RingtoneManager.getDefaultUri(2);
        } else {
            ringtoneUri = Uri.parse(this.mCustomRingtone);
        }
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", ringtoneUri);
        startActivityForResult(intent, action);
    }

    private void changeIntervalShowDialog() {
        if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG, 0) > 0) {
            getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG, 0).commit();
            this.btn_set_remind_interval_prompt_dialog.setText(getStr(R.string.str_interval_show_dialog) + ":" + getString(R.string.str_off));
            GeneralUtils.toastShort(this.context, getStr(R.string.str_close) + getStr(R.string.str_interval_show_dialog));
            return;
        }
        getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG, 1).commit();
        this.btn_set_remind_interval_prompt_dialog.setText(getStr(R.string.str_interval_show_dialog) + ":" + getString(R.string.str_on));
        GeneralUtils.toastShort(this.context, getStr(R.string.str_open) + getStr(R.string.str_interval_show_dialog));
    }

    private void setSummarizePromptDialog(final String preName) {
        CharSequence title = getString(R.string.str_summarize_prompt);
        String text = PreferUtils.getSP(this.context).getString(preName, getString(R.string.str_summarize_prompt_defualt));
        if (preName != null && preName.equals(Val.CONFIGURE_MORNING_VOICE_PROMPT)) {
            title = getString(R.string.str_moning_voice_prompt);
            text = PreferUtils.getSP(this.context).getString(preName, getString(R.string.str_moning_voice_prompt_defualt));
        }
        this.template_edit_text = (EditText) getLayoutInflater().inflate(R.layout.template_edit_text, null);
        this.template_edit_text.setText(text);
        new Builder(this.context).setTitle(title).setView(this.template_edit_text).setPositiveButton(getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String prompt = SetRemindActivity.this.template_edit_text.getText().toString().trim();
                if (prompt.length() > 0) {
                    PreferUtils.getSP(SetRemindActivity.this.context).edit().putString(preName, prompt).commit();
                    GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_modify_success));
                }
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showChooseNoRingDialog() {
        this.hours24 = new String[24];
        this.hours24Boolean = new boolean[24];
        this.hours24Set = new TreeSet();
        String[] noRinghourArr = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR, "23,0,1,2,3,4,5,6,7").split(",");
        if (noRinghourArr != null) {
            try {
                for (String str : noRinghourArr) {
                    this.hours24Set.add(Integer.valueOf(Integer.parseInt(str)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 24; i++) {
            this.hours24[i] = "" + i + ":00";
            if (this.hours24Set.contains(Integer.valueOf(i))) {
                this.hours24Boolean[i] = true;
            } else {
                this.hours24Boolean[i] = false;
            }
        }
        new Builder(this.context).setTitle(getStr(R.string.str_rest_time_no_ring)).setMultiChoiceItems(this.hours24, this.hours24Boolean, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    SetRemindActivity.this.hours24Set.add(Integer.valueOf(which));
                } else {
                    SetRemindActivity.this.hours24Set.remove(Integer.valueOf(which));
                }
                SetRemindActivity.log("which:" + which + ",hours24Set:" + SetRemindActivity.this.hours24Set.toString());
            }
        }).setPositiveButton(getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Object[] arr = SetRemindActivity.this.hours24Set.toArray();
                if (arr == null || arr.length != 24) {
                    if (arr == null || arr.length <= 0) {
                        PreferUtils.getSP(SetRemindActivity.this.context).edit().putString(Val.CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR, "").commit();
                        SetRemindActivity.this.btn_set_remind_interval_rest_clock.setText(SetRemindActivity.this.getString(R.string.str_rest_range) + ":" + SetRemindActivity.this.getString(R.string.str_none));
                    } else {
                        String noRingHour = SetRemindActivity.this.hours24Set.toString().replace("{", "").replace("}", "").replace(" ", "").replace("[", "").replace("]", "");
                        PreferUtils.getSP(SetRemindActivity.this.context).edit().putString(Val.CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR, noRingHour).commit();
                        if (noRingHour == null || noRingHour.length() <= 0) {
                            SetRemindActivity.this.btn_set_remind_interval_rest_clock.setText(SetRemindActivity.this.getString(R.string.str_rest_range) + ":" + SetRemindActivity.this.getString(R.string.str_none));
                        } else {
                            SetRemindActivity.this.btn_set_remind_interval_rest_clock.setText(SetRemindActivity.this.getString(R.string.str_rest_range) + ":" + noRingHour);
                        }
                    }
                    dialog.cancel();
                    return;
                }
                GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_need_less_than_1_oclock));
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private String[] getIntervalIntems(Context context) {
        return new String[]{"15" + getString(R.string.str_minute), "30" + getString(R.string.str_minute), "1" + getString(R.string.str_hour), "3" + getString(R.string.str_hour)};
    }

    private void showChooseIntervalTimeDialog() {
        final String[] intervalItems = getIntervalIntems(this.context);
        new Builder(this.context).setTitle(getString(R.string.str_choose_interval)).setItems(getIntervalIntems(this.context), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String intervalStr = intervalItems[which];
                int interval = SetRemindActivity.this.intervaItemsVal[which];
                SetRemindActivity.this.btn_set_remind_interval_time.setText(SetRemindActivity.this.getString(R.string.str_interval) + ":" + intervalStr);
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_VAL, interval).commit();
                RemindUtils.setRemindInterval(SetRemindActivity.this.context);
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showChooseIntervalLEDDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_is_LED)).setPositiveButton(getString(R.string.str_on), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1).commit();
                SetRemindActivity.this.btn_set_remind_interval_sound.setText(SetRemindActivity.this.getString(R.string.str_sound2) + ":" + SetRemindActivity.this.getString(R.string.str_on));
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_off), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (SetRemindActivity.this.getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1) > 0) {
                    SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 0).commit();
                    SetRemindActivity.this.btn_set_remind_interval_sound.setText(SetRemindActivity.this.getString(R.string.str_sound2) + ":" + SetRemindActivity.this.getString(R.string.str_off));
                } else {
                    GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getString(R.string.str_shake_or_sound));
                }
                dialog.cancel();
            }
        }).create().show();
    }

    private void showChooseIntervalSoundDialog() {
        if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1) <= 0) {
            getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1).commit();
            this.btn_set_remind_interval_sound.setText(getString(R.string.str_sound2) + ":" + getString(R.string.str_on));
        } else if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1) > 0) {
            getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 0).commit();
            this.btn_set_remind_interval_sound.setText(getString(R.string.str_sound2) + ":" + getString(R.string.str_off));
        } else {
            GeneralUtils.toastShort(this.context, getString(R.string.str_shake_or_sound));
        }
    }

    private void showChooseIntervalShakeDialog() {
        if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1) <= 0) {
            getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1).commit();
            this.btn_set_remind_interval_shake.setText(getString(R.string.str_shake) + ":" + getString(R.string.str_on));
        } else if (getsp().getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1) > 0) {
            getsp().edit().putInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 0).commit();
            this.btn_set_remind_interval_shake.setText(getString(R.string.str_shake) + ":" + getString(R.string.str_off));
        } else {
            GeneralUtils.toastShort(this.context, getString(R.string.str_shake_or_sound));
        }
    }

    private void switchRemindInterval() {
        if (getsp().getInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 0) > 0) {
            getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 0).commit();
            RemindUtils.cancelRemindInterval(this.context);
            updateUIWithSwithInterval(0);
            GeneralUtils.toastShort(this.context, getString(R.string.str_interval_remind_off));
            return;
        }
        getsp().edit().putInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 1).commit();
        RemindUtils.setRemindInterval(this.context);
        updateUIWithSwithInterval(1);
        GeneralUtils.toastShort(this.context, getString(R.string.str_interval_remind_on));
    }

    private void showSetUpdateTime() {
        String str_minute = getResources().getString(R.string.str_minute);
        String str_hour = getResources().getString(R.string.str_hour);
        final String[] restArr = new String[]{"5" + str_minute, "10" + str_minute, "30" + str_minute, "1" + str_hour, "3" + str_hour};
        final int[] restIntArr = new int[]{5, 10, 30, 60, Opcodes.GETFIELD};
        new Builder(this.context).setTitle(getResources().getString(R.string.str_update_ui_interval)).setNeutralButton(getStr(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setItems(restArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String temp = restArr[which];
                SetRemindActivity.this.getsp().edit().putInt(Val.CONFIGURE_UPDATE_WIDGETS_INTERAL, restIntArr[which]).commit();
                GeneralUtils.toastShort(SetRemindActivity.this.context, SetRemindActivity.this.getStr(R.string.str_update_ui_interval) + ":" + temp);
                SetRemindActivity.this.btn_set_update_ui_interval.setText(SetRemindActivity.this.getStr(R.string.str_update_ui_interval) + ":" + temp);
                RemindUtils.setUpdateWidgetUI(SetRemindActivity.this.context);
                dialog.cancel();
            }
        }).create().show();
    }

    public void setLearnTextColor(int temp) {
        String str = "学习:" + temp + "分钟";
        int start = str.indexOf(":");
        int end = str.indexOf("分");
        SpannableStringBuilder style2 = new SpannableStringBuilder(str);
        style2.setSpan(new ForegroundColorSpan(-16777216), start, end, 33);
        style2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_tran_es)), 0, start, 33);
        style2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_tran_es)), end, str.length(), 33);
        this.btn_set_remind_learn_time.setText(style2);
    }

    public void setRestTextColor(int temp) {
        String str = "休息:" + temp + "分钟";
        int start = str.indexOf(":");
        int end = str.indexOf("分");
        SpannableStringBuilder style2 = new SpannableStringBuilder(str);
        style2.setSpan(new ForegroundColorSpan(-16777216), start, end, 33);
        style2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_tran_es)), 0, start, 33);
        style2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_tran_es)), end, str.length(), 33);
        this.btn_set_remind_rest_time.setText(style2);
    }

    private SharedPreferences getsp() {
        if (this.sp == null) {
            this.sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return this.sp;
    }

    private void handleRingtonePicked(Uri pickedUri, int type) {
        if (pickedUri == null || RingtoneManager.isDefault(pickedUri)) {
            this.mCustomRingtone = null;
        } else {
            this.mCustomRingtone = pickedUri.toString();
        }
        if (type == 22) {
            getsp().edit().putString(Val.CONFIGURE_REMIND_REST_CLASS_START_RING, this.mCustomRingtone).commit();
            GeneralUtils.toastShort(this.context, getString(R.string.str_set_successful));
            return;
        }
        getsp().edit().putString(Val.CONFIGURE_REMIND_REST_CLASS_OVER_RING, this.mCustomRingtone).commit();
        GeneralUtils.toastShort(this.context, getString(R.string.str_set_successful));
    }

    protected void onActivityResult(int r5, int r6, android.content.Intent r7) {
        /*
        r4 = this;
        switch(r5) {
            case 22: goto L_0x0007;
            case 23: goto L_0x0021;
            default: goto L_0x0003;
        };
    L_0x0003:
        super.onActivityResult(r5, r6, r7);
        return;
    L_0x0007:
        r2 = "android.intent.extra.ringtone.PICKED_URI";
        r1 = r7.getParcelableExtra(r2);	 Catch:{ Exception -> 0x0015 }
        r1 = (android.net.Uri) r1;	 Catch:{ Exception -> 0x0015 }
        r2 = 22;
        r4.handleRingtonePicked(r1, r2);	 Catch:{ Exception -> 0x0015 }
        goto L_0x0003;
    L_0x0015:
        r0 = move-exception;
        com.record.utils.db.DbUtils.exceptionHandler(r0);
        r2 = r4.context;
        r3 = "出错啦，请稍候再试！";
        com.record.utils.GeneralUtils.toastShort(r2, r3);
        goto L_0x0003;
    L_0x0021:
        r2 = "android.intent.extra.ringtone.PICKED_URI";
        r1 = r7.getParcelableExtra(r2);	 Catch:{ Exception -> 0x0015 }
        r1 = (android.net.Uri) r1;	 Catch:{ Exception -> 0x0015 }
        r2 = 23;
        r4.handleRingtonePicked(r1, r2);	 Catch:{ Exception -> 0x0015 }
        goto L_0x0003;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.myLife.settings.remind.SetRemindActivity.onActivityResult(int, int, android.content.Intent):void");
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public String getStr(int str) {
        return getResources().getString(str);
    }

    public static void log(String str) {
        Log.i("override SetRemind", ":" + str);
    }
}
