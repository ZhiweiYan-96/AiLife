package com.record.myLife.main.tomato;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.record.adapter.TomatoListAdapter;
import com.record.bean.Tomato;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;

public class UnhandlerTomatoActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {
    static String TAG = "override";
    TomatoListAdapter adapter;
    Context context;
    ArrayList items = new ArrayList();
    UiComponent uiComponent;

    class UiComponent {
        ListView listView;
        TextView tv_none;

        UiComponent() {
        }
    }

    public void setUiComponent(UiComponent uiComponent) {
        uiComponent.listView = (ListView) findViewById(R.id.listView);
        uiComponent.tv_none = (TextView) findViewById(R.id.tv_none);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Tomato tomato = (Tomato) this.adapter.getItem(position);
        if (tomato != null) {
            RemindTomatoActivity.startActivity(this.context, Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT, tomato.startTime, tomato.length);
        }
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final Tomato tomato = (Tomato) this.adapter.getItem(position);
        if (tomato != null) {
            DialogUtils.showPromptWithHandler(this.context, "是否要丢弃这个番茄？", "删除", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DbUtils.getDb(UnhandlerTomatoActivity.this.context).delete("t_unhandler_tomato", "startTime = '" + tomato.startTime + "'", null);
                    UnhandlerTomatoActivity.this.adapter.removeItem(tomato);
                    UnhandlerTomatoActivity.this.items.remove(tomato);
                    UnhandlerTomatoActivity.this.updateUI();
                    ToastUtils.toastShort(UnhandlerTomatoActivity.this.context, UnhandlerTomatoActivity.this.getString(R.string.str_delete_success));
                    dialog.cancel();
                }
            });
        }
        return false;
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, UnhandlerTomatoActivity.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unhandler_tomato);
        init();
        initView();
    }

    private void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
        initData();
    }

    private void initData() {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_unhandler_tomato order by startTime desc", null);
        if (cursor.getCount() <= 0) {
            this.items = new ArrayList();
        } else if (this.items == null || this.items.size() == 0 || this.items.size() != cursor.getCount()) {
            loadDate(cursor);
        }
        DbUtils.close(cursor);
    }

    private void loadDate(Cursor cursor) {
        this.items = new ArrayList();
        while (cursor.moveToNext()) {
            this.items.add(new Tomato(cursor.getString(cursor.getColumnIndex("startTime")), cursor.getDouble(cursor.getColumnIndex("length"))));
        }
    }

    private void initView() {
        this.uiComponent = new UiComponent();
        setUiComponent(this.uiComponent);
        this.adapter = new TomatoListAdapter(this.context);
        this.uiComponent.listView.setAdapter(this.adapter);
        this.uiComponent.listView.setOnItemClickListener(this);
        this.uiComponent.listView.setOnItemLongClickListener(this);
    }

    private void requestServerTime() {
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initData();
        updateUI();
    }

    public void updateUI() {
        if (this.items == null || this.items.size() <= 0) {
            this.uiComponent.tv_none.setVisibility(0);
            this.uiComponent.listView.setVisibility(8);
            finish();
            return;
        }
        this.adapter.setItems(this.items);
        this.uiComponent.tv_none.setVisibility(8);
        this.uiComponent.listView.setVisibility(0);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
