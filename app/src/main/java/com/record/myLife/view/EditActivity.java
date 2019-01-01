package com.record.myLife.view;

import android.content.ContentValues;
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
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.record.bean.User;
import com.record.conts.Sofeware;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.main.UserInfoActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.GeneralUtils;
import com.record.utils.NetUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;

public class EditActivity extends BaseActivity {
    static String TAG = "override";
    private int EDIT_TYPE = 0;
    private int EDIT_USER_INFO_NICKNAME = 1;
    private int EDIT_USER_INFO_QQ = 2;
    int MSG_WHAT_ERROR = -1;
    int MSG_WHAT_IS_NICKNAME_EXIST = 1;
    Button btn_edit_save;
    Button btn_support_back;
    Context context;
    EditText et_edit_tv;
    int maxCount = 12;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_support_back) {
                EditActivity.this.onBackPressed();
            } else if (id == R.id.btn_edit_save) {
                EditActivity.this.saveData();
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.what == EditActivity.this.MSG_WHAT_IS_NICKNAME_EXIST) {
                    if (msg == null || msg.obj == null) {
                        GeneralUtils.toastShort(EditActivity.this.context, "访问出错，请稍候再试！");
                        return;
                    }
                    try {
                        JSONObject json = (JSONObject) JSON.parse(msg.obj.toString());
                        if (json != null) {
                            int status = json.getInteger("status").intValue();
                            String str = json.getString("msg");
                            String nickName = json.getString("nickName");
                            if (status <= 0) {
                                GeneralUtils.toastShort(EditActivity.this.context, str);
                                return;
                            } else if (nickName != null && nickName.trim().length() > 0) {
                                ContentValues values = new ContentValues();
                                values.put("nickname", nickName);
                                values.put("endUpdateTime", DateTime.getTimeString());
                                DbUtils.getDb(EditActivity.this.context).update("t_user", values, "id = " + DbUtils.queryUserId(EditActivity.this.context), null);
                                GeneralUtils.toastShort(EditActivity.this.context, str);
                                if (EditActivity.this != null && !EditActivity.this.isFinishing()) {
                                    EditActivity.this.onBackPressed();
                                    return;
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                        GeneralUtils.toastShort(EditActivity.this.context, "访问出错，请稍候再试！");
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                        GeneralUtils.toastShort(EditActivity.this.context, "访问出错，请稍候再试！");
                    }
                } else if (msg.what == EditActivity.this.MSG_WHAT_ERROR) {
                    GeneralUtils.toastShort(EditActivity.this.context, "访问出错，请稍候再试！");
                }
            } catch (Exception e2) {
                DbUtils.exceptionHandler(e2);
            }
        }
    };
    Thread myThread = null;
    TextView tv_edit_title;

    class myRunnable implements Runnable {
        String nickName;

        public myRunnable(String nickName) {
            this.nickName = nickName;
        }

        public void run() {
            try {
                HashMap<String, Object> map = new HashMap();
                map.put("nickName", this.nickName);
                if (User.getInstance().getUid() != null && User.getInstance().getUid().length() > 0) {
                    map.put("uid", User.getInstance().getUid());
                }
                String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.IS_NICKNAME_EXIST, map);
                Message msg = new Message();
                msg.obj = result;
                msg.what = EditActivity.this.MSG_WHAT_IS_NICKNAME_EXIST;
                EditActivity.this.myHandler.sendMessage(msg);
                EditActivity.this.myThread = null;
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                EditActivity.this.myHandler.sendEmptyMessage(EditActivity.this.MSG_WHAT_ERROR);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
        this.btn_support_back = (Button) findViewById(R.id.btn_support_back);
        this.btn_edit_save = (Button) findViewById(R.id.btn_edit_save);
        this.tv_edit_title = (TextView) findViewById(R.id.tv_edit_title);
        this.et_edit_tv = (EditText) findViewById(R.id.et_edit_tv);
        this.btn_support_back.setOnClickListener(this.myClickListener);
        this.btn_edit_save.setOnClickListener(this.myClickListener);
        Intent it = getIntent();
        String action = it.getAction();
        if (action == null) {
            finish();
        } else if (Val.INTENT_ACTION_EDIT_USER_INFO.equals(action)) {
            editUserInfo(it);
        }
    }

    private void editUserInfo(Intent it) {
        String column = it.getStringExtra("column");
        String columnValue = it.getStringExtra("columnValue");
        if (UserInfoActivity.COLUMN_NICKNAME.equals(column)) {
            this.EDIT_TYPE = this.EDIT_USER_INFO_NICKNAME;
            this.tv_edit_title.setText(getString(R.string.str_nickname));
            if (isNotnull(columnValue)) {
                this.et_edit_tv.setText(columnValue);
            }
            this.et_edit_tv.setInputType(1);
            this.et_edit_tv.setHint(getString(R.string.str_input_your_nickname) + "（不超过" + this.maxCount + "个字符）");
        } else if (UserInfoActivity.COLUMN_QQ.equals(column)) {
            this.EDIT_TYPE = this.EDIT_USER_INFO_QQ;
            this.tv_edit_title.setText(getString(R.string.str_qq));
            if (isNotnull(columnValue)) {
                this.et_edit_tv.setText(columnValue);
            }
            this.et_edit_tv.setInputType(2);
            this.et_edit_tv.setHint(getString(R.string.str_qq));
        }
    }

    private void saveData() {
        if (this.EDIT_TYPE == this.EDIT_USER_INFO_NICKNAME) {
            isServerHadNickName(this.et_edit_tv.getText().toString().trim());
        } else if (this.EDIT_TYPE == this.EDIT_USER_INFO_QQ) {
            String qq = this.et_edit_tv.getText().toString().trim();
            if (qq.length() > 0) {
                ContentValues values = new ContentValues();
                values.put("qq", qq);
                values.put("endUpdateTime", DateTime.getTimeString());
                DbUtils.getDb(this.context).update("t_user", values, "id = " + DbUtils.queryUserId(this.context), null);
            }
            onBackPressed();
        }
    }

    private void isServerHadNickName(String nickName) {
        if (nickName != null) {
            String str = nickName.trim();
            if (str.length() > this.maxCount) {
                nickName = str.substring(0, this.maxCount);
            }
        }
        if (nickName == null || nickName.length() == 0) {
            GeneralUtils.toastShort(this.context, getResources().getString(R.string.str_please_input_nick));
            return;
        }
        try {
            if (nickName.equals(User.getInstance().getNickname())) {
                finish();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.myThread != null && this.myThread.isAlive()) {
            GeneralUtils.toastShort(this.context, getResources().getString(R.string.str_wait_please));
        } else if (NetUtils.isNetworkAvailable(this.context)) {
            this.myThread = new Thread(new myRunnable(nickName));
            this.myThread.start();
            GeneralUtils.toastShort(this.context, getResources().getString(R.string.str_is_nickname_exist));
        }
    }

    private boolean isNotnull(String str) {
        if (str == null || str.length() <= 0 || str.equalsIgnoreCase("null") || str.equals("0")) {
            return false;
        }
        return true;
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
