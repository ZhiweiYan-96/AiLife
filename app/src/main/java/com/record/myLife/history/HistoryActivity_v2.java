package com.record.myLife.history;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.Act2;
import com.record.bean.TimePieRecord;
import com.record.bean.User;
import com.record.bean.net.RecordBean;
import com.record.bean.net.ResponseBean;
import com.record.conts.Sofeware;
import com.record.myLife.BaseApplication;
import com.record.myLife.IActivity;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.add.AddRecordWheelActivity;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.settings.label.LabelSelectActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.MyCalendarActivity;
import com.record.myLife.view.MyView;
import com.record.myLife.view.TimePie;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.task.BaseTask;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.thread.UploadRunnable;
import com.record.thread.UploadThread;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.NetUtils;
import com.record.utils.PreferUtils;
import com.record.utils.ShowGuideImgUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class HistoryActivity_v2 extends BaseActivity implements IActivity, OnClickListener {
    static String TAG = "override";
    int ACTIVITY_FLAG = ((int) System.currentTimeMillis());
    Button btn_history_back;
    Context context;
    String currentDate;
    int currentDateRecordCount = 0;
    int currentPosition = 0;
    ArrayList<String> currentWeekDateList = null;
    LayoutInflater inflater;
    EditText isAddLabelEditText = null;
    ItemsFactory itemsFactory;
    ImageView iv_history_calendar;
    ImageView iv_history_show_empty;
    ImageView iv_history_sync;
    ImageView iv_history_v2_add;
    View iv_history_v2_add_title;
    View ll_add;
    View ll_history_calendar;
    View ll_history_show_empty;
    View ll_history_sync;
    View ll_history_v2_add;
    long moonCal = 0;
    OnPageChangeListener myChangeListener = new OnPageChangeListener() {
        public void onPageSelected(int position) {
            HistoryActivity_v2.this.currentPosition = position;
            int diff = position - 1073741823;
            Calendar cal = DateTime.pars2Calender2(HistoryActivity_v2.this.selectDate);
            cal.add(Calendar.DAY_OF_MONTH, diff);
            String date = DateTime.formatDate(cal);
            HistoryActivity_v2.this.currentDate = date;
            if (HistoryActivity_v2.this.currentWeekDateList != null) {
                int index = HistoryActivity_v2.this.currentWeekDateList.indexOf(date);
                HistoryActivity_v2.log("滑屏监听，当前列表currentWeekDateList:" + HistoryActivity_v2.this.currentWeekDateList.toString() + "index:" + index);
                if (index > 0) {
                    try {
                        HistoryActivity_v2.this.updateUiSelectWeekDate(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                        HistoryActivity_v2.log("滑动到日期：" + date + ",position：" + position + ",diff:" + diff);
                        HistoryActivity_v2.this.setDates(date);
                    }
                } else {
                    HistoryActivity_v2.log("滑动到日期：" + date + ",position：" + position + ",diff:" + diff);
                    HistoryActivity_v2.this.setDates(date);
                }
            } else {
                HistoryActivity_v2.log("滑动到日期：" + date + ",position：" + position + ",diff:" + diff);
                HistoryActivity_v2.this.setDates(date);
            }
            MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_calendar_switching_page");
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    };
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.ll_history_calendar) {
                MyCalendarActivity.startActivity((Activity) HistoryActivity_v2.this.context, HistoryActivity_v2.this.currentDate);
                HistoryActivity_v2.this.rl_history_v2_title_menu_outter.setVisibility(GONE);
                MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_calendar_btn");
            } else if (id == R.id.btn_history_back) {
                HistoryActivity_v2.this.finish();
            } else if (id == R.id.ll_history_v2_add) {
                HistoryActivity_v2.this.goAddRecordActivity(null, null);
                HistoryActivity_v2.this.rl_history_v2_title_menu_outter.setVisibility(GONE);
                MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_add_activate_record_btn");
            } else if (id == R.id.ll_history_sync) {
                HistoryActivity_v2.this.isUpload();
                HistoryActivity_v2.this.rl_history_v2_title_menu_outter.setVisibility(GONE);
                MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_refresh_btn");
            } else if (id == R.id.iv_history_v2_add_title || id == R.id.rl_history_v2_title_menu_outter) {
                HistoryActivity_v2.this.showOrHideMenu();
            } else if (id == R.id.ll_history_show_empty) {
                HistoryActivity_v2.this.configShowOrHideEmptyRecord();
                HistoryActivity_v2.this.rl_history_v2_title_menu_outter.setVisibility(GONE);
            }
        }
    };
    OnClickListener myClickListener2 = new OnClickListener() {
        public void onClick(View v) {
            String date = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            try {
                if (HistoryActivity_v2.this.currentDate.equals(date)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int diff = DateTime.cal_daysBetween(HistoryActivity_v2.this.currentDate, date);
            if (diff == 1 || diff == -1) {
                HistoryActivity_v2.this.vp_my_calendar_pager.setCurrentItem(HistoryActivity_v2.this.currentPosition + diff);
                return;
            }
            HistoryActivity_v2.this.initPager(date);
            MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_calendar_switching_btn");
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action = msg.arg1;
            if (action == 100) {
                GeneralUtils.toastShort(HistoryActivity_v2.this.context, msg.obj.toString());
            } else if (action == 101) {
                HistoryActivity_v2.this.requestRecord(HistoryActivity_v2.this.currentDate + " 00:00:00");
            } else if (action == 102) {
                GeneralUtils.toastShort(HistoryActivity_v2.this.context, msg.obj.toString());
                HistoryActivity_v2.this.stopUploadAnimation();
                HistoryActivity_v2.this.rl_history_v2_title_menu_outter.setVisibility(GONE);
            } else {
                if (action == 103) {
                }
            }
        }
    };
    OnClickListener myItemsClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_tem_history_record_add_label) {
                HistoryActivity_v2.this.clickAddLabel(v);
                MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_Timeline_tag_activate_btn");
            } else if (id == R.id.iv_tem_history_record_modify) {
                HistoryActivity_v2.log("点击修改");
                HistoryActivity_v2.this.clickModify(v);
                MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_Timeline_Time_select_activate_btn");
            } else if (id == R.id.rl_tem_history_record_item_outter) {
                HistoryActivity_v2.this.clickAddRecordItems(v);
            } else if (id == R.id.rl_tem_history_record_color_label || id == R.id.tp_tem_history_record_name || id == R.id.tp_tem_history_record_take) {
                HistoryActivity_v2.this.clickLabelIntoItemDetail(v);
                MobclickAgent.onEvent(HistoryActivity_v2.this.getApplicationContext(), "Timeline_click_Timeline_Time_select_activate_item");
            }
        }
    };
    HashMap<Integer, View> pagerMap = new HashMap();
    RelativeLayout rl_history_v2_date;
    RelativeLayout rl_history_v2_title_items;
    View rl_history_v2_title_menu_outter;
    RelativeLayout rl_my_calendar_pager;
    String selectDate;
    String theDayBeforeYesterday = DateTime.beforeNDays2Str(-2);
    String today = DateTime.getDateString();
    long twentyFourCal = 0;
    ViewPager vp_my_calendar_pager;
    String yesterday = DateTime.beforeNDays2Str(-1);
    long zeroCal = 0;

    class ItemsFactory {
        Context context;
        LayoutInflater inflater;

        public ItemsFactory(Context context) {
            this.context = context;
            this.inflater = HistoryActivity_v2.this.getLayoutInflater();
        }

        public RelativeLayout createEmptyItems(String note) {
            RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.temp_hitory_item_empty, null);
            ((TextView) rl.findViewById(R.id.tp_tem_history_record_name)).setText(note);
            return rl;
        }

        public RelativeLayout createItems(int id, int goadId, String startTime, String endTime, String remark, int isRecord, HashMap<String, String> other_data) {
            RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.temp_hitory_item, null);
            TextView tp_tem_history_record_name = (TextView) rl.findViewById(R.id.tp_tem_history_record_name);
            TimePie tp_tem_history_record_time_pie = (TimePie) rl.findViewById(R.id.tp_tem_history_record_time_pie);
            ImageView iv_tem_history_record_label = (ImageView) rl.findViewById(R.id.iv_tem_history_record_label);
            ImageView iv_tem_history_record_color = (ImageView) rl.findViewById(R.id.iv_tem_history_record_color);
            ImageView iv_tem_history_record_modify = (ImageView) rl.findViewById(R.id.iv_tem_history_record_modify);
            TextView iv_tem_history_record_add_label = (TextView) rl.findViewById(R.id.iv_tem_history_record_add_label);
            EditText et_tem_history_record_mark = (EditText) rl.findViewById(R.id.tp_tem_history_record_mark);
            TextView tv_tem_history_record_time_start = (TextView) rl.findViewById(R.id.tv_tem_history_record_time_start);
            TextView tv_tem_history_record_time_end = (TextView) rl.findViewById(R.id.tv_tem_history_record_time_end);
            TextView tp_tem_history_record_take = (TextView) rl.findViewById(R.id.tp_tem_history_record_take);
            RelativeLayout rl_tem_history_record_item_outter = (RelativeLayout) rl.findViewById(R.id.rl_tem_history_record_item_outter);
            RelativeLayout rl_tem_history_record_color_label = (RelativeLayout) rl.findViewById(R.id.rl_tem_history_record_color_label);
            ((TextView) rl.findViewById(R.id.tv_tem_history_record_id)).setText(id + "");
            tv_tem_history_record_time_start.setText(DateTime.getTimeStr1229(startTime));
            tv_tem_history_record_time_end.setText(DateTime.getTimeStr1229(endTime));
            if (goadId > 0) {
                if (startTime != null) {
                    if (!startTime.contains(HistoryActivity_v2.this.today)) {
                        if (!startTime.contains(HistoryActivity_v2.this.yesterday)) {
                            if (!startTime.contains(HistoryActivity_v2.this.theDayBeforeYesterday)) {
                                iv_tem_history_record_modify.setVisibility(INVISIBLE);
                            }
                        }
                    }
                    iv_tem_history_record_modify.setOnClickListener(HistoryActivity_v2.this.myItemsClickListener);
                }
                rl_tem_history_record_color_label.setOnClickListener(HistoryActivity_v2.this.myItemsClickListener);
                tp_tem_history_record_name.setOnClickListener(HistoryActivity_v2.this.myItemsClickListener);
                tp_tem_history_record_take.setOnClickListener(HistoryActivity_v2.this.myItemsClickListener);
                Act2 act = DbUtils.getAct2ByActId(this.context, goadId + "");
                tp_tem_history_record_time_pie.setRecord(new TimePieRecord(startTime, endTime, HistoryActivity_v2.this.setTextColor(startTime, endTime, tv_tem_history_record_time_start, tv_tem_history_record_time_end)));
                iv_tem_history_record_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(act.getColor())).intValue());
                try {
                    iv_tem_history_record_label.setImageResource(((Integer) Val.label2IntMap2.get(act.getImage())).intValue());
                } catch (Exception e1) {
                    iv_tem_history_record_label.setImageResource(R.drawable.ic_label_desklamp);
                    DbUtils.exceptionHandler(e1);
                }
                String take = "";
                try {
                    take = DateTime.calculateTime5(this.context, (long) DateTime.cal_secBetween(startTime, endTime));
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                tp_tem_history_record_take.setText(take);
                tp_tem_history_record_name.setText(act.getActName());
                if (remark == null) {
                    remark = "";
                }
                et_tem_history_record_mark.setText(remark);
                if (isRecord == 3) {
                    Drawable drawable = HistoryActivity_v2.this.getResources().getDrawable(R.drawable.ic_tomato_26);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth() - 7, drawable.getMinimumHeight() - 7);
                    et_tem_history_record_mark.setCompoundDrawables(drawable, null, null, null);
                }
                String totalVal = "";
                if (other_data != null) {
                    try {
                        if (other_data.containsKey("totalVal")) {
                            totalVal = (String) other_data.get("totalVal");
                        }
                    } catch (Exception e2) {
                        DbUtils.exceptionHandler(e2);
                    }
                }
                if (totalVal.length() > 0) {
                    if (Double.parseDouble(totalVal) > 3.0d) {
                        iv_tem_history_record_add_label.setTextColor(HistoryActivity_v2.this.getResources().getColor(R.color.bg_green2));
                    }
                    iv_tem_history_record_add_label.setText(totalVal + "");
                } else {
                    iv_tem_history_record_add_label.setText("");
                }
                et_tem_history_record_mark.addTextChangedListener(new MyTextWatcher(et_tem_history_record_mark));
            } else {
                iv_tem_history_record_modify.setVisibility(GONE);
                iv_tem_history_record_add_label.setVisibility(GONE);
                et_tem_history_record_mark.setVisibility(GONE);
                tp_tem_history_record_name.setVisibility(GONE);
                tp_tem_history_record_take.setVisibility(GONE);
                int access$1000 = HistoryActivity_v2.this.setTextColor(startTime, endTime, tv_tem_history_record_time_start, tv_tem_history_record_time_end);
                tp_tem_history_record_time_pie.setRecord(new TimePieRecord(startTime, endTime, HistoryActivity_v2.this.getResources().getColor(R.color.gray)));
                rl_tem_history_record_item_outter.setOnClickListener(HistoryActivity_v2.this.myItemsClickListener);
            }
            return rl;
        }
    }

    class MyTextWatcher implements TextWatcher {
        EditText editText;
        String str = "";

        public MyTextWatcher(EditText editText) {
            this.editText = editText;
            this.str = editText.getText().toString();
        }

        public void afterTextChanged(Editable s) {
            String temp = s.toString().trim();
            if (temp != this.str) {
                String itemsId = ((TextView) ((RelativeLayout) this.editText.getParent().getParent()).findViewById(R.id.tv_tem_history_record_id)).getText().toString();
                ContentValues values = new ContentValues();
                values.put("remarks", temp);
                values.put("endUpdateTime", DateTime.getTimeString());
                DbUtils.getDb(HistoryActivity_v2.this.context).update("t_act_item", values, "id = " + itemsId, null);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    class MyPager extends PagerAdapter {
        MyPager() {
        }

        public int getCount() {
            return 1;
//            return ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) HistoryActivity_v2.this.pagerMap.get(Integer.valueOf(position)));
        }

        /* JADX WARNING: Missing block: B:8:0x0064, code:
            if (r3 != null) goto L_0x0066;
     */
        public java.lang.Object instantiateItem(android.view.ViewGroup r7, int r8) {
            /*
            r6 = this;
            r2 = 0;
            r4 = 536870911; // 0x1fffffff float:1.0842021E-19 double:2.652494734E-315;
            if (r8 <= r4) goto L_0x006a;
        L_0x0006:
            r4 = 1073741823; // 0x3fffffff float:1.9999999 double:5.304989472E-315;
            r2 = r8 - r4;
        L_0x000b:
            r4 = com.record.myLife.history.HistoryActivity_v2.this;
            r4 = r4.selectDate;
            r0 = com.record.utils.DateTime.pars2Calender2(r4);
            r4 = 5;
            r0.add(r4, r2);
            r1 = com.record.utils.DateTime.formatDate(r0);
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "初始化日期：";
            r4 = r4.append(r5);
            r4 = r4.append(r1);
            r5 = ",position：";
            r4 = r4.append(r5);
            r4 = r4.append(r8);
            r5 = ",diff:";
            r4 = r4.append(r5);
            r4 = r4.append(r2);
            r4 = r4.toString();
            com.record.myLife.history.HistoryActivity_v2.log(r4);
            r3 = 0;
            r4 = com.record.myLife.history.HistoryActivity_v2.this;
            r4 = r4.pagerMap;
            if (r4 == 0) goto L_0x006c;
        L_0x004c:
            r4 = com.record.myLife.history.HistoryActivity_v2.this;
            r4 = r4.pagerMap;
            r4 = r4.size();
            if (r4 <= 0) goto L_0x006c;
        L_0x0056:
            r4 = com.record.myLife.history.HistoryActivity_v2.this;
            r4 = r4.pagerMap;
            r5 = java.lang.Integer.valueOf(r8);
            r3 = r4.get(r5);
            r3 = (android.view.View) r3;
            if (r3 == 0) goto L_0x006c;
        L_0x0066:
            r7.addView(r3);
            return r3;
        L_0x006a:
            r2 = r8;
            goto L_0x000b;
        L_0x006c:
            r4 = com.record.myLife.history.HistoryActivity_v2.this;
            r3 = r4.getListViewByDate(r1);
            r4 = com.record.myLife.history.HistoryActivity_v2.this;
            r4 = r4.pagerMap;
            r5 = java.lang.Integer.valueOf(r8);
            r4.put(r5, r3);
            goto L_0x0066;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.record.myLife.history.HistoryActivity_v2.MyPager.instantiateItem(android.view.ViewGroup, int):java.lang.Object");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_v2);
        SystemBarTintManager.setMIUIbar(this);
        init();
        initView();
    }

    public void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
        this.inflater = getLayoutInflater();
        this.today = DateTime.getDateString();
        TimerService.updateCounter2Db(this.context);
        try {
            getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).edit().putInt(Val.CONFIGURE_IS_SHOW_MAIN_HISTORY_DOT, 1).commit();
            ShowGuideImgUtils.showImage_v2(this.context, Val.CONFIGURE_IS_SHOW_GUIDE_HISTORY, 2, R.drawable.guide_item_history, 2);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void initView() {
        this.iv_history_v2_add = (ImageView) findViewById(R.id.iv_history_v2_add);
        this.iv_history_calendar = (ImageView) findViewById(R.id.iv_history_calendar);
        this.iv_history_sync = (ImageView) findViewById(R.id.iv_history_sync);
        this.btn_history_back = (Button) findViewById(R.id.btn_history_back);
        this.rl_history_v2_date = (RelativeLayout) findViewById(R.id.rl_history_v2_date);
        this.rl_history_v2_title_items = (RelativeLayout) findViewById(R.id.rl_history_v2_title_items);
        this.rl_my_calendar_pager = (RelativeLayout) findViewById(R.id.rl_my_calendar_pager);
        this.iv_history_v2_add_title = findViewById(R.id.iv_history_v2_add_title);
        this.rl_history_v2_title_menu_outter = findViewById(R.id.rl_history_v2_title_menu_outter);
        this.ll_history_v2_add = findViewById(R.id.ll_history_v2_add);
        this.ll_history_sync = findViewById(R.id.ll_history_sync);
        this.ll_history_calendar = findViewById(R.id.ll_history_calendar);
        this.ll_history_show_empty = findViewById(R.id.ll_history_show_empty);
        this.iv_history_show_empty = (ImageView) findViewById(R.id.iv_history_show_empty);
        this.itemsFactory = new ItemsFactory(this.context);
        this.btn_history_back.setOnClickListener(this.myClickListener);
        this.iv_history_v2_add_title.setOnClickListener(this.myClickListener);
        this.rl_history_v2_title_menu_outter.setOnClickListener(this.myClickListener);
        this.ll_history_v2_add.setOnClickListener(this.myClickListener);
        this.ll_history_sync.setOnClickListener(this.myClickListener);
        this.ll_history_calendar.setOnClickListener(this.myClickListener);
        this.ll_history_show_empty.setOnClickListener(this.myClickListener);
        updateUiWeekTitle();
        setDates(this.today);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                HistoryActivity_v2.this.initPager(HistoryActivity_v2.this.today);
            }
        }, 150);
    }

    private void initPager(String selectDate) {
        this.rl_my_calendar_pager.removeAllViews();
        this.pagerMap.clear();
        setDates(selectDate);
        this.currentDate = selectDate;
        this.selectDate = selectDate;
        this.vp_my_calendar_pager = (ViewPager) this.inflater.inflate(R.layout.tem_view_pager, null);
        this.vp_my_calendar_pager.setLayoutParams(new LayoutParams(-1, -1));
        this.vp_my_calendar_pager.setAdapter(new MyPager());
        this.currentPosition = 1073741823;
        this.vp_my_calendar_pager.setCurrentItem(this.currentPosition);
        this.vp_my_calendar_pager.setOnPageChangeListener(this.myChangeListener);
        this.rl_my_calendar_pager.addView(this.vp_my_calendar_pager);
    }

    private void updateUiSelectWeekDate(int index) throws Exception {
        LinearLayout ll = (LinearLayout) this.rl_history_v2_date.getChildAt(0);
        int count = ll.getChildCount();
        for (int i = 0; i < count; i++) {
            RelativeLayout rl = (RelativeLayout) ll.getChildAt(i);
            if (this.today.equals(((TextView) rl.getChildAt(0)).getText().toString())) {
                rl.setBackgroundResource(R.drawable.x_gray_bg_red_frame_middle);
            } else {
                rl.setBackgroundResource(R.drawable.x_gray_bg_gray_frame_middle);
            }
        }
        ll.getChildAt(index).setBackgroundResource(R.drawable.x_white_bg_red_frame_middle);
    }

    private void configShowOrHideEmptyRecord() {
        if (1 == PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE, 1)) {
            PreferUtils.putInt(this.context, Val.CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE, 0);
        } else {
            PreferUtils.putInt(this.context, Val.CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE, 1);
        }
        initPager(this.currentDate);
    }

    private void initUiIsShowEmptyRecord() {
        if (1 == PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE, 1)) {
            this.iv_history_show_empty.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_history_show_empty.setImageResource(R.drawable.ic_off_v2);
        }
    }

    private void showOrHideMenu() {
        if (GONE == this.rl_history_v2_title_menu_outter.getVisibility()) {
            initUiIsShowEmptyRecord();
            this.rl_history_v2_title_menu_outter.setVisibility(VISIBLE);
            return;
        }
        this.rl_history_v2_title_menu_outter.setVisibility(GONE);
    }

    private void isUpload() {
        if (User.getInstance().getUserId() == DbUtils.queryTryUserId(this.context)) {
            GeneralUtils.toastShort(this.context, "登陆后才能同步数据哦！");
        } else if (NetUtils.isNetworkAvailable(this.context)) {
            double count = UploadRunnable.getUploadCount(this.context);
            log("上传个数：" + count);
            if (count > 0.0d) {
                Thread thread = UploadThread.getInstance(this.context, this.myHandler);
                if (thread.isAlive()) {
                    GeneralUtils.toastShort(this.context, getString(R.string.str_syncing2));
                } else {
                    thread.start();
                    GeneralUtils.toastShort(this.context, getString(R.string.str_syncing));
                }
                startUploadAnimation();
                return;
            }
            GeneralUtils.toastShort(this.context, getString(R.string.str_syncing));
            requestRecord(this.currentDate + " 00:00:00");
        }
    }

    private void startUploadAnimation() {
        new AnimationController().rotateView(this.iv_history_sync, 1000, 0);
    }

    private void stopUploadAnimation() {
        this.iv_history_sync.clearAnimation();
    }

    private void goAddRecordActivity(String startTime, String stopTime) {
        if (this.currentDate == null || this.currentDate.length() == 0) {
            this.currentDate = DateTime.getDateString();
        }
        if (startTime == null || startTime.length() == 0 || stopTime == null || stopTime.length() == 0) {
            String[] timeArr = DbUtils.queryLastRecordStopTime(this.context, this.currentDate);
            startTime = timeArr[0];
            stopTime = timeArr[1];
        }
        Intent it;
        if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_ADD_RECORD_TYPE, 1) == 1) {
            it = new Intent(this.context, AddRecordDigitActivity.class);
            it.putExtra("startTime", startTime);
            it.putExtra("stopTime", stopTime);
            startActivityForResult(it, 27);
            return;
        }
        it = new Intent(this.context, AddRecordWheelActivity.class);
        it.putExtra("startTime", startTime);
        it.putExtra("stopTime", stopTime);
        startActivityForResult(it, 27);
    }

    private void showDigitAddActivity(String startTime, String stopTime) {
        Intent it = new Intent(this.context, AddRecordDigitActivity.class);
        it.putExtra("startTime", startTime);
        it.putExtra("stopTime", stopTime);
        startActivityForResult(it, 27);
    }

    private void showWheelAddActivity(String startTime, String stopTime) {
        Intent it = new Intent(this.context, AddRecordWheelActivity.class);
        it.putExtra("startTime", startTime);
        it.putExtra("stopTime", stopTime);
        startActivityForResult(it, 27);
    }

    private int calculateCurrentDateRecord() {
        String zeroTime = this.currentDate + " 00:00:00";
        String twentyFourTime = this.currentDate + " 24:00:00";
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select id from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and startTime < '" + zeroTime + "' and stopTime > '" + zeroTime + "' and stopTime <= '" + twentyFourTime + "' order by startTime", null);
        this.currentDateRecordCount = cursor.getCount();
        DbUtils.close(cursor);
        cursor = DbUtils.getDb(this.context).rawQuery("Select * from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and startTime >= '" + zeroTime + "' and startTime <= '" + twentyFourTime + "'  order by startTime", null);
        this.currentDateRecordCount += cursor.getCount();
        DbUtils.close(cursor);
        return this.currentDateRecordCount;
    }

    private View getListViewByDate(String date) {
        View sv = this.inflater.inflate(R.layout.temp_hitory_pager, null);
        LinearLayout ll_history_v2_items = (LinearLayout) sv.findViewById(R.id.ll_history_v2_items);
        View v_vertical_line = sv.findViewById(R.id.v_vertical_line);
        ll_history_v2_items.removeAllViews();
        this.zeroCal = DateTime.pars2Calender(date + " 00:00:00").getTime().getTime();
        this.moonCal = DateTime.pars2Calender(date + " 12:00:00").getTime().getTime();
        this.twentyFourCal = DateTime.pars2Calender(date + " 23:59:59").getTime().getTime();
        String zeroTime = date + " 00:00:00";
        String twentyFourTime = date + " 24:00:00";
        ll_history_v2_items = addItems(addItems(ll_history_v2_items, "Select a.*,d.totalVal from t_act_item as a LEFT JOIN t_deliberate_record as d ON a.id = d.itemsId where a.userId is " + DbUtils.queryUserId(this.context) + " and a.isDelete is not 1 and a.startTime < '" + zeroTime + "' and a.stopTime > '" + zeroTime + "' and a.stopTime <= '" + twentyFourTime + "' order by a.startTime", 0), "Select a.*,d.totalVal from t_act_item as a LEFT JOIN t_deliberate_record as d ON a.id = d.itemsId where a.userId is " + DbUtils.queryUserId(this.context) + " and a.isDelete is not 1 and a.startTime >= '" + zeroTime + "' and a.startTime <= '" + twentyFourTime + "'  order by a.startTime", 1);
        if (ll_history_v2_items.getChildCount() == 0) {
            ll_history_v2_items.addView(this.itemsFactory.createEmptyItems("暂无记录哦！"));
            v_vertical_line.setVisibility(INVISIBLE);
        }
        return sv;
    }

    private void updateUiWeekTitle() {
        new MyView((Activity) this.context).addWeekTitle(this.rl_history_v2_title_items);
    }

    private void setDates(String date) {
        this.rl_history_v2_date.removeAllViews();
        this.currentWeekDateList = new ArrayList();
        int startDayOfWeek = PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2);
        Calendar calendar = DateTime.pars2Calender(date + " 00:00:00");
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (startDayOfWeek == 1) {
            if (dayOfWeek > startDayOfWeek) {
                calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - startDayOfWeek));
            }
        } else if (dayOfWeek == 1) {
            calendar.add(Calendar.DAY_OF_MONTH, -6);
        } else if (dayOfWeek > startDayOfWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - startDayOfWeek));
        }
        MyView myView = new MyView((Activity) this.context);
        LinearLayout ll = (LinearLayout) this.inflater.inflate(R.layout.temp_my_calendar_items, null);
        for (int i = 0; i < 7; i++) {
            this.currentWeekDateList.add(DateTime.formatDate(calendar));
            ll = myView.setCalendarItems(ll, calendar, startDayOfWeek, calendar.get(Calendar.MONTH), this.myClickListener2, date, true);
            calendar.add(5, 1);
        }
        this.rl_history_v2_date.addView(ll);
    }

    private LinearLayout addItems(LinearLayout ll_history_v2_items, String sql, int findIntervalOnFirst) {
        int is_show = PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE, 1);
        Cursor cursor2 = DbUtils.getDb2(this.context).rawQuery(sql, null);
        if (cursor2.getCount() > 0) {
            String lastEndTime = null;
            int lastId = 0;
            while (cursor2.moveToNext()) {
                int id = cursor2.getInt(cursor2.getColumnIndex("id"));
                int goalId = cursor2.getInt(cursor2.getColumnIndex("actId"));
                int isRecord = cursor2.getInt(cursor2.getColumnIndex("isRecord"));
                String startTime = cursor2.getString(cursor2.getColumnIndex("startTime"));
                String stopTime = cursor2.getString(cursor2.getColumnIndex("stopTime"));
                String remark = cursor2.getString(cursor2.getColumnIndex("remarks"));
                double totalVal = cursor2.getDouble(cursor2.getColumnIndex("totalVal"));
                if (findIntervalOnFirst > 0 && cursor2.isFirst() && 1 == is_show) {
                    Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select id,stopTime from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and stopTime <= '" + startTime + "'  order by stopTime desc limit 1", null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        String tempStopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                        int tempid = cursor.getInt(cursor.getColumnIndex("id"));
                        int range = DateTime.cal_secBetween(tempStopTime, startTime);
                        if (range > 180 && range < 43200) {
                            ll_history_v2_items.addView(this.itemsFactory.createItems(tempid, 0, tempStopTime, startTime, "", 0, null));
                        }
                    }
                    DbUtils.close(cursor);
                }
                if (lastId > 0 && 1 == is_show && ((startTime.contains(this.today) || startTime.contains(this.yesterday) || startTime.contains(this.theDayBeforeYesterday)) && DateTime.cal_secBetween(lastEndTime, startTime) > 180)) {
                    LinearLayout linearLayout = ll_history_v2_items;
                    linearLayout.addView(this.itemsFactory.createItems(lastId, 0, lastEndTime, startTime, "", 0, null));
                }
                HashMap<String, String> other_data = null;
                if (totalVal > 0.0d) {
                    other_data = new HashMap();
                    other_data.put("totalVal", "" + totalVal);
                }
                ll_history_v2_items.addView(this.itemsFactory.createItems(id, goalId, startTime, stopTime, remark, isRecord, other_data));
                lastEndTime = stopTime;
                lastId = id;
            }
        }
        DbUtils.close(cursor2);
        return ll_history_v2_items;
    }

    private int setTextColor(String startTime, String endtime, TextView tvUp, TextView tvDown) {
        long startCal = DateTime.pars2Calender(startTime).getTime().getTime();
        long endCal = DateTime.pars2Calender(endtime).getTime().getTime();
        int color = getResources().getColor(R.color.bg_blue1);
        if (startCal >= this.zeroCal && startCal <= this.moonCal) {
            tvUp.setTextColor(color);
        } else if (startCal > this.moonCal) {
            color = getResources().getColor(R.color.bg_yellow1);
            tvUp.setTextColor(getResources().getColor(R.color.bg_yellow1));
        }
        if (endCal >= this.zeroCal && endCal <= this.moonCal) {
            tvDown.setTextColor(getResources().getColor(R.color.bg_blue1));
        } else if (endCal > this.moonCal && endCal < this.twentyFourCal) {
            tvDown.setTextColor(getResources().getColor(R.color.bg_yellow1));
        }
        return color;
    }

    private void clickLabelIntoItemDetail(View v) {
        String temp_id = ((TextView) ((RelativeLayout) v.getParent().getParent()).getChildAt(0)).getText().toString();
        Intent it = new Intent(this.context, ItemDetailActivity.class);
        it.setAction(Val.INTENT_ACTION_ITEMS);
        it.putExtra("Id", temp_id);
        startActivityForResult(it, 2);
    }

    private void clickAddRecordItems(View v) {
        String lastId = ((TextView) ((RelativeLayout) v.getParent()).getChildAt(0)).getText().toString();
        if (lastId != null && lastId.length() > 0) {
            String stopTime = DbUtils.queryStopTimebyItemsId(this.context, lastId);
            if (stopTime != null) {
                try {
                    Cursor cursor = DbUtils.getDb(this.context).rawQuery("select startTime from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and startTime > '" + stopTime + "' order by startTime limit 1", null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                        if (startTime != null && startTime.length() > 0) {
                            goAddRecordActivity(stopTime, startTime);
                        }
                    }
                    DbUtils.close(cursor);
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
            }
        }
    }

    private void clickModify(final View v) {
        final String temp_id = ((TextView) ((RelativeLayout) v.getParent().getParent()).getChildAt(0)).getText().toString();
        final String[] arr = new String[]{getString(R.string.str_modify), getString(R.string.str_add_label), getString(R.string.str_delete)};
        new Builder(this.context).setTitle((int) R.string.str_choose).setItems(arr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String currentStr = arr[which];
                if (HistoryActivity_v2.this.getString(R.string.str_modify) == currentStr) {
                    Intent it = new Intent(HistoryActivity_v2.this.context, AddRecordDigitActivity.class);
                    it.putExtra("itemId", temp_id);
                    HistoryActivity_v2.this.startActivityForResult(it, 24);
                    dialog.dismiss();
                } else if (HistoryActivity_v2.this.getString(R.string.str_add_label) == currentStr) {
                    HistoryActivity_v2.this.clickAddLabel(v);
                } else if (HistoryActivity_v2.this.getString(R.string.str_delete) == currentStr) {
                    HistoryActivity_v2.this.deleteComfire(v, temp_id);
                    dialog.cancel();
                }
            }
        }).create().show();
    }

    private void deleteComfire(View v, final String temp_id) {
        new Builder(this.context).setTitle((CharSequence) "是否删除").setMessage((CharSequence) "删除后相关信息(标签记录等)也会被删除!").setNegativeButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton((CharSequence) "删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ContentValues values;
                int isUpload = 0;
                int temp_actId = 0;
                String startTime = "";
                String stopTime = "";
                Cursor cursor2 = DbUtils.getDb(HistoryActivity_v2.this.context).rawQuery("select * from t_act_item where " + DbUtils.getWhereUserId(HistoryActivity_v2.this.context) + " and id = " + temp_id, null);
                if (cursor2.getCount() > 0) {
                    cursor2.moveToNext();
                    isUpload = cursor2.getInt(cursor2.getColumnIndex("isUpload"));
                    temp_actId = cursor2.getInt(cursor2.getColumnIndex("actId"));
                    startTime = cursor2.getString(cursor2.getColumnIndex("startTime"));
                    stopTime = cursor2.getString(cursor2.getColumnIndex("stopTime"));
                }
                DbUtils.close(cursor2);
                HistoryActivity_v2.log("查询所删除是否上传,isUpload：" + isUpload);
                if (isUpload > 0) {
                    values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(1));
                    values.put("deleteTime", DateTime.getTimeString());
                    values.put("endUpdateTime", DateTime.getTimeString());
                    DbUtils.getDb(HistoryActivity_v2.this.context).update("t_act_item", values, "id is ?", new String[]{temp_id});
                } else {
                    DbUtils.getDb(HistoryActivity_v2.this.context).delete("t_act_item", "id is ?", new String[]{temp_id});
                    HistoryActivity_v2.log("删除记录");
                }
                GeneralHelper.toastShort(HistoryActivity_v2.this.context, "删除成功！");
                dialog.cancel();
                Cursor cursor = DbUtils.getDb(HistoryActivity_v2.this.context).rawQuery("select * from t_routine_link where itemsId is " + temp_id, null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int isUpload2 = cursor.getInt(cursor.getColumnIndex("isUpload"));
                        int Id = cursor.getInt(cursor.getColumnIndex("Id"));
                        if (isUpload2 > 0) {
                            values = new ContentValues();
                            values.put("isDelete", Integer.valueOf(1));
                            values.put("deleteTime", DateTime.getTimeString());
                            values.put("endUpdateTime", DateTime.getTimeString());
                            DbUtils.getDb(HistoryActivity_v2.this.context).update("t_routine_link", values, "Id is " + Id, null);
                        } else {
                            DbUtils.getDb(HistoryActivity_v2.this.context).delete("t_routine_link", "Id is " + Id, null);
                        }
                    }
                }
                DbUtils.close(cursor);
                try {
                    String tempStartDate = "";
                    if (startTime.length() > 0) {
                        tempStartDate = startTime.substring(0, startTime.indexOf(" "));
                    }
                    String tempEndDate = stopTime.substring(0, stopTime.indexOf(" "));
                    TreeSet<String> changeDateArr = new TreeSet();
                    changeDateArr.add(tempStartDate);
                    changeDateArr.add(tempEndDate);
                    TreeSet<Integer> goalIdSet = new TreeSet();
                    goalIdSet.add(Integer.valueOf(temp_actId));
                    new Thread(new AllocationAndStaticsRunnable(HistoryActivity_v2.this.context, goalIdSet, changeDateArr)).start();
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                HistoryActivity_v2.this.initPager(HistoryActivity_v2.this.currentDate);
                dialog.cancel();
            }
        }).create().show();
    }

    private void clickAddLabel(View v) {
        RelativeLayout rl = (RelativeLayout) v.getParent().getParent();
        String temp_id = ((TextView) rl.getChildAt(0)).getText().toString();
        this.isAddLabelEditText = (EditText) rl.findViewById(R.id.tp_tem_history_record_mark);
        log("点击添加标签");
        int actType = DbUtils.queryActTypeByItemsId(this.context, Integer.parseInt(temp_id));
        if (actType == 11) {
            actType = 10;
        }
        Intent it = new Intent(this.context, LabelSelectActivity.class);
        it.putExtra("itemsId", Integer.parseInt(temp_id));
        it.putExtra("actType", actType);
        startActivity(it);
    }

    private void isAddLabel() {
        if (this.isAddLabelEditText != null) {
            String dbRemarks = DbUtils.queryRemarkByItemsId(this.context, ((TextView) ((RelativeLayout) this.isAddLabelEditText.getParent().getParent()).getChildAt(0)).getText().toString() + "");
            if (dbRemarks != null && dbRemarks.length() > 0) {
                this.isAddLabelEditText.setText(dbRemarks);
            }
            this.isAddLabelEditText = null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 24) {
            if (-1 == resultCode) {
                int deleteId = data.getIntExtra("isDeleteItemsId", 0);
                initPager(this.currentDate);
            }
        } else if (requestCode == 26) {
            if (-1 == resultCode) {
                String date = data.getStringExtra("Date");
                this.selectDate = date;
                log("通过月历选择日期currentDate：" + date);
                initPager(this.selectDate);
            }
        } else if (requestCode == 27) {
            if (resultCode == 28) {
                initPager(this.currentDate);
            }
        } else if (requestCode == 2 && resultCode == 11) {
            initPager(this.currentDate);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        isAddLabel();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }

    protected void onStart() {
        super.onStart();
        BaseApplication.getInstance().getControllerManager().addIActivity(this);
    }

    protected void onStop() {
        super.onStop();
        BaseApplication.getInstance().getControllerManager().removeIActivity(this);
    }

    private void requestRecord(String time) {
        try {
            JSONObject object = new JSONObject();
            object.put(BaseTask.COMPLETED_ID_FIELD, 1);
            object.put(BaseTask.FAILED_ID_FIELD, 2);
            object.put(BaseTask.REQUEST_URL_FIELD, Sofeware.GET_DATE_RECORD);
            object.put(BaseTask.ACTIVITY_FLAG, this.ACTIVITY_FLAG);
            object.put("time", time);
            BaseApplication.getInstance().getControllerManager().startTask(object);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void refresh(Message msg) {
        if (msg.arg2 == this.ACTIVITY_FLAG) {
            switch (msg.what) {
                case 1:
                    parseRecordBean((ResponseBean) msg.obj);
                    stopUploadAnimation();
                    this.rl_history_v2_title_menu_outter.setVisibility(GONE);
                    return;
                case 2:
                    stopUploadAnimation();
                    ToastUtils.toastShort(this.context, msg.obj.toString());
                    this.rl_history_v2_title_menu_outter.setVisibility(GONE);
                    return;
                default:
                    return;
            }
        }
    }

    private void parseRecordBean(ResponseBean responseBean) {
        try {
            if (responseBean.status == 1) {
                ArrayList<RecordBean> arrayList = RecordBean.getRecordBeanArr(new JSONObject(responseBean.data).get("items").toString());
                if (arrayList.size() > 0) {
                    ArrayList<RecordBean> tempList = new ArrayList();
                    String uid = User.getInstance().getUid();
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        RecordBean record = (RecordBean) it.next();
                        if (uid != null && uid.equals(record.userId)) {
                            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and sGoalItemId = " + record.id, null);
                            if (cursor.getCount() == 0) {
                                Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select id from t_act where " + DbUtils.getWhereUserId(this.context) + " and severId = " + record.goalId, null);
                                if (cursor2.getCount() > 0) {
                                    DbUtils.getDb(this.context).insert("t_act_item", null, getValues(record));
                                } else {
                                    tempList.add(record);
                                }
                                DbUtils.close(cursor2);
                            } else {
                                cursor.moveToNext();
                                DbUtils.getDb(this.context).update("t_act_item", getValues(record), "id = " + cursor.getString(cursor.getColumnIndex("id")), null);
                            }
                            DbUtils.close(cursor);
                        }
                    }
                    if (tempList.size() > 0) {
                    }
                    GeneralUtils.toastShort(this.context, getString(R.string.str_syncing_finish));
                    initPager(this.currentDate);
                    return;
                }
                GeneralUtils.toastShort(this.context, getString(R.string.str_no_record2));
                return;
            }
            ToastUtils.toastShort(this.context, responseBean.data.toString());
        } catch (Exception e) {
            ToastUtils.toastShort(this.context, "出错啦！");
            DbUtils.exceptionHandler(e);
        }
    }

    private ContentValues getValues(RecordBean record) {
        ContentValues values = new ContentValues();
        values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
        values.put("actId", Integer.valueOf(DbUtils.queryActIdBysGoalId(this.context, record.goalId)));
        values.put("actType", record.goalType);
        values.put("startTime", record.startTime);
        if (inNotNull(record.take)) {
            values.put("take", record.take);
        }
        if (inNotNull(record.stopTime)) {
            values.put("stopTime", record.stopTime);
        }
        if (inNotNull(record.isEnd)) {
            values.put("isEnd", record.isEnd);
        }
        if (inNotNull(record.isRecord)) {
            values.put("isRecord", record.isRecord);
        }
        if (inNotNull(record.remarks)) {
            values.put("remarks", record.remarks);
        }
        values.put("isUpload", "1");
        values.put("sGoalItemId", record.id);
        if (inNotNull(record.isDelete)) {
            values.put("isDelete", record.isDelete);
        }
        if (inNotNull(record.deleteTime)) {
            values.put("deleteTime", record.deleteTime);
        }
        if (inNotNull(record.endUpdateTime)) {
            values.put("endUpdateTime", record.endUpdateTime);
        }
        values.put("uploadTime", DateTime.getTimeString());
        return values;
    }

    private boolean inNotNull(String str) {
        if (str == null || str.toLowerCase().equals("null") || str.length() <= 0) {
            return false;
        }
        return true;
    }

    public void onClick(View v) {
    }
}
