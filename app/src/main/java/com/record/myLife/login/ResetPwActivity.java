package com.record.myLife.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.record.conts.Sofeware;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.GeneralUtils;
import com.record.utils.NetUtils;
import com.record.utils.PreferUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.Map;

public class ResetPwActivity extends BaseActivity {
    static String TAG = "override";
    int HANDLER_SHOW_DIALOG = 2;
    int HANDLER_TOAST = 1;
    Button btn_find_pw_2_next;
    Button btn_set_back;
    Context context;
    EditText ed_find_pw_2_another_pw;
    EditText ed_find_pw_2_name;
    EditText ed_find_pw_2_new_pw;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_find_pw_2_next) {
                ResetPwActivity.this.resetPw();
            } else if (id == R.id.btn_set_back) {
                ResetPwActivity.this.finish();
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == ResetPwActivity.this.HANDLER_TOAST) {
                GeneralUtils.toastLong(ResetPwActivity.this.context, msg.obj.toString());
            } else if (msg.arg1 == ResetPwActivity.this.HANDLER_SHOW_DIALOG) {
                ResetPwActivity.this.showSendSuccessDialog(msg.obj.toString());
            }
        }
    };
    String password = "";
    Thread sendVerifyThread;
    String userName = "";

    class sendVerifyCode implements Runnable {
        String email = "";
        String pass = "";
        String verify = "";

        public sendVerifyCode(String email, String pass, String verify) {
            this.email = email;
            this.pass = pass;
            this.verify = verify;
        }

        public void run() {
            try {
                Map<String, String> map = new HashMap();
                map.put("userName", this.email);
                map.put("verifyCode", this.verify);
                map.put("password", this.pass);
                String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.RESET_PASSWORD, map);
                if (result != null) {
                    JSONObject rootJson = JSON.parseObject(result);
                    int status = rootJson.getIntValue("status");
                    String msg = rootJson.getString("msg");
                    if (status == 1) {
                        SharedPreferences sp = PreferUtils.getSP(ResetPwActivity.this.context);
                        sp.edit().putString(Val.CONFIGURE_FIND_PW_USERNAME, "").commit();
                        sp.edit().putString(Val.CONFIGURE_FIND_PW_TIME, "").commit();
                        ResetPwActivity.this.sendMsgDialog(msg);
                        return;
                    }
                    ResetPwActivity.this.sendMsg(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
        this.ed_find_pw_2_new_pw = (EditText) findViewById(R.id.ed_find_pw_2_new_pw);
        this.ed_find_pw_2_another_pw = (EditText) findViewById(R.id.ed_find_pw_2_another_pw);
        this.ed_find_pw_2_name = (EditText) findViewById(R.id.ed_find_pw_2_name);
        this.btn_find_pw_2_next = (Button) findViewById(R.id.btn_find_pw_2_next);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_find_pw_2_next.setOnClickListener(this.myClickListener);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.userName = getIntent().getStringExtra("userName");
        if (this.userName == null || this.userName.equalsIgnoreCase("null")) {
            finish();
        }
    }

    private void initSetUI() {
    }

    private void resetPw() {
        String pass = this.ed_find_pw_2_new_pw.getText().toString();
        String pw2 = this.ed_find_pw_2_another_pw.getText().toString();
        String verifyCode = this.ed_find_pw_2_name.getText().toString();
        if (pass == null || pass.length() == 0) {
            ToastUtils.toastLong(this.context, getString(R.string.str_password_not_null));
        } else if (verifyCode == null || verifyCode.length() == 0) {
            ToastUtils.toastLong(this.context, getString(R.string.str_verify_code_is_not_null));
        } else if (pass.length() < 6) {
            ToastUtils.toastLong(this.context, getString(R.string.str_password_should_more_than_six));
        } else if (pass.equals(pw2)) {
            if (pass.length() > 64) {
                pass = pass.substring(0, 64);
            }
            if (!NetUtils.isNetworkAvailable(this.context)) {
                return;
            }
            if (this.sendVerifyThread == null || !this.sendVerifyThread.isAlive()) {
                this.password = pass;
                this.sendVerifyThread = new Thread(new sendVerifyCode(this.userName, this.password, verifyCode));
                this.sendVerifyThread.start();
                GeneralUtils.toastShort(this.context, getString(R.string.str_verifying));
                return;
            }
            GeneralUtils.toastShort(this.context, getString(R.string.str_verifying2));
        } else {
            ToastUtils.toastLong(this.context, getString(R.string.str_two_pw_not_same));
        }
    }

    private void showSendSuccessDialog(String msg) {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage((CharSequence) msg).setPositiveButton(getString(R.string.str_finish), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent();
                it.putExtra("password", ResetPwActivity.this.password);
                it.putExtra("userName", ResetPwActivity.this.userName);
                ResetPwActivity.this.setResult(-1, it);
                dialog.cancel();
                ResetPwActivity.this.finish();
            }
        }).create().show();
    }

    private void sendMsg(String str) {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_TOAST;
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    private void sendMsgDialog(String str) {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_SHOW_DIALOG;
        msg.obj = str;
        this.myHandler.sendMessage(msg);
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

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
