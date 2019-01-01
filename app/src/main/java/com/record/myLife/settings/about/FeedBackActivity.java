package com.record.myLife.settings.about;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.NetUtils;
import com.record.utils.PushInitUtils;
import com.record.utils.db.DbUtils;
import com.record.utils.net.EmailUtils;
import com.umeng.analytics.MobclickAgent;

public class FeedBackActivity extends BaseActivity {
    Button btn_feedback_back;
    Button btn_feedback_commit;
    OnClickListener clickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_feedback_commit) {
                if (FeedBackActivity.this.et_feedback_msg.getText().toString().equals("")) {
                    GeneralHelper.toastShort(FeedBackActivity.this.context, "您还没输入反馈内容哦！");
                } else if (FeedBackActivity.this.et_feedback_msg.getText().toString().trim().length() > 0) {
                    FeedBackActivity.this.saveData();
                    if (!NetUtils.isNetworkAvailable2noToast(FeedBackActivity.this.context)) {
                        GeneralHelper.toastShort(FeedBackActivity.this.context, FeedBackActivity.this.getString(R.string.str_didnt_detect_network));
                        GeneralHelper.toastLong(FeedBackActivity.this.context, FeedBackActivity.this.getString(R.string.str_save_feed_back_data));
                        FeedBackActivity.this.finish();
                    } else if (FeedBackActivity.this.emailThread == null) {
                        GeneralHelper.toastShort(FeedBackActivity.this.context, FeedBackActivity.this.getString(R.string.str_sending));
                        FeedBackActivity.this.emailThread = new Thread(new commitRunnable());
                        FeedBackActivity.this.emailThread.start();
                    }
                } else {
                    GeneralHelper.toastShort(FeedBackActivity.this.context, "您还没输入信息哦！");
                }
            } else if (id == R.id.btn_feedback_back) {
                FeedBackActivity.this.finish();
                FeedBackActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            }
        }
    };
    private Activity context;
    Thread emailThread = null;
    EditText et_feedback_email;
    EditText et_feedback_msg;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GeneralHelper.toastShort(FeedBackActivity.this.context, "发送成功！");
            GeneralHelper.toastShort(FeedBackActivity.this.context, "感请您对爱今天的支持！");
            FeedBackActivity.this.finish();
        }
    };
    long saveDbId = 0;

    class commitRunnable implements Runnable {
        commitRunnable() {
        }

        public void run() {
            String content = "";
            try {
                content = FeedBackActivity.this.et_feedback_msg.getText().toString().trim();
                if (content.length() > 100) {
                    content = content.substring(0, 99);
                }
                EmailUtils.send(FeedBackActivity.this.getString(R.string.app_name) + FeedBackActivity.this.getPackageManager().getPackageInfo(FeedBackActivity.this.context.getPackageName(), 0).versionName + " 反馈:" + FeedBackActivity.this.et_feedback_email.getText().toString() + " 内容：" + content, FeedBackActivity.this.et_feedback_msg.getText().toString().trim());
                FeedBackActivity.this.myHandler.sendEmptyMessage(1);
                if (FeedBackActivity.this.saveDbId > 0) {
                    ContentValues values = new ContentValues();
                    values.put("isSended", Integer.valueOf(1));
                    DbUtils.getDb(FeedBackActivity.this.context).update("t_feed_back", values, " Id is " + FeedBackActivity.this.saveDbId, null);
                }
                FeedBackActivity.this.emailThread = null;
            } catch (Exception e) {
                e.printStackTrace();
                FeedBackActivity.this.emailThread = null;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.et_feedback_msg = (EditText) findViewById(R.id.et_feedback_msg);
        this.et_feedback_email = (EditText) findViewById(R.id.et_feedback_email);
        this.btn_feedback_commit = (Button) findViewById(R.id.btn_feedback_commit);
        this.btn_feedback_back = (Button) findViewById(R.id.btn_feedback_back);
        this.btn_feedback_commit.setOnClickListener(this.clickListener);
        this.btn_feedback_back.setOnClickListener(this.clickListener);
    }

    private void saveData() {
        String content = this.et_feedback_msg.getText().toString().trim();
        String email = this.et_feedback_email.getText().toString().trim();
        ContentValues values = new ContentValues();
        values.put(PushInitUtils.RESPONSE_CONTENT, content);
        values.put("userId", DbUtils.queryUserId(this.context));
        values.put("contact", email);
        values.put("sendTime", DateTime.getTimeString());
        this.saveDbId = DbUtils.getDb(this.context).insert("t_feed_back", null, values);
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

    public void log(String str) {
        Log.i("override SetActivity", ":" + str);
    }
}
