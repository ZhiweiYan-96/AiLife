package com.record.myLife.settings;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
//import com.qq.e.ads.appwall.APPWall;
import com.record.bean.Act;
import com.record.bean.User;
import com.record.conts.Consts;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.login.LoginActivity;
import com.record.myLife.other.DebugActivity;
import com.record.myLife.other.GuideActivity;
import com.record.myLife.settings.about.FeedBackActivity;
import com.record.myLife.settings.about.SupportActivity;
import com.record.myLife.settings.about.VersionActivity;
import com.record.myLife.settings.general.GeneralActivity;
import com.record.myLife.settings.label.LabelInfoActivity_v2;
import com.record.myLife.settings.remind.SetRemindActivity;
import com.record.myLife.view.MyTextDialog.Builder;
import com.record.myLife.view.dialog.AlertDialogM;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.service.UpdateServices;
import com.record.utils.GeneralHelper;
import com.record.utils.MyNotification;
import com.record.utils.ShowGuideImgUtils;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.Map;

public class SetActivity extends BaseActivity {
    int ACTION_DIALOG = 2;
    int ACTION_TOAST = 1;
    int DEBUG_VERSEION_CODE = 38;
    Button btn_set_apps;
    Button btn_set_back;
    Button btn_set_general;
    Button btn_set_getversion;
    Button btn_set_remind;
    Button btn_set_subtype;
    Context context;
    Thread getVersionThread;
    String http = "http://itoday.sinaapp.com/api/getversion.php";
    boolean isLogining = false;
    ImageView iv_set_filter;
    ImageView iv_set_general_dot;
    ImageView iv_set_had_backup_dot;
    ImageView iv_set_had_version;
    ImageView iv_set_noti;
    ImageView iv_set_remind_dot;
    ImageView iv_set_subtype_dot;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.set_btn_about) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, VersionActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(SetActivity.this.getApplicationContext(), "Settings_about_activate_btn");
            } else if (id == R.id.set_btn_support) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, SupportActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_version) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, VersionActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_filter) {
                GeneralHelper.toastShort(SetActivity.this.context, "开启后不保存一分钟以下记录。");
            } else if (id == R.id.set_btn_noti) {
                GeneralHelper.toastLong(SetActivity.this.context, "开启后可直接在通知栏里开启/关闭时间，非常方便哦！");
            } else if (id == R.id.set_btn_feedback) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, FeedBackActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_set_remind) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, SetRemindActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(SetActivity.this.getApplicationContext(), "Settings_remind_activate_btn");
            } else if (id == R.id.set_btn_backup) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, BackupDbActivity_v2.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(SetActivity.this.getApplicationContext(), "Settings_derived_backups_activate_btn");
            } else if (id == R.id.btn_set_back) {
                SetActivity.this.finish();
                SetActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.set_btn_login) {
                SetActivity.this.isLogin();
                MobclickAgent.onEvent(SetActivity.this.getApplicationContext(), "Settings_Login_activate_btn");
            } else if (id == R.id.btn_set_getversion) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, VersionActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.iv_set_noti) {
                SetActivity.this.isNoti();
            } else if (id == R.id.iv_set_filter) {
                SetActivity.this.isFilter();
            } else if (id == R.id.btn_set_subtype) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, LabelInfoActivity_v2.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(SetActivity.this.getApplicationContext(), "Settings_tag_activate_btn");
            } else if (id == R.id.set_btn_help) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, GuideActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_set_general) {
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, GeneralActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(SetActivity.this.getApplicationContext(), "Settings_Common_activate_btn");
            } else if (id == R.id.btn_set_apps) {
//                if (SetActivity.this.wall != null) {
////                    SetActivity.this.wall.doShowAppWall();
//                }
            } else if (id == R.id.tv_set_title) {
                try {
                    PackageInfo info = SetActivity.this.getPackageManager().getPackageInfo(SetActivity.this.getPackageName(), 0);
                    if (DbUtils.queryAuthorizationByUserId(SetActivity.this.context, DbUtils.queryUserId2(SetActivity.this.context)) > 0) {
                        SetActivity.this.clickTenTimeIntoDebugActivity();
                    } else if ("624604006@qq.com".equals(User.getInstance().getUserName())) {
                        SetActivity.this.startActivity(new Intent(SetActivity.this.context, DebugActivity.class));
                    } else if (info.versionCode == SetActivity.this.DEBUG_VERSEION_CODE) {
                        SetActivity.this.clickTenTimeIntoDebugActivity();
                    }
                } catch (NameNotFoundException e) {
                    DbUtils.exceptionHandler(e);
                }
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action = msg.arg1;
            if (action == SetActivity.this.ACTION_TOAST) {
                GeneralHelper.toastShort(SetActivity.this.context, (String) msg.obj);
            } else if (action == SetActivity.this.ACTION_DIALOG) {
                Bundle data = (Bundle)msg.obj;
                String versionName = (String) data.get("versionName");
                String updateLog = (String) data.get("updateLog");
                Val.versionUrl = (String) data.get("url");
                String updateLog2 = "";
                if (updateLog != null) {
                    for (String str : updateLog.split(";")) {
                        updateLog2 = updateLog2 + "\n" + str;
                    }
                    if (updateLog2.length() == 0) {
                        updateLog2 = updateLog;
                    }
                }
                new Builder(SetActivity.this.context).setTitle("发现新版本").setMessage("版本号:" + versionName + "\n更新内容：\n" + updateLog2).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SetActivity.this.startService(new Intent(SetActivity.this.context, UpdateServices.class));
                        dialog.cancel();
                        GeneralHelper.toastShort(SetActivity.this.context, "开始下载...");
                    }
                }).setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        }
    };
    Button set_btn_about;
    Button set_btn_backup;
    Button set_btn_feedback;
    Button set_btn_filter;
    Button set_btn_help;
    Button set_btn_login;
    Button set_btn_noti;
    Button set_btn_support;
    Button set_btn_version;
    TextView tv_set_title;
    int tv_today_title_counter = 0;
