package com.record.myLife.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.record.bean.Act2;
import com.record.bean.Record2;
import com.record.bean.TimePieRecord;
import com.record.bean.User;
import com.record.bean.net.RankBean;
import com.record.bean.net.ResponseBean;
import com.record.conts.Sofeware;
import com.record.myLife.BaseApplication;
import com.record.myLife.IActivity;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.AddNoteActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.DayPie;
import com.record.myLife.view.DotsView;
import com.record.myLife.view.LeftRightView;
import com.record.myLife.view.MyCalendarActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.task.BaseTask;
import com.record.utils.AndroidUtils;
import com.record.utils.DateTime;
import com.record.utils.DensityUtil;
import com.record.utils.FormatUtils;
import com.record.utils.LogUtils;
import com.record.utils.NetUtils;
import com.record.utils.PreferUtils;
import com.record.utils.PushInitUtils;
import com.record.utils.ShowGuideImgUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.share.QuickShareUtil;
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
import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class StatisticActivity extends BaseActivity implements IActivity {
    public static int DOUGHTYPE_DAY = 1;
    public static int DOUGHTYPE_MONTH = 3;
    public static int DOUGHTYPE_WEEK = 2;
    private int ACTIVITY_FLAG = ((int) System.currentTimeMillis());
    int DoughType = 1;
    int activity_flag = -1;
    AnimationController animControl;
    View btn_statistic_calendar;
    Button btn_statistic_change;
    Context context;
    DotsView dv_dots;
    LayoutInflater inflater;
    Button iv_statistic_share2;
    String lastTempDate = "";
    LinearLayout ll_tem_statistic;
    ListView lv_statistic_items;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_statistic_share || id == R.id.iv_statistic_share2) {
                new QuickShareUtil(StatisticActivity.this.context).SceenCutAndShare(DbUtils.queryWeibo(StatisticActivity.this.context, User.getInstance().getUserId() + "", "4", "1"));
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_share_activate_btn");
            } else if (id == R.id.tv_statistic_unit) {
                SharedPreferences sp = StatisticActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0);
                int unit = sp.getInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0);
                if (unit == 0) {
                    sp.edit().putInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 1).commit();
                    StatisticActivity.this.tv_statistic_unit.setText(StatisticActivity.this.getResources().getString(R.string.str_uint) + ":H");
                } else if (unit == 1) {
                    sp.edit().putInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0).commit();
                    StatisticActivity.this.tv_statistic_unit.setText(StatisticActivity.this.getResources().getString(R.string.str_uint) + ":%");
                }
                if (StatisticActivity.this.DoughType == StatisticActivity.DOUGHTYPE_WEEK || StatisticActivity.this.DoughType == StatisticActivity.DOUGHTYPE_MONTH) {
                    StatisticActivity.this.initAllocationUI_v4(StatisticActivity.this.tempDate, 2, StatisticActivity.this.DoughType);
                } else if (StatisticActivity.this.DoughType == StatisticActivity.DOUGHTYPE_DAY) {
                    StatisticActivity.this.initAllocationUI_v3(StatisticActivity.this.tempDate, 2);
                }
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_unit_btn");
            } else if (id == R.id.btn_statistic_change) {
                new Builder(StatisticActivity.this.context).setTitle(StatisticActivity.this.getResources().getString(R.string.str_choose_type)).setNegativeButton(StatisticActivity.this.getResources().getString(R.string.str_add_morningVoice), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StatisticActivity.this.showMorningVoice();
                        dialog.cancel();
                    }
                }).setPositiveButton(StatisticActivity.this.getResources().getString(R.string.str_add_summarize), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StatisticActivity.this.showSummarize();
                        dialog.cancel();
                    }
                }).create().show();
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_select_top_left_btn");
            } else if (id == R.id.btn_statistic_calendar) {
                MyCalendarActivity.startActivity((Activity) StatisticActivity.this.context, StatisticActivity.this.tempDate);
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_calendar_btn");
            } else if (id == R.id.tv_statistic_day) {
                if (StatisticActivity.this.lastTempDate != null && StatisticActivity.this.lastTempDate.length() > 0) {
                    StatisticActivity.this.tempDate = StatisticActivity.this.lastTempDate;
                }
                StatisticActivity.this.updateUiByType2(StatisticActivity.DOUGHTYPE_DAY);
            } else if (id == R.id.tv_statistic_week) {
                if (StatisticActivity.this.lastTempDate != null && StatisticActivity.this.lastTempDate.length() > 0) {
                    StatisticActivity.this.tempDate = StatisticActivity.this.lastTempDate;
                }
                StatisticActivity.this.updateUiByType2(StatisticActivity.DOUGHTYPE_WEEK);
            } else if (id == R.id.tv_statistic_month) {
                if (StatisticActivity.this.lastTempDate != null && StatisticActivity.this.lastTempDate.length() > 0) {
                    StatisticActivity.this.tempDate = StatisticActivity.this.lastTempDate;
                }
                StatisticActivity.this.updateUiByType2(StatisticActivity.DOUGHTYPE_MONTH);
            }
        }
    };
    RankBean rankBean = null;
    RelativeLayout rl_statistic_show;
    RelativeLayout rl_tem_statistic;
    ScrollView sv_today_items;
    String tempDate = "";
    TextView tv_date_title;
    TextView tv_statistic_day;
    TextView tv_statistic_month;
    TextView tv_statistic_more;
    TextView tv_statistic_rank;
    TextView tv_statistic_rank_week;
    TextView tv_statistic_title;
    TextView tv_statistic_today;
    TextView tv_statistic_unit;
    TextView tv_statistic_week;
    UiComponent uiComponent;
    ViewPager vp_chart;

    class UiComponent {
        public LinearLayout ll_rank;
        public TextView tv_rank;
        public LeftRightView v_left;
        public LeftRightView v_right;

        UiComponent() {
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private int count = 2;
        private String currentDate;

        public MyPagerAdapter(String date) {
            this.currentDate = date;
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        public int getCount() {
            return this.count;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if (position == 0) {
                view = StatisticActivity.this.getPie1(this.currentDate);
            } else {
                view = StatisticActivity.this.getTimePie(this.currentDate);
            }
            container.addView(view);
            return view;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
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
        if (sp.getInt(Val.CONFIGURE_STATICS_BUTTON_LOCATION, 2) == 1) {
            showLeft(false);
        } else {
            showRight(false);
        }
        if (NetUtils.isNetworkAvailable2noToast(this.context)) {
            requestTodayRank();
        }
    }

    public void init() {
        this.context = this;
        this.animControl = new AnimationController();
        this.inflater = getLayoutInflater();
        this.tempDate = DateTime.getDateString();
        this.lastTempDate = this.tempDate;
        this.activity_flag = (int) System.currentTimeMillis();
        ShowGuideImgUtils.showImage(this.context, Val.CONFIGURE_IS_SHOW_GUIDE_STATICS, 1, R.drawable.guide_statics);
    }

    private void requestTodayRank() {
        try {
            String today = DateTime.getDateString();
            int invest = DbUtils.queryAllocationInvest(this.context, today);
            JSONObject object = new JSONObject();
            object.put(BaseTask.COMPLETED_ID_FIELD, 7);
            object.put(BaseTask.FAILED_ID_FIELD, 8);
            object.put(BaseTask.REQUEST_URL_FIELD, Sofeware.GET_TODAY_RANK);
            object.put(BaseTask.ACTIVITY_FLAG, this.ACTIVITY_FLAG);
            object.put("deviceId", AndroidUtils.getDeviceId(this));
            object.put("date", today);
            object.put("invest", invest);
            BaseApplication.getInstance().getControllerManager().startTask(object);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void refresh(Message msg) {
        if (msg.arg2 == this.ACTIVITY_FLAG) {
            switch (msg.what) {
                case 7:
                    parseTodayRank((ResponseBean) msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    private void parseTodayRank(ResponseBean bean) {
        try {
            if (bean.status == 1) {
                this.rankBean = RankBean.getBean(bean.data);
                if (this.rankBean != null) {
                    showRank(this.rankBean.rank_str);
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void showRank(String rankString) {
        if (rankString == null || !DateTime.getDateString().equals(this.tempDate) || rankString.length() <= 0 || rankString.equals("null")) {
            this.uiComponent.ll_rank.setVisibility(GONE);
            return;
        }
        this.uiComponent.ll_rank.setVisibility(VISIBLE);
        this.uiComponent.tv_rank.setText(rankString);
    }

    private void getRankAndBarUI(String tempDate2, boolean before) {
        try {
            ArrayList<Integer> list = DbUtils.queryActIdByType(this.context, "11");
            if (list != null && list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    int i = ((Integer) it.next()).intValue();
                    int spend = DbUtils.queryActHadSpend(this.context, tempDate2, i + "");
                    if (spend > 0) {
                        Act2 act2 = DbUtils.getAct2ByActId(this.context, i + "");
                        DbUtils.queryColorByActId(this.context, i);
                        if (act2 != null) {
                            RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.lv_progressbar, null);
                            ProgressBar pb_progress = (ProgressBar) rl.findViewById(R.id.pb_progress);
                            TextView tv_prompt = (TextView) rl.findViewById(R.id.tv_content);
                            ((TextView) rl.findViewById(R.id.tv_name)).setText(act2.getActName());
                            int everyday = act2.getTimeOfEveryday();
                            String spendStr = DateTime.calculateTime5(this.context, (long) spend);
                            String everyDayStr = DateTime.calculateTime5(this.context, (long) everyday);
                            if (everyday > 0) {
                                tv_prompt.setText(spendStr + "/" + everyDayStr);
                                pb_progress.setMax(everyday);
                                if (spend > everyday) {
                                    tv_prompt.setText(spendStr + "/" + everyDayStr + " " + getResources().getString(R.string.finish));
                                }
                            } else {
                                tv_prompt.setText(spendStr);
                                pb_progress.setMax(spend + 1800);
                            }
                            pb_progress.setProgress(spend);
                            this.animControl.slideFadeIn(rl, 300, 50);
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
        } else if (this.DoughType == DOUGHTYPE_DAY) {
            initAllocationUI_v3(this.tempDate, 3);
        }
        if (this.rankBean != null) {
            showRank(this.rankBean.rank_str);
        }
    }

    private void preDate() {
        this.lastTempDate = "";
        if (this.DoughType == DOUGHTYPE_WEEK || this.DoughType == DOUGHTYPE_MONTH) {
            initAllocationUI_v4(this.tempDate, 1, this.DoughType);
        } else if (this.DoughType == DOUGHTYPE_DAY) {
            initAllocationUI_v3(this.tempDate, 1);
        }
        if (this.rankBean != null) {
            showRank(this.rankBean.rank_str);
        }
    }

    private void showLeft(boolean click) {
        this.uiComponent.v_left.setVisibility(VISIBLE);
        if (click) {
            this.animControl.fadeIn(this.uiComponent.v_left, 400, 0);
        }
        this.uiComponent.v_right.setVisibility(View.INVISIBLE);
        if (click) {
            this.animControl.fadeOut(this.uiComponent.v_right, 500, 0);
        }
    }

    private void showRight(boolean click) {
        this.uiComponent.v_right.setVisibility(VISIBLE);
        if (click) {
            this.animControl.fadeIn(this.uiComponent.v_right, 400, 0);
        }
        this.uiComponent.v_left.setVisibility(View.INVISIBLE);
        if (click) {
            this.animControl.fadeOut(this.uiComponent.v_left, 500, 0);
        }
    }

    private void showDayWeekMonthDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_choose)).setItems(new String[]{getString(R.string.str_day), getString(R.string.str_week), getString(R.string.str_month)}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                StatisticActivity.this.DoughType = which + 1;
                StatisticActivity.this.updateUiByType2(StatisticActivity.this.DoughType);
                dialog.cancel();
            }
        }).create().show();
    }

    private void updateUiByType2(int DoughType) {
        this.DoughType = DoughType;
        if (DoughType == DOUGHTYPE_WEEK) {
            initAllocationUI_v4(this.tempDate, 2, DoughType);
            this.tv_statistic_title.setText(getString(R.string.str_everyWeek));
            this.btn_statistic_change.setVisibility(View.INVISIBLE);
            this.tv_statistic_day.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_week.setTextColor(getResources().getColor(R.color.white2));
            this.tv_statistic_month.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_day.setBackgroundResource(R.drawable.x_tran_bg_black_frame_left);
            this.tv_statistic_week.setBackgroundResource(R.drawable.x_blue_bg_black_frame_middle);
            this.tv_statistic_month.setBackgroundResource(R.drawable.x_tran_bg_black_frame_right);
        } else if (DoughType == DOUGHTYPE_MONTH) {
            initAllocationUI_v4(this.tempDate, 2, DoughType);
            this.tv_statistic_title.setText(getString(R.string.str_everyMonth));
            this.btn_statistic_change.setVisibility(View.INVISIBLE);
            this.tv_statistic_day.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_week.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_month.setTextColor(getResources().getColor(R.color.white2));
            this.tv_statistic_day.setBackgroundResource(R.drawable.x_tran_bg_black_frame_left);
            this.tv_statistic_week.setBackgroundResource(R.drawable.x_tran_bg_black_frame_middle);
            this.tv_statistic_month.setBackgroundResource(R.drawable.x_blue_bg_black_frame_right);
        } else if (DoughType == DOUGHTYPE_DAY) {
            initAllocationUI_v3(this.tempDate, 2);
            this.tv_statistic_title.setText(getString(R.string.str_everyday));
            this.btn_statistic_change.setVisibility(VISIBLE);
            this.tv_statistic_day.setTextColor(getResources().getColor(R.color.white2));
            this.tv_statistic_week.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_month.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_day.setBackgroundResource(R.drawable.x_blue_bg_black_frame_left);
            this.tv_statistic_week.setBackgroundResource(R.drawable.x_tran_bg_black_frame_middle);
            this.tv_statistic_month.setBackgroundResource(R.drawable.x_tran_bg_black_frame_right);
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
            this.tempDate = arr[0];
            if (waste < 0.0d) {
            }
            Cursor cursor2 = DbUtils.getDb2(this.context).rawQuery("Select Id from t_allocation where userId is " + DbUtils.queryUserId(this.context) + timeSql, null);
            TextView tv = (TextView) this.inflater.inflate(R.layout.template_text, null);
            tv.setTextColor(getResources().getColor(R.color.black_tran_es));
            String start = arr[0].substring(arr[0].indexOf("-") + 1, arr[0].length());
            tv.setText(start + "(" + DateTime.getDay3(this.context, arr[0] + " 00:00:00") + ")~" + arr[1].substring(arr[1].indexOf("-") + 1, arr[1].length()) + "(" + DateTime.getDay3(this.context, arr[1] + " 00:00:00") + ") \n" + cursor2.getCount() + getString(R.string.str_day) + "/" + days + getString(R.string.str_day) + " " + getString(R.string.str_records));
            this.ll_tem_statistic.removeAllViews();
            this.ll_tem_statistic.addView(tv);
            getRankAndBarUI(this.tempDate, true);
            try {
                if (DoughType == DOUGHTYPE_WEEK) {
                    String[] rateArr = Val.getWeeKNextRankNameAndTime(this.context, (int) invest);
                    if (invest > 100.0d) {
                        showWeekRate();
                        this.tv_statistic_rank_week.setText(rateArr[0] + getString(R.string.str_level));
                        this.tv_statistic_rank_week.setTextColor(Integer.parseInt(rateArr[3]));
                        this.tv_statistic_rank_week.setAnimation(getAnimation());
                    } else {
                        dontShowRate();
                    }
                    setSummarizeAndMorning(arr[0], arr[1]);
                } else {
                    dontShowRate();
                    setSummarizeAndMorning(arr[0], arr[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.toastShort(this.context, "没有记录啦...");
        }
        DbUtils.close(cursor);
    }

    private void showDayRate() {
        this.tv_statistic_rank_week.setText("");
        this.tv_statistic_rank_week.setVisibility(GONE);
        this.tv_statistic_rank.setVisibility(VISIBLE);
    }

    private void showWeekRate() {
        this.tv_statistic_rank_week.setVisibility(VISIBLE);
        this.tv_statistic_rank.setText("");
        this.tv_statistic_rank.setVisibility(GONE);
    }

    private void dontShowRate() {
        this.tv_statistic_rank_week.setText("");
        this.tv_statistic_rank_week.setVisibility(GONE);
        this.tv_statistic_rank.setText("");
        this.tv_statistic_rank.setVisibility(GONE);
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

    private void setPieTitle(String str) {
        this.tv_date_title.setText(getDateTitle() + " " + str);
    }

    private void initAllocationUI_v3(String tempDate2, int before) {
        String sql = "";
        if (before == 1) {
            sql = "Select * from t_allocation where userId is " + DbUtils.queryUserId(this.context) + " and time < '" + tempDate2 + "' order by time desc";
        } else if (before == 3) {
            sql = "Select * from t_allocation where userId is " + DbUtils.queryUserId(this.context) + " and time > '" + tempDate2 + "' order by time";
        } else if (before == 2) {
            sql = "Select * from t_allocation where userId is " + DbUtils.queryUserId(this.context) + " and time is '" + tempDate2 + "' order by time";
        }
        Cursor cursor = DbUtils.getDb2(this.context).rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            RelativeLayout remarkRl;
            TextView tv2;
            cursor.moveToNext();
            double invest = cursor.getDouble(cursor.getColumnIndex("invest"));
            String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
            String morningVoice = cursor.getString(cursor.getColumnIndex("morningVoice"));
            this.tempDate = cursor.getString(cursor.getColumnIndex("time"));
            this.vp_chart.setAdapter(new MyPagerAdapter(this.tempDate));
            this.ll_tem_statistic.removeAllViews();
            getRankAndBarUI(this.tempDate, true);
            setRank(invest);
            showDayRate();
            if (morningVoice != null && morningVoice.length() > 0) {
                remarkRl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_summarize, null);
                tv2 = (TextView) remarkRl.findViewById(R.id.tv_tem_summarize);
                ((TextView) remarkRl.findViewById(R.id.tv_tem_pre)).setText("晨音：");
                tv2.setText(morningVoice);
                this.animControl.slideFadeIn(remarkRl, 350, 50);
                this.ll_tem_statistic.addView(remarkRl);
            }
            if (remarks != null && remarks.length() > 0) {
                remarkRl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_summarize, null);
                tv2 = (TextView) remarkRl.findViewById(R.id.tv_tem_summarize);
                ((TextView) remarkRl.findViewById(R.id.tv_tem_pre)).setText("总结：");
                tv2.setText(remarks);
                this.animControl.slideFadeIn(remarkRl, 350, 50);
                this.ll_tem_statistic.addView(remarkRl);
            }
            if ((morningVoice == null || morningVoice.length() == 0) && ((remarks == null || remarks.length() == 0) && this.tempDate.equals(DateTime.getDateString()))) {
                log("tempDate2" + this.tempDate + ",DateTime.getDateString()" + DateTime.getDateString());
                showKnowledge();
            }
            if (PreferUtils.getInt(this.context, Val.CONFIGURE_STATICS_PIE_TYPE, 1) == 1) {
                this.vp_chart.setCurrentItem(0);
                if (this.dv_dots != null) {
                    this.dv_dots.setCurrentDot(0);
                }
                setPieTitle(getString(R.string.str_static_allocation_pie));
            } else {
                this.vp_chart.setCurrentItem(1);
                if (this.dv_dots != null) {
                    this.dv_dots.setCurrentDot(1);
                }
                setPieTitle(getString(R.string.str_static_time_pie));
            }
        } else if (this.tempDate.contains(DateTime.getDateString())) {
            ToastUtils.toastShort(this.context, "没有记录~");
        } else {
            ToastUtils.toastShort(this.context, "手机没有" + tempDate2 + "记录!");
        }
        DbUtils.close(cursor);
    }

    private void showKnowledge() {
        try {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_prompt order by random() limit 1", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String str = cursor.getString(cursor.getColumnIndex(PushInitUtils.RESPONSE_CONTENT));
                RelativeLayout remarkRl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_summarize1, null);
                ((TextView) remarkRl.findViewById(R.id.tv_tem_pre)).setVisibility(GONE);
                ((TextView) remarkRl.findViewById(R.id.tv_tem_summarize)).setText(str);
                remarkRl.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        try {
                            StatisticActivity.this.ll_tem_statistic.removeViewAt(StatisticActivity.this.ll_tem_statistic.getChildCount() - 1);
                        } catch (Exception e) {
                            DbUtils.exceptionHandler(StatisticActivity.this.context, e);
                        }
                        StatisticActivity.this.showKnowledge();
                        MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_Contents_btn");
                    }
                });
                this.ll_tem_statistic.addView(remarkRl);
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
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

    private void setRank(double invest) {
        if (invest < 1.0d) {
            try {
                this.tv_statistic_rank.setText("E");
                this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.black_tran_es));
                this.tv_statistic_rank.setVisibility(GONE);
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                return;
            }
        } else if (invest >= 1.0d && invest < 3600.0d) {
            this.tv_statistic_rank.setText("E");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.black_tran_es));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        } else if (invest >= 3600.0d && invest < 7200.0d) {
            this.tv_statistic_rank.setText("D");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.bg_blue1));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        } else if (invest >= 7200.0d && invest < 18000.0d) {
            this.tv_statistic_rank.setText("C");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.bg_blue2));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        } else if (invest >= 18000.0d && invest < 28800.0d) {
            this.tv_statistic_rank.setText("B");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.bg_green1));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        } else if (invest >= 28800.0d && invest <= 32400.0d) {
            this.tv_statistic_rank.setText("A");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.bg_green2));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        } else if (invest >= 32400.0d && invest <= 36000.0d) {
            this.tv_statistic_rank.setText("AA");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.bg_green3));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        } else if (invest >= 36000.0d) {
            this.tv_statistic_rank.setText("AAA");
            this.tv_statistic_rank.setTextColor(getResources().getColor(R.color.bg_green3));
            this.tv_statistic_rank.setVisibility(VISIBLE);
        }
        this.tv_statistic_rank.setAnimation(getAnimation());
    }

    private ScaleAnimation getAnimation() {
        ScaleAnimation animation = new ScaleAnimation(2.5f, 1.0f, 2.5f, 1.0f, 1, 0.5f, 1, 0.5f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(500);
        animation.setStartOffset(150);
        return animation;
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

    private View getTimePie(String currentDate) {
        View view = getLayoutInflater().inflate(R.layout.tem_day_pie, null);
        DayPie timePie = (DayPie) view.findViewById(R.id.tp_time_pie);
        ArrayList<Record2> list = DbUtils.queryItemsIdByDate2(this.context, currentDate);
        ArrayList<TimePieRecord> lists = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Record2 record = (Record2) it.next();
            lists.add(new TimePieRecord(record.getBegin(), record.getEnd(), record.getColor(), 24));
        }
        timePie.setRecords(lists);
        return view;
    }

    private View getPie1(String date) {
        Cursor cursor = DbUtils.getDb2(this.context).rawQuery("Select * from t_allocation where userId is " + DbUtils.queryUserId(this.context) + " and time is '" + date + "' order by time", null);
        if (cursor.getCount() <= 0) {
            return new View(this.context);
        }
        cursor.moveToNext();
        double invest = cursor.getDouble(cursor.getColumnIndex("invest"));
        double waste = cursor.getDouble(cursor.getColumnIndex("waste"));
        View gv = buildDough(invest, cursor.getDouble(cursor.getColumnIndex("routine")), cursor.getDouble(cursor.getColumnIndex("sleep")), waste, 86400.0d);
        LinearLayout ll = new LinearLayout(this.context);
        ll.setLayoutParams(new LayoutParams(-1, DensityUtil.dip2px(this.context, 280.0f)));
        ll.addView(gv);
        return ll;
    }

    public void onBackPressed() {
        setResult(-1);
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 12 && requestCode == 26 && -1 == resultCode) {
            this.tempDate = data.getStringExtra("Date");
        }
    }

    protected void onResume() {
        super.onResume();
        updateUiByType2(DOUGHTYPE_DAY);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.str_summarize2));
        menu.add(getResources().getString(R.string.str_Morning_voice));
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.str_summarize2))) {
            log("点击设置");
            showSummarize();
        } else if (item.getTitle().equals(getResources().getString(R.string.str_Morning_voice))) {
            showMorningVoice();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void showSummarize() {
        Intent it = new Intent(this.context, AddNoteActivity.class);
        it.putExtra("Date", this.tempDate);
        startActivityForResult(it, 12);
    }

    private void showMorningVoice() {
        Intent it = new Intent(this.context, AddNoteActivity.class);
        it.putExtra("Date", this.tempDate);
        it.setAction(Val.INTENT_ACTION_NOTI_MORNING_VOICE);
        startActivityForResult(it, 12);
    }

    private void setUiComponent() {
        this.uiComponent.v_left = (LeftRightView) findViewById(R.id.v_left);
        this.uiComponent.v_left.setType(1);
        this.uiComponent.v_right = (LeftRightView) findViewById(R.id.v_right);
        this.uiComponent.ll_rank = (LinearLayout) findViewById(R.id.ll_rank);
        this.uiComponent.tv_rank = (TextView) findViewById(R.id.tv_rank);
        this.btn_statistic_change = (Button) findViewById(R.id.btn_statistic_change);
        this.iv_statistic_share2 = (Button) findViewById(R.id.iv_statistic_share2);
        this.btn_statistic_calendar = findViewById(R.id.btn_statistic_calendar);
        this.rl_tem_statistic = (RelativeLayout) findViewById(R.id.rl_tem_statistic);
        this.tv_date_title = (TextView) findViewById(R.id.tv_data_title);
        this.vp_chart = (ViewPager) findViewById(R.id.vp_chart);
        this.vp_chart.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int position) {
                StatisticActivity.this.updateDot(position);
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
        this.ll_tem_statistic = (LinearLayout) findViewById(R.id.ll_tem_statistic);
        this.sv_today_items = (ScrollView) findViewById(R.id.sv_today_items);
        this.tv_statistic_rank = (TextView) findViewById(R.id.tv_statistic_rank);
        this.tv_statistic_unit = (TextView) findViewById(R.id.tv_statistic_unit);
        this.tv_statistic_title = (TextView) findViewById(R.id.tv_statistic_title);
        this.tv_statistic_rank_week = (TextView) findViewById(R.id.tv_statistic_rank_week);
        this.tv_statistic_day = (TextView) findViewById(R.id.tv_statistic_day);
        this.tv_statistic_week = (TextView) findViewById(R.id.tv_statistic_week);
        this.tv_statistic_month = (TextView) findViewById(R.id.tv_statistic_month);
        this.dv_dots = (DotsView) findViewById(R.id.dv_dots);
        this.dv_dots.setCount(2);
        this.iv_statistic_share2.setOnClickListener(this.myClickListener);
        this.btn_statistic_change.setOnClickListener(this.myClickListener);
        this.tv_statistic_unit.setOnClickListener(this.myClickListener);
        this.btn_statistic_calendar.setOnClickListener(this.myClickListener);
        OnClickListener preListener = new OnClickListener() {
            public void onClick(View arg0) {
                StatisticActivity.this.preDate();
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_left_Moving_time_btn");
            }
        };
        OnClickListener nextListener = new OnClickListener() {
            public void onClick(View arg0) {
                StatisticActivity.this.nextDate();
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_right_Moving_time_btn");
            }
        };
        this.uiComponent.v_left.setOnClickListenerLeft(preListener);
        this.uiComponent.v_left.setOnClickListenerRight(nextListener);
        this.uiComponent.v_right.setOnClickListenerLeft(preListener);
        this.uiComponent.v_right.setOnClickListenerRight(nextListener);
        this.uiComponent.v_left.setOnClickListenerIv(new OnClickListener() {
            public void onClick(View arg0) {
                PreferUtils.putInt(StatisticActivity.this.context, Val.CONFIGURE_STATICS_BUTTON_LOCATION, 2);
                StatisticActivity.this.showRight(true);
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_Moving_btn");
            }
        });
        this.uiComponent.v_right.setOnClickListenerIv(new OnClickListener() {
            public void onClick(View arg0) {
                PreferUtils.putInt(StatisticActivity.this.context, Val.CONFIGURE_STATICS_BUTTON_LOCATION, 1);
                StatisticActivity.this.showLeft(true);
                MobclickAgent.onEvent(StatisticActivity.this.getApplicationContext(), "everyday_click_Moving_btn");
            }
        });
        this.tv_statistic_day.setOnClickListener(this.myClickListener);
        this.tv_statistic_week.setOnClickListener(this.myClickListener);
        this.tv_statistic_month.setOnClickListener(this.myClickListener);
    }

    private void updateDot(int position) {
        this.dv_dots.setCurrentDot(position);
        String date = this.tv_date_title.getText().toString().replace(" 时序图", "").replace(" 分配图", "");
        if (position == 1) {
            this.tv_date_title.setText(date + " 时序图");
        } else {
            this.tv_date_title.setText(date + " 分配图");
        }
        PreferUtils.putInt(this.context, Val.CONFIGURE_STATICS_PIE_TYPE, position + 1);
    }

    private void log(String str) {
        LogUtils.log(str);
    }
}
