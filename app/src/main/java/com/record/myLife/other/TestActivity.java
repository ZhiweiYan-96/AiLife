package com.record.myLife.other;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.User;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.login.SignUpActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.NetUtils;
import com.record.utils.ToastUtils;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpClients;
import com.umeng.analytics.MobclickAgent;

public class TestActivity extends BaseActivity {
    Button btn_login_login;
    Context context;
    EditText ed_login_name;
    EditText ed_login_password;
    String http = "";
    boolean isLogining = false;
    Thread loginThread = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_login_login) {
                String email = TestActivity.this.ed_login_name.getText().toString().trim();
                if (NetUtils.isNetworkAvailable(TestActivity.this.context)) {
                    TestActivity.this.login(email, "");
                }
            } else if (id == R.id.tv_login_reg) {
                TestActivity.this.startActivityForResult(new Intent(TestActivity.this.context, SignUpActivity.class), 8);
                TestActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.tv_login_try) {
                Cursor cur = DbUtils.getDb(TestActivity.this.context).rawQuery("Select * from t_user", null);
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id2 = cur.getString(cur.getColumnIndex("id"));
                        String username = cur.getString(cur.getColumnIndex("userName"));
                        if (username != null && username.equals("测试")) {
                            TestActivity.this.initLoginDb();
                            ContentValues values = new ContentValues();
                            values.put("isLogin", Integer.valueOf(1));
                            DbUtils.getDb(TestActivity.this.context).update("t_user", values, "id is ?", new String[]{id2});
                            UserUtils.isLoginUser(TestActivity.this.context);
                            TestActivity.this.finish();
                            return;
                        }
                    }
                }
                UserUtils.isLoginUser(TestActivity.this.context);
                TestActivity.this.sendMsg("离线试用成功!");
                TestActivity.this.sendBroadcast(new Intent(Val.INTENT_ACTION_LOGIN));
                TestActivity.this.finish();
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastUtils.toastShort(TestActivity.this.context, (String)msg.obj);
        }
    };
    RelativeLayout rl_login_title;
    TextView tv_login_reg;
    TextView tv_login_try;

    class loginRun implements Runnable {
        loginRun() {
        }

        public void run() {
            TestActivity.this.log(new HttpClients((Activity) TestActivity.this.context).doGet(TestActivity.this.http));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.rl_login_title = (RelativeLayout) findViewById(R.id.rl_login_title);
        this.ed_login_name = (EditText) findViewById(R.id.ed_login_name);
        this.ed_login_password = (EditText) findViewById(R.id.ed_login_password);
        this.btn_login_login = (Button) findViewById(R.id.btn_login_login);
        this.tv_login_reg = (TextView) findViewById(R.id.tv_login_reg);
        this.tv_login_try = (TextView) findViewById(R.id.tv_login_try);
        Cursor cur = DbUtils.getDb2(this.context).rawQuery("select Id from t_user", null);
        if ("测试".equals(User.getInstance().getUserName()) || cur.getCount() >= 2) {
            this.tv_login_try.setVisibility(8);
        } else {
            this.tv_login_try.setVisibility(0);
        }
        DbUtils.close(cur);
        this.rl_login_title.setVisibility(8);
        this.btn_login_login.setOnClickListener(this.myClickListener);
        this.tv_login_reg.setOnClickListener(this.myClickListener);
        this.tv_login_try.setOnClickListener(this.myClickListener);
    }

    public void login(String email, String pass) {
        ToastUtils.toastLong(this.context, "登陆中...");
        this.http = "http://4dian.sinaapp.com/api/settings/" + email + ".json";
        log(this.http);
        if (this.isLogining) {
            ToastUtils.toastLong(this.context, "登陆中,请稍候...");
            return;
        }
        this.loginThread = new Thread(new loginRun());
        this.loginThread.start();
    }

    public void initLoginDb() {
        ContentValues values = new ContentValues();
        values.put("isLogin", Integer.valueOf(0));
        DbUtils.getDb(this.context).update("t_user", values, "isLogin is ?", new String[]{"1"});
    }

    public void sendMsg(String str) {
        Message msg = new Message();
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            data.getStringExtra("email");
            this.ed_login_name.setText(data.getStringExtra("email"));
            this.ed_login_password.setText(data.getStringExtra("password"));
        }
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
