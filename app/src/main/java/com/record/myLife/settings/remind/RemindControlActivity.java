package com.record.myLife.settings.remind;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.MyNotification;
import com.record.utils.Val;
import com.umeng.analytics.MobclickAgent;

public class RemindControlActivity extends BaseActivity {
    static String TAG = "override";
    String action;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        TAG += getClass().getSimpleName();
        this.action = getIntent().getAction();
        if (this.action == null) {
            finish();
            return;
        }
        SystemBarTintManager.setMIUIbar(this);
        if (Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(this.action)) {
            new MyNotification(this.context).initRetrospectNoti();
        } else if (Val.INTENT_ACTION_NOTI_MORNING_VOICE.equals(this.action)) {
            new MyNotification(this.context).initMorningVoiceNoti();
        }
        finish();
    }

    private void initSetUI() {
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
