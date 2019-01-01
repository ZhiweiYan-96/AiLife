package com.record.myLife.add;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.settings.label.LabelSelectActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.MyGoalItemsLayout;
import com.record.myLife.view.MyGoalItemsLayout.MyOnItemsClickListener;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.utils.DateTime;
import com.record.utils.Sql;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils.PopWindowM;
import com.record.view.wheel.widget.NumericWheelAdapter;
import com.record.view.wheel.widget.OnWheelChangedListener;
import com.record.view.wheel.widget.WheelView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class AddRecordWheelActivity extends BaseActivity implements OnClickListener {
    public static HashMap<Integer, Integer> Act2TypeMap;
    private static Context context;
    int TYPE_DATE_ADD = 2;
    int TYPE_DATE_LEFT = 1;
    int TYPE_DATE_RIGHT = 2;
    int TYPE_DATE_SUB = 1;
    AnimationController aController;
    String addDbEndTime = "";
    String addDbStartTime = "";
    String addEndDate = "";
    String addEndTime = "";
    String addStartDate = "";
    String addStartTime = "";
    OnClickListener addTimeOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_tem_time_cancel) {
                AddRecordWheelActivity.this.onBackPressed();
            } else if (id == R.id.btn_tem_time_save) {
                AddRecordWheelActivity.this.clickSaveData();
            } else if (id == R.id.tv_today_addtime_start_sub) {
                AddRecordWheelActivity.this.clickChangeDate(AddRecordWheelActivity.this.TYPE_DATE_LEFT, AddRecordWheelActivity.this.TYPE_DATE_SUB);
            } else if (id == R.id.tv_today_addtime_start_add) {
                AddRecordWheelActivity.this.clickChangeDate(AddRecordWheelActivity.this.TYPE_DATE_LEFT, AddRecordWheelActivity.this.TYPE_DATE_ADD);
            } else if (id == R.id.tv_today_addtime_end_sub) {
                AddRecordWheelActivity.this.clickChangeDate(AddRecordWheelActivity.this.TYPE_DATE_RIGHT, AddRecordWheelActivity.this.TYPE_DATE_SUB);
            } else if (id == R.id.tv_today_addtime_end_add) {
                AddRecordWheelActivity.this.clickChangeDate(AddRecordWheelActivity.this.TYPE_DATE_RIGHT, AddRecordWheelActivity.this.TYPE_DATE_ADD);
            } else if (id == R.id.tv_add_record_wheel_title) {
                PopWindowM.showChooseAddRecordTypeDialog(AddRecordWheelActivity.context, PopWindowM.ADD_RECORD_WHEEL, AddRecordWheelActivity.this.addStartTime, AddRecordWheelActivity.this.addEndTime);
            }
        }
    };
    private OnWheelChangedListener changedListener1 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddRecordWheelActivity.this.hour1 = newValue;
            AddRecordWheelActivity.this.log("OnWheelChangedListener hour1:" + newValue);
            AddRecordWheelActivity.this.isOverrideOther();
        }
    };
    private OnWheelChangedListener changedListener2 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddRecordWheelActivity.this.min1 = newValue;
            AddRecordWheelActivity.this.log("OnWheelChangedListener min1:" + newValue);
            AddRecordWheelActivity.this.isOverrideOther();
        }
    };
    private OnWheelChangedListener changedListener3 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddRecordWheelActivity.this.hour2 = newValue;
            AddRecordWheelActivity.this.log("OnWheelChangedListener hour2:" + newValue);
            AddRecordWheelActivity.this.isOverrideOther();
        }
    };
    private OnWheelChangedListener changedListener4 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddRecordWheelActivity.this.min2 = newValue;
            AddRecordWheelActivity.this.log("OnWheelChangedListener min2:" + newValue);
            AddRecordWheelActivity.this.isOverrideOther();
        }
    };
    String checkActId = "";
    MyOnItemsClickListener goalItemsIdClickLister = new MyOnItemsClickListener() {
        public void onClick(View v, String id) {
            AddRecordWheelActivity.this.checkActId = id;
            AddRecordWheelActivity.this.log("checkActId:" + AddRecordWheelActivity.this.checkActId);
        }
    };
    int hour1 = 0;
    int hour2 = 0;
    LayoutInflater inflater;
    int itemsRlId;
    int labelId = 0;
    int min1 = 0;
    int min2 = 0;
    private boolean onResume;
    String theDayBefore = DateTime.beforeNDays2Str(-2);
    String today = DateTime.getDateString();
    TextView tv_today_addtime_end_add;
    TextView tv_today_addtime_end_sub;
    TextView tv_today_addtime_end_to;
    TextView tv_today_addtime_info;
    TextView tv_today_addtime_start_add;
    TextView tv_today_addtime_start_sub;
    TextView tv_today_addtime_start_to;
    TextView tv_today_total_time;
    UiComponent uiComponent;
    String yesterday = DateTime.beforeNDays2Str(-1);

    class UiComponent {
        TextView tv_remind_tomato_label;

        UiComponent() {
        }
    }

    public void setUiComponent(UiComponent uiComponent) {
        uiComponent.tv_remind_tomato_label = (TextView) findViewById(R.id.tv_remind_tomato_label);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_wheel_v2);
        context = this;
        this.inflater = getLayoutInflater();
        this.aController = new AnimationController();
        Intent it = getIntent();
        setAddTime(it.getStringExtra("startTime"), it.getStringExtra("stopTime"));
        initFind();
        MobclickAgent.onEvent(getApplicationContext(), "addrecord_open_wheel_activity");
    }

    public String getItemsDbId(View v) {
        return ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
    }

    private void setAddTime(String startTime, String stopTime) {
        if (startTime == null || startTime.length() <= 0 || stopTime == null || stopTime.length() <= 0) {
            setAddTimeFromLastRecord();
            return;
        }
        try {
            this.addStartTime = startTime;
            this.addStartDate = this.addStartTime.substring(0, this.addStartTime.indexOf(" "));
            this.addEndTime = stopTime;
            this.addEndDate = this.addEndTime.substring(0, this.addStartTime.indexOf(" "));
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            setAddTimeFromLastRecord();
        }
    }

    private void setAddTimeFromLastRecord() {
        Cursor cursor = DbUtils.getDb(context).rawQuery("select stopTime from t_act_item where " + DbUtils.getWhereUserId(context) + " and isEnd is 1  and isDelete is not 1  order by startTime desc limit 1", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            this.addStartTime = cursor.getString(cursor.getColumnIndex("stopTime"));
            if (!(this.addStartTime.contains(this.today) || this.addStartTime.contains(this.yesterday) || this.addStartTime.contains(this.theDayBefore))) {
                this.addStartTime = DateTime.beforeSecond(-3600);
            }
            this.addStartDate = this.addStartTime.substring(0, this.addStartTime.indexOf(" "));
        } else {
            this.addStartTime = DateTime.getTimeString();
            this.addStartDate = DateTime.getDateString();
        }
        DbUtils.close(cursor);
        this.addEndTime = DateTime.getTimeString();
        this.addEndDate = DateTime.getDateString();
    }

    private void initFind() {
        this.uiComponent = new UiComponent();
        setUiComponent(this.uiComponent);
        this.tv_today_addtime_start_to = (TextView) findViewById(R.id.tv_today_addtime_start_to);
        this.tv_today_addtime_end_to = (TextView) findViewById(R.id.tv_today_addtime_end_to);
        this.tv_today_addtime_info = (TextView) findViewById(R.id.tv_today_addtime_info);
        this.tv_today_total_time = (TextView) findViewById(R.id.tv_today_total_time);
        this.tv_today_addtime_start_sub = (TextView) findViewById(R.id.tv_today_addtime_start_sub);
        this.tv_today_addtime_start_add = (TextView) findViewById(R.id.tv_today_addtime_start_add);
        this.tv_today_addtime_end_sub = (TextView) findViewById(R.id.tv_today_addtime_end_sub);
        this.tv_today_addtime_end_add = (TextView) findViewById(R.id.tv_today_addtime_end_add);
        LinearLayout ll_tem_time_items = new MyGoalItemsLayout((Activity) context, (LinearLayout) findViewById(R.id.ll_tem_time_items), this.goalItemsIdClickLister).getAddItems();
        WheelView hours = (WheelView) findViewById(R.id.wv_tem_hour1);
        hours.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        hours.setVisibleItems(5);
        hours.setLabel("时");
        hours.setCyclic(true);
        WheelView mins = (WheelView) findViewById(R.id.wv_tem_min1);
        mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        mins.setVisibleItems(5);
        mins.setLabel("分");
        mins.setCyclic(true);
        WheelView hours2 = (WheelView) findViewById(R.id.wv_tem_hour2);
        hours2.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        hours2.setLabel("时");
        hours2.setVisibleItems(5);
        hours2.addChangingListener(this.changedListener3);
        hours2.setCyclic(true);
        WheelView mins2 = (WheelView) findViewById(R.id.wv_tem_min2);
        mins2.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        mins2.setLabel("分");
        mins2.setVisibleItems(5);
        mins2.addChangingListener(this.changedListener4);
        mins2.setCyclic(true);
        try {
            log("添加时间：,,addStartTime" + this.addStartTime + ",,addEndTime" + this.addEndTime);
            int[] arr1 = getHourAndMin(this.addStartTime);
            hours.setCurrentItem(arr1[0]);
            mins.setCurrentItem(arr1[1]);
            this.hour1 = arr1[0];
            this.min1 = arr1[1];
            int[] arr2 = getHourAndMin(this.addEndTime);
            hours2.setCurrentItem(arr2[0]);
            hours.addChangingListener(this.changedListener1);
            mins.addChangingListener(this.changedListener2);
            hours2.addChangingListener(this.changedListener3);
            mins2.addChangingListener(this.changedListener4);
            mins2.setCurrentItem(arr2[1]);
            this.hour2 = arr2[0];
            this.min2 = arr2[1];
            log("添加时间：,,arr1[1]" + arr1[0] + ",,arr1[1]" + arr1[1] + ",,arr2[0]" + arr2[0] + ",,arr2[1]" + arr2[1]);
            ImageView btn_tem_time_cancel = (ImageView) findViewById(R.id.btn_tem_time_cancel);
            TextView tv_add_record_wheel_title = (TextView) findViewById(R.id.tv_add_record_wheel_title);
            ((ImageView) findViewById(R.id.btn_tem_time_save)).setOnClickListener(this.addTimeOnClickListener);
            btn_tem_time_cancel.setOnClickListener(this.addTimeOnClickListener);
            tv_add_record_wheel_title.setOnClickListener(this.addTimeOnClickListener);
            this.tv_today_addtime_start_to.setOnClickListener(this.addTimeOnClickListener);
            this.tv_today_addtime_end_to.setOnClickListener(this.addTimeOnClickListener);
            this.tv_today_addtime_start_sub.setOnClickListener(this.addTimeOnClickListener);
            this.tv_today_addtime_start_add.setOnClickListener(this.addTimeOnClickListener);
            this.tv_today_addtime_end_sub.setOnClickListener(this.addTimeOnClickListener);
            this.tv_today_addtime_end_add.setOnClickListener(this.addTimeOnClickListener);
            this.uiComponent.tv_remind_tomato_label.setOnClickListener(this);
            updateDateUi();
            log("添加时间：弹出popup");
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void updateDateUi() {
        String date1 = this.addStartDate;
        String date2 = this.addEndDate;
        TextView btn_add_record_digit_sub_date1 = this.tv_today_addtime_start_sub;
        TextView btn_add_record_digit_date1 = this.tv_today_addtime_start_to;
        TextView btn_add_record_digit_add_date1 = this.tv_today_addtime_start_add;
        TextView btn_add_record_digit_sub_date2 = this.tv_today_addtime_end_sub;
        TextView btn_add_record_digit_date2 = this.tv_today_addtime_end_to;
        TextView btn_add_record_digit_add_date2 = this.tv_today_addtime_end_add;
        if (date1.equals(this.today)) {
            btn_add_record_digit_date1.setText(getString(R.string.str_today));
            btn_add_record_digit_sub_date1.setVisibility(0);
            btn_add_record_digit_add_date1.setVisibility(4);
        } else if (date1.equals(this.yesterday)) {
            btn_add_record_digit_date1.setText(getString(R.string.str_Yesterday));
            btn_add_record_digit_sub_date1.setVisibility(0);
            btn_add_record_digit_add_date1.setVisibility(0);
        } else if (date1.equals(this.theDayBefore)) {
            btn_add_record_digit_date1.setText(getString(R.string.str_the_day_before_yesterday));
            btn_add_record_digit_sub_date1.setVisibility(4);
            btn_add_record_digit_add_date1.setVisibility(0);
        }
        if (date2.equals(this.today)) {
            btn_add_record_digit_date2.setText(getString(R.string.str_today));
            btn_add_record_digit_sub_date2.setVisibility(0);
            btn_add_record_digit_add_date2.setVisibility(4);
        } else if (date2.equals(this.yesterday)) {
            btn_add_record_digit_date2.setText(getString(R.string.str_Yesterday));
            btn_add_record_digit_sub_date2.setVisibility(0);
            btn_add_record_digit_add_date2.setVisibility(0);
        } else if (date2.equals(this.theDayBefore)) {
            btn_add_record_digit_date2.setText(getString(R.string.str_the_day_before_yesterday));
            btn_add_record_digit_sub_date2.setVisibility(4);
            btn_add_record_digit_add_date2.setVisibility(0);
        }
        if (date1.equals(date2)) {
            btn_add_record_digit_date1.setTextColor(getResources().getColor(R.color.black));
            btn_add_record_digit_date2.setTextColor(getResources().getColor(R.color.black));
            return;
        }
        btn_add_record_digit_date1.setTextColor(getResources().getColor(R.color.bg_yellow2));
        btn_add_record_digit_date2.setTextColor(getResources().getColor(R.color.bg_yellow2));
    }

    private int[] getHourAndMin(String Time) {
        int spaceInt = Time.indexOf(" ");
        int mou1Int = Time.indexOf(":");
        int mou2Int = Time.lastIndexOf(":");
        String hour = Time.substring(spaceInt + 1, mou1Int);
        String min = Time.substring(mou1Int + 1, mou2Int);
        return new int[]{Integer.parseInt(hour), Integer.parseInt(min)};
    }

    private void clickChangeDate(int dateType, int changeType) {
        String date1 = this.addStartDate;
        String date2 = this.addEndDate;
        if (dateType == this.TYPE_DATE_LEFT) {
            date1 = changeType == this.TYPE_DATE_SUB ? DateTime.beforeNDays_v2(date1, -1) : DateTime.beforeNDays_v2(date1, 1);
        } else if (dateType == this.TYPE_DATE_RIGHT) {
            date2 = changeType == this.TYPE_DATE_SUB ? DateTime.beforeNDays_v2(date2, -1) : DateTime.beforeNDays_v2(date2, 1);
        }
        this.addStartDate = date1;
        this.addEndDate = date2;
        updateDateUi();
        isOverrideOther();
    }

    private void clickSaveData() {
        if (this.checkActId.equals("")) {
            ToastUtils.toastShort(context, getString(R.string.str_please_choose_type));
            return;
        }
        if (this.addStartDate == null) {
            this.addStartDate = DateTime.getDateString();
        }
        try {
            this.addStartTime = this.addStartTime.substring(0, this.addStartTime.lastIndexOf(":")) + ":00";
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        long addStartTimeInt = DateTime.pars2Calender(this.addStartTime).getTime().getTime();
        long addEndTimeInt = DateTime.pars2Calender(this.addEndTime).getTime().getTime();
        long nowInt = Calendar.getInstance().getTime().getTime();
        if (addEndTimeInt < addStartTimeInt) {
            ToastUtils.toastShort(context, getString(R.string.str_starttime_should_less_than_endtime));
        } else if ((addEndTimeInt - addStartTimeInt) / 1000 <= 60) {
            ToastUtils.toastShort(context, getString(R.string.str_add_time_too_short));
        } else if (addEndTimeInt > nowInt) {
            ToastUtils.toastLong(context, getString(R.string.str_starttime_should_less_than_now));
        } else {
            Cursor cursor = DbUtils.getDb(context).rawQuery(Sql.actItemIsContainOthers(context, this.addStartTime, this.addEndTime), null);
            if (cursor.getCount() > 0) {
                try {
                    new Builder(context).setTitle(getResources().getString(R.string.str_is_add)).setMessage(getResources().getString(R.string.str_add_time_contain_other_items_1).replace("{几个}", "" + cursor.getCount())).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).setPositiveButton(getResources().getString(R.string.str_add), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AddRecordWheelActivity.this.addTimeSave();
                            dialog.cancel();
                        }
                    }).create().show();
                } catch (Exception e2) {
                    DbUtils.exceptionHandler(context, e2);
                }
            } else {
                addTimeSave();
            }
            DbUtils.close(cursor);
        }
    }

    private void addTimeSave() {
        TreeSet<Integer> goalIdSet1 = DbUtils.updateDbActItem_ChangeEndTime(context, this.addStartTime, "");
        TreeSet<Integer> goalIdSet2 = DbUtils.updateDbActItem_ChangeStartTime(context, this.addEndTime, "");
        TreeSet<Integer> goalIdSet3 = DbUtils.deleteActItem_deleteRecords(context, this.addStartTime, this.addEndTime, "");
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(context));
        values.put("actId", this.checkActId);
        values.put("actType", DbUtils.queryActTypeById(context, this.checkActId));
        values.put("startTime", this.addStartTime);
        values.put("take", Integer.valueOf(DateTime.cal_secBetween(this.addStartTime, this.addEndTime)));
        values.put("stopTime", this.addEndTime);
        values.put("isEnd", Integer.valueOf(1));
        values.put("isRecord", Integer.valueOf(0));
        int tempItemid = (int) DbUtils.getDb(context).insert("t_act_item", null, values);
        ToastUtils.toastShort(context, "添加成功！");
        DbUtils.addLabelLink(context, this.labelId, tempItemid);
        TreeSet<String> dateArr = new TreeSet();
        dateArr.add(this.addEndDate);
        dateArr.add(this.addStartDate);
        goalIdSet3.add(Integer.valueOf(Integer.parseInt(this.checkActId)));
        Iterator it = goalIdSet1.iterator();
        while (it.hasNext()) {
            goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
        }
        it = goalIdSet2.iterator();
        while (it.hasNext()) {
            goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
        }
        new Thread(new AllocationAndStaticsRunnable(context, goalIdSet3, dateArr)).start();
        setResult(28);
        onBackPressed();
    }

    public boolean isOverrideOther() {
        log("是否与其它时间重叠1---addStartTime:" + this.addStartTime + ",addEndTime:" + this.addEndTime);
        DecimalFormat df = new DecimalFormat("00");
        if (this.addStartDate.equals("")) {
            this.addStartDate = DateTime.getDateString();
        }
        if (this.addEndDate.equals("")) {
            this.addEndDate = DateTime.getDateString();
        }
        this.addStartTime = this.addStartDate + " " + df.format((long) this.hour1) + ":" + df.format((long) this.min1) + ":59";
        this.addEndTime = this.addEndDate + " " + df.format((long) this.hour2) + ":" + df.format((long) this.min2) + ":00";
        this.tv_today_total_time.setVisibility(4);
        log("是否与其它时间重叠2---addStartTime:" + this.addStartTime + ",addEndTime:" + this.addEndTime);
        if (DateTime.pars2Calender(this.addStartTime).getTime().getTime() > DateTime.pars2Calender(this.addEndTime).getTime().getTime()) {
            this.tv_today_addtime_info.setText(getString(R.string.str_starttime_should_less_than_endtime));
        } else if (DateTime.pars2Calender(this.addStartTime).getTime().getTime() > Calendar.getInstance().getTime().getTime()) {
            this.tv_today_addtime_info.setText(getString(R.string.str_starttime_should_less_than_now));
        } else if (DateTime.pars2Calender(this.addEndTime).getTime().getTime() > Calendar.getInstance().getTime().getTime()) {
            this.tv_today_addtime_info.setText(getString(R.string.str_endtime_should_less_than_now));
        } else {
            this.tv_today_total_time.setVisibility(0);
            this.tv_today_total_time.setText(DateTime.calculateTime5(context, (long) DateTime.cal_secBetween(this.addStartTime, this.addEndTime)));
            Cursor cursor = DbUtils.getDb(context).rawQuery(Sql.actItemIsContainOthers(context, this.addStartTime, this.addEndTime), null);
            if (cursor.getCount() > 0) {
                this.tv_today_addtime_info.setText(getString(R.string.str_contain) + " " + cursor.getCount() + " " + getString(R.string.str_records_ok_will_delete_it));
                DbUtils.close(cursor);
            } else {
                this.tv_today_addtime_info.setText("");
                DbUtils.close(cursor);
                cursor = DbUtils.getDb(context).rawQuery(Sql.actItemIsStartTimeOverrideOthers(context, this.addStartTime), null);
                if (cursor.getCount() > 0) {
                    this.tv_today_addtime_info.setText(getString(R.string.str_starttime_contain_other_ok_will_override_other));
                    DbUtils.close(cursor);
                } else {
                    this.tv_today_addtime_info.setText("");
                    DbUtils.close(cursor);
                    cursor = DbUtils.getDb(context).rawQuery(Sql.actItemIsStartTimeOverrideOthers(context, this.addEndTime), null);
                    if (cursor.getCount() > 0) {
                        this.tv_today_addtime_info.setText(getString(R.string.str_endtime_contain_other_ok_will_override_other));
                        DbUtils.close(cursor);
                    } else {
                        this.tv_today_addtime_info.setText("");
                        DbUtils.close(cursor);
                    }
                }
            }
        }
        return false;
    }

    private LinearLayout getAddItems(LinearLayout ll) {
        Cursor cur = DbUtils.getDb(context).rawQuery(Sql.GoalsList(context), null);
        if (cur.getCount() > 0) {
            ll.removeAllViews();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("id"));
                if (Act2TypeMap == null || Act2TypeMap.get(id) == null) {
                    Act2TypeMap = getAct2TypeMap();
                }
                if (cur.getCount() <= 4 || ((Integer) Act2TypeMap.get(Integer.valueOf(Integer.parseInt(id)))).intValue() != 10) {
                    ll.addView(getAddActItems(id, cur.getString(cur.getColumnIndex("actName")), cur.getString(cur.getColumnIndex("image")), cur.getString(cur.getColumnIndex("color"))));
                }
            }
        }
        DbUtils.close(cur);
        return ll;
    }

    private RelativeLayout getAddActItems(String id, String name, String label, String color) {
        RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.tem_sigle_act, null);
        rl_temp_show_outer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) v.getParent();
                int count = ll.getChildCount();
                for (int i = 0; i < count; i++) {
                    ll.getChildAt(i).setBackgroundColor(AddRecordWheelActivity.this.getResources().getColor(R.color.gray));
                }
                v.setBackgroundColor(AddRecordWheelActivity.this.getResources().getColor(R.color.black));
                RelativeLayout rl = (RelativeLayout) v;
                AddRecordWheelActivity.this.checkActId = ((TextView) rl.getChildAt(0)).getText().toString();
                AddRecordWheelActivity.this.log("checkActId:" + AddRecordWheelActivity.this.checkActId);
            }
        });
        TextView tv_temp_act_name = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_name);
        ImageView iv_temp_color = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_color);
        ((TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_id)).setText(id);
        tv_temp_act_name.setText(name);
        iv_temp_color.setImageResource(Val.getLabelIntByName(label));
        iv_temp_color.setBackgroundColor(getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()));
        return rl_temp_show_outer;
    }

    public static HashMap<Integer, Integer> getAct2TypeMap() {
        HashMap<Integer, Integer> map = new HashMap();
        Cursor cur = DbUtils.getDb2(context).rawQuery("select id,type from t_act where " + DbUtils.getWhereUserId(context), null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                map.put(Integer.valueOf(cur.getInt(cur.getColumnIndex("id"))), Integer.valueOf(cur.getInt(cur.getColumnIndex(a.a))));
            }
        }
        DbUtils.close(cur);
        return map;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remind_tomato_label:
                if (this.checkActId == null || this.checkActId.length() <= 0) {
                    ToastUtils.toastShort(context, "请先选择目标！");
                    return;
                }
                int actType = DbUtils.queryActTypeById(context, this.checkActId).intValue();
                if (actType == 11) {
                    actType = 10;
                }
                LabelSelectActivity.startActivity((Activity) context, actType);
                return;
            default:
                return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (25 == requestCode && resultCode == -1) {
            String labelId = data.getStringExtra("labelIdStr");
            this.labelId = Integer.parseInt(labelId);
            this.uiComponent.tv_remind_tomato_label.setText(DbUtils.queryLabelNameByLabelId(context, labelId));
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_to_up, R.anim.push_to_down);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    protected void onResume() {
        super.onResume();
        this.onResume = true;
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        this.onResume = false;
        MobclickAgent.onPause(this);
    }

    private void log(String str) {
        Log.i("override Main", ":" + str);
    }
}
