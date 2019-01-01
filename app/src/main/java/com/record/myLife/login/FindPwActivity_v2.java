package com.record.myLife.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import com.record.utils.DateTime;
import com.record.utils.GeneralUtils;
import com.record.utils.NetUtils;
import com.record.utils.PreferUtils;
import com.record.utils.Regexp;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FindPwActivity_v2 extends BaseActivity {
    static String TAG = "override";
    int HANDLER_SHOW_DIALOG = 2;
    int HANDLER_TOAST = 1;
    int REQUEST_CODE_RESET_PASSWORD = 1;
    Button btn_find_pw_2_next;
    Button btn_find_pw_2_next2;
    Button btn_set_back;
    Context context;
    EditText ed_find_pw_2_name;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_set_back) {
                FindPwActivity_v2.this.finish();
            } else if (id == R.id.btn_find_pw_2_next) {
                FindPwActivity_v2.this.next();
            } else if (id == R.id.btn_find_pw_2_next2) {
                FindPwActivity_v2.this.next2();
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == FindPwActivity_v2.this.HANDLER_TOAST) {
                GeneralUtils.toastLong(FindPwActivity_v2.this.context, msg.obj.toString());
            } else if (msg.arg1 == FindPwActivity_v2.this.HANDLER_SHOW_DIALOG) {
                FindPwActivity_v2.this.btn_find_pw_2_next2.setVisibility(0);
                FindPwActivity_v2.this.showSendSuccessDialog(msg.obj.toString());
            }
        }
    };
    TextWatcher myTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            FindPwActivity_v2.this.isShowBtnNext2();
        }
    };
    Thread sendVerifyThread;
    String userName = "";

    class sendVerifyCode implements Runnable {
        String email = "";

        public sendVerifyCode(String email) {
            this.email = email;
        }

        public void run() {
            try {
                Map<String, String> map = new HashMap();
                map.put("userName", this.email);
                String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.SEND_VERRIFY_CODE, map);
                if (result != null) {
                    JSONObject rootJson = JSON.parseObject(result);
                    int status = rootJson.getIntValue("status");
                    String msg = rootJson.getString("msg");
                    if (status == 1) {
                        SharedPreferences sp = PreferUtils.getSP(FindPwActivity_v2.this.context);
                        sp.edit().putString(Val.CONFIGURE_FIND_PW_USERNAME, this.email).commit();
                        sp.edit().putString(Val.CONFIGURE_FIND_PW_TIME, DateTime.getTimeString()).commit();
                        FindPwActivity_v2.this.sendMsgDialog(msg);
                        return;
                    }
                    FindPwActivity_v2.this.sendMsg(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw_v2);
        SystemBarTintManager.setMIUIbar(this);
        this.context = this;
        TAG += getClass().getSimpleName();
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_find_pw_2_next = (Button) findViewById(R.id.btn_find_pw_2_next);
        this.btn_find_pw_2_next2 = (Button) findViewById(R.id.btn_find_pw_2_next2);
        this.ed_find_pw_2_name = (EditText) findViewById(R.id.ed_find_pw_2_name);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.btn_find_pw_2_next.setOnClickListener(this.myClickListener);
        this.btn_find_pw_2_next2.setOnClickListener(this.myClickListener);
        this.ed_find_pw_2_name.addTextChangedListener(this.myTextWatcher);
        this.userName = getIntent().getStringExtra("userName");
        if (this.userName == null || this.userName.equalsIgnoreCase("null")) {
            this.userName = "";
        }
        if (this.userName.length() > 0) {
            this.ed_find_pw_2_name.setText(this.userName);
        }
    }

    private void isShowBtnNext2() {
        try {
            String email = this.ed_find_pw_2_name.getText().toString();
            String tempUserName = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_FIND_PW_USERNAME, "");
            log("email:" + email + ",tempUserName:" + tempUserName);
            if (email == null || email.length() <= 0 || !email.matches(Regexp.email_regexp) || tempUserName == null || tempUserName.length() <= 0 || !email.equals(tempUserName)) {
                this.btn_find_pw_2_next2.setVisibility(8);
                this.btn_find_pw_2_next.setText(getString(R.string.str_get_verifyCode));
                return;
            }
            String tempTime = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_FIND_PW_TIME, "2000-01-01 00:00:00");
            if (tempTime == null || DateTime.pars2Calender(tempTime).getTime().getTime() <= Calendar.getInstance().getTime().getTime() - 1800000) {
                this.btn_find_pw_2_next2.setVisibility(8);
                this.btn_find_pw_2_next.setText(getString(R.string.str_get_verifyCode));
                return;
            }
            this.btn_find_pw_2_next2.setVisibility(0);
            this.btn_find_pw_2_next.setText(getString(R.string.str_get_verifyCode2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSetUI() {
    }

    private void next2() {
        String email = this.ed_find_pw_2_name.getText().toString();
        if (email != null && email.length() != 0 && email.matches(Regexp.email_regexp)) {
            this.userName = email;
            Intent it = new Intent(this.context, ResetPwActivity.class);
            it.putExtra("userName", this.userName);
            startActivityForResult(it, this.REQUEST_CODE_RESET_PASSWORD);
        }
    }

    private void next() {
        String email = this.ed_find_pw_2_name.getText().toString();
        if (email == null || email.length() == 0) {
            ToastUtils.toastShort(this.context, getString(R.string.str_userName_not_null));
        } else if (email.matches(Regexp.email_regexp)) {
            this.userName = email;
            if (!NetUtils.isNetworkAvailable(this.context)) {
                return;
            }
            if (this.sendVerifyThread == null || !this.sendVerifyThread.isAlive()) {
                this.sendVerifyThread = new Thread(new sendVerifyCode(this.userName));
                this.sendVerifyThread.start();
                GeneralUtils.toastShort(this.context, getStr(R.string.str_sending_verifycode));
                return;
            }
            GeneralUtils.toastShort(this.context, getStr(R.string.str_sending_verifycode2));
        } else {
            ToastUtils.toastShort(this.context, getString(R.string.str_email_format_is_not_correct));
        }
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

    private void showSendSuccessDialog(String msg) {
        new Builder(this.context).setTitle(getStr(R.string.str_prompt)).setMessage((CharSequence) msg).setPositiveButton(getString(R.string.str_next), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(FindPwActivity_v2.this.context, ResetPwActivity.class);
                it.putExtra("userName", FindPwActivity_v2.this.userName);
                FindPwActivity_v2.this.startActivityForResult(it, FindPwActivity_v2.this.REQUEST_CODE_RESET_PASSWORD);
                dialog.cancel();
            }
        }).create().show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.REQUEST_CODE_RESET_PASSWORD && resultCode == -1) {
            String password = data.getStringExtra("password");
            Intent it = new Intent();
            it.putExtra("password", password);
            it.putExtra("userName", this.userName);
            setResult(-1, it);
            finish();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        isShowBtnNext2();
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
        Log.i(TAG, ":" + str);
    }
}
