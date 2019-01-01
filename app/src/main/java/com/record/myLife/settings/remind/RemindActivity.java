package com.record.myLife.settings.remind;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

public class RemindActivity extends BaseActivity {
    static String TAG = "override";
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id != R.id.btn_set_remind_retrospection && id == R.id.btn_set_remind_retrospection_value) {
            }
        }
    };
    Thread myThread;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
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

    public String getStr(int str) {
        return getResources().getString(str);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
