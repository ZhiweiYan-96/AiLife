package com.record.myLife.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.record.conts.Sofeware;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.NetUtils;
import com.record.utils.Regexp;
import com.record.utils.ToastUtils;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {
    Button btn_login_back;
    Button btn_login_login;
    Context context;
    EditText ed_login_name;
    EditText ed_login_password;
    EditText ed_login_password2;
    String http = "";
    boolean isLogining = false;
    Thread loginThread = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_login_login) {
                String email = SignUpActivity.this.ed_login_name.getText().toString().trim();
                String pass = SignUpActivity.this.ed_login_password.getText().toString();
                String pass2 = SignUpActivity.this.ed_login_password2.getText().toString();
                if (email == null || email.length() == 0) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_userName_not_null));
                } else if (!email.matches(Regexp.email_regexp)) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_email_format_is_not_correct));
                } else if (email.length() > 64) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_email_to_long));
                } else if (pass == null || pass.length() == 0) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_password_not_null));
                } else if (pass2 == null || pass2.length() == 0) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_another_pw2));
                } else if (!pass.equals(pass2)) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_two_pw_not_same));
                } else if (pass.length() < 6) {
                    ToastUtils.toastShort(SignUpActivity.this.context, SignUpActivity.this.getString(R.string.str_password_should_more_than_six));
                } else {
                    if (pass.length() > 64) {
                        pass = pass.substring(0, 64);
                    }
                    if (NetUtils.isNetworkAvailable(SignUpActivity.this.context)) {
                        SignUpActivity.this.clikcLogin(email, pass);
                    }
                }
            } else if (id == R.id.btn_login_back) {
                SignUpActivity.this.finish();
                SignUpActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            ToastUtils.toastShort(SignUpActivity.this.context, msg.obj);
        }
    };
    RelativeLayout rl_login_title;

    class loginRun implements Runnable {
        loginRun() {
        }

        public void run() {
            try {
                SignUpActivity.this.isLogining = true;
                SignUpActivity.this.sendMsg("注册中...");
                String result = HttpRequestProxy.doPost(SignUpActivity.this.http, new HashMap(), "UTF-8");
                SignUpActivity.this.log(result);
                if (result != null) {
                    Map<String, Object> map = (Map) JSON.parse(result);
                    String error = (String) map.get("error");
                    if (error != null) {
                        SignUpActivity.this.isLogining = false;
                        if ("email has existed".equals(error)) {
                            SignUpActivity.this.sendMsg("用户名已存在！");
                            return;
                        } else if ("mail address is not valid".equals(error)) {
                            SignUpActivity.this.sendMsg("邮箱格式不正确！");
                            return;
                        } else {
                            return;
                        }
                    }
                    String uid = map.get("uid") + "";
                    if (uid == null || uid.length() <= 0) {
                        SignUpActivity.this.isLogining = false;
                        SignUpActivity.this.sendMsg("注册失败，请稍候再试!");
                        return;
                    }
                    String email = (String) map.get("email");
                    String password = (String) map.get("password");
                    Intent data = new Intent();
                    data.putExtra("email", email);
                    data.putExtra("password", password);
                    SignUpActivity.this.setResult(1, data);
                    SignUpActivity.this.sendMsg("注册成功!");
                    SignUpActivity.this.finish();
                    SignUpActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
                    return;
                }
                SignUpActivity.this.isLogining = false;
                SignUpActivity.this.sendMsg("注册失败，请稍候再试!");
            } catch (Exception e) {
                SignUpActivity.this.isLogining = false;
                SignUpActivity.this.sendMsg("注册失败，请稍候再试!");
                DbUtils.exceptionHandler(e);
            }
        }
    }

    class loginRun_v2 implements Runnable {
        String password;
        String userName;

        public loginRun_v2(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public void run() {
            try {
                SignUpActivity.this.isLogining = true;
                SignUpActivity.this.sendMsg(SignUpActivity.this.getString(R.string.str_signing_up));
                String http = Sofeware.HTTP_BASE + Sofeware.SIGN_UP;
                HashMap<String, String> params = new HashMap();
                params.put("token", Sofeware.getToken());
                params.put("userName", this.userName);
                params.put("password", this.password);
                String result = HttpRequestProxy.doPost(http, params, "UTF-8");
                SignUpActivity.this.log(result);
                if (result != null) {
                    JSONObject jsonObject = (JSONObject) JSON.parse(result);
                    int status = jsonObject.getIntValue("status");
                    if (status == 1) {
                        Intent data = new Intent();
                        data.putExtra("userName", this.userName);
                        data.putExtra("password", this.password);
                        SignUpActivity.this.setResult(-1, data);
                        SignUpActivity.this.sendMsg(jsonObject.getString("msg"));
                        SignUpActivity.this.finish();
                        SignUpActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
                        return;
                    } else if (status == 2) {
                        SignUpActivity.this.sendMsg(jsonObject.getString("msg"));
                        return;
                    } else if (status == 3) {
                        SignUpActivity.this.sendMsg(jsonObject.getString("msg"));
                        return;
                    } else {
                        SignUpActivity.this.sendMsg(SignUpActivity.this.getString(R.string.str_sign_up_fail));
                        return;
                    }
                }
                SignUpActivity.this.isLogining = false;
                SignUpActivity.this.sendMsg(SignUpActivity.this.getString(R.string.str_sign_up_fail));
            } catch (Exception e) {
                SignUpActivity.this.isLogining = false;
                SignUpActivity.this.sendMsg(SignUpActivity.this.getString(R.string.str_sign_up_fail));
                DbUtils.exceptionHandler(e);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.rl_login_title = (RelativeLayout) findViewById(R.id.rl_login_title);
        this.ed_login_name = (EditText) findViewById(R.id.ed_login_name);
        this.ed_login_password = (EditText) findViewById(R.id.ed_login_password);
        this.ed_login_password2 = (EditText) findViewById(R.id.ed_login_password2);
        this.btn_login_login = (Button) findViewById(R.id.btn_login_login);
        this.btn_login_back = (Button) findViewById(R.id.btn_login_back);
        this.btn_login_login.setText(getString(R.string.str_sign_up));
        this.rl_login_title.setVisibility(View.VISIBLE);
        this.ed_login_name.setHint(getString(R.string.str_email));
        this.ed_login_password.setHint(getString(R.string.str_password));
        this.btn_login_login.setOnClickListener(this.myClickListener);
        this.btn_login_back.setOnClickListener(this.myClickListener);
    }

    public void clikcLogin(String email, String pass) {
        if (!NetUtils.isNetworkAvailable(this.context)) {
            return;
        }
        if (this.loginThread == null || !this.loginThread.isAlive()) {
            this.loginThread = new Thread(new loginRun_v2(email, pass));
            this.loginThread.start();
            return;
        }
        ToastUtils.toastLong(this.context, getString(R.string.str_signing_up2));
    }

    public void sendMsg(String str) {
        Message msg = new Message();
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    public void onBackPressed() {
        super.onBackPressed();
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

    public void log(String str) {
        Log.i("override Login", ":" + str);
    }
}
