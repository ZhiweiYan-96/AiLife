package com.record.myLife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.record.bean.Record2;
import com.record.bean.TimePieRecord;
import com.record.myLife.view.TimePie;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Iterator;

public class TemActivity extends Activity {
    static String TAG = "override";
    Context context;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id != R.id.btn_set_remind_retrospection && id == R.id.btn_set_remind_retrospection_value) {
            }
        }
    };
    TimePie tp_time_pie;
    UiComponent uiComponent;

    class UiComponent {
        UiComponent() {
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TemActivity.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        init();
        initView();
    }

    private void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
    }

    private void initView() {
        this.tp_time_pie = (TimePie) findViewById(R.id.tp_time_pie);
        ArrayList<Record2> list = DbUtils.queryItemsIdByDate2(this.context, DateTime.getDateString());
        ArrayList<TimePieRecord> lists = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Record2 record = (Record2) it.next();
            lists.add(new TimePieRecord(record.getBegin(), record.getEnd(), record.getColor()));
        }
        this.tp_time_pie.setRecords(lists);
    }

    private void requestServerTime() {
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
