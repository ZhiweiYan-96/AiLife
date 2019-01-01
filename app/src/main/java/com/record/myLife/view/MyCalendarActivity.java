package com.record.myLife.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.Lunar;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.Calendar;
import java.util.HashMap;

public class MyCalendarActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
    static String TAG = "override";
    Button btn_add_record_digit_number1;
    Context context;
    AnimationController controller;
    int currentMonth = 0;
    String deliverDate = "";
    LinearLayout ll_my_calendar_items;
    RelativeLayout ll_my_calendar_title_items;
    PagerAdapter myAdapter;
    OnClickListener myClickListener2 = new OnClickListener() {
        public void onClick(View v) {
            String date = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            Intent it = new Intent();
            it.putExtra("Date", date);
            MyCalendarActivity.this.setResult(-1, it);
            MyCalendarActivity.this.finish();
        }
    };
    String today = DateTime.getDateString();
    TextView tv_my_calendar_month;
    UiComponent uComponent;
    ViewPager vp_my_calendar_pager;

    class UiComponent {
        Button btn_back;
        Button btn_today;
        LinearLayout ll_space;

        UiComponent() {
        }
    }

    class myViewPager extends PagerAdapter {
        HashMap<Integer, LinearLayout> viewMap = new HashMap();

        public int getCount() {
            return 0;
//            return ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) this.viewMap.get(Integer.valueOf(position)));
            this.viewMap.remove(Integer.valueOf(position));
        }

        public Object instantiateItem(ViewGroup container, int position) {
            int dif = position - 1073741823;
            Calendar cal = DateTime.pars2Calender2(MyCalendarActivity.this.deliverDate);
            cal.add(Calendar.MONTH, dif);
            String date = DateTime.formatDate(cal);
            int current = MyCalendarActivity.this.vp_my_calendar_pager.getCurrentItem();
            LinearLayout ll = MyCalendarActivity.this.initData(date);
            container.addView(ll);
            this.viewMap.put(Integer.valueOf(position), ll);
            return ll;
        }
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);
        init();
        initView();
    }

    private void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
        this.controller = new AnimationController();
        SystemBarTintManager.setMIUIbar(this);
        this.today = DateTime.getDateString();
        this.deliverDate = getIntent().getStringExtra("Date");
        if (this.deliverDate == null || this.deliverDate.indexOf(" ") <= 0) {
            this.deliverDate = this.today;
        } else {
            this.deliverDate = this.deliverDate.substring(0, this.deliverDate.indexOf(" "));
        }
        this.currentMonth = DateTime.pars2Calender2(this.deliverDate).get(Calendar.MONTH);
    }

    private void initView() {
        this.uComponent = new UiComponent();
        setUiComponent(this.uComponent);
        initTitle();
        initPager();
    }

    private void updateUiShowToday() {
        this.uComponent.btn_today.setVisibility(View.GONE);
    }

    private void setUiComponent(UiComponent uComponent) {
        uComponent.ll_space = (LinearLayout) findViewById(R.id.ll_space);
        this.vp_my_calendar_pager = (ViewPager) findViewById(R.id.vp_my_calendar_pager);
        this.ll_my_calendar_items = (LinearLayout) findViewById(R.id.ll_my_calendar_items);
        this.ll_my_calendar_title_items = (RelativeLayout) findViewById(R.id.ll_my_calendar_title_items);
        this.tv_my_calendar_month = (TextView) findViewById(R.id.tv_my_calendar_month);
        uComponent.btn_today = (Button) findViewById(R.id.tv_my_calendar_today);
        uComponent.btn_back = (Button) findViewById(R.id.btn_add_record_digit_back);
        uComponent.btn_today.setOnClickListener(this);
        uComponent.btn_back.setOnClickListener(this);
        uComponent.ll_space.setOnClickListener(this);
    }

    private void initPager() {
        this.tv_my_calendar_month.setText(DateTime.formatMonth(DateTime.pars2Calender2(this.deliverDate)));
        if (this.myAdapter == null) {
            this.myAdapter = new myViewPager();
        }
        this.vp_my_calendar_pager.setAdapter(this.myAdapter);
        this.vp_my_calendar_pager.setCurrentItem(1073741823);
        this.vp_my_calendar_pager.setOnPageChangeListener(this);
        updateUiShowToday();
    }

    private void initTitle() {
        new MyView((Activity) this.context).addWeekTitle(this.ll_my_calendar_title_items);
    }

    public void onPageSelected(int position) {
        int dif2 = position - 1073741823;
        Calendar cal2 = DateTime.pars2Calender2(this.deliverDate);
        cal2.add(Calendar.MONTH, dif2);
        this.tv_my_calendar_month.setText(DateTime.formatMonth(cal2));
        updateUiShowToday();
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    private LinearLayout initData(String date) {
        int startDayOfWeek;
        int endDayOfWeek;
        LinearLayout ll_my_calendar_items = (LinearLayout) getLayoutInflater().inflate(R.layout.tem_ll, null);
        Calendar calendar = DateTime.pars2Calender2(date);
        int maxNumber = calendar.getActualMaximum(Calendar.DATE);
        int currentNumber = calendar.get(Calendar.DATE);
        calendar.add(Calendar.DAY_OF_MONTH, -(currentNumber - 1));
        this.currentMonth = calendar.get(Calendar.MONTH);
        if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2) == 1) {
            startDayOfWeek = 1;
            endDayOfWeek = 7;
        } else {
            startDayOfWeek = 2;
            endDayOfWeek = 1;
        }
        log("本月最大天数：" + maxNumber);
        Calendar calendar2 = DateTime.pars2Calender2(date);
        calendar2.add(Calendar.DAY_OF_MONTH, maxNumber - currentNumber);
        int dayOfWeek2 = calendar2.get(Calendar.DAY_OF_WEEK);
        if (startDayOfWeek == 1) {
            if (dayOfWeek2 < 7) {
                maxNumber += 7 - dayOfWeek2;
            }
        } else if (dayOfWeek2 > 1) {
            maxNumber += (7 - dayOfWeek2) + 1;
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dif;
        if (startDayOfWeek == 1) {
            if (dayOfWeek > startDayOfWeek) {
                dif = dayOfWeek - startDayOfWeek;
                calendar.add(Calendar.DAY_OF_MONTH, -dif);
                maxNumber += dif;
            }
        } else if (dayOfWeek == 1) {
            dif = 7 - dayOfWeek;
            calendar.add(Calendar.DAY_OF_MONTH, -dif);
            maxNumber += dif;
        } else if (dayOfWeek > startDayOfWeek) {
            dif = dayOfWeek - startDayOfWeek;
            calendar.add(Calendar.DAY_OF_MONTH, -dif);
            maxNumber += dif;
        }
        MyView myView = new MyView((Activity) this.context);
        LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.temp_my_calendar_items, null);
        int i = 0;
        while (i < maxNumber) {
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (startDayOfWeek == dayOfWeek && i != 0) {
                ll = (LinearLayout) getLayoutInflater().inflate(R.layout.temp_my_calendar_items, null);
            }
            ll = myView.setCalendarItems(ll, calendar, startDayOfWeek, this.currentMonth, this.myClickListener2, null, false);
            if (dayOfWeek == endDayOfWeek || i == maxNumber - 1) {
                ll_my_calendar_items.addView(ll);
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            i++;
        }
        return ll_my_calendar_items;
    }

    private LinearLayout setItems(LinearLayout ll, Calendar cal, int startDayOfWeek) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        RelativeLayout itemRl = null;
        TextView idTv = null;
        TextView numberTv = null;
        ImageView circleIv = null;
        ImageView upCircleIv = null;
        TextView infoTv = null;
        if (startDayOfWeek == 1) {
            if (dayOfWeek == 1) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_1);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_1);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_1);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1_1);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_1);
            } else if (dayOfWeek == 2) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_2);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_2);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_2);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2_2);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_2);
            } else if (dayOfWeek == 3) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_3);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_3);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_3);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3_3);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_3);
            } else if (dayOfWeek == 4) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_4);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_4);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_4);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4_4);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_4);
            } else if (dayOfWeek == 5) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_5);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_5);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_5);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5_5);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_5);
            } else if (dayOfWeek == 6) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_6);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_6);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_6);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6_6);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_6);
            } else if (dayOfWeek == 7) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_7);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_7);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_7);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7_7);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_7);
            }
        } else if (dayOfWeek == 2) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_1);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_1);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_1);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1_1);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_1);
        } else if (dayOfWeek == 3) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_2);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_2);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_2);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2_2);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_2);
        } else if (dayOfWeek == 4) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_3);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_3);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_3);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3_3);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_3);
        } else if (dayOfWeek == 5) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_4);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_4);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_4);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4_4);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_4);
        } else if (dayOfWeek == 6) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_5);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_5);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_5);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5_5);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_5);
        } else if (dayOfWeek == 7) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_6);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_6);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_6);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6_6);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_6);
        } else if (dayOfWeek == 1) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_7);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_7);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_7);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7_7);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_7);
        }
        itemRl.setOnClickListener(this.myClickListener2);
        String date = DateTime.formatDate(cal);
        idTv.setText(date);
        int month = cal.get(Calendar.MONTH);
        if (this.currentMonth != month) {
            numberTv.setTextColor(getResources().getColor(R.color.black_tran_tw));
        }
        numberTv.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
        boolean isShowCircle = false;
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and stopTime >= '" + date + " 00:00:00' and stopTime <= '" + date + " 23:59:59' limit 1 ", null);
        if (cursor.getCount() > 0) {
            isShowCircle = true;
        }
        DbUtils.close(cursor);
        String info = "";
        if (this.today != null && this.today.equals(date)) {
            info = getResources().getString(R.string.str_today);
            itemRl.setBackgroundResource(R.drawable.x_tran_bg_red_frame_middle);
        }
        if (this.currentMonth == month) {
            info = new Lunar(cal).getFestival();
        }
        if (info != null && info.length() > 0) {
            infoTv.setText(info);
            infoTv.setVisibility(View.VISIBLE);
        }
        if (isShowCircle && info.length() > 0) {
            circleIv.setVisibility(View.INVISIBLE);
            upCircleIv.setVisibility(View.VISIBLE);
        } else if (isShowCircle) {
            circleIv.setVisibility(View.VISIBLE);
            upCircleIv.setVisibility(View.INVISIBLE);
        } else {
            circleIv.setVisibility(View.INVISIBLE);
            upCircleIv.setVisibility(View.INVISIBLE);
        }
        return ll;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_record_digit_back:
            case R.id.ll_space:
                onBackPressed();
                return;
            case R.id.tv_my_calendar_today:
                this.deliverDate = this.today;
                initPager();
                return;
            default:
                return;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (4 != event.getAction()) {
            return super.onTouchEvent(event);
        }
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_top);
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

    public static void startActivity(Activity activity, String currentDate) {
        Intent it = new Intent(activity, MyCalendarActivity.class);
        it.putExtra("Date", currentDate);
        activity.startActivityForResult(it, 26);
    }
}
