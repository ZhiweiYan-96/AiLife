package com.record.myLife.settings.about;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.GeneralHelper;
import com.umeng.analytics.MobclickAgent;

public class SupportActivity extends BaseActivity {
    TextView about_tv_info3;
    Button btn_support_back;
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        @SuppressLint({"NewApi"})
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.about_tv_info3) {
                if (SupportActivity.this.copyStr("624604006@qq.com")) {
                    GeneralHelper.toastShort(SupportActivity.this.context, SupportActivity.this.getString(R.string.str_had_copied));
                } else {
                    GeneralHelper.toastShort(SupportActivity.this.context, SupportActivity.this.getString(R.string.str_copy_failure));
                }
            } else if (id == R.id.btn_support_back) {
                SupportActivity.this.finish();
                SupportActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.about_tv_info3 = (TextView) findViewById(R.id.about_tv_info3);
        this.btn_support_back = (Button) findViewById(R.id.btn_support_back);
        this.about_tv_info3.setOnClickListener(this.myClickListener);
        this.btn_support_back.setOnClickListener(this.myClickListener);
    }

    @SuppressLint({"NewApi"})
    public boolean copyStr(String str) {
        try {
            ClipboardManager cmb = (ClipboardManager) getSystemService("clipboard");
            if (cmb != null) {
                cmb.setText(str);
                return true;
            }
            android.text.ClipboardManager cmb2 = (android.text.ClipboardManager) getSystemService("clipboard");
            if (cmb2 != null) {
                cmb2.setText(str);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }
}
