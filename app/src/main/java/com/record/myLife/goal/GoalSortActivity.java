package com.record.myLife.goal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.Sql;
import com.record.utils.ToastUtils;
import com.record.utils.db.DbUtils;
import com.record.view.draglistview.DragSortListView;
import com.record.view.draglistview.DragSortListView.DragScrollProfile;
import com.record.view.draglistview.DragSortListView.DropListener;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class GoalSortActivity extends BaseActivity {
    static String TAG = "override";
    private ArrayAdapter<String> adapter;
    Button btn_set_back;
    Context context;
    private HashMap<String, Integer> goalNameMap;
    private ArrayList<String> list;
    private ArrayList<String> listClone;
    DragSortListView lv;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.btn_set_back) {
                GoalSortActivity.this.onBackPressed();
            }
        }
    };
    private DropListener onDrop = new DropListener() {
        public void drop(int from, int to) {
            String item = (String) GoalSortActivity.this.adapter.getItem(from);
            GoalSortActivity.this.adapter.notifyDataSetChanged();
            GoalSortActivity.this.adapter.remove(item);
            GoalSortActivity.this.adapter.insert(item, to);
        }
    };
    private DragScrollProfile ssProfile = new DragScrollProfile() {
        public float getSpeed(float w, long t) {
            if (w > 0.8f) {
                return ((float) GoalSortActivity.this.adapter.getCount()) / 0.001f;
            }
            return 10.0f * w;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_sort);
        init();
        initView();
    }

    private void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
    }

    private void initView() {
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.lv = (DragSortListView) findViewById(R.id.list);
        this.lv.setDropListener(this.onDrop);
        this.lv.setDragScrollProfile(this.ssProfile);
        this.list = getGoalList();
        this.listClone = (ArrayList) this.list.clone();
        this.adapter = new ArrayAdapter(this, R.layout.list_item_handle_right, R.id.text, this.list);
        this.lv.setAdapter(this.adapter);
    }

    private ArrayList<String> getGoalList() {
        ArrayList<String> list = new ArrayList();
        Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.GoalsList(this.context), null);
        this.goalNameMap = new HashMap();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String actName = DbUtils.getStr(cur, "actName");
                int id = DbUtils.getInt(cur, "id");
                list.add(DbUtils.getStr(cur, "actName"));
                this.goalNameMap.put(actName, Integer.valueOf(id));
            }
        }
        DbUtils.close(cur);
        return list;
    }

    public void onBackPressed() {
        if (isChange()) {
            saveSort();
            setResult(-1);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    private void saveSort() {
        int count = this.adapter.getCount();
        String time = DateTime.getTimeString();
        String whereUser = DbUtils.getWhereUserId(this.context);
        for (int i = 0; i < count; i++) {
            String actName = (String) this.adapter.getItem(i);
            ContentValues values = new ContentValues();
            values.put("position", Integer.valueOf(i + 1));
            values.put("endUpdateTime", time);
            DbUtils.getDb(this.context).update("t_act", values, whereUser + " and id = " + ((Integer) this.goalNameMap.get(actName)).intValue(), null);
        }
        ToastUtils.toastShort(this.context, "修改成功！");
    }

    private boolean isChange() {
        int count = this.adapter.getCount();
        for (int i = 0; i < count; i++) {
            if (!((String) this.adapter.getItem(i)).equals(this.listClone.get(i))) {
                return true;
            }
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

    public static void startActivity(Context context) {
        ((Activity) context).startActivityForResult(new Intent(context, GoalSortActivity.class), 1);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