//    APPWall wall = null;

    class loginRun implements Runnable {
        loginRun() {
        }

        public void run() {
            try {
                SetActivity.this.isLogining = true;
                SetActivity.this.toastMsg("正在获取，请稍候...");
                String result = HttpRequestProxy.doPost(SetActivity.this.http, new HashMap(), "UTF-8");
                SetActivity.this.log(result);
                if (result != null) {
                    Map<String, String> map = (Map) JSON.parse(result);
                    String versionName = (String) map.get("versionName");
                    String updateLog = (String) map.get("updateLog");
                    String url = (String) map.get("url");
                    if (Integer.parseInt((String) map.get("versionCode")) > SetActivity.this.getPackageManager().getPackageInfo(SetActivity.this.context.getPackageName(), 0).versionCode) {
                        Bundle data = new Bundle();
                        data.putString("versionName", versionName);
                        data.putString("updateLog", updateLog);
                        data.putString("url", url);
                        SetActivity.this.showDialog(data);
                        SetActivity.this.isLogining = false;
                        return;
                    }
                    SetActivity.this.toastMsg("当前已是最新版哦！");
                    SetActivity.this.isLogining = false;
                    return;
                }
                SetActivity.this.toastMsg("获取失败，请稍候再试！");
                SetActivity.this.isLogining = false;
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                SetActivity.this.isLogining = false;
                SetActivity.this.toastMsg("获取失败，请稍候再试！");
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.set_btn_version = (Button) findViewById(R.id.set_btn_version);
        this.set_btn_filter = (Button) findViewById(R.id.set_btn_filter);
        this.set_btn_noti = (Button) findViewById(R.id.set_btn_noti);
        this.set_btn_about = (Button) findViewById(R.id.set_btn_about);
        this.set_btn_feedback = (Button) findViewById(R.id.set_btn_feedback);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.set_btn_login = (Button) findViewById(R.id.set_btn_login);
        this.btn_set_getversion = (Button) findViewById(R.id.btn_set_getversion);
        this.btn_set_subtype = (Button) findViewById(R.id.btn_set_subtype);
        this.set_btn_support = (Button) findViewById(R.id.set_btn_support);
        this.btn_set_remind = (Button) findViewById(R.id.btn_set_remind);
        this.set_btn_help = (Button) findViewById(R.id.set_btn_help);
        this.set_btn_backup = (Button) findViewById(R.id.set_btn_backup);
        this.btn_set_general = (Button) findViewById(R.id.btn_set_general);
        this.btn_set_apps = (Button) findViewById(R.id.btn_set_apps);
        this.iv_set_had_version = (ImageView) findViewById(R.id.iv_set_had_version);
        this.iv_set_noti = (ImageView) findViewById(R.id.iv_set_noti);
        this.iv_set_filter = (ImageView) findViewById(R.id.iv_set_filter);
        this.iv_set_remind_dot = (ImageView) findViewById(R.id.iv_set_remind_dot);
        this.iv_set_subtype_dot = (ImageView) findViewById(R.id.iv_set_subtype_dot);
        this.iv_set_had_backup_dot = (ImageView) findViewById(R.id.iv_set_had_backup_dot);
        this.iv_set_general_dot = (ImageView) findViewById(R.id.iv_set_general_dot);
        this.tv_set_title = (TextView) findViewById(R.id.tv_set_title);
        if ("测试".equals(DbUtils.queryUserName(this.context))) {
            this.set_btn_login.setText(getString(R.string.str_sign_in));
        } else {
            this.set_btn_login.setText(getString(R.string.str_logout));
        }
        initSetUI();
        this.set_btn_about.setOnClickListener(this.myClickListener);
        this.set_btn_feedback.setOnClickListener(this.myClickListener);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.set_btn_login.setOnClickListener(this.myClickListener);
        this.btn_set_getversion.setOnClickListener(this.myClickListener);
        this.iv_set_noti.setOnClickListener(this.myClickListener);
        this.iv_set_filter.setOnClickListener(this.myClickListener);
        this.btn_set_subtype.setOnClickListener(this.myClickListener);
        this.set_btn_support.setOnClickListener(this.myClickListener);
        this.set_btn_version.setOnClickListener(this.myClickListener);
        this.set_btn_filter.setOnClickListener(this.myClickListener);
        this.btn_set_remind.setOnClickListener(this.myClickListener);
        this.set_btn_noti.setOnClickListener(this.myClickListener);
        this.set_btn_help.setOnClickListener(this.myClickListener);
        this.set_btn_backup.setOnClickListener(this.myClickListener);
        this.btn_set_general.setOnClickListener(this.myClickListener);
        this.btn_set_apps.setOnClickListener(this.myClickListener);
        this.tv_set_title.setOnClickListener(this.myClickListener);
        getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).edit().putInt(Val.CONFIGURE_IS_SHOW_MAIN_SET_DOT, 3).commit();
//        this.wall = new APPWall(this.context, Consts.GDT_APP_ID, Consts.GDT_APPWallPosID);
//        this.wall.setScreenOrientation(1);
//        this.wall.prepare();
    }

    private void initSetUI() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, MODE_WORLD_WRITEABLE);
        if (sp.getInt(Val.CONFIGURE_IS_HAD_NEW_VERSION, 0) > 0) {
            this.iv_set_had_version.setVisibility(View.VISIBLE);
        } else {
            this.iv_set_had_version.setVisibility(View.GONE);
        }
        SharedPreferences sp2 = getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0);
        if (sp2.getInt(Val.CONFIGURE_SET_REMIND_DOT, 0) < 5) {
            this.iv_set_remind_dot.setVisibility(View.VISIBLE);
        } else {
            this.iv_set_remind_dot.setVisibility(View.GONE);
        }
        if (sp2.getInt(Val.CONFIGURE_SET_BACK_UP, 0) < 1) {
            this.iv_set_had_backup_dot.setVisibility(View.VISIBLE);
        } else {
            this.iv_set_had_backup_dot.setVisibility(View.GONE);
        }
        if (sp2.getInt(Val.CONFIGURE_IS_SHOW_LABEL_DOT, 0) < 3) {
            this.iv_set_subtype_dot.setVisibility(View.VISIBLE);
        } else {
            this.iv_set_subtype_dot.setVisibility(View.GONE);
        }
        ShowGuideImgUtils.isShowDot(this.context, this.iv_set_general_dot, Val.CONFIGURE_IS_SHOW_GENERAL_DOT, 1);
        boolean isNoti = sp.getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true);
        if (VERSION.SDK_INT < 11) {
            isNoti = false;
            sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, false).commit();
        }
        if (isNoti) {
            this.iv_set_noti.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_noti.setImageResource(R.drawable.ic_off_v2);
        }
        if (sp.getBoolean(Val.CONFIGURE_IS_FILTER_RECORD, true)) {
            this.iv_set_filter.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_set_filter.setImageResource(R.drawable.ic_off_v2);
        }
    }

    private void isNoti() {
        if (VERSION.SDK_INT < 11) {
            GeneralHelper.toastShort(this.context, "Android 3.0.1 以下版本不支持通知栏,暂无法使用！");
            return;
        }
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, MODE_WORLD_WRITEABLE);
        if (sp.getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true)) {
            sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, false).commit();
            this.iv_set_noti.setImageResource(R.drawable.ic_off_v2);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(0);
            return;
        }
        sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true).commit();
        this.iv_set_noti.setImageResource(R.drawable.ic_on_v2);
        log("" + TimerService.isInsertDb);
        MyNotification myNoti = new MyNotification(this.context);
        if (TimerService.timer == null) {
            myNoti.initNoti();
        } else {
            myNoti.initCountingNoti(Act.getInstance().getId() + "");
        }
    }

    private void isFilter() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, MODE_WORLD_WRITEABLE);
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

    private void clickTenTimeIntoDebugActivity() {
        this.tv_today_title_counter++;
        if (this.tv_today_title_counter == 5) {
            startActivity(new Intent(this.context, DebugActivity.class));
            this.tv_today_title_counter = 0;
        }
    }

    private void isLogin() {
        if ("测试".equals(DbUtils.queryUserName(this.context))) {
            finish();
            startActivity(new Intent(this.context, LoginActivity.class));
            overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            return;
        }
        showLoginDialog();
    }

    public void getNewVersion() {
        if (this.isLogining) {
            GeneralHelper.toastShort(this.context, "正在获取版本号，请稍候...");
            return;
        }
        this.getVersionThread = new Thread(new loginRun());
        this.getVersionThread.start();
    }

    private void toastMsg(String str) {
        Message msg = new Message();
        msg.arg1 = this.ACTION_TOAST;
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    private void showDialog(Bundle data) {
        Message msg = new Message();
        msg.arg1 = this.ACTION_DIALOG;
        msg.obj = data;
        this.myHandler.sendMessage(msg);
    }

    private void showLoginDialog() {
        new AlertDialogM.Builder(this.context).setTitle(getString(R.string.str_is_logout)).setPositiveButton(getString(R.string.str_logout), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UserUtils.initLoginDb(SetActivity.this.context);
                SetActivity.this.finish();
                SetActivity.this.startActivity(new Intent(SetActivity.this.context, LoginActivity.class));
                SetActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void initLoginDb() {
        ContentValues values = new ContentValues();
        values.put("isLogin", Integer.valueOf(0));
        DbUtils.getDb(this.context).update("t_user", values, "isLogin is ?", new String[]{"1"});
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
