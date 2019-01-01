package com.record.myLife.settings;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.alibaba.fastjson.JSON;
import com.record.bean.User;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.login.LoginActivity;
import com.record.myLife.other.GuideActivity;
import com.record.myLife.settings.about.AboutActivity;
import com.record.myLife.settings.about.FeedBackActivity;
import com.record.myLife.settings.about.SupportActivity;
import com.record.myLife.settings.about.VersionActivity;
import com.record.myLife.settings.label.LabelInfoActivity_v2;
import com.record.myLife.settings.remind.SetRemindActivity;
import com.record.myLife.view.MyTextDialog.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.UpdateServices;
import com.record.utils.GeneralHelper;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.Map;

public class AboutItemsActivity extends BaseActivity {
    int ACTION_DIALOG = 2;
    int ACTION_TOAST = 1;
    Button btn_set_back;
    Button btn_set_getversion;
    Context context;
    Thread getVersionThread;
    String http = "http://itoday.sinaapp.com/api/getversion.php";
    boolean isLogining = false;
    ImageView iv_set_had_backup_dot;
    ImageView iv_set_had_version;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.set_btn_about) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, AboutActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_support) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, SupportActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_version) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, VersionActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_filter) {
                GeneralHelper.toastShort(AboutItemsActivity.this.context, "开启后不保存一分钟以下记录。");
            } else if (id == R.id.set_btn_noti) {
                GeneralHelper.toastLong(AboutItemsActivity.this.context, "开启后可直接在通知栏里开启/关闭时间，非常方便哦！");
            } else if (id == R.id.set_btn_feedback) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, FeedBackActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_set_remind) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, SetRemindActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_backup) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, BackupDbActivity_v2.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_set_back) {
                AboutItemsActivity.this.finish();
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.set_btn_login) {
                if ("测试".equals(User.getInstance().getUserName())) {
                    AboutItemsActivity.this.finish();
                    AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, LoginActivity.class));
                    AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                    return;
                }
                AboutItemsActivity.this.showLoginDialog();
            } else if (id == R.id.btn_set_getversion) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, VersionActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_set_subtype) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, LabelInfoActivity_v2.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_help) {
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, GuideActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action = msg.arg1;
            if (action == AboutItemsActivity.this.ACTION_TOAST) {
                GeneralHelper.toastShort(AboutItemsActivity.this.context, (String) msg.obj);
            } else if (action == AboutItemsActivity.this.ACTION_DIALOG) {
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
                new Builder(AboutItemsActivity.this.context).setTitle("发现新版本").setMessage("版本号:" + versionName + "\n更新内容：\n" + updateLog2).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AboutItemsActivity.this.startService(new Intent(AboutItemsActivity.this.context, UpdateServices.class));
                        dialog.cancel();
                        GeneralHelper.toastShort(AboutItemsActivity.this.context, "开始下载...");
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
    Button set_btn_help;
    Button set_btn_support;
    Button set_btn_version;

    class loginRun implements Runnable {
        loginRun() {
        }

        public void run() {
            try {
                AboutItemsActivity.this.isLogining = true;
                AboutItemsActivity.this.toastMsg("正在获取，请稍候...");
                String result = HttpRequestProxy.doPost(AboutItemsActivity.this.http, new HashMap(), "UTF-8");
                AboutItemsActivity.this.log(result);
                if (result != null) {
                    Map<String, String> map = (Map) JSON.parse(result);
                    String versionName = (String) map.get("versionName");
                    String updateLog = (String) map.get("updateLog");
                    String url = (String) map.get("url");
                    if (Integer.parseInt((String) map.get("versionCode")) > AboutItemsActivity.this.getPackageManager().getPackageInfo(AboutItemsActivity.this.context.getPackageName(), 0).versionCode) {
                        Bundle data = new Bundle();
                        data.putString("versionName", versionName);
                        data.putString("updateLog", updateLog);
                        data.putString("url", url);
                        AboutItemsActivity.this.showDialog(data);
                        AboutItemsActivity.this.isLogining = false;
                        return;
                    }
                    AboutItemsActivity.this.toastMsg("当前已是最新版哦！");
                    AboutItemsActivity.this.isLogining = false;
                    return;
                }
                AboutItemsActivity.this.toastMsg("获取失败，请稍候再试！");
                AboutItemsActivity.this.isLogining = false;
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                AboutItemsActivity.this.isLogining = false;
                AboutItemsActivity.this.toastMsg("获取失败，请稍候再试！");
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_itodayss);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.set_btn_version = (Button) findViewById(R.id.set_btn_version);
        this.set_btn_about = (Button) findViewById(R.id.set_btn_about);
        this.set_btn_feedback = (Button) findViewById(R.id.set_btn_feedback);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_set_getversion = (Button) findViewById(R.id.btn_set_getversion);
        this.set_btn_support = (Button) findViewById(R.id.set_btn_support);
        this.set_btn_help = (Button) findViewById(R.id.set_btn_help);
        this.set_btn_backup = (Button) findViewById(R.id.set_btn_backup);
        this.iv_set_had_version = (ImageView) findViewById(R.id.iv_set_had_version);
        this.iv_set_had_backup_dot = (ImageView) findViewById(R.id.iv_set_had_backup_dot);
        initSetUI();
        this.set_btn_about.setOnClickListener(this.myClickListener);
        this.set_btn_feedback.setOnClickListener(this.myClickListener);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.btn_set_getversion.setOnClickListener(this.myClickListener);
        this.set_btn_support.setOnClickListener(this.myClickListener);
        this.set_btn_version.setOnClickListener(this.myClickListener);
        this.set_btn_help.setOnClickListener(this.myClickListener);
        this.set_btn_backup.setOnClickListener(this.myClickListener);
        getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).edit().putInt(Val.CONFIGURE_IS_SHOW_MAIN_SET_DOT, 3).commit();
    }

    private void initSetUI() {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 2);
        if (sp.getInt(Val.CONFIGURE_IS_HAD_NEW_VERSION, 0) > 0) {
            this.iv_set_had_version.setVisibility(0);
        } else {
            this.iv_set_had_version.setVisibility(8);
        }
        if (getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).getInt(Val.CONFIGURE_SET_BACK_UP, 0) < 1) {
            this.iv_set_had_backup_dot.setVisibility(0);
        } else {
            this.iv_set_had_backup_dot.setVisibility(8);
        }
        boolean isNoti = sp.getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true);
        if (VERSION.SDK_INT < 11) {
            sp.edit().putBoolean(Val.CONFIGURE_IS_SHOW_NOTI, false).commit();
        }
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
        new Builder(this.context).setTitle("是否退出当前账户？").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AboutItemsActivity.this.initLoginDb();
                AboutItemsActivity.this.finish();
                AboutItemsActivity.this.startActivity(new Intent(AboutItemsActivity.this.context, LoginActivity.class));
                AboutItemsActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
