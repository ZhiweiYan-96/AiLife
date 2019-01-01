package com.record.myLife.add;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils.PopWindowM;
import com.umeng.analytics.MobclickAgent;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;

public class AddRecordDigitActivity extends BaseActivity implements OnClickListener {
    static String TAG = "override";
    int FOCUS_HOUR1 = 1;
    int FOCUS_HOUR2 = 3;
    int FOCUS_MINUTE1 = 2;
    int FOCUS_MINUTE2 = 4;
    int TYPE_DATE_ADD = 2;
    int TYPE_DATE_LEFT = 1;
    int TYPE_DATE_RIGHT = 2;
    int TYPE_DATE_SUB = 1;
    String addEndTime;
    String addStartTime;
    Button btn_add_record_digit_add_date1;
    Button btn_add_record_digit_add_date2;
    ImageView btn_add_record_digit_back;
    Button btn_add_record_digit_backspace;
    Button btn_add_record_digit_date1;
    Button btn_add_record_digit_date2;
    TextView btn_add_record_digit_hour1;
    TextView btn_add_record_digit_hour2;
    TextView btn_add_record_digit_minute1;
    TextView btn_add_record_digit_minute2;
    Button btn_add_record_digit_number0;
    Button btn_add_record_digit_number1;
    Button btn_add_record_digit_number2;
    Button btn_add_record_digit_number3;
    Button btn_add_record_digit_number4;
    Button btn_add_record_digit_number5;
    Button btn_add_record_digit_number6;
    Button btn_add_record_digit_number7;
    Button btn_add_record_digit_number8;
    Button btn_add_record_digit_number9;
    Button btn_add_record_digit_numbernext;
    Button btn_add_record_digit_numberpre;
    Button btn_add_record_digit_save;
    Button btn_add_record_digit_sub_date1;
    Button btn_add_record_digit_sub_date2;
    TextView btn_add_record_digit_time_range;
    String checkActId = "";
    Context context;
    AnimationController controller;
    String date1 = "";
    String date2 = "";
    String defaultCheckActId = "";
    EditText et_remind_add_remark;
    int focusOn = this.FOCUS_HOUR1;
    MyOnItemsClickListener goalItemsIdClickLister = new MyOnItemsClickListener() {
        public void onClick(View v, String id) {
            AddRecordDigitActivity.this.checkActId = id;
            MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_goal");
            AddRecordDigitActivity.log("checkActId:" + AddRecordDigitActivity.this.checkActId);
        }
    };
    int hour1 = 0;
    int hour2 = 0;
    boolean isClickNumberFocus = false;
    boolean isFocuseNext = false;
    boolean isShowResetAnimation = false;
    int itemsId = 0;
    int labelId = 0;
    LinearLayout ll_add_record_digit_items;
    LinearLayout ll_add_record_label;
    int minute1 = 0;
    int minute2 = 0;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_add_record_digit_number1) {
                AddRecordDigitActivity.this.clickNumber(1);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_1");
            } else if (id == R.id.btn_add_record_digit_number2) {
                AddRecordDigitActivity.this.clickNumber(2);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_2");
            } else if (id == R.id.btn_add_record_digit_number3) {
                AddRecordDigitActivity.this.clickNumber(3);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_3");
            } else if (id == R.id.btn_add_record_digit_number4) {
                AddRecordDigitActivity.this.clickNumber(4);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_4");
            } else if (id == R.id.btn_add_record_digit_number5) {
                AddRecordDigitActivity.this.clickNumber(5);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_5");
            } else if (id == R.id.btn_add_record_digit_number6) {
                AddRecordDigitActivity.this.clickNumber(6);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_6");
            } else if (id == R.id.btn_add_record_digit_number7) {
                AddRecordDigitActivity.this.clickNumber(7);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_7");
            } else if (id == R.id.btn_add_record_digit_number8) {
                AddRecordDigitActivity.this.clickNumber(8);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_8");
            } else if (id == R.id.btn_add_record_digit_number9) {
                AddRecordDigitActivity.this.clickNumber(9);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_9");
            } else if (id == R.id.btn_add_record_digit_number0) {
                AddRecordDigitActivity.this.clickNumber(0);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_num_0");
            } else if (id == R.id.btn_add_record_digit_hour1) {
                AddRecordDigitActivity.this.focusOn = AddRecordDigitActivity.this.FOCUS_HOUR1;
                AddRecordDigitActivity.this.isClickNumberFocus = true;
                AddRecordDigitActivity.this.updateFocusView();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_left_hour");
            } else if (id == R.id.btn_add_record_digit_hour2) {
                AddRecordDigitActivity.this.focusOn = AddRecordDigitActivity.this.FOCUS_HOUR2;
                AddRecordDigitActivity.this.isClickNumberFocus = true;
                AddRecordDigitActivity.this.updateFocusView();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_right_hour");
            } else if (id == R.id.btn_add_record_digit_minute1) {
                AddRecordDigitActivity.this.focusOn = AddRecordDigitActivity.this.FOCUS_MINUTE1;
                AddRecordDigitActivity.this.isClickNumberFocus = true;
                AddRecordDigitActivity.this.updateFocusView();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_left_min");
            } else if (id == R.id.btn_add_record_digit_minute2) {
                AddRecordDigitActivity.this.focusOn = AddRecordDigitActivity.this.FOCUS_MINUTE2;
                AddRecordDigitActivity.this.isClickNumberFocus = true;
                AddRecordDigitActivity.this.updateFocusView();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_right_min");
            } else if (id == R.id.btn_add_record_digit_numberpre) {
                AddRecordDigitActivity.this.focusPre();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_pre");
            } else if (id == R.id.btn_add_record_digit_numbernext) {
                AddRecordDigitActivity.this.focusNext();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_next");
            } else if (id == R.id.btn_add_record_digit_backspace) {
                AddRecordDigitActivity.this.backspace();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_c");
            } else if (id == R.id.btn_add_record_digit_save) {
                AddRecordDigitActivity.this.clickSaveData();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_broad_enter");
            } else if (id == R.id.btn_add_record_digit_sub_date1) {
                AddRecordDigitActivity.this.clickChangeDate(AddRecordDigitActivity.this.TYPE_DATE_LEFT, AddRecordDigitActivity.this.TYPE_DATE_SUB);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_left_sub");
            } else if (id == R.id.btn_add_record_digit_add_date1) {
                AddRecordDigitActivity.this.clickChangeDate(AddRecordDigitActivity.this.TYPE_DATE_LEFT, AddRecordDigitActivity.this.TYPE_DATE_ADD);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_left_add");
            } else if (id == R.id.btn_add_record_digit_sub_date2) {
                AddRecordDigitActivity.this.clickChangeDate(AddRecordDigitActivity.this.TYPE_DATE_RIGHT, AddRecordDigitActivity.this.TYPE_DATE_SUB);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_right_sub");
            } else if (id == R.id.btn_add_record_digit_add_date2) {
                AddRecordDigitActivity.this.clickChangeDate(AddRecordDigitActivity.this.TYPE_DATE_RIGHT, AddRecordDigitActivity.this.TYPE_DATE_ADD);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_right_add");
            } else if (id == R.id.btn_add_record_digit_back) {
                AddRecordDigitActivity.this.onBackPressed();
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_close");
            } else if (id == R.id.tv_add_record_digit_title) {
                PopWindowM.showChooseAddRecordTypeDialog(AddRecordDigitActivity.this.context, PopWindowM.ADD_RECORD_DIGIT, AddRecordDigitActivity.this.addStartTime, AddRecordDigitActivity.this.addEndTime);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_title");
            }
        }
    };
    MyGoalItemsLayout myGoalItemsLayout = null;
    String theDayBefore = DateTime.beforeNDays2Str(-2);
    String today = DateTime.getDateString();
    TextView tv_add_record_digit_info;
    TextView tv_add_record_digit_title;
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

    public static void startActivity(Context context, String startTime, String stopTime, String defaultCheckActId) {
        Intent it = new Intent(context, AddRecordDigitActivity.class);
        it.putExtra("startTime", startTime);
        it.putExtra("stopTime", stopTime);
        it.putExtra("defaultCheckActId", defaultCheckActId);
        context.startActivity(it);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_digit_v2);
        this.context = this;
        TAG += getClass().getSimpleName();
        this.controller = new AnimationController();
        this.today = DateTime.getDateString();
        this.yesterday = DateTime.beforeNDays2Str(-1);
        this.theDayBefore = DateTime.beforeNDays2Str(-2);
        log("启动数字添加/修改界面");
        String tempId = getIntent().getStringExtra("itemId");
        if (tempId != null && tempId.length() > 0) {
            this.itemsId = Integer.parseInt(tempId);
        }
        String startTime = getIntent().getStringExtra("startTime");
        String stopTime = getIntent().getStringExtra("stopTime");
        this.defaultCheckActId = getIntent().getStringExtra("defaultCheckActId");
        initFind();
        initData(startTime, stopTime);
        clearNofi();
        MobclickAgent.onEvent(getApplicationContext(), "addrecord_open_digit_activity");
    }

    private void clearNofi() {
        try {
            int clearNotification = getIntent().getIntExtra("clearNotification", 0);
            if (clearNotification > 0) {
                ((NotificationManager) this.context.getSystemService("notification")).cancel(clearNotification);
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void initData(String startTime, String stopTime) {
        setAddTime(startTime, stopTime);
        updateDateUi();
        isOverOthersAndupdateTimeRange();
        updateNumber();
    }

    private void setAddTime(String startTime, String endTime) {
        Cursor cursor;
        if (this.itemsId != 0) {
            cursor = DbUtils.getDb(this.context).rawQuery("select startTime,stopTime from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and id = " + this.itemsId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                this.addStartTime = cursor.getString(cursor.getColumnIndex("startTime"));
                this.addEndTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                this.date1 = this.addStartTime.substring(0, this.addStartTime.indexOf(" "));
                this.date2 = this.addEndTime.substring(0, this.addEndTime.indexOf(" "));
                DbUtils.close(cursor);
            } else {
                finish();
                return;
            }
        } else if (startTime == null || startTime.length() <= 0 || endTime == null || endTime.length() <= 0) {
            cursor = DbUtils.getDb(this.context).rawQuery("select stopTime from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isEnd is 1  and isDelete is not 1 and stopTime >= '" + this.theDayBefore + " 00:00:00' order by startTime desc limit 1", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                this.addStartTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                this.date1 = this.addStartTime.substring(0, this.addStartTime.indexOf(" "));
            } else {
                this.addStartTime = DateTime.getTimeString();
                this.date1 = this.today;
            }
            DbUtils.close(cursor);
            this.addEndTime = DateTime.getTimeString();
            this.date2 = this.today;
        } else {
            this.addStartTime = startTime;
            this.addEndTime = endTime;
            this.date1 = this.addStartTime.substring(0, this.addStartTime.indexOf(" "));
            this.date2 = this.addEndTime.substring(0, this.addEndTime.indexOf(" "));
        }
        if (this.date1 == null || this.date1.length() == 0) {
            this.date1 = this.today;
        }
        if (this.date2 == null || this.date2.length() == 0) {
            this.date2 = this.today;
        }
        setHourMinuteByTime(this.addStartTime, this.TYPE_DATE_LEFT);
        setHourMinuteByTime(this.addEndTime, this.TYPE_DATE_RIGHT);
    }

    private void setHourMinuteByTime(String addStartTime, int type) {
        int space = addStartTime.indexOf(" ");
        int ma1 = addStartTime.indexOf(":");
        int ma2 = addStartTime.lastIndexOf(":");
        if (type == this.TYPE_DATE_LEFT) {
            this.hour1 = Integer.parseInt(addStartTime.substring(space + 1, ma1));
            this.minute1 = Integer.parseInt(addStartTime.substring(ma1 + 1, ma2));
        } else {
            this.hour2 = Integer.parseInt(addStartTime.substring(space + 1, ma1));
            this.minute2 = Integer.parseInt(addStartTime.substring(ma1 + 1, ma2));
        }
        if (this.isShowResetAnimation) {
            this.isShowResetAnimation = false;
            this.controller.slideLeft(this.btn_add_record_digit_hour1, 350, 0);
            this.controller.slideLeft(this.btn_add_record_digit_minute1, 350, 0);
            this.controller.slideLeft(this.btn_add_record_digit_hour2, 350, 0);
            this.controller.slideLeft(this.btn_add_record_digit_minute2, 350, 0);
            this.checkActId = "";
            this.myGoalItemsLayout.cancelAllSelect();
        }
    }

    private void updateDateUi() {
        if (this.date1.equals(this.today)) {
            this.btn_add_record_digit_date1.setText(getString(R.string.str_today));
            this.btn_add_record_digit_sub_date1.setVisibility(0);
            this.btn_add_record_digit_add_date1.setVisibility(4);
        } else if (this.date1.equals(this.yesterday)) {
            this.btn_add_record_digit_date1.setText(getString(R.string.str_Yesterday));
            this.btn_add_record_digit_sub_date1.setVisibility(0);
            this.btn_add_record_digit_add_date1.setVisibility(0);
        } else if (this.date1.equals(this.theDayBefore)) {
            this.btn_add_record_digit_date1.setText(getString(R.string.str_the_day_before_yesterday));
            this.btn_add_record_digit_sub_date1.setVisibility(4);
            this.btn_add_record_digit_add_date1.setVisibility(0);
        }
        if (this.date2.equals(this.today)) {
            this.btn_add_record_digit_date2.setText(getString(R.string.str_today));
            this.btn_add_record_digit_sub_date2.setVisibility(0);
            this.btn_add_record_digit_add_date2.setVisibility(4);
        } else if (this.date2.equals(this.yesterday)) {
            this.btn_add_record_digit_date2.setText(getString(R.string.str_Yesterday));
            this.btn_add_record_digit_sub_date2.setVisibility(0);
            this.btn_add_record_digit_add_date2.setVisibility(0);
        } else if (this.date2.equals(this.theDayBefore)) {
            this.btn_add_record_digit_date2.setText(getString(R.string.str_the_day_before_yesterday));
            this.btn_add_record_digit_sub_date2.setVisibility(4);
            this.btn_add_record_digit_add_date2.setVisibility(0);
        }
        if (this.date1.equals(this.date2)) {
            this.btn_add_record_digit_date1.setTextColor(getResources().getColor(R.color.black));
            this.btn_add_record_digit_date2.setTextColor(getResources().getColor(R.color.black));
            return;
        }
        this.btn_add_record_digit_date1.setTextColor(getResources().getColor(R.color.bg_yellow2));
        this.btn_add_record_digit_date2.setTextColor(getResources().getColor(R.color.bg_yellow2));
    }

    private void initFind() {
        this.uiComponent = new UiComponent();
        setUiComponent(this.uiComponent);
        this.btn_add_record_digit_number1 = (Button) findViewById(R.id.btn_add_record_digit_number1);
        this.btn_add_record_digit_number2 = (Button) findViewById(R.id.btn_add_record_digit_number2);
        this.btn_add_record_digit_number3 = (Button) findViewById(R.id.btn_add_record_digit_number3);
        this.btn_add_record_digit_number4 = (Button) findViewById(R.id.btn_add_record_digit_number4);
        this.btn_add_record_digit_number5 = (Button) findViewById(R.id.btn_add_record_digit_number5);
        this.btn_add_record_digit_number6 = (Button) findViewById(R.id.btn_add_record_digit_number6);
        this.btn_add_record_digit_number7 = (Button) findViewById(R.id.btn_add_record_digit_number7);
        this.btn_add_record_digit_number8 = (Button) findViewById(R.id.btn_add_record_digit_number8);
        this.btn_add_record_digit_number9 = (Button) findViewById(R.id.btn_add_record_digit_number9);
        this.btn_add_record_digit_number0 = (Button) findViewById(R.id.btn_add_record_digit_number0);
        this.btn_add_record_digit_numberpre = (Button) findViewById(R.id.btn_add_record_digit_numberpre);
        this.btn_add_record_digit_numbernext = (Button) findViewById(R.id.btn_add_record_digit_numbernext);
        this.btn_add_record_digit_backspace = (Button) findViewById(R.id.btn_add_record_digit_backspace);
        this.btn_add_record_digit_save = (Button) findViewById(R.id.btn_add_record_digit_save);
        this.btn_add_record_digit_sub_date1 = (Button) findViewById(R.id.btn_add_record_digit_sub_date1);
        this.btn_add_record_digit_date1 = (Button) findViewById(R.id.btn_add_record_digit_date1);
        this.btn_add_record_digit_add_date1 = (Button) findViewById(R.id.btn_add_record_digit_add_date1);
        this.btn_add_record_digit_sub_date2 = (Button) findViewById(R.id.btn_add_record_digit_sub_date2);
        this.btn_add_record_digit_date2 = (Button) findViewById(R.id.btn_add_record_digit_date2);
        this.btn_add_record_digit_add_date2 = (Button) findViewById(R.id.btn_add_record_digit_add_date2);
        this.btn_add_record_digit_back = (ImageView) findViewById(R.id.btn_add_record_digit_back);
        this.btn_add_record_digit_hour1 = (TextView) findViewById(R.id.btn_add_record_digit_hour1);
        this.btn_add_record_digit_hour2 = (TextView) findViewById(R.id.btn_add_record_digit_hour2);
        this.btn_add_record_digit_minute1 = (TextView) findViewById(R.id.btn_add_record_digit_minute1);
        this.btn_add_record_digit_minute2 = (TextView) findViewById(R.id.btn_add_record_digit_minute2);
        this.btn_add_record_digit_time_range = (TextView) findViewById(R.id.btn_add_record_digit_time_range);
        this.ll_add_record_digit_items = (LinearLayout) findViewById(R.id.ll_add_record_digit_items);
        this.ll_add_record_label = (LinearLayout) findViewById(R.id.ll_add_record_label);
        this.tv_add_record_digit_info = (TextView) findViewById(R.id.tv_add_record_digit_info);
        this.tv_add_record_digit_title = (TextView) findViewById(R.id.tv_add_record_digit_title);
        this.et_remind_add_remark = (EditText) findViewById(R.id.et_remind_add_remark);
        this.et_remind_add_remark.setCursorVisible(false);
        this.et_remind_add_remark.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AddRecordDigitActivity.this.et_remind_add_remark.setCursorVisible(true);
                MobclickAgent.onEvent(AddRecordDigitActivity.this.context, "add_record_digit_remarks");
            }
        });
        this.btn_add_record_digit_number1.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number2.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number3.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number4.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number5.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number6.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number7.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number8.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number9.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_number0.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_hour1.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_hour2.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_minute1.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_minute2.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_numberpre.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_numbernext.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_backspace.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_save.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_sub_date1.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_date1.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_add_date1.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_sub_date2.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_date2.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_add_date2.setOnClickListener(this.myClickListener);
        this.btn_add_record_digit_back.setOnClickListener(this.myClickListener);
        this.tv_add_record_digit_title.setOnClickListener(this.myClickListener);
        this.uiComponent.tv_remind_tomato_label.setOnClickListener(this);
        if (this.itemsId == 0) {
            this.myGoalItemsLayout = new MyGoalItemsLayout((Activity) this.context, this.ll_add_record_digit_items, this.goalItemsIdClickLister, this.defaultCheckActId);
            this.myGoalItemsLayout.getAddItems();
        } else {
            this.ll_add_record_digit_items.setVisibility(8);
            this.uiComponent.tv_remind_tomato_label.setVisibility(8);
            this.ll_add_record_label.setVisibility(8);
        }
        this.myClickListener.onClick(this.btn_add_record_digit_hour1);
    }

    private void initSetUI() {
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remind_tomato_label:
                if (this.checkActId == null || this.checkActId.length() <= 0) {
                    ToastUtils.toastShort(this.context, "请先选择目标！");
                    MobclickAgent.onEvent(this.context, "add_record_digit_label_noselect_goal");
                    return;
                }
                int actType = DbUtils.queryActTypeById(this.context, this.checkActId).intValue();
                if (actType == 11) {
                    actType = 10;
                }
                LabelSelectActivity.startActivity((Activity) this.context, actType);
                MobclickAgent.onEvent(this.context, "add_record_digit_label");
                return;
            default:
                return;
        }
    }

    private void clickChangeDate(int dateType, int changeType) {
        if (dateType == this.TYPE_DATE_LEFT) {
            if (changeType == this.TYPE_DATE_SUB) {
                this.date1 = DateTime.beforeNDays_v2(this.date1, -1);
            } else {
                this.date1 = DateTime.beforeNDays_v2(this.date1, 1);
            }
        } else if (dateType == this.TYPE_DATE_RIGHT) {
            if (changeType == this.TYPE_DATE_SUB) {
                this.date2 = DateTime.beforeNDays_v2(this.date2, -1);
            } else {
                this.date2 = DateTime.beforeNDays_v2(this.date2, 1);
            }
        }
        updateDateUi();
        isOverOthersAndupdateTimeRange();
    }

    private void backspace() {
        if (this.focusOn == this.FOCUS_HOUR1) {
            this.hour1 = backpaceHour(this.hour1);
        } else if (this.focusOn == this.FOCUS_MINUTE1) {
            this.minute1 = backpaceMinute(this.minute1);
        } else if (this.focusOn == this.FOCUS_HOUR2) {
            this.hour2 = backpaceHour(this.hour2);
        } else if (this.focusOn == this.FOCUS_MINUTE2) {
            this.minute2 = backpaceMinute(this.minute2);
        }
        updateNumber();
    }

    private int backpaceMinute(int minute) {
        if (minute == 0) {
            focusPre();
        }
        if (minute <= 9 || minute >= 60) {
            return 0;
        }
        return minute / 10;
    }

    private int backpaceHour(int hour1) {
        if (hour1 == 0) {
            focusPre();
        }
        if (hour1 <= 9 || hour1 > 24) {
            return 0;
        }
        return hour1 / 10;
    }

    private void clickNumber(int number) {
        if (this.focusOn == this.FOCUS_HOUR1) {
            this.hour1 = changeHour(this.hour1, number);
        } else if (this.focusOn == this.FOCUS_MINUTE1) {
            this.minute1 = changeMinute(this.minute1, number);
        } else if (this.focusOn == this.FOCUS_HOUR2) {
            this.hour2 = changeHour(this.hour2, number);
        } else if (this.focusOn == this.FOCUS_MINUTE2) {
            this.minute2 = changeMinute(this.minute2, number);
        }
        updateNumber();
        isOverOthersAndupdateTimeRange();
    }

    private void isOverOthersAndupdateTimeRange() {
        if (this.date1 == null || this.date1.length() == 0) {
            log("isOverOthersAndupdateTimeRange date1为空，date1:" + this.date1);
            this.date1 = this.today;
        }
        if (this.date2 == null || this.date2.length() == 0) {
            log("isOverOthersAndupdateTimeRange date2为空，date2:" + this.date1);
            this.date2 = this.today;
        }
        String addStartTime = this.date1 + " " + String.format("%02d", new Object[]{Integer.valueOf(this.hour1)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute1)}) + ":59";
        String addEndTime = this.date2 + " " + String.format("%02d", new Object[]{Integer.valueOf(this.hour2)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute2)}) + ":00";
        long range = (DateTime.pars2Calender(addEndTime).getTime().getTime() - DateTime.pars2Calender(addStartTime).getTime().getTime()) / 1000;
        if (range > 1) {
            this.btn_add_record_digit_time_range.setVisibility(0);
            this.btn_add_record_digit_time_range.setText(DateTime.calculateTime5(this.context, (long) DateTime.cal_secBetween(addStartTime, addEndTime)));
            if (range > 28800) {
                this.btn_add_record_digit_time_range.setTextColor(getResources().getColor(R.color.bg_yellow2));
            } else {
                this.btn_add_record_digit_time_range.setTextColor(getResources().getColor(R.color.black));
            }
            if (DateTime.pars2Calender(addStartTime).getTime().getTime() > Calendar.getInstance().getTime().getTime()) {
                updateErrorInfo(getString(R.string.str_starttime_should_less_than_now));
                return;
            } else if (DateTime.pars2Calender(addEndTime).getTime().getTime() > Calendar.getInstance().getTime().getTime()) {
                updateErrorInfo(getString(R.string.str_endtime_should_less_than_now));
                return;
            } else {
                String sql = "";
                if (this.itemsId == 0) {
                    sql = Sql.actItemIsContainOthers(this.context, addStartTime, addEndTime);
                } else {
                    sql = Sql.actItemIsContainOthers(this.context, addStartTime, addEndTime, " and id is not " + this.itemsId);
                }
                Cursor cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
                if (cursor.getCount() > 0) {
                    updateErrorInfo(getString(R.string.str_contain) + " " + cursor.getCount() + " " + getString(R.string.str_records_ok_will_delete_it));
                    DbUtils.close(cursor);
                    return;
                }
                this.tv_add_record_digit_info.setText("");
                DbUtils.close(cursor);
                if (this.itemsId == 0) {
                    sql = Sql.actItemIsStartTimeOverrideOthers(this.context, addStartTime);
                } else {
                    sql = Sql.actItemIsStartTimeOverrideOthers(this.context, addStartTime, " and id is not " + this.itemsId);
                }
                cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
                if (cursor.getCount() > 0) {
                    this.tv_add_record_digit_info.setText(getString(R.string.str_starttime_contain_other_ok_will_override_other));
                    DbUtils.close(cursor);
                    return;
                }
                this.tv_add_record_digit_info.setText("");
                DbUtils.close(cursor);
                if (this.itemsId == 0) {
                    sql = Sql.actItemIsStartTimeOverrideOthers(this.context, addEndTime);
                } else {
                    sql = Sql.actItemIsStartTimeOverrideOthers(this.context, addEndTime, " and id is not " + this.itemsId);
                }
                cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
                if (cursor.getCount() > 0) {
                    this.tv_add_record_digit_info.setText(getString(R.string.str_endtime_contain_other_ok_will_override_other));
                    DbUtils.close(cursor);
                    return;
                }
                this.tv_add_record_digit_info.setText("");
                DbUtils.close(cursor);
                return;
            }
        }
        updateErrorInfo(getString(R.string.str_starttime_should_less_than_endtime));
        this.btn_add_record_digit_time_range.setVisibility(4);
    }

    private void updateErrorInfo(String str) {
        this.tv_add_record_digit_info.setVisibility(0);
        this.tv_add_record_digit_info.setTextColor(getResources().getColor(R.color.bg_yellow2));
        this.tv_add_record_digit_info.setText(str);
    }

    private void updateNumber() {
        this.btn_add_record_digit_hour1.setText(String.format("%02d", new Object[]{Integer.valueOf(this.hour1)}));
        this.btn_add_record_digit_hour2.setText(String.format("%02d", new Object[]{Integer.valueOf(this.hour2)}));
        this.btn_add_record_digit_minute1.setText(String.format("%02d", new Object[]{Integer.valueOf(this.minute1)}));
        this.btn_add_record_digit_minute2.setText(String.format("%02d", new Object[]{Integer.valueOf(this.minute2)}));
    }

    private int changeHour(int hour1, int number) {
        if (this.isClickNumberFocus) {
            hour1 = 0;
            this.isClickNumberFocus = false;
        }
        if (hour1 == 0) {
            hour1 = number;
        } else if (hour1 == 1) {
            hour1 = (hour1 * 10) + number;
        } else if (hour1 == 2) {
            if (number > 3) {
                number = 3;
            }
            hour1 = (hour1 * 10) + number;
        } else {
            hour1 = number;
        }
        isJumpNext(hour1);
        return hour1;
    }

    private void isJumpNext(int hour1) {
        boolean justNext = false;
        if (hour1 > 2 || this.isFocuseNext) {
            justNext = true;
            focusNext();
        }
        if (hour1 < 2 && hour1 > 0 && !justNext) {
            this.isFocuseNext = true;
        }
    }

    private int changeMinute(int minute, int number) {
        if (this.isClickNumberFocus) {
            minute = 0;
            this.isClickNumberFocus = false;
        }
        if (minute == 0 || minute > 5) {
            minute = number;
        } else if (minute > 0 && minute <= 5) {
            minute = (minute * 10) + number;
        } else if (minute > 59) {
            minute = 59;
        }
        boolean justNext = false;
        if (minute > 5 || this.isFocuseNext) {
            justNext = true;
            focusNext();
        }
        if (minute < 5 && !justNext) {
            this.isFocuseNext = true;
        }
        return minute;
    }

    private void focusNext() {
        this.isFocuseNext = false;
        if (this.focusOn >= 4) {
            this.focusOn = 1;
        } else {
            this.focusOn++;
        }
        this.isClickNumberFocus = true;
        updateFocusView();
    }

    private void focusPre() {
        if (this.focusOn <= 1) {
            this.focusOn = 4;
        } else {
            this.focusOn--;
        }
        updateFocusView();
    }

    private void updateFocusView() {
        this.btn_add_record_digit_hour1.setTextColor(getResources().getColor(R.color.black_tran_es));
        this.btn_add_record_digit_hour2.setTextColor(getResources().getColor(R.color.black_tran_es));
        this.btn_add_record_digit_minute1.setTextColor(getResources().getColor(R.color.black_tran_es));
        this.btn_add_record_digit_minute2.setTextColor(getResources().getColor(R.color.black_tran_es));
        if (this.focusOn == 1) {
            this.btn_add_record_digit_hour1.setBackgroundResource(R.color.bg_blue1);
            this.btn_add_record_digit_hour2.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute1.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute2.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_hour1.setTextColor(getResources().getColor(R.color.white2));
        } else if (this.focusOn == 2) {
            this.btn_add_record_digit_hour1.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute1.setBackgroundResource(R.color.bg_blue1);
            this.btn_add_record_digit_hour2.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute2.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute1.setTextColor(getResources().getColor(R.color.white2));
        } else if (this.focusOn == 3) {
            this.btn_add_record_digit_hour1.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute1.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_hour2.setBackgroundResource(R.color.bg_blue1);
            this.btn_add_record_digit_minute2.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_hour2.setTextColor(getResources().getColor(R.color.white2));
        } else {
            this.btn_add_record_digit_hour1.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute1.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_hour2.setBackgroundResource(R.color.gray);
            this.btn_add_record_digit_minute2.setBackgroundResource(R.color.bg_blue1);
            this.btn_add_record_digit_minute2.setTextColor(getResources().getColor(R.color.white2));
        }
    }

    private void clickSaveData() {
        this.addStartTime = this.date1 + " " + String.format("%02d", new Object[]{Integer.valueOf(this.hour1)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute1)}) + ":00";
        this.addEndTime = this.date2 + " " + String.format("%02d", new Object[]{Integer.valueOf(this.hour2)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.minute2)}) + ":00";
        if (this.itemsId == 0 && this.checkActId.equals("")) {
            ToastUtils.toastShort(this.context, getString(R.string.str_please_choose_type));
            return;
        }
        long addStartTimeInt = DateTime.pars2Calender(this.addStartTime).getTime().getTime();
        long addEndTimeInt = DateTime.pars2Calender(this.addEndTime).getTime().getTime();
        long nowInt = Calendar.getInstance().getTime().getTime();
        if (addEndTimeInt < addStartTimeInt) {
            ToastUtils.toastShort(this.context, getString(R.string.str_starttime_should_less_than_endtime));
        } else if ((addEndTimeInt - addStartTimeInt) / 1000 <= 60) {
            ToastUtils.toastShort(this.context, getString(R.string.str_add_time_too_short));
        } else if (addEndTimeInt > nowInt) {
            ToastUtils.toastShort(this.context, getString(R.string.str_starttime_should_less_than_now));
        } else {
            String sql;
            if (this.itemsId == 0) {
                sql = Sql.actItemIsContainOthers(this.context, this.addStartTime, this.addEndTime);
            } else {
                sql = Sql.actItemIsContainOthers(this.context, this.addStartTime, this.addEndTime, " and id is not " + this.itemsId);
            }
            Cursor cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                new Builder(this.context).setTitle(getResources().getString(R.string.str_is_add)).setMessage(getResources().getString(R.string.str_add_time_contain_other_items_1).replace("{几个}", "" + cursor.getCount())).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton(getResources().getString(R.string.str_add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AddRecordDigitActivity.this.itemsId == 0) {
                            AddRecordDigitActivity.this.addTimeSave();
                        } else {
                            AddRecordDigitActivity.this.saveChange();
                        }
                        dialog.cancel();
                    }
                }).create().show();
            } else if (this.itemsId == 0) {
                addTimeSave();
            } else {
                saveChange();
            }
            DbUtils.close(cursor);
        }
    }

    private void addTimeSave() {
        TreeSet<Integer> goalIdSet1 = DbUtils.updateDbActItem_ChangeEndTime(this.context, this.addStartTime, "");
        TreeSet<Integer> goalIdSet2 = DbUtils.updateDbActItem_ChangeStartTime(this.context, this.addEndTime, "");
        TreeSet<Integer> goalIdSet3 = DbUtils.deleteActItem_deleteRecords(this.context, this.addStartTime, this.addEndTime, "");
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(this.context));
        values.put("actId", this.checkActId);
        values.put("actType", DbUtils.queryActTypeById(this.context, this.checkActId));
        values.put("startTime", this.addStartTime);
        values.put("take", Integer.valueOf(DateTime.cal_secBetween(this.addStartTime, this.addEndTime)));
        values.put("stopTime", this.addEndTime);
        values.put("isEnd", Integer.valueOf(1));
        values.put("isRecord", Integer.valueOf(0));
        String remarks = this.et_remind_add_remark.getText().toString().trim();
        if (remarks != null && remarks.length() > 0) {
            values.put("remarks", remarks);
        }
        long itemsId = DbUtils.getDb(this.context).insert("t_act_item", null, values);
        ToastUtils.toastShort(this.context, "添加成功！");
        DbUtils.addLabelLink(this.context, this.labelId, (int) itemsId);
        TreeSet<String> dateArr = new TreeSet();
        dateArr.add(this.date1);
        dateArr.add(this.date2);
        goalIdSet3.add(Integer.valueOf(Integer.parseInt(this.checkActId)));
        Iterator it = goalIdSet1.iterator();
        while (it.hasNext()) {
            goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
        }
        it = goalIdSet2.iterator();
        while (it.hasNext()) {
            goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
        }
        new Thread(new AllocationAndStaticsRunnable(this.context, goalIdSet3, dateArr)).start();
        this.isShowResetAnimation = true;
        String[] timeArr = DbUtils.queryLastRecordStopTime(this.context, this.date2);
        initData(timeArr[0], timeArr[1]);
        setResult(28);
        this.et_remind_add_remark.setText("");
        this.labelId = 0;
        this.uiComponent.tv_remind_tomato_label.setText("");
        this.et_remind_add_remark.setCursorVisible(false);
    }

    private void saveChange() {
        String temp_id = this.itemsId + "";
        TreeSet<Integer> goalIdSet1 = DbUtils.updateDbActItem_ChangeEndTime(this.context, this.addStartTime, " and id is not " + temp_id);
        TreeSet<Integer> goalIdSet2 = DbUtils.updateDbActItem_ChangeStartTime(this.context, this.addEndTime, " and id is not " + temp_id);
        TreeSet<Integer> goalIdSet3 = DbUtils.deleteActItem_deleteRecords(this.context, this.addStartTime, this.addEndTime, " and id is not " + temp_id);
        int take = DateTime.cal_secBetween(this.addStartTime, this.addEndTime);
        ContentValues values = new ContentValues();
        values.put("startTime", this.addStartTime);
        values.put("take", Integer.valueOf(take));
        values.put("stopTime", this.addEndTime);
        if (DbUtils.queryIsUploadByActItemId(this.context, temp_id) > 0) {
            values.put("endUpdateTime", DateTime.getTimeString());
        }
        DbUtils.getDb(this.context).update("t_act_item", values, "id is ?", new String[]{temp_id});
        ToastUtils.toastShort(this.context, "修改成功！");
        values = new ContentValues();
        values.put("take", Integer.valueOf(take));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_routine_link", values, " itemsId is ? ", new String[]{temp_id});
        try {
            TreeSet<String> changeDateArr = new TreeSet();
            changeDateArr.add(this.date1);
            changeDateArr.add(this.date2);
            Iterator it = goalIdSet1.iterator();
            while (it.hasNext()) {
                goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
            }
            it = goalIdSet2.iterator();
            while (it.hasNext()) {
                goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
            }
            goalIdSet3.add(Integer.valueOf(Integer.parseInt(temp_id)));
            new Thread(new AllocationAndStaticsRunnable(this.context, goalIdSet3, changeDateArr)).start();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        if (goalIdSet3 == null || goalIdSet3.size() <= 0) {
            setResult(-1);
        } else {
            Intent it2 = new Intent();
            it2.putExtra("isDeleteItemsId", 1);
            setResult(-1, it2);
        }
        onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (25 == requestCode && resultCode == -1) {
            String labelId = data.getStringExtra("labelIdStr");
            this.labelId = Integer.parseInt(labelId);
            this.uiComponent.tv_remind_tomato_label.setText(DbUtils.queryLabelNameByLabelId(this.context, labelId));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_up, R.anim.push_to_down);
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
