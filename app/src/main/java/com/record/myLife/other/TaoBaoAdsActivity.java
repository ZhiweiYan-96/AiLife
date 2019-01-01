package com.record.myLife.other;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

public class TaoBaoAdsActivity extends BaseActivity {
    static String TAG = "override";
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id != R.id.btn_set_remind_retrospection && id == R.id.btn_set_remind_retrospection_value) {
            }
        }
    };
    WebView wv_tao_bao_ads_content;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_ads);
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
        this.wv_tao_bao_ads_content = (WebView) findViewById(R.id.wv_tao_bao_ads_content);
        this.wv_tao_bao_ads_content.getSettings().setCacheMode(0);
        this.wv_tao_bao_ads_content.getSettings().setJavaScriptEnabled(true);
        this.wv_tao_bao_ads_content.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        this.wv_tao_bao_ads_content.loadUrl("http://ai.m.taobao.com?pid=mm_50223077_7106540_23788560");
    }

    private void initSetUI() {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !this.wv_tao_bao_ads_content.canGoBack()) {
            return super.onKeyDown(keyCode, event);
        }
        this.wv_tao_bao_ads_content.goBack();
        return true;
    }

    public void onBackPressed() {
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
