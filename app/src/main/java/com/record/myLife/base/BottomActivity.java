package com.record.myLife.base;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.main.DynamicActivity;
import com.record.myLife.main.MeActivity;
import com.record.myLife.main.StatisticActivity;
import com.record.myLife.main.TopActivity;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.thread.UploadThread;
import com.record.utils.DateTime;
import com.record.utils.LogUtils;
import com.record.utils.NetUtils;
import com.record.utils.PreferUtils;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.log.MyLog;
import com.umeng.analytics.MobclickAgent;

public class BottomActivity extends TabActivity {
    static String TAG = "override";
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id != R.id.btn_set_remind_retrospection && id == R.id.btn_set_remind_retrospection_value) {
            }
        }
    };
    TabHost tabHost;
    ImageView tv_tem_bottom_everyday;
    ImageView tv_tem_bottom_getup;
    ImageView tv_tem_bottom_me;
    ImageView tv_tem_bottom_tagit;
    TextView tv_tem_every;
    TextView tv_tem_getUp;
    TextView tv_tem_me;
    TextView tv_tem_today;

    public interface OnTabActivityResultListener {
        void onTabActivityResult(int i, int i2, Intent intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        SystemBarTintManager.setMIUIbar(this);
        init();
        initView();
    }

    private void init() {
        log("Create");
        this.context = this;
        TAG += getClass().getSimpleName();
        MyLog.MYLOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/itodayss/";
    }

    private void initView() {
        this.tv_tem_bottom_tagit = findViewById(R.id.tv_tem_bottom_tagit);
        this.tv_tem_bottom_everyday = findViewById(R.id.tv_tem_bottom_everyday);
        this.tv_tem_bottom_getup = findViewById(R.id.tv_tem_bottom_getup);
        this.tv_tem_bottom_me = findViewById(R.id.tv_tem_bottom_me);
        this.tv_tem_today = findViewById(R.id.tv_tem_today);
        this.tv_tem_every = findViewById(R.id.tv_tem_statistic);
        this.tv_tem_getUp = findViewById(R.id.tv_tem_getUp);
        this.tv_tem_me = findViewById(R.id.tv_tem_more);
        initTab();
        RelativeLayout rl_tem_bottom_menu_2 = (RelativeLayout) findViewById(R.id.rl_tem_bottom_menu_2);
        RelativeLayout rl_tem_bottom_menu_3 = (RelativeLayout) findViewById(R.id.rl_tem_bottom_menu_3);
        RelativeLayout rl_tem_bottom_menu_4 = (RelativeLayout) findViewById(R.id.rl_tem_bottom_menu_4);
        ((RelativeLayout) findViewById(R.id.rl_tem_bottom_menu_1)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                BottomActivity.this.tabHost.setCurrentTabByTag("first");
                BottomActivity.this.updateUiBottom(0);
            }
        });
        rl_tem_bottom_menu_2.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                BottomActivity.this.tabHost.setCurrentTabByTag("second");
                BottomActivity.this.updateUiBottom(1);
            }
        });
        rl_tem_bottom_menu_3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BottomActivity.this.tabHost.setCurrentTabByTag("third");
                BottomActivity.this.updateUiBottom(2);
            }
        });
        rl_tem_bottom_menu_4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BottomActivity.this.tabHost.setCurrentTabByTag("four");
                BottomActivity.this.updateUiBottom(3);
            }
        });
    }

    private void updateUiBottom(int index) {
        this.tv_tem_bottom_tagit.setImageResource(R.drawable.ic_bottom_tagit);
        this.tv_tem_today.setTextColor(getResources().getColor(R.color.gray_title));
        this.tv_tem_bottom_everyday.setImageResource(R.drawable.ic_bottom_everyday);
        this.tv_tem_every.setTextColor(getResources().getColor(R.color.gray_title));
        this.tv_tem_bottom_getup.setImageResource(R.drawable.ic_bottom_getup);
        this.tv_tem_getUp.setTextColor(getResources().getColor(R.color.gray_title));
        this.tv_tem_bottom_me.setImageResource(R.drawable.ic_bottom_me);
        this.tv_tem_me.setTextColor(getResources().getColor(R.color.gray_title));
        if (index == 0) {
            this.tv_tem_bottom_tagit.setImageResource(R.drawable.ic_bottom_tagit_blue);
            this.tv_tem_today.setTextColor(getResources().getColor(R.color.bg_blue1));
        } else if (1 == index) {
            this.tv_tem_bottom_everyday.setImageResource(R.drawable.ic_bottom_everyday_blue);
            this.tv_tem_every.setTextColor(getResources().getColor(R.color.bg_blue1));
        } else if (2 == index) {
            this.tv_tem_bottom_getup.setImageResource(R.drawable.ic_bottom_getup_blue);
            this.tv_tem_getUp.setTextColor(getResources().getColor(R.color.bg_blue1));
        } else if (3 == index) {
            this.tv_tem_bottom_me.setImageResource(R.drawable.ic_bottom_me_blue);
            this.tv_tem_me.setTextColor(getResources().getColor(R.color.bg_blue1));
        }
    }

    private void initTab() {
        this.tabHost = getTabHost();
        this.tabHost.addTab(this.tabHost.newTabSpec("first").setIndicator("first").setContent(new Intent(this, TopActivity.class)));
        this.tabHost.addTab(this.tabHost.newTabSpec("second").setIndicator("second").setContent(new Intent(this, StatisticActivity.class)));
        this.tabHost.addTab(this.tabHost.newTabSpec("third").setIndicator("third").setContent(new Intent(this, DynamicActivity.class)));
        this.tabHost.addTab(this.tabHost.newTabSpec("four").setIndicator("four").setContent(new Intent(this, MeActivity.class)));
        if (getIntent() != null) {
            int index = getIntent().getIntExtra("item", 0);
            this.tabHost.setCurrentTab(index);
            updateUiBottom(index);
            return;
        }
        updateUiBottom(0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity subActivity = getLocalActivityManager().getCurrentActivity();
        if (subActivity instanceof OnTabActivityResultListener) {
            ((OnTabActivityResultListener) subActivity).onTabActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        log("onResume");
        MobclickAgent.onResume(this);
        startService(new Intent(this.context, TimerService.class));
    }

    public void onPause() {
        super.onPause();
        log("onPause");
        MobclickAgent.onPause(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        uploadData();
    }

    private void uploadData() {
        try {
            if (DateTime.getDateString().equals(PreferUtils.getString(this.context, Val.CONFIGURE_LAST_UPLOAD_DATE, ""))) {
                LogUtils.log("今天已上传过数据！");
            } else if (PreferUtils.getInt(this.context, Val.CONFIGURE_UPLOAD_NET_TYPE, 1) == 1) {
                if (NetUtils.isWiFiAvailable(this.context)) {
                    isUserLogin();
                }
            } else if (NetUtils.isNetworkAvailable2noToast(this.context)) {
                isUserLogin();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void isUserLogin() {
        if (UserUtils.isUserLogin(this.context)) {
            Thread thread = UploadThread.getInstance(this.context);
            if (!thread.isAlive()) {
                thread.start();
            }
            PreferUtils.putString(this.context, Val.CONFIGURE_LAST_UPLOAD_DATE, DateTime.getDateString());
        }
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
