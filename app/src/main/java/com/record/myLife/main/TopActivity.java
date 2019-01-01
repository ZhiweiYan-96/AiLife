package com.record.myLife.main;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BottomActivity.OnTabActivityResultListener;
import com.record.myLife.history.HistoryActivity_v2;
import com.record.myLife.main.tomato.TomatoActivity;
import com.record.myLife.settings.SetActivity;
import com.record.utils.FormatUtils;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.Calendar;

public class TopActivity extends TabActivity implements OnClickListener {
    static String TAG = "override";
    public static TextView tv_today_upload_progress;
    Context context;
    View iv_today_history;
    Button iv_today_set_v2;
    TabHost tabHost;
    TextView tv_today_v4_today;
    TextView tv_today_v4_tomato;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        init();
        initView();
    }

    private void initTab() {
        this.tabHost = getTabHost();
        this.tabHost.addTab(this.tabHost.newTabSpec("first").setIndicator("first").setContent(new Intent(this, TodayActivity.class)));
        this.tabHost.addTab(this.tabHost.newTabSpec("second").setIndicator("second").setContent(new Intent(this, TomatoActivity.class)));
    }

    private void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
    }

    private void initView() {
        initTab();
        tv_today_upload_progress = (TextView) findViewById(R.id.tv_today_upload_progress);
        this.iv_today_history = findViewById(R.id.ll_today_history);
        this.iv_today_set_v2 = (Button) findViewById(R.id.iv_today_set_v2);
        this.tv_today_v4_today = (TextView) findViewById(R.id.tv_today_v4_today);
        this.tv_today_v4_tomato = (TextView) findViewById(R.id.tv_today_v4_tomato);
        this.tv_today_v4_today.setOnClickListener(this);
        this.tv_today_v4_tomato.setOnClickListener(this);
        this.iv_today_history.setOnClickListener(this);
        this.iv_today_set_v2.setOnClickListener(this);
        if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_TYPE_OF_EXIT_APP, 0) == 1) {
            this.tabHost.setCurrentTab(1);
            updateUiTomato();
        } else {
            updateUiToday();
        }
        setTodayLeftHour(this.context, tv_today_upload_progress);
    }

    public static void setTodayLeftHour(Context context, TextView tv) {
        if (tv != null) {
            try {
                double left2;
                long now = Calendar.getInstance().getTime().getTime();
                int startHour = PreferUtils.getInt(context, Val.CONFIGURE_COUNTER_DOWN_START_TIME, 0);
                Calendar c = Calendar.getInstance();
                if (startHour - 1 >= 0) {
                    c.set(Calendar.HOUR_OF_DAY, startHour - 1);
                } else {
                    c.set(Calendar.HOUR_OF_DAY, 23);
                }
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                long end2 = c.getTime().getTime();
                if (end2 < now) {
                    left2 = (86400.0d - (((double) (now - end2)) / 1000.0d)) / 3600.0d;
                } else {
                    left2 = (((double) (end2 - now)) / 1000.0d) / 3600.0d;
                }
                tv.setText(FormatUtils.format_1fra(left2) + "h");
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
        }
    }

    private void updateUiToday() {
        this.tv_today_v4_today.setBackgroundResource(R.drawable.x_blue_bg_black_frame_left);
        this.tv_today_v4_today.setTextColor(-1);
        this.tv_today_v4_tomato.setBackgroundResource(R.drawable.x_tran_bg_black_frame_right);
        this.tv_today_v4_tomato.setTextColor(getResources().getColor(R.color.gray_title));
    }

    private void updateUiTomato() {
        this.tv_today_v4_today.setBackgroundResource(R.drawable.x_tran_bg_black_frame_left);
        this.tv_today_v4_today.setTextColor(getResources().getColor(R.color.gray_title));
        this.tv_today_v4_tomato.setBackgroundResource(R.drawable.x_blue_bg_black_frame_right);
        this.tv_today_v4_tomato.setTextColor(-1);
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
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_today_set_v2:
                startSetActivity();
                MobclickAgent.onEvent(getApplicationContext(), "today_today_click_Settings_btn");
                return;
            case R.id.tv_today_v4_today:
                this.tabHost.setCurrentTabByTag("first");
                PreferUtils.getSP(this.context).edit().putInt(Val.CONFIGURE_TYPE_OF_EXIT_APP, 0).commit();
                updateUiToday();
                MobclickAgent.onEvent(getApplicationContext(), "today_today_click_today_btn");
                return;
            case R.id.tv_today_v4_tomato:
                this.tabHost.setCurrentTabByTag("second");
                PreferUtils.getSP(this.context).edit().putInt(Val.CONFIGURE_TYPE_OF_EXIT_APP, 1).commit();
                updateUiTomato();
                MobclickAgent.onEvent(getApplicationContext(), "today_today_click_tomato_btn");
                return;
            case R.id.ll_today_history:
                startHistoryAcitivity();
                MobclickAgent.onEvent(getApplicationContext(), "today_today_click_top_left_corner_Timeline_btn");
                return;
            default:
                return;
        }
    }

    private void startHistoryAcitivity() {
        startActivity(new Intent(this, HistoryActivity_v2.class));
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    private void startSetActivity() {
        startActivity(new Intent(this.context, SetActivity.class));
        overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
    }
}
