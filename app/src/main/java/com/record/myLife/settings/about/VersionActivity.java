package com.record.myLife.settings.about;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.login.LoginActivity;
import com.record.myLife.other.GuideActivity;
import com.record.myLife.view.MyTextDialog.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.UpdateServices;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.NetUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
//import com.umeng.fb.FeedbackAgent;
import java.util.HashMap;
import java.util.Map;

public class VersionActivity extends BaseActivity {
    int ACTION_DIALOG = 2;
    int ACTION_TOAST = 1;
    Button btn_set_back;
    TextView btn_set_getVersion;
    TextView btn_set_recommend;
    TextView btn_set_version;
    Context context;
    Thread getVersionThread;
    String http = "http://itoday.sinaapp.com/api/getversion.php";
    boolean isLogining = false;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_set_back) {
                VersionActivity.this.finish();
                VersionActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.btn_set_getVersion) {
                if (NetUtils.isNetworkAvailable(VersionActivity.this.context)) {
                    VersionActivity.this.umengUpdate();
                }
            } else if (id == R.id.btn_set_recommend) {
                try {
                    VersionActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.record.myLife")));
                } catch (Exception e) {
                    e.printStackTrace();
                    GeneralUtils.toastShort(VersionActivity.this.context, VersionActivity.this.getString(R.string.str_rate_failure));
                }
            } else if (id == R.id.set_btn_feedback) {
//                new FeedbackAgent(VersionActivity.this.context).startFeedbackActivity();
                VersionActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_about) {
                VersionActivity.this.startActivity(new Intent(VersionActivity.this.context, AboutActivity.class));
                VersionActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_support) {
                VersionActivity.this.startActivity(new Intent(VersionActivity.this.context, SupportActivity.class));
                VersionActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.set_btn_help) {
                VersionActivity.this.startActivity(new Intent(VersionActivity.this.context, GuideActivity.class));
                VersionActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action = msg.arg1;
            if (action == VersionActivity.this.ACTION_TOAST) {
                GeneralHelper.toastShort(VersionActivity.this.context, (String) msg.obj);
            } else if (action == VersionActivity.this.ACTION_DIALOG) {
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
                new Builder(VersionActivity.this.context).setTitle("发现新版本").setMessage("版本号:" + versionName + "\n更新内容：\n" + updateLog2).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        VersionActivity.this.startService(new Intent(VersionActivity.this.context, UpdateServices.class));
                        dialog.cancel();
                        GeneralHelper.toastShort(VersionActivity.this.context, "开始下载...");
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
    Button set_btn_feedback;
    Button set_btn_help;
    Button set_btn_support;

    class loginRun implements Runnable {
        loginRun() {
        }

        public void run() {
            try {
                VersionActivity.this.isLogining = true;
                VersionActivity.this.toastMsg("正在获取，请稍候...");
                String result = HttpRequestProxy.doPost(VersionActivity.this.http, new HashMap(), "UTF-8");
                VersionActivity.this.log(result);
                if (result != null) {
                    Map<String, String> map = (Map) JSON.parse(result);
                    String versionName = (String) map.get("versionName");
                    String updateLog = (String) map.get("updateLog");
                    String url = (String) map.get("url");
                    if (Integer.parseInt((String) map.get("versionCode")) > VersionActivity.this.getPackageManager().getPackageInfo(VersionActivity.this.context.getPackageName(), 0).versionCode) {
                        Bundle data = new Bundle();
                        data.putString("versionName", versionName);
                        data.putString("updateLog", updateLog);
                        data.putString("url", url);
                        VersionActivity.this.showDialog(data);
                        VersionActivity.this.isLogining = false;
                        return;
                    }
                    VersionActivity.this.toastMsg("当前已是最新版哦！");
                    VersionActivity.this.isLogining = false;
                    return;
                }
                VersionActivity.this.toastMsg("获取失败，请稍候再试！");
                VersionActivity.this.isLogining = false;
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                VersionActivity.this.isLogining = false;
                VersionActivity.this.toastMsg("获取失败，请稍候再试！");
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_v2);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_set_version = (TextView) findViewById(R.id.btn_set_version);
        this.btn_set_getVersion = (TextView) findViewById(R.id.btn_set_getVersion);
        this.btn_set_recommend = (TextView) findViewById(R.id.btn_set_recommend);
        this.set_btn_feedback = (Button) findViewById(R.id.set_btn_feedback);
        this.set_btn_about = (Button) findViewById(R.id.set_btn_about);
        this.set_btn_support = (Button) findViewById(R.id.set_btn_support);
        this.set_btn_help = (Button) findViewById(R.id.set_btn_help);
        try {
            this.btn_set_version.setText(getString(R.string.str_current_version) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            DbUtils.exceptionHandler(e);
        }
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.btn_set_getVersion.setOnClickListener(this.myClickListener);
        this.btn_set_recommend.setOnClickListener(this.myClickListener);
        this.set_btn_feedback.setOnClickListener(this.myClickListener);
        this.set_btn_about.setOnClickListener(this.myClickListener);
        this.set_btn_support.setOnClickListener(this.myClickListener);
        this.set_btn_help.setOnClickListener(this.myClickListener);
    }

    private void umengUpdate() {
        toastMsg("正在获取，请稍候...");
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
                VersionActivity.this.initLoginDb();
                VersionActivity.this.finish();
                VersionActivity.this.startActivity(new Intent(VersionActivity.this.context, LoginActivity.class));
                VersionActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    public void initLoginDb() {
        ContentValues values = new ContentValues();
        values.put("isLogin", Integer.valueOf(0));
        DbUtils.getDb(this.context).update("t_user", values, "isLogin is ?", new String[]{"1"});
    }

    protected void onResume() {
        super.onResume();
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
