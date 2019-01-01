package com.record.myLife.goal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qq.e.comm.constants.ErrorCode.InitError;
import com.record.bean.DayInfo;
import com.record.bean.User;
import com.record.bean.XYColumn;
import com.record.myLife.R;
import com.record.myLife.view.AbstractDemoChart;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.LeftRightView;
import com.record.myLife.view.MyGoalItemsLayout;
import com.record.myLife.view.MyGoalItemsLayout.MyOnItemsClickListener;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.DensityUtil;
import com.record.utils.GeneralHelper;
import com.record.utils.PreferUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.share.QuickShareUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
//import org.achartengine.ChartFactory;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class GetUpActivity extends AbstractDemoChart {
    public static HashMap<Integer, Integer> Act2TypeMap;
    AnimationController animControl;
    String checkActId = "";
    MyOnItemsClickListener goalItemsIdClickLister = new MyOnItemsClickListener() {
        public void onClick(View v, String id) {
            GetUpActivity.this.checkActId = id;
            AbstractDemoChart.log("checkActId:" + GetUpActivity.this.checkActId);
        }
    };
    private LayoutInflater inflater;
    boolean isSleepType = true;
    private Button iv_getup_share;
    int maxTake = 0;
    OnClickListener myListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_getup_share) {
                new QuickShareUtil(AbstractDemoChart.context).SceenCutAndShare(DbUtils.queryWeibo(AbstractDemoChart.context, User.getInstance().getUserId() + "", "5", "1"));
            }
            if (R.id.tv_getup_title == id) {
                GetUpActivity.this.showSelectActDialog();
            }
        }
    };
    private RelativeLayout rl_getup_getup;
    private LinearLayout rl_getup_time_of_sleep;
    String selectEndDate = "";
    String selectStartDate = "";
    String sleepActId;
    HashMap<Integer, TempActBean> tempActMap = null;
    private TextView tv_getup_title;
    public LeftRightView v_left;
    public LeftRightView v_right;

    class TempActBean {
        String actName;
        String color;
        int id;
        String image;

        public TempActBean(int id, String actName, String image, String color) {
            this.id = id;
            this.actName = actName;
            this.image = image;
            this.color = color;
        }

        public int getId() {
            return this.id;
        }

        public String getActName() {
            return this.actName;
        }

        public String getImage() {
            return this.image;
        }

        public String getColor() {
            return this.color;
        }
    }

    public static void startActivity(Context context, String actId) {
        Intent it = new Intent(context, GetUpActivity.class);
        it.putExtra("actId", actId);
        context.startActivity(it);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getup);
        init();
        initView();
        SystemBarTintManager.setMIUIbar(this);
    }

    private void init() {
        this.inflater = getLayoutInflater();
        this.animControl = new AnimationController();
        this.sleepActId = DbUtils.queryActId(context, DbUtils.queryUserId(context), "30");
        String actId = getIntent().getStringExtra("actId");
        if (actId == null && actId.length() == 0) {
            ToastUtils.toastShort(context, "出错啦！");
            onBackPressed();
            return;
        }
        this.sleepActId = actId;
    }

    private void initView() {
        this.rl_getup_getup = (RelativeLayout) findViewById(R.id.rl_getup_getup);
        this.rl_getup_time_of_sleep = (LinearLayout) findViewById(R.id.rl_getup_time_of_sleep);
        this.iv_getup_share = (Button) findViewById(R.id.iv_getup_share);
        this.tv_getup_title = (TextView) findViewById(R.id.tv_getup_title);
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GetUpActivity.this.onBackPressed();
            }
        });
        this.iv_getup_share.setOnClickListener(this.myListener);
        this.tv_getup_title.setOnClickListener(this.myListener);
        this.v_left = (LeftRightView) findViewById(R.id.v_left);
        this.v_left.setType(1);
        this.v_right = (LeftRightView) findViewById(R.id.v_right);
        OnClickListener preListener = new OnClickListener() {
            public void onClick(View arg0) {
                GetUpActivity.this.getMonthGetUpTime(GetUpActivity.this.sleepActId, GetUpActivity.this.selectStartDate, true, GetUpActivity.this.isSleepType);
            }
        };
        OnClickListener nextListener = new OnClickListener() {
            public void onClick(View arg0) {
                GetUpActivity.this.getMonthGetUpTime(GetUpActivity.this.sleepActId, GetUpActivity.this.selectEndDate, false, GetUpActivity.this.isSleepType);
            }
        };
        this.v_left.setOnClickListenerLeft(preListener);
        this.v_left.setOnClickListenerRight(nextListener);
        this.v_right.setOnClickListenerLeft(preListener);
        this.v_right.setOnClickListenerRight(nextListener);
        this.v_left.setOnClickListenerIv(new OnClickListener() {
            public void onClick(View arg0) {
                GetUpActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_STATICS_BUTTON_LOCATION, 2).commit();
                GetUpActivity.this.showRight(true);
            }
        });
        this.v_right.setOnClickListenerIv(new OnClickListener() {
            public void onClick(View arg0) {
                GetUpActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_STATICS_BUTTON_LOCATION, 1).commit();
                GetUpActivity.this.showLeft(true);
            }
        });
        if (PreferUtils.getSP(context).getInt(Val.CONFIGURE_STATICS_BUTTON_LOCATION, 2) == 1) {
            showLeft(false);
        } else {
            showRight(false);
        }
        if (DbUtils.queryActTypeById(context, this.sleepActId).intValue() == 30) {
            this.isSleepType = true;
        } else {
            this.isSleepType = false;
        }
        this.tv_getup_title.setText(DbUtils.getActNameById(context, Integer.parseInt(this.sleepActId)));
        getMonthGetUpTime(this.sleepActId, DateTime.getTimeString(), true, this.isSleepType);
    }

    private void showLeft(boolean click) {
        this.v_left.setVisibility(View.VISIBLE);
        if (click) {
            this.animControl.fadeIn(this.v_left, 500, 0);
        }
        this.v_right.setVisibility(View.INVISIBLE);
        if (click) {
            this.animControl.fadeOut(this.v_right, 500, 0);
        }
    }

    private void showRight(boolean click) {
        this.v_right.setVisibility(View.VISIBLE);
        if (click) {
            this.animControl.fadeIn(this.v_left, 500, 0);
        }
        this.v_left.setVisibility(View.INVISIBLE);
        if (click) {
            this.animControl.fadeOut(this.v_left, 500, 0);
        }
    }

    public void getMonthGetUpTime(String actId, String start, boolean before, boolean isSleepType) {
        Object[] objArr;
        DayInfo info;
        if (actId == null || actId.length() == 0) {
            actId = DbUtils.queryActId(context, DbUtils.queryUserId(context), "30");
        }
        this.rl_getup_getup.removeAllViews();
        this.rl_getup_time_of_sleep.removeAllViews();
        if (before) {
            objArr = DateTime.beforeNDaysArr(start, 15);
        } else {
            objArr = DateTime.afterNDaysArr(start, 15);
        }
        Set<String> daySet = (Set<String>)objArr[0];
        Map<String, DayInfo> dayMap = (Map<String, DayInfo>)objArr[1];
        String startDate = ((String) daySet.iterator().next()) + " 00:00:00";
        String endDate = "";
        for (String endDate2 : daySet) {
            endDate2 = endDate2 + " 23:59:59";
            AbstractDemoChart.log("startDate" + startDate + ",endDate" + endDate2);
            this.selectStartDate = startDate;
            this.selectEndDate = endDate2;
            this.maxTake = 0;
            Cursor cursor = DbUtils.getDb2(context).rawQuery("select stopTime,take from t_act_item where " + DbUtils.getWhereUserId(context) + " and actId is " + actId + " and stopTime >= '" + startDate + "' and stopTime <= '" + endDate2 + "' order by stopTime", null);
            if (cursor.getCount() > 0) {
                String tempDate = "";
                while (cursor.moveToNext()) {
                    String startTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                    int take = cursor.getInt(cursor.getColumnIndex("take"));
                    int j = startTime.indexOf(" ");
                    if (j > 0) {
                        String date = startTime.substring(0, j);
                        int takeInfo;
                        if (!isSleepType) {
                            info = (DayInfo) dayMap.get(date);
                            if (info != null) {
                                takeInfo = info.getTake();
                                if (takeInfo >= 0 && take >= 0) {
                                    int total = takeInfo + take;
                                    info.setY(total);
                                    info.setTime(DateTime.calculateTime5(context, (long) total));
                                    info.setTake(total);
                                    if (this.maxTake < takeInfo) {
                                        this.maxTake = takeInfo;
                                    }
                                }
                            }
                        } else if (tempDate.equals(date)) {
                            info = (DayInfo) dayMap.get(date);
                            takeInfo = info.getTake();
                            if (takeInfo >= 0 && take >= 0) {
                                info.setTake(takeInfo + take);
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
                                if (this.maxTake < take) {
                                    this.maxTake = take;
                                }
                            }
                        }
                    }
                }
            }
            DbUtils.close(cursor);
        }

        AbstractDemoChart.log("得到其子目标，将时间投入时间相加");
        ArrayList<Integer> subGoalIdArr = DbUtils.querySubGoalIdByBigGoalIdContainDelte(context, Integer.parseInt(actId));
        if (subGoalIdArr != null) {
            Iterator it = subGoalIdArr.iterator();
            while (it.hasNext()) {
                dayMap = addSubGoalToBigGoal(dayMap, DbUtils.getXseries2(context, ((Integer) it.next()).intValue() + "", start, before));
            }
        }
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        int type = DbUtils.queryActTypeById(context, this.sleepActId).intValue();
        if (type == 10 || type == 11) {
            r.setColor(getResources().getColor(R.color.bg_green1));
        } else if (type == 20) {
            r.setColor(getResources().getColor(R.color.bg_yellow1));
        } else if (type == 30) {
            r.setColor(getResources().getColor(R.color.bg_blue1));
        } else if (type == 40) {
            r.setColor(getResources().getColor(R.color.bg_red1));
        }
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        r.setLineWidth((float) DensityUtil.dip2px(context, 1.0f));
        renderer.addSeriesRenderer(r);
        renderer.setBackgroundColor(getResources().getColor(R.color.gray));
        renderer.setXAxisMax((double) (daySet.size() + 1));
        renderer.setZoomEnabled(false);
        renderer.setMarginsColor(getResources().getColor(R.color.gray));
        renderer.setPanEnabled(true, false);
        renderer.setAxesColor(getResources().getColor(R.color.black_tran_es));
        renderer.setLabelsColor(getResources().getColor(R.color.black_tran_es));
        renderer.setShowLegend(false);
        renderer.setLabelsTextSize((float) DensityUtil.dip2px(context, 9.0f));
        renderer.setXAxisMin(0.0d);
        renderer.setGridColor(getResources().getColor(R.color.black_tran_es));
        renderer.setShowGrid(true);
        renderer.setShowGridX(true);
        renderer.setXLabels(0);
        renderer.setYLabels(0);
        renderer.setYLabelsAlign(Align.CENTER);
        renderer.setYLabelsPadding((float) DensityUtil.dip2px(context, 9.0f));
        renderer.setLabelsColor(getResources().getColor(R.color.black_tran_es));
        renderer.setAntialiasing(true);
        renderer.setClickEnabled(true);
        renderer.setPointSize((float) DensityUtil.dip2px(context, 5.0f));
        renderer.setShowAxes(false);
        XYSeries xySeries = new XYSeries("起床");
        double temMax = 0.0d;
        double temMin = 0.0d;
        for (String day : daySet) {
            info = (DayInfo) dayMap.get(day);
            if (info.getY() > 0) {
                if (temMin == 0.0d) {
                    temMin = (double) info.getY();
                }
                temMax = Math.max((double) info.getY(), temMax);
                temMin = Math.min((double) info.getY(), temMin);
                xySeries.add((double) info.getX(), (double) info.getY());
            }
            renderer.addXTextLabel((double) info.getX(), info.getDayOfMonth());
        }
        renderer = setYTextLabel(renderer, ((int) temMax) + InitError.INIT_AD_ERROR, (int) (temMin - 300.0d > 0.0d ? temMin - 300.0d : 0.0d), isSleepType);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(xySeries);
//        this.rl_getup_getup.addView(ChartFactory.getCubeLineChartView(context, dataset, renderer, 0.0f));
        try {
            setTakeOfSleep(daySet, dayMap, this.maxTake, isSleepType);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private Map<String, DayInfo> addSubGoalToBigGoal(Map<String, DayInfo> dayMap, ArrayList<XYColumn> sub) {
        Iterator it = sub.iterator();
        while (it.hasNext()) {
            XYColumn xy = (XYColumn) it.next();
            DayInfo info = (DayInfo) dayMap.get(xy.getValue());
            if (info != null) {
                info.setY(xy.getY() + info.getY());
                info.setTake(xy.getY() + info.getTake());
                if (info.getTake() > this.maxTake) {
                    this.maxTake = info.getTake();
                }
            }
        }
        return dayMap;
    }

    public void setTakeOfSleep(Set<String> daySet, Map<String, DayInfo> dayMap, int max, boolean isSleepType) {
        String day;
        TextView tv2 = (TextView) this.inflater.inflate(R.layout.template_text, null);
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.topMargin = DensityUtil.dip2px(context, 16.0f);
        layoutParams.bottomMargin = DensityUtil.dip2px(context, 16.0f);
        tv2.setLayoutParams(layoutParams);
        if (isSleepType) {
            tv2.setText(getResources().getString(R.string.str_getUp_time));
        } else {
            tv2.setText(getResources().getString(R.string.str_curve_bar_graph));
        }
        tv2.setTextColor(getResources().getColor(R.color.black_tran_es));
        this.rl_getup_time_of_sleep.addView(tv2);
        String[] dateArr = new String[daySet.size()];
        int i = 0;
        for (String day2 : daySet) {
            dateArr[i] = day2;
            i++;
            int length = dateArr.length;
            int tempInt = -1;
            int j = 0;
            while (j < length) {
                try {
                    day2 = dateArr[(length - 1) - j];
                    DayInfo info = (DayInfo) dayMap.get(day2);
                    RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_progressbar_blue, null);
                    ProgressBar pb_tem_blue_sleep = (ProgressBar) rl.findViewById(R.id.pb_tem_blue_sleep);
                    TextView tv_tem_blue_sleep = (TextView) rl.findViewById(R.id.tv_tem_blue_sleep);
                    TextView tv_tem_blue_date = (TextView) rl.findViewById(R.id.tv_tem_blue_date);
                    int type = DbUtils.queryActTypeById(context, this.sleepActId).intValue();
                    if (type == 10 || type == 11) {
                        pb_tem_blue_sleep.setProgressDrawable(getResources().getDrawable(R.drawable.pb_grass_v2));
                    } else if (type == 20) {
                        pb_tem_blue_sleep.setProgressDrawable(getResources().getDrawable(R.drawable.pb_yellow_v1));
                    } else if (type == 30) {
                        pb_tem_blue_sleep.setProgressDrawable(getResources().getDrawable(R.drawable.pb_water_v3));
                    } else if (type == 40) {
                        pb_tem_blue_sleep.setProgressDrawable(getResources().getDrawable(R.drawable.pb_red_v2));
                    }
                    pb_tem_blue_sleep.setMax(max + 1800);
                    pb_tem_blue_sleep.setProgress(info.getTake());
                    if (info.getTake() == 0) {
                        tv_tem_blue_sleep.setText(getString(R.string.str_no_record));
                    } else {
                        tv_tem_blue_sleep.setText(DateTime.calculateTime3((long) info.getTake()));
                    }
                    if (info.getDayOfMonth().contains("01") || j == tempInt) {
                        if (tempInt < 0) {
                            tempInt = j + 1;
                        }
                        tv_tem_blue_date.setText(DateTime.getMonth2(DateTime.pars2Calender(day2 + " 00:00:00")) + " " + info.getDayOfMonth() + " " + info.getDayOfWeek());
                    } else if (j == 0) {
                        tv_tem_blue_date.setText(DateTime.getMonth2(DateTime.pars2Calender(day2 + " 00:00:00")) + " " + info.getDayOfMonth() + " " + info.getDayOfWeek());
                    } else {
                        tv_tem_blue_date.setText(info.getDayOfMonth() + " " + info.getDayOfWeek());
                    }
                    this.rl_getup_time_of_sleep.addView(rl);
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                j++;
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private XYMultipleSeriesRenderer setYTextLabel(XYMultipleSeriesRenderer renderer, int max, int min, boolean isSleepType) {
        int mid = (max - min) / 3;
        int max1 = (max / 900) * 900;
        int mid2 = (((mid * 2) + min) / 900) * 900;
        int mid1 = ((min + mid) / 900) * 900;
        int min1 = (min / 900) * 900;
        if (isSleepType) {
            renderer.addYTextLabel((double) max1, DateTime.calculateTime4((long) max1));
            renderer.addYTextLabel((double) mid2, DateTime.calculateTime4((long) mid2));
            renderer.addYTextLabel((double) mid1, DateTime.calculateTime4((long) mid1));
            renderer.addYTextLabel((double) min1, DateTime.calculateTime4((long) min1));
        } else {
            renderer.addYTextLabel((double) max1, DateTime.calculateTime10((long) max1));
            renderer.addYTextLabel((double) mid2, DateTime.calculateTime10((long) mid2));
            renderer.addYTextLabel((double) mid1, DateTime.calculateTime10((long) mid1));
            renderer.addYTextLabel((double) min1, DateTime.calculateTime10((long) min1));
        }
        renderer.setYAxisMax((double) (max1 + 900));
        renderer.setYAxisMin((double) (min1 - 900));
        return renderer;
    }

    private void showSelectActDialog() {
        View hs = (HorizontalScrollView) this.inflater.inflate(R.layout.tem_horizontal, null);
        LinearLayout ll = new MyGoalItemsLayout((Activity) context, (LinearLayout) hs.findViewById(R.id.ll_tem_time_items), this.goalItemsIdClickLister).getAddItems();
        new Builder(context).setTitle(getResources().getString(R.string.str_change_type)).setView(hs).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (GetUpActivity.this.updateUiByActId()) {
                    dialog.cancel();
                }
            }
        }).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private boolean updateUiByActId() {
        if (this.checkActId == null || this.checkActId.length() == 0) {
            GeneralHelper.toastShort(context, getResources().getString(R.string.str_please_choose));
            return false;
        }
        if (DbUtils.queryActTypeById(context, this.checkActId).intValue() == 30) {
            this.isSleepType = true;
        } else {
            this.isSleepType = false;
        }
        this.sleepActId = this.checkActId;
        getMonthGetUpTime(this.checkActId, this.selectEndDate, true, this.isSleepType);
        this.tv_getup_title.setText(DbUtils.getActNameById(context, Integer.parseInt(this.checkActId)));
        this.checkActId = "";
        return true;
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

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.str_change_type));
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.str_change_type))) {
            showSelectActDialog();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
