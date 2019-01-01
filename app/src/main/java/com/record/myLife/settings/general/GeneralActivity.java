package com.record.myLife.settings.general;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.alibaba.fastjson.asm.Opcodes;
import com.record.bean.Act;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.settings.BackupDbActivity_v2;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.utils.AndroidUtils;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.MyNotification;
import com.record.utils.PreferUtils;
import com.record.utils.RemindUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.dialog.DialogEdit;
import com.record.utils.dialog.DialogUtils;
import com.umeng.analytics.MobclickAgent;
import java.io.File;

public class GeneralActivity extends BaseActivity {
    Button btn_set_back;
    Button btn_set_cache;
    Button btn_set_count_down_start_time;
    Button btn_set_first_day_of_week;
    Button btn_set_general_goal_list_type;
    Button btn_set_language;
    Button btn_set_morning;
    Button btn_set_noti_items_number;
    Button btn_set_summarize;
    Button btn_set_update_ui_interval;
    Button btn_set_upload_net_type;
    Context context;
    ImageView iv_set_counter_shake;
    ImageView iv_set_counter_sound;
    ImageView iv_set_filter;
    ImageView iv_set_had_backup_dot;
    ImageView iv_set_noti;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.set_btn_backup) {
                GeneralActivity.this.startActivity(new Intent(GeneralActivity.this.context, BackupDbActivity_v2.class));
                GeneralActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_set_back) {
                GeneralActivity.this.finish();
                GeneralActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.iv_set_noti) {
                GeneralActivity.this.noti();
            } else if (id == R.id.iv_set_filter) {
                GeneralActivity.this.filter();
            } else if (id == R.id.set_btn_noti) {
                GeneralHelper.toastLong(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_status_bar_prompt));
            } else if (id == R.id.set_btn_filter) {
                GeneralHelper.toastShort(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_only_save_more_minute_records));
            } else if (id == R.id.btn_set_update_ui_interval) {
                GeneralActivity.this.showSetUpdateTime();
            } else if (id == R.id.btn_set_first_day_of_week) {
                GeneralActivity.this.showSetFirstDayOfWeekDialog();
            } else if (id == R.id.iv_set_counter_sound) {
                GeneralActivity.this.changeIsRing();
            } else if (id == R.id.btn_set_summarize) {
                GeneralActivity.this.setSummarizePromptDialog(Val.CONFIGURE_SUMMARIZE_PROMPT);
            } else if (id == R.id.btn_set_morning) {
                GeneralActivity.this.setSummarizePromptDialog(Val.CONFIGURE_MORNING_VOICE_PROMPT);
            } else if (id == R.id.iv_set_counter_shake) {
                GeneralActivity.this.changeIsShake();
            } else if (id == R.id.btn_set_cache) {
                GeneralActivity.this.clickClearCache();
            } else if (id == R.id.btn_set_noti_items_number) {
                GeneralActivity.this.clickNotiItemsNumber();
            } else if (id == R.id.btn_set_general_goal_list_type) {
            } else {
                if (id == R.id.btn_set_upload_net_type) {
                    GeneralActivity.this.changeUploadNetType();
                } else if (id == R.id.btn_set_language) {
                    GeneralActivity.this.showLanguageDialog();
                } else if (id == R.id.btn_set_count_down_start_time) {
                    GeneralActivity.this.setCountDownStartTime();
                }
            }
        }
    };
    Button set_btn_backup;
    Button set_btn_filter;
    Button set_btn_noti;
    SharedPreferences sp;
    Button str_start_counter_ring;
    EditText template_edit_text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenal);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.set_btn_backup = (Button) findViewById(R.id.set_btn_backup);
        this.set_btn_filter = (Button) findViewById(R.id.set_btn_filter);
        this.set_btn_noti = (Button) findViewById(R.id.set_btn_noti);
        this.btn_set_first_day_of_week = (Button) findViewById(R.id.btn_set_first_day_of_week);
        this.btn_set_update_ui_interval = (Button) findViewById(R.id.btn_set_update_ui_interval);
        this.btn_set_summarize = (Button) findViewById(R.id.btn_set_summarize);
        this.btn_set_morning = (Button) findViewById(R.id.btn_set_morning);
        this.btn_set_cache = (Button) findViewById(R.id.btn_set_cache);
        this.btn_set_noti_items_number = (Button) findViewById(R.id.btn_set_noti_items_number);
        this.btn_set_general_goal_list_type = (Button) findViewById(R.id.btn_set_general_goal_list_type);
        this.btn_set_upload_net_type = (Button) findViewById(R.id.btn_set_upload_net_type);
        this.iv_set_had_backup_dot = (ImageView) findViewById(R.id.iv_set_had_backup_dot);
        this.iv_set_noti = (ImageView) findViewById(R.id.iv_set_noti);
        this.iv_set_filter = (ImageView) findViewById(R.id.iv_set_filter);
        this.iv_set_counter_sound = (ImageView) findViewById(R.id.iv_set_counter_sound);
        this.iv_set_counter_shake = (ImageView) findViewById(R.id.iv_set_counter_shake);
        this.btn_set_language = (Button) findViewById(R.id.btn_set_language);
        this.btn_set_count_down_start_time = (Button) findViewById(R.id.btn_set_count_down_start_time);
        initSetUI();
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.set_btn_backup.setOnClickListener(this.myClickListener);
        this.set_btn_filter.setOnClickListener(this.myClickListener);
        this.set_btn_noti.setOnClickListener(this.myClickListener);
        this.iv_set_noti.setOnClickListener(this.myClickListener);
        this.iv_set_filter.setOnClickListener(this.myClickListener);
        this.btn_set_update_ui_interval.setOnClickListener(this.myClickListener);
        this.btn_set_first_day_of_week.setOnClickListener(this.myClickListener);
        this.iv_set_counter_sound.setOnClickListener(this.myClickListener);
        this.btn_set_summarize.setOnClickListener(this.myClickListener);
        this.btn_set_morning.setOnClickListener(this.myClickListener);
        this.iv_set_counter_shake.setOnClickListener(this.myClickListener);
        this.btn_set_cache.setOnClickListener(this.myClickListener);
        this.btn_set_noti_items_number.setOnClickListener(this.myClickListener);
        this.btn_set_general_goal_list_type.setOnClickListener(this.myClickListener);
        this.btn_set_upload_net_type.setOnClickListener(this.myClickListener);
        this.btn_set_language.setOnClickListener(this.myClickListener);
        this.btn_set_count_down_start_time.setOnClickListener(this.myClickListener);
        getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).edit().putInt(Val.CONFIGURE_IS_SHOW_GENERAL_DOT, 1).commit();
    }

    private void initSetUI() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 2);
        if (getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).getInt(Val.CONFIGURE_SET_BACK_UP, 0) < 1) {
            this.iv_set_had_backup_dot.setVisibility(0);
        } else {
            this.iv_set_had_backup_dot.setVisibility(8);
        }
        boolean isNoti = sp.getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true);
        if (VERSION.SDK_INT < 11) {
            isNoti = false;
            sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, false).commit();
        }
        if (sp.getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2) == 1) {
            this.btn_set_first_day_of_week.setText(getString(R.string.str_first_day_of_week) + ":" + getString(R.string.str_Sun));
        } else {
            this.btn_set_first_day_of_week.setText(getString(R.string.str_first_day_of_week) + ":" + getString(R.string.str_Mon));
        }
        updateUiCountDown();
        if (sp.getInt(Val.CONFIGURE_IS_RING_WHILE_START_COUNTER, 1) == 1) {
            this.iv_set_counter_sound.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_counter_sound.setImageResource(R.drawable.ic_off_v2);
        }
        if (sp.getInt(Val.CONFIGURE_IS_SHAKE_WHILE_START_COUNTER, 1) == 1) {
            this.iv_set_counter_shake.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_counter_shake.setImageResource(R.drawable.ic_off_v2);
        }
        updateUiIsNoti(isNoti);
        this.btn_set_noti_items_number.setText(getString(R.string.str_noti_items_number) + ":" + getsp().getInt(Val.CONFIGURE_NOTI_ITEMS_NUMBER, 6));
        if (sp.getBoolean(Val.CONFIGURE_IS_FILTER_RECORD, true)) {
            this.iv_set_filter.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_filter.setImageResource(R.drawable.ic_off_v2);
        }
        int widgets = getsp().getInt(Val.CONFIGURE_UPDATE_WIDGETS_INTERAL, Val.DEFAULT_UPDATE_WIDGET_INTERVAL);
        if (widgets / 60 > 0) {
            this.btn_set_update_ui_interval.setText(getString(R.string.str_update_ui_interval) + ":" + (widgets / 60) + getString(R.string.str_hour));
        } else {
            this.btn_set_update_ui_interval.setText(getString(R.string.str_update_ui_interval) + ":" + widgets + getString(R.string.str_minute));
        }
        if (getsp().getInt(Val.CONFIGURE_UPLOAD_NET_TYPE, 1) == 1) {
            this.btn_set_upload_net_type.setText(getString(R.string.str_upload_net_type) + ":" + getString(R.string.str_only_wifi));
        } else {
            this.btn_set_upload_net_type.setText(getString(R.string.str_upload_net_type) + ":" + getString(R.string.str_all_net));
        }
        updateUiLanguage();
    }

    private void updateUiCountDown() {
        this.btn_set_count_down_start_time.setText(getString(R.string.str_count_down_start_time) + ": " + PreferUtils.getInt(this.context, Val.CONFIGURE_COUNTER_DOWN_START_TIME, 0) + ":00");
    }

    private void updateUiIsNoti(boolean isShowNoti) {
        if (isShowNoti) {
            this.iv_set_noti.setImageResource(R.drawable.ic_on_v2);
            this.btn_set_noti_items_number.setVisibility(0);
            return;
        }
        this.iv_set_noti.setImageResource(R.drawable.ic_off_v2);
        this.btn_set_noti_items_number.setVisibility(8);
    }

    private SharedPreferences getsp() {
        if (this.sp == null) {
            this.sp = getSharedPreferences(Val.CONFIGURE_NAME, 2);
        }
        return this.sp;
    }

    private void setCountDownStartTime() {
        new DialogEdit(this.context, getString(R.string.str_input), getString(R.string.str_unit_hour), 2, new DialogEdit.OnClickListener() {
            public void onClick(DialogInterface dialog, int which, EditText et) {
                if (et != null) {
                    try {
                        int hour = Integer.parseInt(et.getText().toString());
                        if (hour < 0 || hour > 23) {
                            ToastUtils.toastShort(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_please_input_num_in24houre));
                            return;
                        }
                        PreferUtils.putInt(GeneralActivity.this.context, Val.CONFIGURE_COUNTER_DOWN_START_TIME, hour);
                        ToastUtils.toastShort(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_had_set));
                        GeneralActivity.this.updateUiCountDown();
                        dialog.dismiss();
                    } catch (Exception e) {
                        ToastUtils.toastShort(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_please_input_valid_number2));
                    }
                }
            }
        }).show();
    }

    private void showLanguageDialog() {
        CharSequence[] arr = new String[]{"中文-简体", "中文-繁体", "English"};
        final String[] arr1 = AndroidUtils.getLanguageArr();
        new Builder(this.context).setTitle(getString(R.string.str_choose_language)).setItems(arr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GeneralActivity.this.setLanguage(arr1[which]);
                GeneralActivity.this.updateUiLanguage();
            }
        }).create().show();
    }

    private void updateUiLanguage() {
        String lan = PreferUtils.getString(this.context, Val.CONFIGURE_LANGUAGE, "default");
        String[] arr1 = AndroidUtils.getLanguageArr();
        String[] arr = new String[]{"中文-简体", "中文-繁体", "English"};
        String show = "";
        if (arr1[0].equalsIgnoreCase(lan)) {
            show = arr[0];
        } else if (arr1[1].equalsIgnoreCase(lan)) {
            show = arr[1];
        } else if (arr1[2].equalsIgnoreCase(lan)) {
            show = arr[2];
        } else {
            show = getString(R.string.str_default);
        }
        this.btn_set_language.setText(getString(R.string.str_language) + ":" + show);
    }

    private void setLanguage(String lan) {
        AndroidUtils.changeAppLanguage(getResources(), lan);
        PreferUtils.putString(this.context, Val.CONFIGURE_LANGUAGE, lan);
        DialogUtils.showPrompt(this.context, getString(R.string.str_set_success_please_restart));
    }

    private void changeUploadNetType() {
        if (getsp().getInt(Val.CONFIGURE_UPLOAD_NET_TYPE, 1) == 1) {
            PreferUtils.putInt(this.context, Val.CONFIGURE_UPLOAD_NET_TYPE, 2);
            this.btn_set_upload_net_type.setText(getString(R.string.str_upload_net_type) + ":" + getString(R.string.str_all_net));
            return;
        }
        PreferUtils.putInt(this.context, Val.CONFIGURE_UPLOAD_NET_TYPE, 1);
        this.btn_set_upload_net_type.setText(getString(R.string.str_upload_net_type) + ":" + getString(R.string.str_only_wifi));
    }

    private void clickNotiItemsNumber() {
        final String[] arr = new String[]{"5", "6", "7", "8"};
        new Builder(this.context).setTitle((int) R.string.str_choose).setItems(arr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int notiNumber = PreferUtils.getSP(GeneralActivity.this.context).getInt(Val.CONFIGURE_NOTI_ITEMS_NUMBER, 6);
                int selectNumber = Integer.parseInt(arr[which]);
                if (selectNumber != notiNumber) {
                    PreferUtils.getSP(GeneralActivity.this.context).edit().putInt(Val.CONFIGURE_NOTI_ITEMS_NUMBER, selectNumber).commit();
                    GeneralActivity.this.showNoti();
                }
                GeneralActivity.this.btn_set_noti_items_number.setText(GeneralActivity.this.getString(R.string.str_noti_items_number) + ":" + selectNumber);
                dialog.cancel();
            }
        }).create().show();
    }

    private void showNoti() {
        if (VERSION.SDK_INT < 11) {
            GeneralHelper.toastShort(this.context, "系统版本不支持通知栏操作,暂无法使用。");
            return;
        }
        MyNotification myNoti = new MyNotification(this.context);
        if (TimerService.timer == null) {
            myNoti.initNoti();
        } else {
            myNoti.initCountingNoti(Act.getInstance().getId() + "");
        }
    }

    private void clickClearCache() {
        File file = new File(Val.SD_BACKUP_DIR);
        if (!file.exists()) {
            GeneralUtils.toastShort(this.context, (int) R.string.str_no_cache);
        } else if (file.isDirectory()) {
            String[] fileList = file.list();
            if (fileList == null || fileList.length <= 0) {
                GeneralUtils.toastShort(this.context, (int) R.string.str_no_cache);
                return;
            }
            int i = 0;
            while (i < fileList.length) {
                File subFile = new File(fileList[i]);
                i = (!subFile.isFile() && subFile.isDirectory()) ? i + 1 : i + 1;
            }
        } else {
            GeneralUtils.toastShort(this.context, (int) R.string.str_no_cache);
        }
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
                String prompt = GeneralActivity.this.template_edit_text.getText().toString().trim();
                if (prompt.length() > 0) {
                    PreferUtils.getSP(GeneralActivity.this.context).edit().putString(preName, prompt).commit();
                    GeneralUtils.toastShort(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_modify_success));
                }
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void changeIsRing() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(Val.CONFIGURE_IS_RING_WHILE_START_COUNTER, 1) == 1) {
            sp.edit().putInt(Val.CONFIGURE_IS_RING_WHILE_START_COUNTER, 2).commit();
            this.iv_set_counter_sound.setImageResource(R.drawable.ic_off_v2);
            GeneralUtils.toastShort(this.context, getString(R.string.str_start_counter_ring_close));
            return;
        }
        sp.edit().putInt(Val.CONFIGURE_IS_RING_WHILE_START_COUNTER, 1).commit();
        this.iv_set_counter_sound.setImageResource(R.drawable.ic_on_v2);
        GeneralUtils.toastShort(this.context, getString(R.string.str_start_counter_ring_open));
    }

    private void changeIsShake() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(Val.CONFIGURE_IS_SHAKE_WHILE_START_COUNTER, 2) == 1) {
            sp.edit().putInt(Val.CONFIGURE_IS_SHAKE_WHILE_START_COUNTER, 2).commit();
            this.iv_set_counter_shake.setImageResource(R.drawable.ic_off_v2);
            GeneralUtils.toastShort(this.context, getString(R.string.str_close_start_counter_shake));
            return;
        }
        sp.edit().putInt(Val.CONFIGURE_IS_SHAKE_WHILE_START_COUNTER, 1).commit();
        this.iv_set_counter_shake.setImageResource(R.drawable.ic_on_v2);
        GeneralUtils.toastShort(this.context, getString(R.string.str_start_counter_shake));
    }

    private void showSetFirstDayOfWeekDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_first_day_of_week)).setPositiveButton(getString(R.string.str_Sun), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GeneralActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_START_DATE_OF_WEEK, 1).commit();
                GeneralActivity.this.btn_set_first_day_of_week.setText(GeneralActivity.this.getString(R.string.str_first_day_of_week) + ":" + GeneralActivity.this.getString(R.string.str_Sun));
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.str_Mon), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GeneralActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2).commit();
                GeneralActivity.this.btn_set_first_day_of_week.setText(GeneralActivity.this.getString(R.string.str_first_day_of_week) + ":" + GeneralActivity.this.getString(R.string.str_Mon));
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showSetUpdateTime() {
        String str_minute = getResources().getString(R.string.str_minute);
        String str_hour = getResources().getString(R.string.str_hour);
        final String[] restArr = new String[]{"5" + str_minute, "10" + str_minute, "30" + str_minute, "1" + str_hour, "3" + str_hour};
        final int[] restIntArr = new int[]{5, 10, 30, 60, Opcodes.GETFIELD};
        new Builder(this.context).setTitle(getResources().getString(R.string.str_update_ui_interval)).setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setItems(restArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String temp = restArr[which];
                GeneralActivity.this.getsp().edit().putInt(Val.CONFIGURE_UPDATE_WIDGETS_INTERAL, restIntArr[which]).commit();
                GeneralUtils.toastShort(GeneralActivity.this.context, GeneralActivity.this.getString(R.string.str_update_ui_interval) + ":" + temp);
                GeneralActivity.this.btn_set_update_ui_interval.setText(GeneralActivity.this.getString(R.string.str_update_ui_interval) + ":" + temp);
                RemindUtils.setUpdateWidgetUI(GeneralActivity.this.context);
                dialog.cancel();
            }
        }).create().show();
    }

    private void filter() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 2);
        if (sp.getBoolean(Val.CONFIGURE_IS_FILTER_RECORD, true)) {
            sp.edit().putBoolean(Val.CONFIGURE_IS_FILTER_RECORD, false).commit();
            this.iv_set_filter.setImageResource(R.drawable.ic_off_v2);
            GeneralHelper.toastLong(this.context, "过滤关闭，保存小于1分钟的记录！");
            return;
        }
        sp.edit().putBoolean(Val.CONFIGURE_IS_FILTER_RECORD, true).commit();
        this.iv_set_filter.setImageResource(R.drawable.ic_on_v2);
        GeneralHelper.toastLong(this.context, "过滤开启，将不保存小于1分钟的记录！");
    }

    private void noti() {
        if (VERSION.SDK_INT < 11) {
            GeneralHelper.toastShort(this.context, "系统版本不支持通知栏操作,暂无法使用。");
            return;
        }
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 2);
        if (sp.getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true)) {
            sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, false).commit();
            updateUiIsNoti(false);
            ((NotificationManager) getSystemService("notification")).cancel(0);
            return;
        }
        sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true).commit();
        updateUiIsNoti(true);
        log("" + TimerService.isInsertDb);
        MyNotification myNoti = new MyNotification(this.context);
        if (TimerService.timer == null) {
            myNoti.initNoti();
        } else {
            myNoti.initCountingNoti(Act.getInstance().getId() + "");
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    protected void onResume() {
        super.onResume();
        initSetUI();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void log(String str) {
        Log.i("override Login", ":" + str);
    }
}
