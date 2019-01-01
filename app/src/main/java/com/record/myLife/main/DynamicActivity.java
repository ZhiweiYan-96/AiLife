package com.record.myLife.main;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.record.bean.DayInfo;
import com.record.custom.DateTimeBean;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils;
import com.record.utils.share.QuickShareUtil;
import com.record.view.chart.Tools;
import com.record.view.chart.listener.OnEntryClickListener;
import com.record.view.chart.model.BarSet;
import com.record.view.chart.model.ChartSet;
import com.record.view.chart.model.LineSet;
import com.record.view.chart.view.AxisController.LabelPosition;
import com.record.view.chart.view.BarChartView;
import com.record.view.chart.view.ChartView.GridType;
import com.record.view.chart.view.LineChartView;
import com.record.view.chart.view.animation.Animation;
import com.record.view.chart.view.animation.easing.BaseEasingMethod;
import com.record.view.chart.view.animation.easing.quint.QuintEaseOut;
import com.record.view.chart.view.animation.style.DashAnimation;
import com.umeng.analytics.MobclickAgent;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DynamicActivity extends BaseActivity {
    private static int BAR_INVEST_MAX = 10;
    private static int BAR_INVEST_MIN = 0;
    private static int BAR_INVEST_STEP = 2;
    private static int BAR_MAX = 10;
    private static int BAR_MIN = 0;
    private static int BAR_STEP = 2;
    private static int LINE_MAX = 10;
    private static int LINE_MIN = 0;
    private static int LINE_STEP = 3;
    private static String[] barLabels = new String[]{"一", "二", "三", "四", "五", "六", "日"};
    private static float[] barValues = new float[]{6.5f, 7.5f, 3.5f, 3.5f, 10.0f, 4.5f, 5.5f};
    private static final int[] beginOrder = new int[]{0, 1, 2, 3, 4, 5, 6};
    private static final int[] endOrder = new int[]{6, 5, 4, 3, 2, 1, 0};
    private static String[] investBarLabels = new String[]{"一", "二", "三", "四", "五", "六", "日"};
    private static float[][] investBarValues = new float[][]{new float[]{6.5f, 7.5f, 3.5f, 3.5f, 10.0f, 4.5f, 5.5f}, new float[]{6.0f, 6.0f, 8.0f, 3.0f, 10.0f, 9.0f, 5.0f}};
    private static float[][] investOrignBarValues = ((float[][]) null);
    private static String[] lineLabels = new String[]{"一", "二", "三", "四", "五", "六", "日"};
    private static float[][] lineValues = new float[][]{new float[]{6.0f, 2.0f, 9.0f, 6.0f, 1.0f, 5.0f, 7.0f}, new float[]{2.0f, 4.0f, 3.0f, 7.0f, 5.0f, 3.0f, 6.0f}};
    private static ImageButton mAlphaBtn;
    private static BarChartView mBarChart;
    private static int mCurrAlpha;
    private static BaseEasingMethod mCurrEasing;
    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    private static float mCurrStartX;
    private static float mCurrStartY;
    private static ImageButton mEaseBtn;
    private static ImageButton mEnterBtn;
    private static BarChartView mInvestBarChart;
    private static LineChartView mLineChart;
    private static int mOldAlpha;
    private static BaseEasingMethod mOldEasing;
    private static float mOldOverlapFactor;
    private static int[] mOldOverlapOrder;
    private static float mOldStartX;
    private static float mOldStartY;
    private static ImageButton mOrderBtn;
    private static final int[] middleOrder = new int[]{3, 2, 4, 1, 5, 0, 6};
    private final OnClickListener barClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (DynamicActivity.this.mBarTooltip != null) {
                DynamicActivity.this.dismissBarTooltip(-1, -1, null);
            }
        }
    };
    private final OnEntryClickListener barEntryListener = new OnEntryClickListener() {
        public void onClick(int setIndex, int entryIndex, Rect rect) {
            if (DynamicActivity.this.mBarTooltip == null) {
                DynamicActivity.this.showBarTooltip(setIndex, entryIndex, rect);
            } else {
                DynamicActivity.this.dismissBarTooltip(setIndex, entryIndex, rect);
            }
        }
    };
    Context context;
    private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
    private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();
    private final OnClickListener investBarClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (DynamicActivity.this.mInvestBarTooltip != null) {
                DynamicActivity.this.dismissInvestBarTooltip(-1, -1, null);
            }
        }
    };
    private final OnEntryClickListener investBarEntryListener = new OnEntryClickListener() {
        public void onClick(int setIndex, int entryIndex, Rect rect) {
            if (DynamicActivity.this.mInvestBarTooltip == null) {
                DynamicActivity.this.showInvestBarTooltip(setIndex, entryIndex, rect);
            } else {
                DynamicActivity.this.dismissInvestBarTooltip(setIndex, entryIndex, rect);
            }
        }
    };
    private final OnClickListener lineClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (DynamicActivity.this.mLineTooltip != null) {
                DynamicActivity.this.dismissLineTooltip(-1, -1, null);
            }
        }
    };
    private final OnEntryClickListener lineEntryListener = new OnEntryClickListener() {
        public void onClick(int setIndex, int entryIndex, Rect rect) {
            if (DynamicActivity.this.mLineTooltip == null) {
                Log.i("override", "setIndex" + setIndex + ",entryIndex" + entryIndex);
                DynamicActivity.this.showLineTooltip(setIndex, entryIndex, rect);
                return;
            }
            DynamicActivity.this.dismissLineTooltip(setIndex, entryIndex, rect);
        }
    };
    private Paint mBarGridPaint;
    private TextView mBarTooltip;
    private Paint mInvestBarGridPaint;
    private TextView mInvestBarTooltip;
    private Paint mLineGridPaint;
    private TextView mLineTooltip;
    int startDateOfWeek = 1;
    private TextView tv_statistic_rank_week;
    UiComponent uiComponent;

    class Compator implements Comparator<Entry<String, DayInfo>> {
        Compator() {
        }

        public int compare(Entry<String, DayInfo> lhs, Entry<String, DayInfo> rhs) {
            return DateTime.compare_date(((String) lhs.getKey()) + " 00:00:00", ((String) rhs.getKey()) + " 00:00:00");
        }
    }

    class UiComponent {
        Button iv_getup_share;
        TextView tv_avg_getup;
        TextView tv_avg_invest;
        TextView tv_avg_week_sleep;
        TextView tv_getup_rate;
        TextView tv_growth_rate;
        TextView tv_more;
        TextView tv_sum_week_sleep;
        TextView tv_total_invest;
        TextView tv_week_sleep_rank;

        UiComponent() {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        init();
        initView();
    }

    private void setUiComponent(UiComponent uiComponent) {
        uiComponent.iv_getup_share = (Button) findViewById(R.id.iv_getup_share);
        uiComponent.tv_total_invest = (TextView) findViewById(R.id.tv_total_invest);
        uiComponent.tv_avg_invest = (TextView) findViewById(R.id.tv_avg_invest);
        uiComponent.tv_growth_rate = (TextView) findViewById(R.id.tv_growth_rate);
        uiComponent.tv_avg_getup = (TextView) findViewById(R.id.tv_avg_getup);
        uiComponent.tv_getup_rate = (TextView) findViewById(R.id.tv_getup_rate);
        uiComponent.tv_sum_week_sleep = (TextView) findViewById(R.id.tv_sum_week_sleep);
        uiComponent.tv_avg_week_sleep = (TextView) findViewById(R.id.tv_avg_week_sleep);
        uiComponent.tv_week_sleep_rank = (TextView) findViewById(R.id.tv_week_sleep_rate);
        uiComponent.tv_more = (TextView) findViewById(R.id.tv_more);
    }

    private void initView() {
        this.uiComponent = new UiComponent();
        setUiComponent(this.uiComponent);
        this.tv_statistic_rank_week = (TextView) findViewById(R.id.tv_statistic_rank_week);
        this.uiComponent.iv_getup_share.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new QuickShareUtil(DynamicActivity.this.context).SceenCutAndShare("过来看看我的投入情况吧！");
                MobclickAgent.onEvent(DynamicActivity.this.getApplicationContext(), "Statistics_click_share_btn");
            }
        });
        ((TextView) findViewById(R.id.tv_getup_title)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DialogUtils.showPrompt(DynamicActivity.this.context, "蚁线表示上周起床记录。\n\n小提示：如何添加起床及睡眠？\n可添加起床类型的记录，目前以每天第一个起床记录的结束时间作为当天起床时间，以每天起床类型记录的总花费时间为睡眠。（午觉也累计在睡眠里哦！）");
            }
        });
        ((TextView) findViewById(R.id.tv_invest_title)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DialogUtils.showPrompt(DynamicActivity.this.context, "天蓝色表示本周投入记录，浅色表示上周投入记录。\n\n小提示：评级是根据本周累计投入动态评级的结果，有投入时才显示。");
            }
        });
        this.uiComponent.tv_more.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SummaryActivity.startActivity(DynamicActivity.this.context);
                DynamicActivity.this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                MobclickAgent.onEvent(DynamicActivity.this.getApplicationContext(), "Statistics_click_more_btn");
            }
        });
        initInvestBarChart();
        initLineChart();
        initBarChart();
        updateInvestBarChart();
        updateLineChart();
        updateBarChart();
        showWeekRate();
        updateUiWeekInvest();
        updateUiGetup();
        updateUiSleep();
    }

    private void updateUiSleep() {
        if (barValues != null) {
            float[] sum = calcSumValiNumByLength(barValues, barValues.length);
            if (sum[0] > 0.0f && sum[1] > 0.0f) {
                this.uiComponent.tv_sum_week_sleep.setText(getString(R.string.str_sum_week_sleep) + "\n" + FormatUtils.format_1fra((double) sum[0]) + "h");
                this.uiComponent.tv_avg_week_sleep.setText(getString(R.string.str_avg_week_sleep) + "\n" + FormatUtils.format_1fra((double) (sum[0] / sum[1])) + "h");
                this.uiComponent.tv_week_sleep_rank.setText(getString(R.string.str_week_sleep_rank) + "\n" + getWeekSleepRate(sum[0] / sum[1]));
                return;
            }
        }
        this.uiComponent.tv_sum_week_sleep.setText(getString(R.string.str_sum_week_sleep) + "\n-");
        this.uiComponent.tv_avg_week_sleep.setText(getString(R.string.str_avg_week_sleep) + "\n-");
        this.uiComponent.tv_week_sleep_rank.setText(getString(R.string.str_week_sleep_rank) + "\n-");
    }

    private String getWeekSleepRate(float avgSleep) {
        String rank = "-";
        if (avgSleep < 6.0f) {
            return getString(R.string.str_week_sleep_rank_short);
        }
        if (avgSleep >= 6.0f && avgSleep <= 9.0f) {
            return getString(R.string.str_week_sleep_rank_normal);
        }
        if (avgSleep > 9.0f && avgSleep < 10.0f) {
            return getString(R.string.str_week_sleep_rank_more);
        }
        if (avgSleep > 12.0f) {
            return getString(R.string.str_week_sleep_rank_too_more);
        }
        return rank;
    }

    private void updateUiGetup() {
        float[] sum = calcSumValiNumByLength(lineValues[0], new DateTimeBean(this.startDateOfWeek).getDiffBeforeWeeks(0) + 1);
        float valiAvg = sum[0] / sum[1];
        if (sum == null || sum[1] <= 0.0f) {
            this.uiComponent.tv_avg_getup.setText(getString(R.string.str_avg_getup_time) + "\n-");
        } else {
            this.uiComponent.tv_avg_getup.setText(getString(R.string.str_avg_getup_time) + "\n" + DateTime.calculateTime9(valiAvg));
        }
        if (sum == null || sum[1] <= 0.0f) {
            this.uiComponent.tv_getup_rate.setText(getString(R.string.str_getup_regular) + "\n-");
            return;
        }
        float avg = 0.0f;
        for (float val : lineValues[0]) {
            if (val > 0.0f) {
                avg += (val - valiAvg) * (val - valiAvg);
            }
        }
        this.uiComponent.tv_getup_rate.setText(getString(R.string.str_getup_regular) + "\n" + FormatUtils.format_2fra((double) (avg / sum[1])));
    }

    private void updateUiWeekInvest() {
        int startToToday = new DateTimeBean(this.startDateOfWeek).getDiffBeforeWeeks(0);
        float lastWeekSum = calcSumByLength(investOrignBarValues[0], startToToday + 1);
        float thisWeekSum = calcSumByLength(investOrignBarValues[1], startToToday + 1);
        if (lastWeekSum > 0.0f) {
            float rate = ((thisWeekSum - lastWeekSum) / lastWeekSum) * 100.0f;
            if (rate > 200.0f) {
                rate = 200.0f;
            }
            this.uiComponent.tv_growth_rate.setText(getString(R.string.str_yearonyear_growth) + "\n" + (rate == 200.0f ? "+" : "") + FormatUtils.format_0fra((double) rate) + "%");
        } else {
            this.uiComponent.tv_growth_rate.setText(getString(R.string.str_yearonyear_growth) + "\n-");
        }
        if (investOrignBarValues != null) {
            float sum = 0.0f;
            for (float f : investOrignBarValues[1]) {
                sum += f;
            }
            this.uiComponent.tv_total_invest.setText(getString(R.string.str_sum_week_invest) + "\n" + FormatUtils.format_1fra((double) (sum / 3600.0f)) + "h");
            this.uiComponent.tv_avg_invest.setText(getString(R.string.str_avg_week_invest) + "\n" + FormatUtils.format_1fra((double) ((sum / 3600.0f) / ((float) (startToToday + 1)))) + "h");
            return;
        }
        this.uiComponent.tv_total_invest.setText(getString(R.string.str_sum_week_invest) + "\n-");
        this.uiComponent.tv_avg_invest.setText(getString(R.string.str_avg_week_invest) + "\n-");
    }

    private float calcSumByLength(float[] arr, int length) {
        if (arr == null) {
            return 0.0f;
        }
        float sum = 0.0f;
        if (length > arr.length) {
            length = arr.length;
        }
        for (int i = 0; i < length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    private float[] calcSumValiNumByLength(float[] arr, int length) {
        if (arr == null) {
            return null;
        }
        float valiNum = 0.0f;
        float sum = 0.0f;
        if (length > arr.length) {
            length = arr.length;
        }
        for (int i = 0; i < length; i++) {
            sum += arr[i];
            if (arr[i] > 0.0f) {
                valiNum += 1.0f;
            }
        }
        return new float[]{sum, valiNum};
    }

    private void showWeekRate() {
        if (investBarValues != null) {
            float invest = 0.0f;
            for (float f : investOrignBarValues[1]) {
                invest += f;
            }
            if (invest > 100.0f) {
                String[] rateArr = Val.getWeeKNextRankNameAndTime(this.context, (int) invest);
                this.tv_statistic_rank_week.setVisibility(0);
                this.tv_statistic_rank_week.setText(rateArr[0] + getString(R.string.str_level));
                this.tv_statistic_rank_week.setTextColor(Integer.parseInt(rateArr[3]));
                this.tv_statistic_rank_week.setAnimation(getAnimation());
                return;
            }
            dontShowRate();
        }
    }

    private ScaleAnimation getAnimation() {
        ScaleAnimation animation = new ScaleAnimation(2.5f, 1.0f, 2.5f, 1.0f, 1, 0.5f, 1, 0.5f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(500);
        animation.setStartOffset(150);
        return animation;
    }

    private void dontShowRate() {
        this.tv_statistic_rank_week.setText("");
        this.tv_statistic_rank_week.setVisibility(8);
    }

    private void init() {
        this.context = this;
        getStartDateOfWeek();
        mCurrOverlapFactor = 1.0f;
        mCurrEasing = new QuintEaseOut();
        mCurrStartX = -1.0f;
        mCurrStartY = 0.0f;
        mCurrAlpha = -1;
        mOldOverlapFactor = 1.0f;
        mOldEasing = new QuintEaseOut();
        mOldStartX = -1.0f;
        mOldStartY = 0.0f;
        mOldAlpha = -1;
        initInvestBarDataSet();
        initLineChartDataSet();
    }

    private void initInvestBarDataSet() {
        float[][] lineValues = (float[][]) Array.newInstance(Float.TYPE, new int[]{2, 7});
        float[][] lineValues2 = (float[][]) Array.newInstance(Float.TYPE, new int[]{2, 7});
        int i = 0;
        int j = 0;
        int maxTake = 0;
        for (Entry<String, Integer> entry : DbUtils.getTypeXseries2(this.context, 10, new DateTimeBean(this.startDateOfWeek).getDiffBeforeWeeks(1)).entrySet()) {
            if (((Integer) entry.getValue()).intValue() > maxTake) {
                maxTake = ((Integer) entry.getValue()).intValue();
            }
            if (i > 6) {
                i = 0;
                j = 1;
            }
            float value = ((float) ((Integer) entry.getValue()).intValue()) / 3600.0f;
            lineValues2[j][i] = (float) ((Integer) entry.getValue()).intValue();
            float[] fArr = lineValues[j];
            if (value == 0.0f) {
                value = 0.01f;
            }
            fArr[i] = value;
            i++;
        }
        investBarValues = lineValues;
        investOrignBarValues = lineValues2;
        BAR_INVEST_MAX = (int) (((float) (maxTake + 1800)) / 3600.0f);
        if (BAR_INVEST_MAX <= 10) {
            BAR_INVEST_MAX = 10;
        }
        BAR_INVEST_STEP = BAR_INVEST_MAX / 3;
    }

    private void getStartDateOfWeek() {
        this.startDateOfWeek = getSharedPreferences(Val.CONFIGURE_NAME, 2).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2);
        if (this.startDateOfWeek == 1) {
            lineLabels = new String[]{"日", "一", "二", "三", "四", "五", "六"};
            barLabels = new String[]{"日", "一", "二", "三", "四", "五", "六"};
            investBarLabels = new String[]{"日", "一", "二", "三", "四", "五", "六"};
            return;
        }
        lineLabels = new String[]{"一", "二", "三", "四", "五", "六", "日"};
        barLabels = new String[]{"一", "二", "三", "四", "五", "六", "日"};
        investBarLabels = new String[]{"一", "二", "三", "四", "五", "六", "日"};
    }

    private void initLineChartDataSet() {
        int j;
        DateTimeBean dateBean = new DateTimeBean(this.startDateOfWeek);
        String startDateOfWeekAgo = dateBean.getStartDateBefordWeeks(1);
        String actId = "";
        if (actId == null || actId.length() == 0) {
            actId = DbUtils.queryActId(this.context, DbUtils.queryUserId(this.context), "30");
        }
        String startDate = startDateOfWeekAgo + " 00:00:00";
        TreeMap<String, DayInfo> dayMap = (TreeMap<String, DayInfo>)DateTime.beforeNDaysArr(DateTime.getTimeString(), dateBean.getDiffBeforeWeeks(1))[1];
        int maxTake = 0;
        Cursor cursor = DbUtils.getDb2(this.context).rawQuery("select stopTime,take from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and actId is " + actId + " and stopTime >= '" + startDate + "' and stopTime <= '" + (DateTime.getDateString() + " 23:59:59") + "' order by stopTime", null);
        if (cursor.getCount() > 0) {
            String tempDate = "";
            while (cursor.moveToNext()) {
                String startTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                int take = cursor.getInt(cursor.getColumnIndex("take"));
                j = startTime.indexOf(" ");
                if (j > 0) {
                    String date = startTime.substring(0, j);
                    DayInfo info;
                    int takeInfo;
                    if (!true) {
                        info = (DayInfo) dayMap.get(date);
                        if (info != null) {
                            takeInfo = info.getTake();
                            if (takeInfo >= 0 && take >= 0) {
                                int total = takeInfo + take;
                                info.setY(total);
                                info.setTime(DateTime.calculateTime5(this.context, (long) total));
                                info.setTake(total);
                                if (maxTake < takeInfo) {
                                    maxTake = takeInfo;
                                }
                            }
                        }
                    } else if (tempDate.equals(date)) {
                        info = (DayInfo) dayMap.get(date);
                        if (info != null) {
                            takeInfo = info.getTake();
                            if (takeInfo >= 0 && take >= 0) {
                                info.setTake(takeInfo + take);
                            }
                        }
                    } else {
                        tempDate = date;
                        info = (DayInfo) dayMap.get(date);
                        if (info != null) {
                            String time = startTime.substring(j + 1, startTime.length());
                            String[] timeArr = time.split(":");
                            info.setY(((Integer.parseInt(timeArr[0]) * 3600) + (Integer.parseInt(timeArr[1]) * 60)) + Integer.parseInt(timeArr[2]));
                            info.setTime(time.substring(0, time.lastIndexOf(":")));
                            info.setTake(take);
                            if (maxTake < take) {
                                maxTake = take;
                            }
                        }
                    }
                }
            }
        }
        DbUtils.close(cursor);
        float[][] lineValues = (float[][]) Array.newInstance(Float.TYPE, new int[]{2, 7});
        int i = 0;
        j = 0;
        for (Entry<String, DayInfo> entry : dayMap.entrySet()) {
            DayInfo dayInfo = (DayInfo) entry.getValue();
            Log.i("override", "j:" + j + ",,i:" + i + "dayInfo:" + dayInfo.toString());
            if (dayInfo.getY() > maxTake) {
                maxTake = dayInfo.getY();
            }
            if (i > 6) {
                i = 0;
                j = 1;
            }
            lineValues[j][i] = ((float) dayInfo.getY()) / 3600.0f;
            Log.i("override", "lineValues[j][i]:" + lineValues[j][i]);
            i++;
        }
        lineValues = (float[][]) Array.newInstance(Float.TYPE, new int[]{2, 7});
        lineValues[0] = lineValues[1];
        lineValues[1] = lineValues[0];
        LINE_MAX = (int) (((float) (maxTake + 3600)) / 3600.0f);
        if (LINE_MAX <= 10) {
            LINE_MAX = 10;
        }
        LINE_STEP = LINE_MAX / 3;
        initBarDataSet(dayMap);
    }

    private void initBarDataSet(TreeMap<String, DayInfo> dayMap) {
        float[][] lineValues = (float[][]) Array.newInstance(Float.TYPE, new int[]{2, 7});
        int i = 0;
        int j = 0;
        int maxTake = 0;
        for (Entry<String, DayInfo> entry : dayMap.entrySet()) {
            DayInfo dayInfo = (DayInfo) entry.getValue();
            if (dayInfo.getY() > maxTake) {
                maxTake = dayInfo.getTake();
            }
            if (i > 6) {
                i = 0;
                j = 1;
            }
            lineValues[j][i] = ((float) dayInfo.getTake()) / 3600.0f;
            i++;
        }
        barValues = lineValues[1];
        BAR_MAX = (int) (((float) (maxTake + 3600)) / 3600.0f);
        if (BAR_MAX <= 10) {
            BAR_MAX = 10;
        }
        BAR_STEP = BAR_MAX / 3;
    }

    private void initLineChart() {
        mLineChart = (LineChartView) findViewById(R.id.linechart);
        mLineChart.setOnEntryClickListener(this.lineEntryListener);
        mLineChart.setOnClickListener(this.lineClickListener);
        this.mLineGridPaint = new Paint();
        this.mLineGridPaint.setColor(getResources().getColor(R.color.black_tran_tw));
        this.mLineGridPaint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0.0f));
        this.mLineGridPaint.setStyle(Style.STROKE);
        this.mLineGridPaint.setAntiAlias(true);
        this.mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(0.75f));
    }

    private void updateLineChart() {
        mLineChart.reset();
        LineSet dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues[0]);
        dataSet.setDots(true).setDotsColor(getResources().getColor(R.color.bg_blue1)).setDotsRadius(Tools.fromDpToPx(5.0f)).setDotsStrokeThickness(Tools.fromDpToPx(2.0f)).setDotsStrokeColor(getResources().getColor(R.color.white2)).setLineColor(getResources().getColor(R.color.white2)).setLineThickness(Tools.fromDpToPx(3.0f)).beginAt(0).endAt(new DateTimeBean(this.startDateOfWeek).diffToStart + 1);
        mLineChart.addData((ChartSet) dataSet);
        dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues[1]);
        dataSet.setLineColor(getResources().getColor(R.color.white2)).setLineThickness(Tools.fromDpToPx(3.0f)).setSmooth(true).setDashed(true);
        mLineChart.addData((ChartSet) dataSet);
        mLineChart.setBorderSpacing(Tools.fromDpToPx(4.0f)).setGrid(GridType.HORIZONTAL, this.mLineGridPaint).setXAxis(false).setXLabels(LabelPosition.OUTSIDE).setYAxis(false).setYLabels(LabelPosition.OUTSIDE).setAxisBorderValues(LINE_MIN, LINE_MAX, LINE_STEP).setLabelsFormat(new DecimalFormat("##':00'")).show(getAnimation(true));
        mLineChart.animateSet(1, new DashAnimation());
    }

    @SuppressLint({"NewApi"})
    private void showLineTooltip(int setIndex, int entryIndex, Rect rect) {
        this.mLineTooltip = (TextView) getLayoutInflater().inflate(R.layout.circular_tooltip, null);
        this.mLineTooltip.setText(DateTime.calculateTime9(lineValues[setIndex][entryIndex]));
        LayoutParams layoutParams = new LayoutParams((int) Tools.fromDpToPx(35.0f), (int) Tools.fromDpToPx(35.0f));
        layoutParams.leftMargin = rect.centerX() - (layoutParams.width / 2);
        layoutParams.topMargin = rect.centerY() - (layoutParams.height / 2);
        this.mLineTooltip.setLayoutParams(layoutParams);
        if (VERSION.SDK_INT >= 12) {
            this.mLineTooltip.setPivotX((float) (layoutParams.width / 2));
            this.mLineTooltip.setPivotY((float) (layoutParams.height / 2));
            this.mLineTooltip.setAlpha(0.0f);
            this.mLineTooltip.setScaleX(0.0f);
            this.mLineTooltip.setScaleY(0.0f);
            this.mLineTooltip.animate().setDuration(150).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).rotation(360.0f).setInterpolator(this.enterInterpolator);
        }
        mLineChart.showTooltip(this.mLineTooltip);
    }

    @SuppressLint({"NewApi"})
    private void dismissLineTooltip(final int setIndex, final int entryIndex, final Rect rect) {
        if (VERSION.SDK_INT >= 16) {
            this.mLineTooltip.animate().setDuration(100).scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setInterpolator(this.exitInterpolator).withEndAction(new Runnable() {
                public void run() {
                    DynamicActivity.mLineChart.removeView(DynamicActivity.this.mLineTooltip);
                    DynamicActivity.this.mLineTooltip = null;
                    if (entryIndex != -1) {
                        DynamicActivity.this.showLineTooltip(setIndex, entryIndex, rect);
                    }
                }
            });
            return;
        }
        mLineChart.dismissTooltip(this.mLineTooltip);
        this.mLineTooltip = null;
        if (entryIndex != -1) {
            showLineTooltip(setIndex, entryIndex, rect);
        }
    }

    private void updateValues(LineChartView chartView) {
        chartView.updateValues(0, lineValues[1]);
        chartView.updateValues(1, lineValues[0]);
        chartView.notifyDataUpdate();
    }

    private void initBarChart() {
        mBarChart = (BarChartView) findViewById(R.id.barchart);
        mBarChart.setOnEntryClickListener(this.barEntryListener);
        mBarChart.setOnClickListener(this.barClickListener);
        this.mBarGridPaint = new Paint();
        this.mBarGridPaint.setColor(getResources().getColor(R.color.black_tran_tw));
        this.mBarGridPaint.setStyle(Style.STROKE);
        this.mBarGridPaint.setAntiAlias(true);
        this.mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(0.75f));
    }

    private void updateBarChart() {
        mBarChart.reset();
        BarSet barSet = new BarSet();
        barSet.addBars(barLabels, barValues);
        barSet.setColor(getResources().getColor(R.color.bg_blue1));
        mBarChart.addData((ChartSet) barSet);
        Paint mBarGridPaint = new Paint();
        mBarGridPaint.setColor(getResources().getColor(R.color.black_tran_ts));
        mBarGridPaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 20.0f}, 0.0f));
        mBarGridPaint.setStyle(Style.STROKE);
        mBarGridPaint.setAntiAlias(true);
        mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(0.75f));
        mBarChart.setBarSpacing(Tools.fromDpToPx(35.0f));
        mBarChart.setSetSpacing(Tools.fromDpToPx(3.0f));
        mBarChart.setBorderSpacing(0.0f).setThresholdLine(8.0f, mBarGridPaint).setAxisBorderValues(BAR_MIN, BAR_MAX, BAR_STEP).setYAxis(true).setXAxis(true).setXLabels(LabelPosition.OUTSIDE).setYLabels(LabelPosition.OUTSIDE).show(getAnimation(true));
    }

    @SuppressLint({"NewApi"})
    private void showBarTooltip(int setIndex, int entryIndex, Rect rect) {
        this.mBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.bar_tooltip, null);
        this.mBarTooltip.setText(FormatUtils.format_1fra((double) barValues[entryIndex]) + "H");
        LayoutParams layoutParams = new LayoutParams(rect.width(), rect.height());
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        this.mBarTooltip.setLayoutParams(layoutParams);
        if (VERSION.SDK_INT >= 12) {
            this.mBarTooltip.setAlpha(0.0f);
            this.mBarTooltip.setScaleY(0.0f);
            this.mBarTooltip.animate().setDuration(200).alpha(1.0f).scaleY(1.0f).setInterpolator(this.enterInterpolator);
        }
        mBarChart.showTooltip(this.mBarTooltip);
    }

    @SuppressLint({"NewApi"})
    private void dismissBarTooltip(final int setIndex, final int entryIndex, final Rect rect) {
        if (VERSION.SDK_INT >= 16) {
            this.mBarTooltip.animate().setDuration(100).scaleY(0.0f).alpha(0.0f).setInterpolator(this.exitInterpolator).withEndAction(new Runnable() {
                public void run() {
                    DynamicActivity.mBarChart.removeView(DynamicActivity.this.mBarTooltip);
                    DynamicActivity.this.mBarTooltip = null;
                    if (entryIndex != -1) {
                        DynamicActivity.this.showBarTooltip(setIndex, entryIndex, rect);
                    }
                }
            });
            return;
        }
        mBarChart.dismissTooltip(this.mBarTooltip);
        this.mBarTooltip = null;
        if (entryIndex != -1) {
            showBarTooltip(setIndex, entryIndex, rect);
        }
    }

    private void initInvestBarChart() {
        mInvestBarChart = (BarChartView) findViewById(R.id.barInvestchart);
        mInvestBarChart.setOnEntryClickListener(this.investBarEntryListener);
        mInvestBarChart.setOnClickListener(this.investBarClickListener);
    }

    private void updateInvestBarChart() {
        mInvestBarChart.reset();
        BarSet barSet = new BarSet();
        barSet.addBars(investBarLabels, investBarValues[0]);
        barSet.setColor(getResources().getColor(R.color.bg_blue_100));
        mInvestBarChart.addData((ChartSet) barSet);
        barSet = new BarSet();
        barSet.addBars(investBarLabels, investBarValues[1]);
        barSet.setColor(getResources().getColor(R.color.bg_blue1));
        mInvestBarChart.addData((ChartSet) barSet);
        Paint mBarGridPaint = new Paint();
        mBarGridPaint.setColor(getResources().getColor(R.color.black_tran_ts));
        mBarGridPaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 20.0f}, 0.0f));
        mBarGridPaint.setStyle(Style.STROKE);
        mBarGridPaint.setAntiAlias(true);
        mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(0.75f));
        mInvestBarChart.setSetSpacing(Tools.fromDpToPx(3.0f));
        mInvestBarChart.setBarSpacing(Tools.fromDpToPx(25.0f));
        mInvestBarChart.setBorderSpacing(0.0f).setThresholdLine(8.0f, mBarGridPaint).setAxisBorderValues(BAR_INVEST_MIN, BAR_INVEST_MAX, BAR_INVEST_STEP).setYAxis(false).setXAxis(true).setXLabels(LabelPosition.OUTSIDE).setYLabels(LabelPosition.NONE).show(getAnimation(true));
    }

    private String showStringInvest(float value) {
        return FormatUtils.format_1fra((double) value) + "\nH";
    }

    @SuppressLint({"NewApi"})
    private void showInvestBarTooltip(int setIndex, int entryIndex, Rect rect) {
        this.mInvestBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.bar_tooltip, null);
        this.mInvestBarTooltip.setText(showStringInvest(investBarValues[setIndex][entryIndex]));
        LayoutParams layoutParams = new LayoutParams(rect.width(), rect.height());
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        this.mInvestBarTooltip.setLayoutParams(layoutParams);
        if (VERSION.SDK_INT >= 12) {
            this.mInvestBarTooltip.setAlpha(0.0f);
            this.mInvestBarTooltip.setScaleY(0.0f);
            this.mInvestBarTooltip.animate().setDuration(200).alpha(1.0f).scaleY(1.0f).setInterpolator(this.enterInterpolator);
        }
        mInvestBarChart.showTooltip(this.mInvestBarTooltip);
    }

    @SuppressLint({"NewApi"})
    private void dismissInvestBarTooltip(final int setIndex, final int entryIndex, final Rect rect) {
        if (VERSION.SDK_INT >= 16) {
            this.mInvestBarTooltip.animate().setDuration(100).scaleY(0.0f).alpha(0.0f).setInterpolator(this.exitInterpolator).withEndAction(new Runnable() {
                public void run() {
                    DynamicActivity.mInvestBarChart.removeView(DynamicActivity.this.mInvestBarTooltip);
                    DynamicActivity.this.mInvestBarTooltip = null;
                    if (entryIndex != -1) {
                        DynamicActivity.this.showInvestBarTooltip(setIndex, entryIndex, rect);
                    }
                }
            });
            return;
        }
        mInvestBarChart.dismissTooltip(this.mInvestBarTooltip);
        this.mInvestBarTooltip = null;
        if (entryIndex != -1) {
            showInvestBarTooltip(setIndex, entryIndex, rect);
        }
    }

    private void setOverlap(int index) {
        switch (index) {
            case 0:
                mCurrOverlapFactor = 1.0f;
                mCurrOverlapOrder = beginOrder;
                mOrderBtn.setImageResource(R.drawable.ordere);
                return;
            case 1:
                mCurrOverlapFactor = 0.5f;
                mCurrOverlapOrder = beginOrder;
                mOrderBtn.setImageResource(R.drawable.orderf);
                return;
            case 2:
                mCurrOverlapFactor = 0.5f;
                mCurrOverlapOrder = endOrder;
                mOrderBtn.setImageResource(R.drawable.orderl);
                return;
            case 3:
                mCurrOverlapFactor = 0.5f;
                mCurrOverlapOrder = middleOrder;
                mOrderBtn.setImageResource(R.drawable.orderm);
                return;
            default:
                return;
        }
    }

    @SuppressLint({"NewApi"})
    private void setAlpha(int index) {
        switch (index) {
            case 0:
                mCurrAlpha = -1;
                if (VERSION.SDK_INT >= 16) {
                    mAlphaBtn.setImageAlpha(255);
                    return;
                } else if (VERSION.SDK_INT >= 11) {
                    mAlphaBtn.setAlpha(1.0f);
                    return;
                } else {
                    return;
                }
            case 1:
                mCurrAlpha = 2;
                if (VERSION.SDK_INT >= 16) {
                    mAlphaBtn.setImageAlpha(115);
                    return;
                } else if (VERSION.SDK_INT >= 11) {
                    mAlphaBtn.setAlpha(0.6f);
                    return;
                } else {
                    return;
                }
            case 2:
                mCurrAlpha = 1;
                if (VERSION.SDK_INT >= 16) {
                    mAlphaBtn.setImageAlpha(55);
                    return;
                } else if (VERSION.SDK_INT >= 11) {
                    mAlphaBtn.setAlpha(0.3f);
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    private Animation getAnimation(boolean newAnim) {
        if (newAnim) {
            return new Animation().setAlpha(mCurrAlpha).setEasing(mCurrEasing).setOverlap(mCurrOverlapFactor, mCurrOverlapOrder).setStartPoint(mCurrStartX, mCurrStartY);
        }
        return new Animation().setAlpha(mOldAlpha).setEasing(mOldEasing).setOverlap(mOldOverlapFactor, mOldOverlapOrder).setStartPoint(mOldStartX, mOldStartY);
    }
}
