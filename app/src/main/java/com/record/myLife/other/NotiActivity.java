package com.record.myLife.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.MyNotification;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;

public class NotiActivity extends BaseActivity {
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("收到通知");
        SystemBarTintManager.setMIUIbar(this);
        Intent it = getIntent();
        if (it != null) {
            if (it.getIntExtra(a.a, 0) == 0) {
            }
            new MyNotification(this.context).initRetrospectNoti();
            return;
        }
        finish();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void log(String str) {
        Log.i("override NotiActivity", ":" + str);
    }
}
