package com.record.myLife.settings.about;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.GeneralHelper;
import com.record.utils.ToastUtils;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends BaseActivity {
    TextView about_tv_info4;
    TextView about_tv_info5;
    TextView about_tv_info6;
    Button btn_support_back;
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.about_tv_info4) {
                try {
                    if (AboutActivity.this.copyStr("202361559")) {
                        GeneralHelper.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_had_copied));
                    } else {
                        GeneralHelper.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_copy_failure));
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                    GeneralHelper.toastShort(AboutActivity.this.context, "复制出错啦，可能是系统版本不支持此操作！");
                }
            } else if (id == R.id.about_tv_info5) {
                if (AboutActivity.this.copyStr("爱今天APP")) {
                    GeneralHelper.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_had_copied));
                } else {
                    GeneralHelper.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_copy_failure));
                }
            } else if (id == R.id.btn_support_back) {
                AboutActivity.this.finish();
                AboutActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.about_tv_info6) {
                try {
                    AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=624604006&version=1")));
                    if (AboutActivity.this.copyStr("624604006")) {
                        GeneralHelper.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_had_copied));
                    } else {
                        GeneralHelper.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_copy_failure));
                    }
                } catch (Exception e2) {
                    DbUtils.exceptionHandler(e2);
                    ToastUtils.toastShort(AboutActivity.this.context, AboutActivity.this.getString(R.string.str_copy_fail));
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.about_tv_info4 = (TextView) findViewById(R.id.about_tv_info4);
        this.about_tv_info5 = (TextView) findViewById(R.id.about_tv_info5);
        this.about_tv_info6 = (TextView) findViewById(R.id.about_tv_info6);
        this.btn_support_back = (Button) findViewById(R.id.btn_support_back);
        this.about_tv_info4.setOnClickListener(this.myClickListener);
        this.about_tv_info5.setOnClickListener(this.myClickListener);
        this.about_tv_info6.setOnClickListener(this.myClickListener);
        this.btn_support_back.setOnClickListener(this.myClickListener);
    }

    @SuppressLint({"NewApi"})
    public boolean copyStr(String str) {
        try {
            ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cmb != null) {
                cmb.setText(str);
                return true;
            }
            android.text.ClipboardManager cmb2 = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cmb2 != null) {
                cmb2.setText(str);
                return true;
            }
            return false;
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return false;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
