package com.record.myLife.settings.label;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;

public class LabelMainActivity extends BaseActivity {
    Button btn_label_back;
    Button btn_label_mood;
    Button btn_label_subtype;
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            Intent it;
            if (id == R.id.btn_label_subtype) {
                it = new Intent(LabelMainActivity.this.context, LabelInfoActivity_v2.class);
                it.putExtra(a.a, 1);
                LabelMainActivity.this.startActivity(it);
            } else if (id == R.id.btn_label_mood) {
                it = new Intent(LabelMainActivity.this.context, LabelInfoActivity_v2.class);
                it.putExtra(a.a, 2);
                LabelMainActivity.this.startActivity(it);
            } else if (id == R.id.btn_label_back) {
                LabelMainActivity.this.finish();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.btn_label_subtype = (Button) findViewById(R.id.btn_label_subtype);
        this.btn_label_mood = (Button) findViewById(R.id.btn_label_mood);
        this.btn_label_back = (Button) findViewById(R.id.btn_label_back);
        this.btn_label_back.setOnClickListener(this.myClickListener);
        this.btn_label_subtype.setOnClickListener(this.myClickListener);
        this.btn_label_mood.setOnClickListener(this.myClickListener);
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
