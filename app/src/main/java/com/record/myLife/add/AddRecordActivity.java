package com.record.myLife.add;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.record.bean.DateData;
import com.record.bean.Record;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.TimeLineView;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.utils.DateTime;
import com.record.utils.GeneralUtils;
import com.record.utils.RecordUtils;
import com.record.utils.Sql;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils.PopWindowM;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class AddRecordActivity extends BaseActivity {
    public static HashMap<Integer, Integer> Act2TypeMap;
    static String TAG = "override";
    Button btn_add_record_save;
    Button btn_set_back;
    String checkActId = "-1";
    Context context;
    String currentDate = "";
    private LayoutInflater inflater;
    boolean isOnContinueSelectState = false;
    boolean isSaveData = false;
    Button iv_add_record_last_date;
    Button iv_add_record_next_date;
    ArrayList<Button> minDotTextViewArr = null;
    ArrayList<TextView> minIdTextViewArr = null;
    ArrayList<Button> minShowTextViewArr = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_add_record_save) {
                AddRecordActivity.this.clickSaveData();
                MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_save");
            } else if (id == R.id.btn_set_back) {
                AddRecordActivity.this.onBackPressed();
                MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_click_back");
            } else if (id == R.id.iv_add_record_last_date) {
                AddRecordActivity.this.preDate();
                MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_bottom_left");
            } else if (id == R.id.iv_add_record_next_date) {
                AddRecordActivity.this.nextDate();
                MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_bottom_right");
            } else if (id == R.id.tv_set_title) {
                String[] timeArr = DbUtils.queryLastRecordStopTime(AddRecordActivity.this.context, DateTime.getDateString());
                PopWindowM.showChooseAddRecordTypeDialog(AddRecordActivity.this.context, PopWindowM.ADD_RECORD_QUICK_ALLOCAT, timeArr[0], timeArr[1]);
                MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_click_title");
            }
        }
    };
    Comparator<Record> myComparator = null;
    OnClickListener myContinuteClickListener = new OnClickListener() {
        public void onClick(View v) {
            String minute = ((TextView) ((RelativeLayout) v.getParent()).getChildAt(0)).getText().toString();
            AddRecordActivity.this.selectMin = Integer.parseInt(minute);
            boolean isNormalClick = true;
            if (!AddRecordActivity.this.isMinHadBeContain()) {
                isNormalClick = AddRecordActivity.this.normalClickMinute(v);
            }
            if (AddRecordActivity.this.checkActId != null && AddRecordActivity.this.checkActId.length() > 0 && !AddRecordActivity.this.checkActId.equals("-1") && isNormalClick) {
                ((Button) v).setBackgroundResource(R.drawable.sel_circle_graychekc2black);
                AddRecordActivity.this.isOnContinueSelectState = true;
                AddRecordActivity.this.selectHourOnContinueState = AddRecordActivity.this.selectHour;
                AddRecordActivity.this.selectMinOnContinueState = AddRecordActivity.this.selectMin;
                int size = AddRecordActivity.this.minDotTextViewArr.size();
                for (int i = 0; i < size; i++) {
                    if (AddRecordActivity.this.selectMinOnContinueState != i * 2) {
                        ((Button) AddRecordActivity.this.minDotTextViewArr.get(i)).setVisibility(8);
                    }
                }
                AddRecordActivity.this.updateUiToContinueState();
            }
            MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_bottom_min_circle");
        }
    };
    OnClickListener myHourClickListener = new OnClickListener() {
        public void onClick(View v) {
            String hour = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            AddRecordActivity.this.selectHour = Integer.parseInt(hour);
            if (!(AddRecordActivity.this.checkActId == null || AddRecordActivity.this.checkActId.length() <= 0 || AddRecordActivity.this.checkActId.equals("-1"))) {
                Record record;
                AddRecordActivity.this.isSaveData = false;
                if (!AddRecordActivity.this.isOnContinueSelectState) {
                    record = AddRecordActivity.this.getHourRecord(AddRecordActivity.this.selectHour);
                } else if (AddRecordActivity.this.selectHourOnContinueState != AddRecordActivity.this.selectHour) {
                    record = AddRecordActivity.this.getHourRecordOnContinute(AddRecordActivity.this.selectHour);
                } else {
                    record = AddRecordActivity.this.getHourRecord(AddRecordActivity.this.selectHour);
                    AddRecordActivity.this.cancelContinueState();
                }
                AddRecordActivity.this.recordUtils.addOrDeleteOrResolveRecord(AddRecordActivity.this.getCurrentDay(), record, AddRecordActivity.this.isOnContinueSelectState);
            }
            DateData dateData = AddRecordActivity.this.recordUtils.getDateData(AddRecordActivity.this.getCurrentDay());
            AddRecordActivity.log("点击小时，当前DateData:\n" + dateData.toString());
            AddRecordActivity.this.tlv_add_record_scale_on_left.setData(dateData);
            AddRecordActivity.this.tlv_add_record_add_hour_items.setData(dateData);
            AddRecordActivity.this.showMinuteUI(dateData);
            if (AddRecordActivity.this.isOnContinueSelectState && AddRecordActivity.this.selectHourOnContinueState != AddRecordActivity.this.selectHour) {
                AddRecordActivity.this.cancelContinueState();
            }
        }
    };
    OnClickListener myMinuteClickListener = new OnClickListener() {
        public void onClick(View v) {
            AddRecordActivity.this.normalClickMinute(v);
            MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_bottom_min");
        }
    };
    HashMap<String, ArrayList<Record>> recordMapFromDb = null;
    RecordUtils recordUtils = null;
    RelativeLayout rl_add_record_date;
    RelativeLayout rl_add_record_hour_items_activity;
    RelativeLayout rl_add_record_items;
    RelativeLayout rl_add_record_mintue_items_activity;
    int selectHour = 0;
    int selectHourOnContinueState = 0;
    int selectMin = 120;
    int selectMinOnContinueState = 0;
    ScrollView sv_hour;
    ScrollView sv_minute;
    String theDayBeforYesterDay = "";
    TimeLineView tlv_add_record_add_hour_items;
    TimeLineView tlv_add_record_add_minute_items;
    TimeLineView tlv_add_record_scale_on_left;
    String today = "";
    TextView tv_add_record_date;
    TextView tv_set_title;
    String yesterday = "";

    class MyComparator implements Comparator<Record> {
        MyComparator() {
        }

        public int compare(Record lhs, Record rhs) {
            if (lhs.getBegin() > rhs.getBegin()) {
                return 1;
            }
            return -1;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        SystemBarTintManager.setMIUIbar(this);
        this.context = this;
        TAG += " " + getClass().getSimpleName();
        this.inflater = getLayoutInflater();
        this.recordUtils = new RecordUtils();
        this.recordMapFromDb = new HashMap();
        this.myComparator = new MyComparator();
        this.rl_add_record_items = (RelativeLayout) findViewById(R.id.rl_add_record_items);
        this.rl_add_record_date = (RelativeLayout) findViewById(R.id.rl_add_record_date);
        this.rl_add_record_hour_items_activity = (RelativeLayout) findViewById(R.id.rl_add_record_hour_items_activity);
        this.rl_add_record_mintue_items_activity = (RelativeLayout) findViewById(R.id.rl_add_record_mintue_items_activity);
        this.btn_add_record_save = (Button) findViewById(R.id.btn_add_record_save);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.tlv_add_record_scale_on_left = (TimeLineView) findViewById(R.id.rl_add_record_scale);
        this.iv_add_record_last_date = (Button) findViewById(R.id.iv_add_record_last_date);
        this.iv_add_record_next_date = (Button) findViewById(R.id.iv_add_record_next_date);
        this.tv_add_record_date = (TextView) findViewById(R.id.tv_add_record_date);
        this.tv_set_title = (TextView) findViewById(R.id.tv_set_title);
        this.btn_add_record_save.setOnClickListener(this.myClickListener);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.iv_add_record_last_date.setOnClickListener(this.myClickListener);
        this.iv_add_record_next_date.setOnClickListener(this.myClickListener);
        this.tv_set_title.setOnClickListener(this.myClickListener);
        initData();
        initSetUI();
        MobclickAgent.onEvent(getApplicationContext(), "addrecord_open_allocation_activity");
    }

    private void initData() {
        this.currentDate = DateTime.getDateString();
        this.tv_add_record_date.setText(getCurrentDay() + " " + getString(R.string.str_today));
        this.iv_add_record_next_date.setVisibility(8);
        this.today = DateTime.getDateString();
        Calendar c = DateTime.pars2Calender2(this.today);
        c.add(5, -1);
        this.yesterday = DateTime.formatDate(c);
        c.add(5, -1);
        this.theDayBeforYesterDay = DateTime.formatDate(c);
    }

    private void initSetUI() {
        if (this.rl_add_record_items.getChildCount() == 0) {
            HorizontalScrollView hs = (HorizontalScrollView) this.inflater.inflate(R.layout.tem_goal_items_for_add_record, null);
            LinearLayout ll = getAddItems((LinearLayout) hs.findViewById(R.id.ll_tem_time_items));
            this.rl_add_record_items.addView(hs);
        }
        if (this.rl_add_record_hour_items_activity.getChildCount() == 0) {
            this.sv_hour = (ScrollView) this.inflater.inflate(R.layout.tem_vertical, null);
            LinearLayout ll_add_record_hour_items = (LinearLayout) this.sv_hour.findViewById(R.id.ll_tem_add_record_hour_items);
            for (int i = 0; i < 24; i++) {
                RelativeLayout hour = (RelativeLayout) this.inflater.inflate(R.layout.tem_add_record_hour_item, null);
                hour.setOnClickListener(this.myHourClickListener);
                TextView tv_tem_add_record_id = (TextView) hour.findViewById(R.id.tv_tem_add_record_id);
                ((TextView) hour.findViewById(R.id.tv_tem_add_record_text)).setText(i + ":00");
                tv_tem_add_record_id.setText(i + "");
                ll_add_record_hour_items.addView(hour);
                MobclickAgent.onEvent(this.context, "add_record_allocat_bottom_hour");
            }
            this.rl_add_record_hour_items_activity.addView(this.sv_hour);
            this.sv_hour.post(new Runnable() {
                public void run() {
                    LayoutParams params = new LayoutParams(-1, -1);
                    AddRecordActivity.this.tlv_add_record_add_hour_items = new TimeLineView(AddRecordActivity.this.context, false);
                    AddRecordActivity.this.tlv_add_record_add_hour_items.setLayoutParams(params);
                    AddRecordActivity.this.tlv_add_record_add_hour_items.setTimelineHeight(((RelativeLayout) AddRecordActivity.this.sv_hour.findViewById(R.id.rl_add_record_hour_items)).getMeasuredHeight());
                    ((RelativeLayout) AddRecordActivity.this.sv_hour.findViewById(R.id.rl_tem_add_record_scale_inner)).addView(AddRecordActivity.this.tlv_add_record_add_hour_items);
                }
            });
        }
        final FrameLayout localFrameLayout = (FrameLayout) findViewById(R.id.rl_add_record_allocation_outer);
        localFrameLayout.post(new Runnable() {
            public void run() {
                AddRecordActivity.this.tlv_add_record_scale_on_left.setTimelineHeight(localFrameLayout.getHeight());
                ArrayList<Record> list = DbUtils.queryItemsIdByDate(AddRecordActivity.this.context, AddRecordActivity.this.getCurrentDay());
                AddRecordActivity.this.recordMapFromDb.put(AddRecordActivity.this.getCurrentDay(), list);
                DateData dateData = new DateData(AddRecordActivity.this.getCurrentDay(), list);
                AddRecordActivity.log(dateData.toString());
                AddRecordActivity.this.recordUtils.setDateData(AddRecordActivity.this.getCurrentDay(), new DateData(AddRecordActivity.this.getCurrentDay(), list));
                AddRecordActivity.this.tlv_add_record_scale_on_left.setData(dateData);
                AddRecordActivity.this.tlv_add_record_add_hour_items.setData(dateData);
                if (list != null) {
                    try {
                        if (list.size() > 0) {
                            int hour = ((Record) list.get(list.size() - 1)).getEnd() / 3600;
                            if (hour > 23) {
                                hour = 23;
                            } else if (hour < 0) {
                                hour = 0;
                            }
                            AddRecordActivity.this.selectHour = hour;
                            AddRecordActivity.this.showMinuteUI(dateData);
                        }
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                        return;
                    }
                }
                AddRecordActivity.this.selectHour = 0;
                AddRecordActivity.this.showMinuteUI(dateData);
            }
        });
    }

    private void updateItems(int itemsId, ContentValues values) {
        if (DbUtils.queryIsUploadByActItemId(this.context, itemsId + "") > 0) {
            values.put("endUpdateTime", DateTime.getTimeString());
        }
        DbUtils.getDb(this.context).update("t_act_items", values, " id = " + itemsId, null);
    }

    private void deleteItems(int itemsId, int isUpload) {
        if (isUpload > 0) {
            String time = DateTime.getTimeString();
            ContentValues values = new ContentValues();
            values.put("isDelete", Integer.valueOf(1));
            values.put("deleteTime", time);
            values.put("endUpdateTime", time);
            DbUtils.getDb(this.context).update("t_act_item", values, " id = " + itemsId, null);
            return;
        }
        DbUtils.getDb(this.context).delete("t_act_item", " id = " + itemsId, null);
    }

    private void deleteItems(int itemsId) {
        if (DbUtils.queryIsUploadByActItemId(this.context, itemsId + "") > 0) {
            String time = DateTime.getTimeString();
            ContentValues values = new ContentValues();
            values.put("isDelete", Integer.valueOf(1));
            values.put("deleteTime", time);
            values.put("endUpdateTime", time);
            DbUtils.getDb(this.context).update("t_act_item", values, " id = " + itemsId, null);
            return;
        }
        DbUtils.getDb(this.context).delete("t_act_item", " id = " + itemsId, null);
    }

    private void showMinuteUI(DateData dateData) {
        int i;
        if (this.minShowTextViewArr == null || this.minShowTextViewArr.size() <= 0) {
            this.rl_add_record_mintue_items_activity.removeAllViews();
            this.sv_minute = (ScrollView) this.inflater.inflate(R.layout.tem_vertical_for_minute, null);
            LinearLayout ll_tem_add_record_minute_items = (LinearLayout) this.sv_minute.findViewById(R.id.ll_tem_add_record_minute_items);
            this.minIdTextViewArr = new ArrayList();
            this.minShowTextViewArr = new ArrayList();
            this.minDotTextViewArr = new ArrayList();
            for (i = 0; i < 60; i += 2) {
                RelativeLayout minute = (RelativeLayout) this.inflater.inflate(R.layout.tem_add_record_minute_item, null);
                TextView tv_tem_add_record_min_id = (TextView) minute.findViewById(R.id.tv_tem_add_record_min_id);
                tv_tem_add_record_min_id.setText(i + "");
                Button tv_tem_add_record_min_text = (Button) minute.findViewById(R.id.tv_tem_add_record_min_text);
                tv_tem_add_record_min_text.setText(this.selectHour + ":" + String.format("%02d", new Object[]{Integer.valueOf(i)}));
                tv_tem_add_record_min_text.setOnClickListener(this.myMinuteClickListener);
                Button tv_tem_add_record_dot = (Button) minute.findViewById(R.id.tv_tem_add_record_dot);
                tv_tem_add_record_dot.setOnClickListener(this.myContinuteClickListener);
                this.minShowTextViewArr.add(tv_tem_add_record_min_text);
                this.minIdTextViewArr.add(tv_tem_add_record_min_id);
                this.minDotTextViewArr.add(tv_tem_add_record_dot);
                ll_tem_add_record_minute_items.addView(minute);
            }
            this.rl_add_record_mintue_items_activity.addView(this.sv_minute);
            this.sv_minute.post(new Runnable() {
                public void run() {
                    LayoutParams params = new LayoutParams(-1, -1);
                    AddRecordActivity.this.tlv_add_record_add_minute_items = new TimeLineView(AddRecordActivity.this.context, false);
                    AddRecordActivity.this.tlv_add_record_add_minute_items.setLayoutParams(params);
                    RelativeLayout rl = (RelativeLayout) AddRecordActivity.this.sv_minute.findViewById(R.id.rl_tem_add_record_minute_items);
                    AddRecordActivity.log("分钟的高" + rl.getMeasuredHeight());
                    AddRecordActivity.this.tlv_add_record_add_minute_items.setTimelineHeight(rl.getMeasuredHeight());
                    ((RelativeLayout) AddRecordActivity.this.sv_minute.findViewById(R.id.rl_tem_add_record_minute_scale_inner)).addView(AddRecordActivity.this.tlv_add_record_add_minute_items);
                    AddRecordActivity.this.tlv_add_record_add_minute_items.setData(AddRecordActivity.this.recordUtils.getDateData(AddRecordActivity.this.getCurrentDay()), 2, AddRecordActivity.this.selectHour);
                }
            });
            return;
        }
        int size = this.minShowTextViewArr.size();
        for (i = 0; i < size; i++) {
            ((TextView) this.minShowTextViewArr.get(i)).setText(this.selectHour + ":" + String.format("%02d", new Object[]{Integer.valueOf(i * 2)}));
            ((TextView) this.minIdTextViewArr.get(i)).setText((i * 2) + "");
        }
        this.tlv_add_record_add_minute_items.setData(dateData, 2, this.selectHour);
    }

    private boolean normalClickMinute(View v) {
        this.selectMin = Integer.parseInt(((TextView) ((RelativeLayout) v.getParent()).getChildAt(0)).getText().toString());
        log("选择的分钟：" + this.selectMin);
        if (ishadCancelContinueState()) {
            return false;
        }
        if (!(this.checkActId == null || this.checkActId.length() <= 0 || this.checkActId.equals("-1"))) {
            Record r;
            if (this.isOnContinueSelectState) {
                r = getMinuteRecordOnContinueState(this.selectHour, this.selectMin);
            } else {
                r = getMinuteRecord(this.selectHour, this.selectMin);
            }
            this.isSaveData = false;
            this.recordUtils.addOrDeleteOrResolveRecord(getCurrentDay(), r, this.isOnContinueSelectState);
            log("要添加的记录：" + r);
        }
        DateData dateData = this.recordUtils.getDateData(getCurrentDay());
        this.tlv_add_record_scale_on_left.setData(dateData);
        this.tlv_add_record_add_hour_items.setData(dateData);
        this.tlv_add_record_add_minute_items.setData(dateData, 2, this.selectHour);
        log("添回后的记录：" + dateData);
        if (this.checkActId != null && this.checkActId.length() > 0 && !this.checkActId.equals("-1") && this.isOnContinueSelectState) {
            cancelContinueState();
        }
        return true;
    }

    private boolean ishadCancelContinueState() {
        if (!this.isOnContinueSelectState || this.selectHourOnContinueState != this.selectHour || this.selectMin != this.selectMinOnContinueState) {
            return false;
        }
        cancelContinueState();
        return true;
    }

    private void cancelContinueState() {
        this.isOnContinueSelectState = false;
        int size = this.minDotTextViewArr.size();
        for (int i = 0; i < size; i++) {
            Button btn = (Button) this.minDotTextViewArr.get(i);
            btn.setVisibility(0);
            if (this.selectMinOnContinueState == i * 2) {
                btn.setBackgroundResource(R.drawable.sel_circle_gray2black);
            }
        }
        updateDateUI();
    }

    private void updateUiToContinueState() {
        this.tv_add_record_date.setText(getString(R.string.str_multi_select_state));
        this.tv_add_record_date.setTextColor(getResources().getColor(R.color.bg_yellow1));
        this.iv_add_record_last_date.setVisibility(8);
        this.iv_add_record_next_date.setVisibility(8);
    }

    private boolean isMinHadBeContain() {
        ArrayList<Record> list = this.recordUtils.getDateData(getCurrentDay()).getRecordList();
        if (list != null) {
            int size = list.size();
            int begin = (this.selectHour * 3600) + (this.selectMin * 60);
            int end = (this.selectHour * 3600) + ((this.selectMin + 2) * 60);
            int goalId = Integer.parseInt(this.checkActId);
            for (int i = 0; i < size; i++) {
                Record record = (Record) list.get(i);
                if (record.getBegin() <= begin && record.getEnd() >= end && goalId == record.getGoalId()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void resetMinuteUi() {
        this.rl_add_record_mintue_items_activity = (RelativeLayout) findViewById(R.id.rl_add_record_mintue_items_activity);
    }

    private String getCurrentDay() {
        return this.currentDate;
    }

    private Record getMinuteRecordOnContinueState(int hour, int minute) {
        int actId = 0;
        int color = 0;
        if (this.checkActId != null && this.checkActId.length() > 0) {
            actId = Integer.parseInt(this.checkActId);
            color = ((Integer) Val.col_Str2Int_Map.get(DbUtils.queryColorByActId(this.context, actId))).intValue();
        }
        int begin = 0;
        int end = 0;
        if (hour == this.selectHourOnContinueState) {
            if (minute > this.selectMinOnContinueState) {
                begin = (hour * 3600) + (this.selectMinOnContinueState * 60);
                end = (hour * 3600) + ((minute + 2) * 60);
            } else {
                begin = (hour * 3600) + (minute * 60);
                end = (hour * 3600) + ((this.selectMinOnContinueState + 2) * 60);
            }
        }
        return new Record(begin, end, actId, color);
    }

    private Record getMinuteRecord(int hour, int minute) {
        int actId = 0;
        int color = 0;
        if (this.checkActId != null && this.checkActId.length() > 0) {
            actId = Integer.parseInt(this.checkActId);
            color = ((Integer) Val.col_Str2Int_Map.get(DbUtils.queryColorByActId(this.context, actId))).intValue();
        }
        return new Record((hour * 3600) + (minute * 60), (hour * 3600) + ((minute + 2) * 60), actId, color);
    }

    private Record getHourRecordOnContinute(int hour) {
        int actId = 0;
        int color = 0;
        if (this.checkActId != null && this.checkActId.length() > 0) {
            actId = Integer.parseInt(this.checkActId);
            color = ((Integer) Val.col_Str2Int_Map.get(DbUtils.queryColorByActId(this.context, actId))).intValue();
        }
        int begin = 0;
        int end = 0;
        if (hour != this.selectHourOnContinueState) {
            if (hour > this.selectHourOnContinueState) {
                begin = (this.selectHourOnContinueState * 3600) + (this.selectMinOnContinueState * 60);
                end = (hour + 1) * 3600;
            } else {
                begin = hour * 3600;
                end = (this.selectHourOnContinueState * 3600) + ((this.selectMinOnContinueState + 2) * 60);
            }
        }
        return new Record(begin, end, actId, color);
    }

    private Record getHourRecord(int hour) {
        int actId = 0;
        int color = 0;
        if (this.checkActId != null && this.checkActId.length() > 0) {
            actId = Integer.parseInt(this.checkActId);
            color = ((Integer) Val.col_Str2Int_Map.get(DbUtils.queryColorByActId(this.context, actId))).intValue();
        }
        return new Record(hour * 3600, (hour + 1) * 3600, actId, color);
    }

    private void preDate() {
        if (this.isOnContinueSelectState) {
            cancelContinueState();
            this.isOnContinueSelectState = false;
        }
        if (!this.isSaveData && isNotSave()) {
            clickSaveData();
        }
        preOrNextDate(-1);
    }

    private void nextDate() {
        if (this.isOnContinueSelectState) {
            cancelContinueState();
            this.isOnContinueSelectState = false;
        }
        if (!this.isSaveData && isNotSave()) {
            clickSaveData();
        }
        preOrNextDate(1);
    }

    private void preOrNextDate(int day) {
        Calendar c = DateTime.pars2Calender2(this.currentDate);
        c.add(5, day);
        this.currentDate = DateTime.formatDate(c);
        updateDateUI();
        initSetUI();
    }

    private void updateDateUI() {
        if (this.currentDate.equals(this.today)) {
            this.iv_add_record_last_date.setVisibility(0);
            this.iv_add_record_next_date.setVisibility(8);
            this.tv_add_record_date.setText(this.currentDate + " " + getString(R.string.str_today));
        } else if (this.currentDate.equals(this.yesterday)) {
            this.iv_add_record_last_date.setVisibility(0);
            this.iv_add_record_next_date.setVisibility(0);
            this.tv_add_record_date.setText(this.currentDate + " " + getString(R.string.str_Yesterday));
        } else if (this.currentDate.equals(this.theDayBeforYesterDay)) {
            this.iv_add_record_last_date.setVisibility(8);
            this.iv_add_record_next_date.setVisibility(0);
            this.tv_add_record_date.setText(this.currentDate + " " + getString(R.string.str_the_day_before_yesterday));
        } else {
            this.iv_add_record_last_date.setVisibility(8);
            this.iv_add_record_next_date.setVisibility(8);
            this.tv_add_record_date.setText(this.currentDate);
        }
        this.tv_add_record_date.setTextColor(getResources().getColor(R.color.black_tran_es));
    }

    private void clickSaveData() {
        this.isSaveData = true;
        String tableName = "t_act_item";
        String now = DateTime.getTimeString();
        String changeDate = getCurrentDay();
        String zeroTime = changeDate + " 00:00:00";
        String twentyFourTime = changeDate + " 23:59:59";
        Calendar cal = DateTime.pars2Calender(zeroTime);
        cal.add(5, -1);
        String YesterdayZeroTime = DateTime.formatTime(cal);
        String Yesterday = DateTime.formatDate(cal);
        String YesterdayTwentyFourTime = Yesterday + " 23:59:59";
        String YesterdayTwentyFourTime2 = Yesterday + " 24:00:00";
        Calendar cal2 = DateTime.pars2Calender(zeroTime);
        cal2.add(5, 1);
        String changeNextZeroTime = DateTime.formatTime(cal2);
        String changeNextZeroDate = DateTime.formatDate(cal2);
        ArrayList<Record> dbList = (ArrayList) this.recordMapFromDb.get(getCurrentDay());
        ArrayList<Record> changeList = this.recordUtils.getDateData(getCurrentDay()).getRecordList();
        ArrayList<Record> changeList2 = (ArrayList) changeList.clone();
        ArrayList<Record> dbList2 = (ArrayList) dbList.clone();
        int crossNightId = 0;
        String crossNightStartTime = "";
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1  and startTime < '" + zeroTime + "' and  stopTime > '" + zeroTime + "' and stopTime < '" + twentyFourTime + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            crossNightId = cursor.getInt(cursor.getColumnIndex("id"));
            crossNightStartTime = cursor.getString(cursor.getColumnIndex("startTime"));
        }
        DbUtils.close(cursor);
        int crossNightId2 = 0;
        String crossNightStopTime2 = "";
        cursor = DbUtils.getDb(this.context).rawQuery("select * from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1  and startTime < '" + twentyFourTime + "' and startTime > '" + zeroTime + "' and  stopTime > '" + twentyFourTime + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            crossNightId2 = cursor.getInt(cursor.getColumnIndex("id"));
            crossNightStopTime2 = cursor.getString(cursor.getColumnIndex("stopTime"));
        }
        DbUtils.close(cursor);
        if (dbList != null && changeList != null) {
            Record dbRecord;
            ContentValues values;
            TreeSet<Integer> goalIdSet = new TreeSet();
            TreeSet<String> dateArr = new TreeSet();
            Iterator it = dbList.iterator();
            while (it.hasNext()) {
                dbRecord = (Record) it.next();
                Iterator it2 = changeList.iterator();
                while (it2.hasNext()) {
                    Record changeRecord = (Record) it2.next();
                    if (changeRecord.getItemsId() > 0 && dbRecord.getItemsId() == changeRecord.getItemsId()) {
                        int end = changeRecord.getEnd();
                        if (end == 86400) {
                            end = 86399;
                        }
                        values = new ContentValues();
                        if (crossNightId > 0 && dbRecord.getItemsId() == crossNightId) {
                            String endTime = changeDate + " " + DateTime.calculateTime2((long) end);
                            values.put("take", Integer.valueOf(DateTime.cal_secBetween(crossNightStartTime, endTime)));
                            values.put("stopTime", endTime);
                        } else if (crossNightId2 <= 0 || dbRecord.getItemsId() != crossNightId2) {
                            values.put("take", Integer.valueOf(changeRecord.getRanage()));
                            values.put("startTime", changeDate + " " + DateTime.calculateTime2((long) changeRecord.getBegin()));
                            values.put("stopTime", changeDate + " " + DateTime.calculateTime2((long) end));
                        } else {
                            String startTime = changeDate + " " + DateTime.calculateTime2((long) changeRecord.getBegin());
                            values.put("take", Integer.valueOf(DateTime.cal_secBetween(startTime, crossNightStopTime2)));
                            values.put("startTime", startTime);
                        }
                        if (DbUtils.queryIsUploadByActItemId(this.context, dbRecord.getItemsId() + "") > 0) {
                            values.put("endUpdateTime", DateTime.getTimeString());
                        }
                        goalIdSet.add(Integer.valueOf(dbRecord.getGoalId()));
                        updateLabel(dbRecord.getItemsId(), values.getAsInteger("take").intValue());
                        DbUtils.getDb(this.context).update("t_act_item", values, "id = " + dbRecord.getItemsId(), null);
                        dbList2.remove(dbRecord);
                        changeList2.remove(changeRecord);
                    }
                }
            }
            it = dbList2.iterator();
            while (it.hasNext()) {
                dbRecord = (Record) it.next();
                if (crossNightId > 0 && dbRecord.getItemsId() == crossNightId) {
                    values = new ContentValues();
                    values.put("take", Integer.valueOf(DateTime.cal_secBetween(crossNightStartTime, YesterdayTwentyFourTime)));
                    values.put("stopTime", YesterdayTwentyFourTime);
                    if (DbUtils.queryIsUploadByActItemId(this.context, dbRecord.getItemsId() + "") > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    DbUtils.getDb(this.context).update("t_act_item", values, "id = " + dbRecord.getItemsId(), null);
                    goalIdSet.add(Integer.valueOf(dbRecord.getGoalId()));
                    DbUtils.deleteLabelLinkByItemsId(this.context, dbRecord.getItemsId() + "");
                } else if (crossNightId2 <= 0 || dbRecord.getItemsId() != crossNightId2) {
                    if (DbUtils.queryIsUploadByActItemId(this.context, dbRecord.getItemsId() + "") > 0) {
                        values = new ContentValues();
                        values.put("endUpdateTime", DateTime.getTimeString());
                        values.put("isDelete", Integer.valueOf(1));
                        values.put("deleteTime", DateTime.getTimeString());
                        DbUtils.getDb(this.context).update("t_act_item", values, "id = " + dbRecord.getItemsId(), null);
                        goalIdSet.add(Integer.valueOf(dbRecord.getGoalId()));
                    } else {
                        DbUtils.getDb(this.context).delete("t_act_item", "id = " + dbRecord.getItemsId(), null);
                        goalIdSet.add(Integer.valueOf(dbRecord.getGoalId()));
                    }
                    DbUtils.deleteLabelLinkByItemsId(this.context, dbRecord.getItemsId() + "");
                } else {
                    values = new ContentValues();
                    values.put("startTime", changeNextZeroTime);
                    values.put("take", Integer.valueOf(DateTime.cal_secBetween(changeNextZeroTime, crossNightStopTime2)));
                    if (DbUtils.queryIsUploadByActItemId(this.context, dbRecord.getItemsId() + "") > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    DbUtils.getDb(this.context).update("t_act_item", values, "id = " + dbRecord.getItemsId(), null);
                    goalIdSet.add(Integer.valueOf(dbRecord.getGoalId()));
                    DbUtils.deleteLabelLinkByItemsId(this.context, dbRecord.getItemsId() + "");
                }
            }
            if (changeList2.size() > 0) {
                int userId = DbUtils.queryUserId2(this.context);
                it = changeList2.iterator();
                while (it.hasNext()) {
                    dbRecord = (Record) it.next();
                    values = new ContentValues();
                    values.put("userId", Integer.valueOf(userId));
                    values.put("actId", Integer.valueOf(dbRecord.getGoalId()));
                    values.put("actType", DbUtils.queryActTypeById(this.context, dbRecord.getGoalId() + ""));
                    values.put("startTime", changeDate + " " + DateTime.calculateTime2((long) dbRecord.getBegin()));
                    values.put("take", Integer.valueOf(dbRecord.getRanage()));
                    values.put("stopTime", changeDate + " " + DateTime.calculateTime2(dbRecord.getEnd() >= 86400 ? 86399 : (long) dbRecord.getEnd()));
                    values.put("isEnd", Integer.valueOf(1));
                    values.put("isRecord", Integer.valueOf(4));
                    DbUtils.getDb(this.context).insert("t_act_item", null, values);
                    goalIdSet.add(Integer.valueOf(dbRecord.getGoalId()));
                }
            }
            Cursor cursor5 = DbUtils.getDb(this.context).rawQuery("select * from " + tableName + " where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and isEnd is 1 and stopTime = '" + YesterdayTwentyFourTime + "'", null);
            Cursor cursor6 = DbUtils.getDb(this.context).rawQuery("select * from " + tableName + " where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and isEnd is 1 and startTime = '" + zeroTime + "'", null);
            if (cursor5.getCount() == 1 && cursor6.getCount() == 1) {
                mergeCorssNightRecord(cursor5, cursor6);
            }
            DbUtils.close(cursor5);
            DbUtils.close(cursor6);
            Cursor cursor7 = DbUtils.getDb(this.context).rawQuery("select * from " + tableName + " where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and isEnd is 1 and stopTime = '" + twentyFourTime + "'", null);
            Cursor cursor8 = DbUtils.getDb(this.context).rawQuery("select * from " + tableName + " where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and isEnd is 1 and startTime = '" + changeNextZeroTime + "'", null);
            if (cursor7.getCount() == 1 && cursor8.getCount() == 1) {
                mergeCorssNightRecord(cursor7, cursor8);
            }
            DbUtils.close(cursor7);
            DbUtils.close(cursor8);
            GeneralUtils.toastShort(this.context, (int) R.string.str_modify_save);
            dateArr.add(getCurrentDay());
            new Thread(new AllocationAndStaticsRunnable(this.context, goalIdSet, dateArr)).start();
            this.recordMapFromDb.put(getCurrentDay(), DbUtils.queryItemsIdByDate(this.context, getCurrentDay()));
        }
    }

    private void updateLabel(int itemId, int take) {
        ContentValues values = new ContentValues();
        values.put("take", Integer.valueOf(take));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_routine_link", values, " itemsId =  " + itemId, null);
    }

    private void mergeCorssNightRecord(Cursor cursor5, Cursor cursor6) {
        cursor5.moveToNext();
        int id1 = cursor5.getInt(cursor5.getColumnIndex("id"));
        int actId1 = cursor5.getInt(cursor5.getColumnIndex("actId"));
        String remarks1 = cursor5.getString(cursor5.getColumnIndex("remarks"));
        String startTime = cursor5.getString(cursor5.getColumnIndex("startTime"));
        int isUpload1 = cursor5.getInt(cursor5.getColumnIndex("isUpload"));
        cursor6.moveToNext();
        int id2 = cursor6.getInt(cursor6.getColumnIndex("id"));
        int actId2 = cursor6.getInt(cursor6.getColumnIndex("actId"));
        String remarks2 = cursor6.getString(cursor6.getColumnIndex("remarks"));
        String stopTime = cursor6.getString(cursor6.getColumnIndex("stopTime"));
        int isUpload2 = cursor6.getInt(cursor6.getColumnIndex("isUpload"));
        if (actId1 != actId2) {
            return;
        }
        if (remarks2 == null || remarks2.length() <= 0 || remarks1 == null || remarks1.length() <= 0) {
            int take = DateTime.cal_secBetween(startTime, stopTime);
            ContentValues values = new ContentValues();
            values.put("startTime", startTime);
            values.put("take", Integer.valueOf(DateTime.cal_secBetween(startTime, stopTime)));
            String str = "remarks";
            StringBuilder stringBuilder = new StringBuilder();
            if (remarks1 == null) {
                remarks1 = "";
            }
            stringBuilder = stringBuilder.append(remarks1);
            if (remarks2 == null) {
                remarks2 = "";
            }
            values.put(str, stringBuilder.append(remarks2).toString());
            if (isUpload2 > 0) {
                values.put("endUpdateTime", DateTime.getTimeString());
            }
            DbUtils.getDb(this.context).update("t_act_item", values, " id = " + id2, null);
            deleteItems(id1, isUpload1);
            ContentValues values2 = new ContentValues();
            values2.put("take", Integer.valueOf(take));
            values2.put("itemsId", Integer.valueOf(id2));
            values2.put("endUpdateTime", DateTime.getTimeString());
            DbUtils.getDb(this.context).update("t_routine_link", values2, " itemsId =  " + id1, null);
            updateLabel(id2, take);
        }
    }

    private LinearLayout getAddMinuteItems(LinearLayout ll) {
        String min = "min";
        String H = "H";
        String[] minArr = new String[]{2 + H, 1 + H, 30 + min, 15 + min, 5 + min, 1 + min};
        int[] minIntArr = new int[]{120, 60, 30, 15, 5, 1};
        for (int i = 0; i < minArr.length; i++) {
            RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.tem_sigle_act_v2, null);
            rl_temp_show_outer.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LinearLayout ll = (LinearLayout) v.getParent();
                    int count = ll.getChildCount();
                    for (int i = 0; i < count; i++) {
                        ll.getChildAt(i).setBackgroundColor(AddRecordActivity.this.getResources().getColor(R.color.gray));
                    }
                    v.setBackgroundColor(AddRecordActivity.this.getResources().getColor(R.color.black));
                    String min = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
                    AddRecordActivity.this.selectMin = Integer.parseInt(min);
                    AddRecordActivity.log("selectMin:" + AddRecordActivity.this.selectMin);
                }
            });
            TextView tv_temp_act_name = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_name);
            ImageView iv_temp_color = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_color);
            ((TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_id)).setText(minIntArr[i] + "");
            tv_temp_act_name.setText(minArr[i]);
            tv_temp_act_name.setBackgroundColor(getResources().getColor(R.color.bg_blue1));
            ll.addView(rl_temp_show_outer);
        }
        return ll;
    }

    private LinearLayout getAddItems(LinearLayout ll) {
        Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.GoalsList(this.context), null);
        if (cur.getCount() > 0) {
            ll.removeAllViews();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("id"));
                if (Act2TypeMap == null || Act2TypeMap.get(id) == null) {
                    Act2TypeMap = getAct2TypeMap();
                }
                if (cur.getCount() <= 4 || ((Integer) Act2TypeMap.get(Integer.valueOf(Integer.parseInt(id)))).intValue() != 10) {
                    String actName = cur.getString(cur.getColumnIndex("actName"));
                    String color = cur.getString(cur.getColumnIndex("color"));
                    if (cur.isFirst()) {
                        ll.addView(getAddActItems("-1", getString(R.string.str_watch), "1", null));
                        ll.addView(getAddActItems("0", getString(R.string.str_clear), null, null));
                    }
                    ll.addView(getAddActItems(id, actName, null, color));
                }
            }
        }
        DbUtils.close(cur);
        return ll;
    }

    private RelativeLayout getAddActItems(String id, String name, String label, String color) {
        RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.tem_sigle_act_v2, null);
        rl_temp_show_outer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) v.getParent();
                int count = ll.getChildCount();
                for (int i = 0; i < count; i++) {
                    ((RelativeLayout) ((RelativeLayout) ll.getChildAt(i)).getChildAt(1)).getChildAt(0).setVisibility(8);
                }
                ((RelativeLayout) ((RelativeLayout) v).getChildAt(1)).getChildAt(0).setVisibility(0);
                RelativeLayout rl = (RelativeLayout) v;
                AddRecordActivity.this.checkActId = ((TextView) rl.getChildAt(0)).getText().toString();
                AddRecordActivity.log("checkActId:" + AddRecordActivity.this.checkActId);
                if (AddRecordActivity.this.isOnContinueSelectState) {
                    AddRecordActivity.this.cancelContinueState();
                }
                if ("-1".equals(AddRecordActivity.this.checkActId)) {
                    MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_watch");
                } else if ("0".equals(AddRecordActivity.this.checkActId)) {
                    MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_clear");
                } else {
                    MobclickAgent.onEvent(AddRecordActivity.this.context, "add_record_allocat_goal");
                }
            }
        });
        TextView tv_temp_act_name = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_name);
        ImageView iv_temp_color = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_color);
        ImageView iv_temp_check = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_check);
        ((TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_id)).setText(id);
        tv_temp_act_name.setText(name);
        tv_temp_act_name.setTextColor(getResources().getColor(R.color.black_tran_es));
        if (color != null) {
            iv_temp_color.setBackgroundColor(getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()));
        }
        if (label != null && label.length() > 0) {
            iv_temp_check.setVisibility(0);
        }
        return rl_temp_show_outer;
    }

    public HashMap<Integer, Integer> getAct2TypeMap() {
        HashMap<Integer, Integer> map = new HashMap();
        Cursor cur = DbUtils.getDb2(this.context).rawQuery("select id,type from t_act where " + DbUtils.getWhereUserId(this.context), null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                map.put(Integer.valueOf(cur.getInt(cur.getColumnIndex("id"))), Integer.valueOf(cur.getInt(cur.getColumnIndex(a.a))));
            }
        }
        DbUtils.close(cur);
        return map;
    }

    private boolean isNotSave() {
        try {
            ArrayList<Record> dbList = (ArrayList) this.recordMapFromDb.get(getCurrentDay());
            ArrayList<Record> changeList = this.recordUtils.getDateData(getCurrentDay()).getRecordList();
            if (changeList != null) {
                if (dbList == null || dbList.size() != changeList.size()) {
                    return true;
                }
                if (dbList.size() == changeList.size()) {
                    int size = dbList.size();
                    for (int i = 0; i < size; i++) {
                        Record dbRecord = (Record) dbList.get(i);
                        Record changeRecord = (Record) changeList.get(i);
                        if (dbRecord.getBegin() != changeRecord.getBegin() || dbRecord.getEnd() != changeRecord.getEnd() || dbRecord.getGoalId() != changeRecord.getGoalId()) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return false;
    }

    private void showNotSaveDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage((int) R.string.str_no_save_prompt).setPositiveButton((int) R.string.str_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddRecordActivity.this.clickSaveData();
                dialog.dismiss();
                AddRecordActivity.this.finish();
            }
        }).setNegativeButton((int) R.string.str_no_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AddRecordActivity.this.finish();
            }
        }).create().show();
    }

    public void onBackPressed() {
        if (this.isSaveData || !isNotSave()) {
            super.onBackPressed();
        } else {
            showNotSaveDialog();
        }
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
