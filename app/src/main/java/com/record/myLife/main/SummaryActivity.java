package com.record.myLife.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.Act2;
import com.record.myLife.BaseApplication;
import com.record.myLife.IActivity;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.LeftRightView;
import com.record.myLife.view.MyCalendarActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.DensityUtil;
import com.record.utils.FormatUtils;
import com.record.utils.LogUtils;
import com.record.utils.PreferUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.achartengine.GraphicalView;
import org.achartengine.chart.DoughnutChart;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class SummaryActivity extends BaseActivity implements IActivity {
    public static int DOUGHTYPE_MONTH = 3;
    public static int DOUGHTYPE_WEEK = 2;
    private int ACTIVITY_FLAG = ((int) System.currentTimeMillis());
    int DoughType = DOUGHTYPE_WEEK;
    int activity_flag = -1;
    AnimationController animControl;
    View btn_statistic_calendar;
    Context context;
    LayoutInflater inflater;
    String lastTempDate = "";
    LinearLayout ll_tem_statistic;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_statistic_unit) {
                SharedPreferences sp = SummaryActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0);
                int unit = sp.getInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0);
                if (unit == 0) {
                    sp.edit().putInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 1).commit();
                    SummaryActivity.this.tv_statistic_unit.setText(SummaryActivity.this.getResources().getString(R.string.str_uint) + ":H");
                } else if (unit == 1) {
                    sp.edit().putInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0).commit();
                    SummaryActivity.this.tv_statistic_unit.setText(SummaryActivity.this.getResources().getString(R.string.str_uint) + ":%");
                }
                if (SummaryActivity.this.DoughType == SummaryActivity.DOUGHTYPE_WEEK || SummaryActivity.this.DoughType == SummaryActivity.DOUGHTYPE_MONTH) {
                    SummaryActivity.this.initAllocationUI_v4(SummaryActivity.this.tempDate, 2, SummaryActivity.this.DoughType);
                }
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_click_Units_btn");
            } else if (id == R.id.tv_summary_week) {
                if (SummaryActivity.this.lastTempDate != null && SummaryActivity.this.lastTempDate.length() > 0) {
                    SummaryActivity.this.tempDate = SummaryActivity.this.lastTempDate;
                }
                SummaryActivity.this.updateUiByType2(SummaryActivity.DOUGHTYPE_WEEK);
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_click_week_btn");
            } else if (id == R.id.tv_summary_month) {
                if (SummaryActivity.this.lastTempDate != null && SummaryActivity.this.lastTempDate.length() > 0) {
                    SummaryActivity.this.tempDate = SummaryActivity.this.lastTempDate;
                }
                SummaryActivity.this.updateUiByType2(SummaryActivity.DOUGHTYPE_MONTH);
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_click_month_btn");
            } else if (id == R.id.btn_statistic_calendar) {
                MyCalendarActivity.startActivity((Activity) SummaryActivity.this.context, SummaryActivity.this.tempDate);
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_click_calendar_btn");
            } else if (id == R.id.btn_back) {
                SummaryActivity.this.onBackPressed();
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_return_btn");
            }
        }
    };
    RelativeLayout rl_tem_statistic;
    String tempDate = "";
    TextView tv_statistic_month;
    TextView tv_statistic_rank_week;
    TextView tv_statistic_unit;
    TextView tv_statistic_week;
    UiComponent uiComponent;

    class UiComponent {
        public View btn_back;
        public RelativeLayout rl_tem_dough;
        public LeftRightView v_left;
        public LeftRightView v_right;

        UiComponent() {
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SummaryActivity.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        SystemBarTintManager.setMIUIbar(this);
        init();
        initView();
    }

    public void initView() {
        BaseApplication.getInstance().getControllerManager().addIActivity(this);
        this.uiComponent = new UiComponent();
        setUiComponent();
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        int unit = sp.getInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0);
        if (unit == 0) {
            this.tv_statistic_unit.setText(getResources().getString(R.string.str_uint) + ":%");
        } else if (unit == 1) {
            this.tv_statistic_unit.setText(getResources().getString(R.string.str_uint) + ":H");
        }
        updateUiByType2(DOUGHTYPE_WEEK);
        if (sp.getInt(Val.CONFIGURE_STATICS_BUTTON_LOCATION, 2) == 1) {
            showLeft(false);
        } else {
            showRight(false);
        }
    }

    public void init() {
        this.context = this;
        this.animControl = new AnimationController();
        this.inflater = getLayoutInflater();
        this.tempDate = DateTime.getDateString();
        this.lastTempDate = this.tempDate;
        this.activity_flag = (int) System.currentTimeMillis();
    }

    public void refresh(Message msg) {
        if (msg.arg2 == this.ACTIVITY_FLAG) {
            switch (msg.what) {
            }
        }
    }

    private void getRankAndBarUI(String[] arr, boolean before) {
        try {
            ArrayList<Integer> list = DbUtils.queryActIdByType(this.context, "11");
            if (list != null && list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    int i = ((Integer) it.next()).intValue();
                    int spend = DbUtils.queryActHadSpend(this.context, arr[0], arr[1], i + "");
                    if (spend > 0) {
                        Act2 act2 = DbUtils.getAct2ByActId(this.context, i + "");
                        DbUtils.queryColorByActId(this.context, i);
                        if (act2 != null) {
                            RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.lv_progressbar, null);
                            ProgressBar pb_progress = (ProgressBar) rl.findViewById(R.id.pb_progress);
                            TextView tv_prompt = (TextView) rl.findViewById(R.id.tv_content);
                            ((TextView) rl.findViewById(R.id.tv_name)).setText(act2.getActName());
                            int days = DateTime.cal_daysBetween(arr[0], arr[1]) + 1;
                            LogUtils.log(arr[0] + arr[1] + days);
                            int everyday = act2.getTimeOfEveryday() * days;
                            String spendStr = DateTime.calculateTime5(this.context, (long) spend);
                            if (everyday > 0) {
                                String everyDayStr = DateTime.calculateTime5(this.context, (long) everyday);
                                tv_prompt.setText(spendStr + "/" + everyDayStr + " " + FormatUtils.format_2fra((((double) spend) / ((double) everyday)) * 100.0d) + "%");
                                pb_progress.setMax(everyday);
                                if (spend > everyday) {
                                    tv_prompt.setText(spendStr + "/" + everyDayStr + " " + getResources().getString(R.string.finish));
                                }
                            } else {
                                tv_prompt.setText(spendStr);
                                pb_progress.setMax(spend + 1800);
                            }
                            pb_progress.setProgress(spend);
                            this.ll_tem_statistic.addView(rl);
                        } else {
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void nextDate() {
        this.lastTempDate = "";
        if (this.DoughType == DOUGHTYPE_WEEK || this.DoughType == DOUGHTYPE_MONTH) {
            initAllocationUI_v4(this.tempDate, 3, this.DoughType);
        }
    }

    private void preDate() {
        this.lastTempDate = "";
        if (this.DoughType == DOUGHTYPE_WEEK || this.DoughType == DOUGHTYPE_MONTH) {
            initAllocationUI_v4(this.tempDate, 1, this.DoughType);
        }
    }

    private void showLeft(boolean click) {
        this.uiComponent.v_left.setVisibility(0);
        if (click) {
            this.animControl.fadeIn(this.uiComponent.v_left, 400, 0);
        }
        this.uiComponent.v_right.setVisibility(4);
        if (click) {
            this.animControl.fadeOut(this.uiComponent.v_right, 500, 0);
        }
    }

    private void showRight(boolean click) {
        this.uiComponent.v_right.setVisibility(0);
        if (click) {
            this.animControl.fadeIn(this.uiComponent.v_right, 400, 0);
        }
        this.uiComponent.v_left.setVisibility(4);
        if (click) {
            this.animControl.fadeOut(this.uiComponent.v_left, 500, 0);
        }
    }

    private void updateUiByType2(int DoughType) {
        this.DoughType = DoughType;
        if (DoughType == DOUGHTYPE_WEEK) {
            initAllocationUI_v4(this.tempDate, 2, DoughType);
            this.tv_statistic_week.setTextColor(getResources().getColor(R.color.white2));
            this.tv_statistic_month.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_week.setBackgroundResource(R.drawable.x_blue_bg_black_frame_left);
            this.tv_statistic_month.setBackgroundResource(R.drawable.x_tran_bg_black_frame_right);
        } else if (DoughType == DOUGHTYPE_MONTH) {
            initAllocationUI_v4(this.tempDate, 2, DoughType);
            this.tv_statistic_week.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_month.setTextColor(getResources().getColor(R.color.white2));
            this.tv_statistic_week.setBackgroundResource(R.drawable.x_tran_bg_black_frame_left);
            this.tv_statistic_month.setBackgroundResource(R.drawable.x_blue_bg_black_frame_right);
        }
    }

    private void initAllocationUI_v4(String tempDate2, int before, int DoughType) {
        String sql = "";
        String[] arr = null;
        int days = 7;
        if (DoughType == DOUGHTYPE_WEEK) {
            days = 7;
            int startDayOfWeek = PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2);
            if (before == 1) {
                arr = DateTime.getDateOfWeekStartAndEndForLastWeek(tempDate2, startDayOfWeek);
            } else if (before == 3) {
                arr = DateTime.getDateOfWeekStartAndEndForNextWeek(tempDate2, startDayOfWeek);
            } else if (before == 2) {
                arr = DateTime.getDateOfWeekStartAndEnd(tempDate2, startDayOfWeek);
            }
        } else if (DoughType == DOUGHTYPE_MONTH) {
            if (before == 1) {
                arr = DateTime.getDateOfMonthStartAndEndLastMonth(tempDate2);
            } else if (before == 3) {
                arr = DateTime.getDateOfMonthStartAndEndNextMonth(tempDate2);
            } else if (before == 2) {
                arr = DateTime.getDateOfMonthStartAndEnd(tempDate2);
            }
            days = (int) Double.parseDouble(arr[2]);
        }
        String timeSql = " and time >= '" + arr[0] + "' and time <= '" + arr[1] + "'";
        Cursor cursor = DbUtils.getDb2(this.context).rawQuery("Select sum(invest),sum(routine),sum(sleep),sum(waste) from t_allocation where userId is " + DbUtils.queryUserId(this.context) + timeSql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            double invest = cursor.getDouble(cursor.getColumnIndex("sum(invest)"));
            double waste = cursor.getDouble(cursor.getColumnIndex("sum(waste)"));
            double routine = cursor.getDouble(cursor.getColumnIndex("sum(routine)"));
            double sleep = cursor.getDouble(cursor.getColumnIndex("sum(sleep)"));
            double total = (double) DateTime.cal_secBetween(arr[0] + " 00:00:00", arr[1] + " 24:00:00");
            this.tempDate = arr[0];
            if (waste < 0.0d) {
                waste = 0.0d;
            }
            View gv = buildDough(invest, routine, sleep, waste, total);
            this.uiComponent.rl_tem_dough.removeAllViews();
            this.uiComponent.rl_tem_dough.addView(gv);
            Cursor cursor2 = DbUtils.getDb2(this.context).rawQuery("Select Id from t_allocation where userId is " + DbUtils.queryUserId(this.context) + timeSql, null);
            TextView tv = (TextView) this.inflater.inflate(R.layout.template_text, null);
            tv.setTextColor(getResources().getColor(R.color.black_tran_es));
            String start = arr[0].substring(arr[0].indexOf("-") + 1, arr[0].length());
            TextView textView = tv;
            textView.setText(start + "(" + DateTime.getDay3(this.context, arr[0] + " 00:00:00") + ")~" + arr[1].substring(arr[1].indexOf("-") + 1, arr[1].length()) + "(" + DateTime.getDay3(this.context, arr[1] + " 00:00:00") + ") \n" + cursor2.getCount() + getString(R.string.str_day) + "/" + days + getString(R.string.str_day) + " " + getString(R.string.str_records));
            this.ll_tem_statistic.removeAllViews();
            this.ll_tem_statistic.addView(tv);
            isShowWeekRank((int) invest);
            getRankAndBarUI(arr, true);
            setSummarizeAndMorning(arr[0], arr[1]);
        } else {
            ToastUtils.toastShort(this.context, "没有记录啦...");
        }
        DbUtils.close(cursor);
    }

    private void isShowWeekRank(int invest) {
        try {
            if (this.DoughType == DOUGHTYPE_WEEK) {
                String[] rateArr = Val.getWeeKNextRankNameAndTime(this.context, invest);
                if (invest > 100) {
                    showWeekRate();
                    this.tv_statistic_rank_week.setText(rateArr[0] + getString(R.string.str_level));
                    this.tv_statistic_rank_week.setTextColor(Integer.parseInt(rateArr[3]));
                    this.tv_statistic_rank_week.setAnimation(getAnimation());
                    return;
                }
                dontShowRate();
                return;
            }
            dontShowRate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ScaleAnimation getAnimation() {
        ScaleAnimation animation = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f, 1, 0.5f, 1, 0.5f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(300);
        animation.setStartOffset(150);
        return animation;
    }

    private void showWeekRate() {
        this.tv_statistic_rank_week.setVisibility(0);
    }

    private void dontShowRate() {
        this.tv_statistic_rank_week.setText("");
        this.tv_statistic_rank_week.setVisibility(8);
    }

    private void setSummarizeAndMorning(String startDate, String endDate) {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select time,remarks,morningVoice from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time >= '" + startDate + "' and time <= '" + endDate + "' order by time desc", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                RelativeLayout remarkRl;
                TextView tv2;
                String morningVoice = cursor.getString(cursor.getColumnIndex("morningVoice"));
                String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                if ((morningVoice != null && morningVoice.length() > 0) || (remarks != null && remarks.length() > 0)) {
                    int dateIndex = time.lastIndexOf("-");
                    String time2 = time.substring(dateIndex + 1, time.length()) + " " + DateTime.getDay3(this.context, time + " 00:00:00");
                    LayoutParams params = new LayoutParams(-1, -2);
                    TextView tv = (TextView) this.inflater.inflate(R.layout.tem_text_summarize_date, null);
                    tv.setText(time2);
                    params.topMargin = DensityUtil.dip2px(this.context, 10.0f);
                    tv.setLayoutParams(params);
                    this.ll_tem_statistic.addView(tv);
                }
                if (morningVoice != null && morningVoice.length() > 0) {
                    remarkRl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_summarize_v2, null);
                    tv2 = (TextView) remarkRl.findViewById(R.id.tv_tem_summarize);
                    ((TextView) remarkRl.findViewById(R.id.tv_tem_pre)).setText(getString(R.string.str_Morning_voice) + "：");
                    tv2.setText(morningVoice);
                    this.ll_tem_statistic.addView(remarkRl);
                }
                if (remarks != null && remarks.length() > 0) {
                    remarkRl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_summarize_v2, null);
                    tv2 = (TextView) remarkRl.findViewById(R.id.tv_tem_summarize);
                    ((TextView) remarkRl.findViewById(R.id.tv_tem_pre)).setText(getString(R.string.str_summarize2) + "：");
                    tv2.setText(remarks);
                    this.ll_tem_statistic.addView(remarkRl);
                }
                if ((morningVoice != null && morningVoice.length() > 0) || (remarks != null && remarks.length() > 0)) {
                    this.ll_tem_statistic.addView((RelativeLayout) this.inflater.inflate(R.layout.tem_line_gray, null));
                }
            }
        }
        DbUtils.close(cursor);
    }

    private View buildDough(double invest, double routine, double sleep, double waste, double total) {
        String invest_Str;
        String waste_Str;
        String routine_Str;
        String sleep_Str;
        String unknow_Str;
        int bg = getResources().getColor(R.color.gray);
        int bg_green1 = getResources().getColor(R.color.bg_green1);
        int bg_blue1 = getResources().getColor(R.color.bg_blue1);
        int bg_red1 = getResources().getColor(R.color.bg_red1);
        int bg_yellow1 = getResources().getColor(R.color.bg_yellow1);
        int bg_ivory = getResources().getColor(R.color.ivory);
        if (waste < 0.0d) {
            waste = 0.0d;
        }
        double unknow = (((total - invest) - waste) - routine) - sleep;
        if (unknow < 0.0d) {
            unknow = 0.0d;
        }
        if (getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0) == 0) {
            invest_Str = FormatUtils.format_1fra((invest / total) * 100.0d) + "% ";
            waste_Str = FormatUtils.format_1fra((waste / total) * 100.0d) + "% ";
            routine_Str = FormatUtils.format_1fra((routine / total) * 100.0d) + "% ";
            sleep_Str = FormatUtils.format_1fra((sleep / total) * 100.0d) + "% ";
            unknow_Str = FormatUtils.format_1fra((unknow / total) * 100.0d) + "% ";
        } else {
            invest_Str = DateTime.calculateTime5(this.context, (long) invest);
            waste_Str = DateTime.calculateTime5(this.context, (long) waste);
            routine_Str = DateTime.calculateTime5(this.context, (long) routine);
            sleep_Str = DateTime.calculateTime5(this.context, (long) sleep);
            unknow_Str = DateTime.calculateTime5(this.context, (long) unknow);
        }
        List<double[]> values = new ArrayList();
        values.add(new double[]{invest, waste, routine, sleep, unknow});
        List<String[]> titles = new ArrayList();
        String str_invest_short = getResources().getString(R.string.str_invest_short);
        String str_waste_short = getResources().getString(R.string.str_waste_short);
        String str_Routine_short = getResources().getString(R.string.str_Routine_short);
        String str_Sleep_short = getResources().getString(R.string.str_Sleep_short);
        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("Select actName,type from (Select * from t_act where " + DbUtils.getWhereUserId(this.context) + " ) where type is 10 or type is 20 or type is 30 or type is 40", null);
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                String actName = cursor2.getString(cursor2.getColumnIndex("actName"));
                int type = cursor2.getInt(cursor2.getColumnIndex(a.a));
                if (type == 10) {
                    str_invest_short = actName;
                } else if (type == 20) {
                    str_Routine_short = actName;
                } else if (type == 30) {
                    str_Sleep_short = actName;
                } else if (type == 40) {
                    str_waste_short = actName;
                }
            }
        }
        DbUtils.close(cursor2);
        String str_unkown_short = getResources().getString(R.string.str_unknow_short);
        titles.add(new String[]{invest_Str + str_invest_short, waste_Str + str_waste_short, routine_Str + str_Routine_short, sleep_Str + str_Sleep_short, unknow_Str + str_unkown_short});
        DefaultRenderer renderer = buildCategoryRenderer(new int[]{bg_green1, bg_red1, bg_yellow1, bg_blue1, bg_ivory});
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(bg);
        renderer.setLabelsColor(-7829368);
        renderer.setShowLegend(false);
        renderer.setPanEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setLabelsTextSize(getResources().getDimension(R.dimen.textSize_Micro_Small_9));
        renderer.setChartTitleTextSize(getResources().getDimension(R.dimen.textSize_Micro_Small_9));
        renderer.setLegendTextSize(getResources().getDimension(R.dimen.textSize_Micro_Small_9));
        return new GraphicalView(this.context, new DoughnutChart(buildMultipleCategoryDataset("时间分配", titles, values), renderer));
    }

    private String getDateTitle() {
        String today = DateTime.getDateString();
        String yesterday = DateTime.beforeNDays2Str(-1);
        String beforyesterday = DateTime.beforeNDays2Str(-2);
        String year = DateTime.getYearStr(null);
        String todayStr = getResources().getString(R.string.str_today);
        String yesterdayStr = getResources().getString(R.string.str_Yesterday);
        String beforYesterdayStr = getResources().getString(R.string.str_the_day_before_yesterday);
        String dayOfWeek = DateTime.getDay3(this.context, this.tempDate + " 00:00:00");
        String str = DateTime.convertTsToYMD(this.tempDate + " 00:00:00");
        if (this.tempDate.contains(year)) {
            str = str.replace(year + "年", "");
        }
        if (today.equals(this.tempDate)) {
            return str + " " + todayStr;
        }
        if (yesterday.equals(this.tempDate)) {
            return str + " " + yesterdayStr;
        }
        if (beforyesterday.equals(this.tempDate)) {
            return str + " " + beforYesterdayStr;
        }
        return str + " " + dayOfWeek;
    }

    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(15.0f);
        renderer.setLegendTextSize(15.0f);
        renderer.setMargins(new int[]{20, 30, 15, 0});
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected MultipleCategorySeries buildMultipleCategoryDataset(String title, List<String[]> titles, List<double[]> values) {
        MultipleCategorySeries series = new MultipleCategorySeries(title);
        int k = 0;
        for (double[] value : values) {
            series.add((k + 2007) + "", (String[]) titles.get(k), value);
            k++;
        }
        return series;
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 26 && -1 == resultCode) {
            this.tempDate = data.getStringExtra("Date");
            initAllocationUI_v4(this.tempDate, 2, this.DoughType);
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

    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().getControllerManager().removeIActivity(this);
    }

    private void setUiComponent() {
        this.uiComponent.v_left = (LeftRightView) findViewById(R.id.v_left);
        this.uiComponent.v_left.setType(1);
        this.uiComponent.v_right = (LeftRightView) findViewById(R.id.v_right);
        this.uiComponent.rl_tem_dough = (RelativeLayout) findViewById(R.id.rl_tem_dough);
        this.uiComponent.btn_back = findViewById(R.id.btn_back);
        this.btn_statistic_calendar = findViewById(R.id.btn_statistic_calendar);
        this.rl_tem_statistic = (RelativeLayout) findViewById(R.id.rl_tem_statistic);
        this.ll_tem_statistic = (LinearLayout) findViewById(R.id.ll_tem_statistic);
        this.tv_statistic_unit = (TextView) findViewById(R.id.tv_statistic_unit);
        this.tv_statistic_rank_week = (TextView) findViewById(R.id.tv_statistic_rank_week);
        this.tv_statistic_week = (TextView) findViewById(R.id.tv_summary_week);
        this.tv_statistic_month = (TextView) findViewById(R.id.tv_summary_month);
        this.tv_statistic_unit.setOnClickListener(this.myClickListener);
        this.btn_statistic_calendar.setOnClickListener(this.myClickListener);
        OnClickListener preListener = new OnClickListener() {
            public void onClick(View arg0) {
                SummaryActivity.this.preDate();
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_click_left_Moving_time_btn");
            }
        };
        OnClickListener nextListener = new OnClickListener() {
            public void onClick(View arg0) {
                SummaryActivity.this.nextDate();
                MobclickAgent.onEvent(SummaryActivity.this.getApplicationContext(), "Statistics_more_click_right_Moving_time_btn");
            }
        };
        this.uiComponent.v_left.setOnClickListenerLeft(preListener);
        this.uiComponent.v_left.setOnClickListenerRight(nextListener);
        this.uiComponent.v_right.setOnClickListenerLeft(preListener);
        this.uiComponent.v_right.setOnClickListenerRight(nextListener);
        this.uiComponent.v_left.setOnClickListenerIv(new OnClickListener() {
            public void onClick(View arg0) {
                PreferUtils.putInt(SummaryActivity.this.context, Val.CONFIGURE_STATICS_BUTTON_LOCATION, 2);
                SummaryActivity.this.showRight(true);
            }
        });
        this.uiComponent.v_right.setOnClickListenerIv(new OnClickListener() {
            public void onClick(View arg0) {
                PreferUtils.putInt(SummaryActivity.this.context, Val.CONFIGURE_STATICS_BUTTON_LOCATION, 1);
                SummaryActivity.this.showLeft(true);
            }
        });
        this.tv_statistic_week.setOnClickListener(this.myClickListener);
        this.tv_statistic_month.setOnClickListener(this.myClickListener);
        this.uiComponent.btn_back.setOnClickListener(this.myClickListener);
    }

    private void log(String str) {
        LogUtils.log(str);
    }
}
