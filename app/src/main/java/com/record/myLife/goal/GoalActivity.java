package com.record.myLife.goal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.qq.e.comm.constants.ErrorCode.InitError;
import com.record.bean.Act;
import com.record.bean.Act2;
import com.record.bean.Statics;
import com.record.bean.XYColumn;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.main.AddActActivity_v2;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralUtils;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.Val.SHARE_STRING;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils;
import com.record.utils.share.QuickShareUtil;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class GoalActivity extends BaseActivity {
    static int index = 4;
    int MESSAGE_ACTION_UPDATE_DATA = 1;
    int MESSAGE_ACTION_UPDATE_DATA_BARChart = 2;
    AnimationController aController;
    String actId;
    BarChart barChart = null;
    Button btn_goal_back;
    Button btn_goal_change;
    Button btn_goal_finish;
    Button btn_tem_goal_reset;
    Context context;
    LayoutInflater inflater;
    int isFinish = 0;
    boolean isShowCorrection = true;
    ImageView iv_goal_share;
    ImageView iv_goal_show_color;
    ImageView iv_goal_show_label;
    LinearLayout ll_goal_correction__hours;
    LinearLayout ll_goal_correction_bias_ratio;
    LinearLayout ll_goal_correction_correction_ratio;
    LinearLayout ll_goal_correction_inner;
    LinearLayout ll_goal_correction_title;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == GoalActivity.this.MESSAGE_ACTION_UPDATE_DATA) {
                GoalActivity.this.updateUiByStaticsInfo(null);
            } else if (msg.arg1 == GoalActivity.this.MESSAGE_ACTION_UPDATE_DATA_BARChart) {
                try {
                    if (GoalActivity.this.barChart != null) {
                        GoalActivity.this.rl_goal_statistics.addView(new GraphicalView(GoalActivity.this.context, GoalActivity.this.barChart));
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
            }
        }
    };
    OnClickListener myListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_goal_back) {
                GoalActivity.this.finish();
            } else if (id == R.id.btn_goal_edit) {
                GoalActivity.this.goAddActAcitivity();
            } else if (id == R.id.btn_goal_finish) {
                GoalActivity.this.showFinishGoalDialog();
            } else if (id == R.id.iv_goal_share) {
                new QuickShareUtil(GoalActivity.this.context).SceenCutAndShare(GoalActivity.this.getShareContent());
            } else if (id == R.id.btn_goal_change) {
                Intent it = new Intent(GoalActivity.this.context, AddActActivity_v2.class);
                it.putExtra("ActId", GoalActivity.this.actId);
                GoalActivity.this.startActivityForResult(it, 5);
            } else if (id == R.id.tv_goal_correction_advance) {
                GoalActivity.this.showAdvance();
                PreferUtils.getSP(GoalActivity.this.context).edit().putInt(Val.CONFIGURE_IS_GOAL_SHOW_SIMPLE, 2).commit();
            } else if (id == R.id.tv_goal_correction_simple) {
                GoalActivity.this.showSimpleUi();
                PreferUtils.getSP(GoalActivity.this.context).edit().putInt(Val.CONFIGURE_IS_GOAL_SHOW_SIMPLE, 1).commit();
            } else if (id == R.id.ll_goal_correction_bias_ratio) {
//                DialogUtils.showPrompt(GoalActivity.this.context, GoalActivity.this.getString(R.string.str_bias_ratio_info));
            } else if (id == R.id.ll_goal_correction_correction_ratio) {
                DialogUtils.showPrompt(GoalActivity.this.context, GoalActivity.this.getString(R.string.str_need_effort_info));
            } else if (id == R.id.ll_goal_correction__hours) {
                DialogUtils.showPrompt(GoalActivity.this.context, GoalActivity.this.getString(R.string.str_correction_everyday_info));
            } else if (id == R.id.btn_tem_goal_reset) {
                GoalActivity.this.showResetGoalDialog();
            }
        }
    };
    LinearLayout rl_goal_btn;
    RelativeLayout rl_goal_correction_reset;
    Button rl_goal_edit;
    LinearLayout rl_goal_info;
    RelativeLayout rl_goal_progressbar;
    RelativeLayout rl_goal_show_label_bg;
    LinearLayout rl_goal_statistics;
    int sevenDayInvestTotal = 0;
    double spendInSevenDay = 0.0d;
    int todayInvest = 0;
    TextView tv_goal_actual_hours;
    TextView tv_goal_correction_advance;
    TextView tv_goal_correction_info;
    TextView tv_goal_correction_prmpt;
    TextView tv_goal_correction_reset_times;
    TextView tv_goal_correction_simple;
    TextView tv_goal_invest;
    TextView tv_goal_name;
    TextView tv_goal_need_hours;
    TextView tv_goal_need_hours2;
    TextView tv_goal_predict_hours;
    TextView tv_goal_startTime;
    TextView tv_goal_week_hours;
    UiComponent uiComponent;
    View v_goal_correction_line2;

    class UiComponent {
        Button btn_more;

        UiComponent() {
        }
    }

    public class XyCompator implements Comparator<XYColumn> {
        public int compare(XYColumn lhs, XYColumn rhs) {
            return lhs.getX() - rhs.getX();
        }
    }

    class updateDataRunnble implements Runnable {
        updateDataRunnble() {
        }

        public void run() {
            try {
                DbUtils.staticsGoalAllAutoUpdateBigGoalByGoalId(GoalActivity.this.context, Integer.parseInt(GoalActivity.this.actId));
                Message msg = new Message();
                msg.arg1 = GoalActivity.this.MESSAGE_ACTION_UPDATE_DATA;
                GoalActivity.this.myHandler.sendMessage(msg);
                GoalActivity.this.initStatistics(GoalActivity.this.actId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setUiComponent(UiComponent uiComponent) {
        uiComponent.btn_more = (Button) findViewById(R.id.btn_more);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_v2);
        SystemBarTintManager.setMIUIbar(this);
        this.inflater = getLayoutInflater();
        this.context = this;
        this.aController = new AnimationController();
        this.uiComponent = new UiComponent();
        setUiComponent(this.uiComponent);
        this.uiComponent.btn_more.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GetUpActivity.startActivity(GoalActivity.this.context, GoalActivity.this.actId);
            }
        });
        this.rl_goal_progressbar = (RelativeLayout) findViewById(R.id.rl_goal_progressbar);
        this.rl_goal_show_label_bg = (RelativeLayout) findViewById(R.id.rl_goal_show_label_bg);
        this.rl_goal_statistics = (LinearLayout) findViewById(R.id.rl_goal_statistics);
        this.rl_goal_info = (LinearLayout) findViewById(R.id.rl_goal_info);
        this.rl_goal_btn = (LinearLayout) findViewById(R.id.rl_goal_btn);
        this.ll_goal_correction_title = (LinearLayout) findViewById(R.id.ll_goal_correction_title);
        this.iv_goal_show_label = (ImageView) findViewById(R.id.iv_goal_show_label);
        this.iv_goal_share = (ImageView) findViewById(R.id.iv_goal_share);
        this.iv_goal_show_color = (ImageView) findViewById(R.id.iv_goal_show_color);
        this.tv_goal_name = (TextView) findViewById(R.id.tv_goal_name);
        this.tv_goal_startTime = (TextView) findViewById(R.id.tv_goal_startTime);
        this.tv_goal_invest = (TextView) findViewById(R.id.tv_goal_invest);
        this.tv_goal_predict_hours = (TextView) findViewById(R.id.tv_goal_predict_hours);
        this.tv_goal_actual_hours = (TextView) findViewById(R.id.tv_goal_actual_hours);
        this.tv_goal_week_hours = (TextView) findViewById(R.id.tv_goal_week_hours);
        this.tv_goal_need_hours = (TextView) findViewById(R.id.tv_goal_need_hours);
        this.tv_goal_need_hours2 = (TextView) findViewById(R.id.tv_goal_need_hours2);
        this.btn_goal_back = (Button) findViewById(R.id.btn_goal_back);
        this.btn_goal_finish = (Button) findViewById(R.id.btn_goal_finish);
        this.rl_goal_edit = (Button) findViewById(R.id.btn_goal_edit);
        this.btn_goal_change = (Button) findViewById(R.id.btn_goal_change);
        this.btn_goal_back.setOnClickListener(this.myListener);
        this.btn_goal_finish.setOnClickListener(this.myListener);
        this.rl_goal_edit.setOnClickListener(this.myListener);
        this.iv_goal_share.setOnClickListener(this.myListener);
        this.btn_goal_change.setOnClickListener(this.myListener);
        this.aController.slideFadeIn(this.rl_goal_show_label_bg, 400, 0);
        this.aController.slideFadeIn(this.rl_goal_info, 400, 200);
        this.aController.slideFadeIn(this.rl_goal_progressbar, 500, 300);
        this.aController.slideFadeIn(this.ll_goal_correction_title, 500, 300);
        this.aController.slideFadeIn(this.rl_goal_statistics, 500, 300);
        this.aController.slideFadeIn(this.rl_goal_btn, 500, 500);
        this.actId = getIntent().getStringExtra("id");
        log("goalId" + this.actId);
        initUI();
    }

    private void initUI() {
        initGoalUI(this.actId);
        new Thread(new updateDataRunnble()).start();
    }

    private void updateUiByStaticsInfo(Statics statics) {
        this.ll_goal_correction_title.removeAllViews();
        this.rl_goal_progressbar.removeAllViews();
        int goalId = Integer.parseInt(this.actId);
        try {
            statics = DbUtils.getStaticsByGoalId(this.context, goalId, Val.STATISTICS_TYPE_COMPARE_LOC_SUB);
            if (statics == null) {
                statics = DbUtils.getStaticsByGoalId(this.context, goalId, Val.STATISTICS_TYPE_COMPARE_LOC_SER);
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        double hadSpend;
        if (statics == null || statics.getExpectInvest() <= 0.0d) {
            this.tv_goal_invest.setText(FormatUtils.format_1fra(statics.getHadInvest() / 3600.0d) + "\nhours");
            this.tv_goal_week_hours.setText(FormatUtils.format_1fra((statics.getSevenInvest() / 3600.0d) / 7.0d) + "h");
            hadSpend = statics.getHadInvest() / 3600.0d;
            String tempTime = statics.getStartTime();
            if (tempTime == null || tempTime.length() == 0) {
                tempTime = statics.getCreateTime();
            }
            if (tempTime == null || tempTime.length() <= 0) {
                ((LinearLayout) this.tv_goal_actual_hours.getParent()).setVisibility(View.GONE);
            } else {
                this.tv_goal_actual_hours.setText(FormatUtils.format_1fra(Math.abs(hadSpend / ((double) (DateTime.cal_daysBetween(statics.getCreateTime(), Calendar.getInstance().getTime()) + 1)))) + "h");
            }
            ((LinearLayout) this.tv_goal_predict_hours.getParent()).setVisibility(View.GONE);
            ((LinearLayout) this.tv_goal_need_hours.getParent()).setVisibility(View.GONE);
            ((LinearLayout) this.btn_goal_finish.getParent()).setVisibility(View.GONE);
            this.rl_goal_progressbar.setVisibility(View.GONE);
            this.ll_goal_correction_title.setVisibility(View.GONE);
            return;
        }
        ((LinearLayout) this.tv_goal_predict_hours.getParent()).setVisibility(View.VISIBLE);
        ((LinearLayout) this.tv_goal_need_hours.getParent()).setVisibility(View.VISIBLE);
        ((LinearLayout) this.btn_goal_finish.getParent()).setVisibility(View.VISIBLE);
        this.rl_goal_progressbar.setVisibility(View.VISIBLE);
        this.ll_goal_correction_title.setVisibility(View.VISIBLE);
        this.tv_goal_invest.setText(FormatUtils.format_1fra(statics.getHadInvest() / 3600.0d) + "\nhours");
        this.tv_goal_week_hours.setText(FormatUtils.format_1fra((statics.getSevenInvest() / 3600.0d) / 7.0d) + "h");
        double expectSpend = statics.getExpectInvest() / 3600.0d;
        hadSpend = statics.getHadInvest() / 3600.0d;
        double need = expectSpend - hadSpend;
        if (need <= 0.0d) {
            this.tv_goal_need_hours.setText(getString(R.string.str_goal));
            this.tv_goal_need_hours2.setText(getString(R.string.str_reach));
        } else {
            this.tv_goal_need_hours.setText(FormatUtils.format_1fra(need) + "h");
            this.tv_goal_need_hours2.setText(getString(R.string.str_to_goal));
        }
        int usedDays = DateTime.cal_daysBetween(statics.getStartTime(), Calendar.getInstance().getTime()) + 1;
        int totalDays = DateTime.cal_daysBetween(statics.getStartTime(), DateTime.pars2Calender(statics.getDeadline()).getTime()) + 1;
        double today = 0.0d;
        if (statics != null) {
            today = statics.getTodayInvest() / 3600.0d;
        }
        double everyday = expectSpend / ((double) totalDays);
        double actual = Math.abs(hadSpend / ((double) usedDays));
        this.tv_goal_predict_hours.setText(FormatUtils.format_1fra(everyday) + "h");
        this.tv_goal_actual_hours.setText(FormatUtils.format_1fra(actual) + "h");
        double[] todayIntArr = new double[]{3600.0d * everyday, 3600.0d * today};
        String todayStr = getPbTodayString(today, everyday);
        String investStr = "(" + FormatUtils.format_1fra(hadSpend) + "/" + FormatUtils.format_1fra(expectSpend) + ")h " + FormatUtils.format_1fra((hadSpend / expectSpend) * 100.0d) + "% " + getString(R.string.str_invest);
        if (need <= 0.0d) {
            investStr = "(" + FormatUtils.format_1fra(hadSpend) + "/" + FormatUtils.format_1fra(expectSpend) + ")h " + FormatUtils.format_1fra((hadSpend / expectSpend) * 100.0d) + "% " + getString(R.string.str_finished);
        }
        String deadlineStr = "(" + FormatUtils.format_1fra((double) usedDays) + "/" + FormatUtils.format_1fra((double) totalDays) + ")" + getString(R.string.str_day) + FormatUtils.format_1fra((((double) usedDays) / ((double) totalDays)) * 100.0d);
        if (usedDays > totalDays) {
            deadlineStr = deadlineStr + "% " + getString(R.string.str_reach_deadLine);
        } else {
            deadlineStr = deadlineStr + "% " + getString(R.string.str_deadLine);
        }
        String[] goalArr = new String[]{getString(R.string.str_progress), todayStr, investStr, deadlineStr};
        this.rl_goal_progressbar.addView(getGoalUI_v2(InitError.INIT_AD_ERROR, goalArr, todayIntArr, new double[]{expectSpend, (double) ((int) hadSpend)}, new double[]{(double) totalDays, (double) usedDays}));
        View correctionRl = (RelativeLayout) this.inflater.inflate(R.layout.tem_goal_correction, null);
        TextView tv_goal_correction_title = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_title);
        TextView tv_goal_correction__hours = (TextView) correctionRl.findViewById(R.id.tv_goal_correction__hours);
        TextView tv_goal_correction_correction_ratio = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_correction_ratio);
        TextView tv_goal_correction_bias_ratio = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_bias_ratio);
        TextView tv_goal_correction_date = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_date);
        this.ll_goal_correction_bias_ratio = (LinearLayout) correctionRl.findViewById(R.id.ll_goal_correction_bias_ratio);
        this.ll_goal_correction__hours = (LinearLayout) correctionRl.findViewById(R.id.ll_goal_correction__hours);
        this.ll_goal_correction_correction_ratio = (LinearLayout) correctionRl.findViewById(R.id.ll_goal_correction_correction_ratio);
        this.tv_goal_correction_advance = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_advance);
        this.tv_goal_correction_simple = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_simple);
        this.tv_goal_correction_info = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_info);
        this.ll_goal_correction_inner = (LinearLayout) correctionRl.findViewById(R.id.ll_goal_correction_inner);
        this.rl_goal_correction_reset = (RelativeLayout) correctionRl.findViewById(R.id.rl_goal_correction_reset);
        this.v_goal_correction_line2 = correctionRl.findViewById(R.id.v_goal_correction_line2);
        this.btn_tem_goal_reset = (Button) correctionRl.findViewById(R.id.btn_tem_goal_reset);
        this.tv_goal_correction_prmpt = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_prmpt);
        this.tv_goal_correction_reset_times = (TextView) correctionRl.findViewById(R.id.tv_goal_correction_reset_times);
        this.ll_goal_correction_bias_ratio.setOnClickListener(this.myListener);
        this.ll_goal_correction__hours.setOnClickListener(this.myListener);
        this.ll_goal_correction_correction_ratio.setOnClickListener(this.myListener);
        if (!this.isShowCorrection) {
            updateUiCorrection(getString(R.string.str_show_correction_info));
        } else if (this.isFinish != 1) {
            int leftDays = totalDays - usedDays;
            if (leftDays > 0) {
                if (need > 0.0d) {
                    double correctionEveryday = need / ((double) leftDays);
                    double correctionRate = ((correctionEveryday - everyday) / everyday) * 100.0d;
                    if (actual > 0.0d) {
                        correctionRate = ((correctionEveryday - actual) / actual) * 100.0d;
                    } else {
                        correctionRate = 310.0d;
                    }
                    double biasRate = ((actual - everyday) / everyday) * 100.0d;
                    this.tv_goal_correction_simple.setOnClickListener(this.myListener);
                    this.tv_goal_correction_advance.setOnClickListener(this.myListener);
                    tv_goal_correction__hours.setText(FormatUtils.format_1fra(correctionEveryday) + "h");
                    if (correctionRate < 0.0d) {
                        tv_goal_correction_correction_ratio.setText(FormatUtils.format_1fra(correctionRate) + "%");
                    } else if (correctionRate >= 300.0d) {
                        tv_goal_correction_correction_ratio.setText(">300%");
                    } else {
                        tv_goal_correction_correction_ratio.setText("+" + FormatUtils.format_1fra(correctionRate) + "%");
                    }
                    if (biasRate < 0.0d) {
                        tv_goal_correction_bias_ratio.setText(FormatUtils.format_1fra(biasRate) + "%");
                    } else if (biasRate >= 300.0d) {
                        tv_goal_correction_bias_ratio.setText(">300%");
                    } else {
                        tv_goal_correction_bias_ratio.setText("+" + FormatUtils.format_1fra(biasRate) + "%");
                    }
                    tv_goal_correction_date.setText(FormatUtils.format_0fra((double) leftDays) + getString(R.string.str_day));
                    if (correctionEveryday > everyday) {
                        tv_goal_correction__hours.setTextColor(getResources().getColor(R.color.bg_red1));
                        tv_goal_correction_correction_ratio.setTextColor(getResources().getColor(R.color.bg_red1));
                        tv_goal_correction_bias_ratio.setTextColor(getResources().getColor(R.color.bg_red1));
                    } else {
                        tv_goal_correction__hours.setTextColor(getResources().getColor(R.color.bg_green1));
                        tv_goal_correction_correction_ratio.setTextColor(getResources().getColor(R.color.bg_green1));
                        tv_goal_correction_bias_ratio.setTextColor(getResources().getColor(R.color.bg_green1));
                    }
                    this.tv_goal_correction_info.setText(getString(R.string.str_goal_correction_info) + FormatUtils.format_1fra(correctionEveryday) + "h");
                    if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_IS_GOAL_SHOW_SIMPLE, 1) == 1) {
                        showSimpleUi();
                    } else {
                        showAdvance();
                    }
                    if (biasRate < -50.0d) {
                        setBiasLess50();
                    } else if (correctionEveryday > 13.0d) {
                        setRemindEveryDaysInvestMoreThan13();
                    } else {
                        serNotShowResetUi(biasRate);
                    }
                } else {
                    updateUiCorrection(getString(R.string.str_goal_finished_2));
                }
            } else if (hadSpend <= 10.0d) {
                updateUiCorrection(getString(R.string.str_goal_over_deadline_and_without_invest));
            } else {
                updateUiCorrection(getString(R.string.str_goal_over_deadline));
            }
        } else {
            updateUiCorrection(getString(R.string.str_goal_finished));
        }
        this.ll_goal_correction_title.addView(correctionRl);
    }

    private String getPbTodayString(double today, double everyday) {
        if (today > everyday) {
            return "(" + FormatUtils.format_1fra(today) + "/" + FormatUtils.format_1fra(everyday) + ")h " + FormatUtils.format_1fra((today / everyday) * 100.0d) + "% " + getString(R.string.str_finished);
        }
        return "(" + FormatUtils.format_1fra(today) + "/" + FormatUtils.format_1fra(everyday) + ")h " + FormatUtils.format_1fra((today / everyday) * 100.0d) + "% " + getString(R.string.str_today);
    }

    private void serNotShowResetUi(double biasRate) {
        if (biasRate >= 0.0d) {
            this.tv_goal_correction_prmpt.setVisibility(View.VISIBLE);
            this.tv_goal_correction_prmpt.setText(getString(R.string.str_goal_progress_good));
        } else if (biasRate <= -50.0d || biasRate >= 0.0d) {
            this.tv_goal_correction_prmpt.setVisibility(View.GONE);
        } else {
            this.tv_goal_correction_prmpt.setVisibility(View.VISIBLE);
            this.tv_goal_correction_prmpt.setText(getString(R.string.str_goal_progress_bad));
        }
        this.btn_tem_goal_reset.setVisibility(View.GONE);
        this.tv_goal_correction_reset_times.setVisibility(View.GONE);
    }

    private void setRemindEveryDaysInvestMoreThan13() {
        this.btn_tem_goal_reset.setOnClickListener(this.myListener);
        this.tv_goal_correction_prmpt.setText(getString(R.string.str_goal_correction_time_over_12_prompt));
        this.tv_goal_correction_reset_times.setText(getString(R.string.str_reset_remaaining_times));
    }

    private void setBiasLess50() {
        this.btn_tem_goal_reset.setOnClickListener(this.myListener);
        this.tv_goal_correction_prmpt.setText(getString(R.string.str_goal_bias_over_50_prompt));
        this.tv_goal_correction_reset_times.setText(getString(R.string.str_reset_remaaining_times));
    }

    private void remindEveryDaysInvestMoreThan13() {
        if (Math.abs(DateTime.cal_daysBetween(PreferUtils.getGoalSP(this.context).getString(PreferUtils.getGoalOver13Key(Integer.parseInt(this.actId)), DateTime.beforeNDays2Str(-7) + " 00:00:00"), DateTime.getTimeString())) >= PreferUtils.BIAS_REMIND_DAY) {
            showModifyGoalDialog(getString(R.string.str_goal_correction_time_over_12_prompt));
            PreferUtils.getGoalSP(this.context).edit().putString(PreferUtils.getGoalOver13Key(Integer.parseInt(this.actId)), DateTime.getTimeString()).commit();
        }
    }

    private void remindBiasLess50() {
        if (Math.abs(DateTime.cal_daysBetween(PreferUtils.getGoalSP(this.context).getString(PreferUtils.getGoalBiasKey(Integer.parseInt(this.actId)), DateTime.beforeNDays2Str(-7) + " 00:00:00"), DateTime.getTimeString())) >= PreferUtils.BIAS_REMIND_DAY) {
            showModifyGoalDialog(getString(R.string.str_goal_bias_over_50_prompt));
            PreferUtils.getGoalSP(this.context).edit().putString(PreferUtils.getGoalBiasKey(Integer.parseInt(this.actId)), DateTime.getTimeString()).commit();
        }
    }

    public void showModifyGoalDialog(String str) {
        new Builder(this.context).setTitle(this.context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setNeutralButton(this.context.getString(R.string.str_go_modify), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GoalActivity.this.goAddActAcitivity();
                dialog.cancel();
            }
        }).setNegativeButton(this.context.getString(R.string.str_i_know), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void updateUiCorrection(String str) {
        showSimpleUi();
        this.tv_goal_correction_info.setText(str);
        this.tv_goal_correction_advance.setVisibility(View.GONE);
        this.tv_goal_correction_simple.setVisibility(View.GONE);
    }

    public ArrayList<XYColumn> addSubGoalToBigGoal(ArrayList<XYColumn> bigGoal, ArrayList<XYColumn> subGoal) {
        if (bigGoal == null || subGoal == null) {
            log("subGoal为空！");
        } else {
            int i = 0;
            while (i < bigGoal.size()) {
                try {
                    XYColumn xy = (XYColumn) bigGoal.get(i);
                    xy.setY(((XYColumn) subGoal.get(i)).getY() + xy.getY());
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bigGoal;
    }

    private void initStatistics(String actId) {
        ArrayList<XYColumn> arr = DbUtils.getXseries(this.context, actId, 6);
        Collections.sort(arr, new XyCompator());
        log("大目标：" + arr.toString());
        ArrayList<Integer> subGoalIdArr = DbUtils.querySubGoalIdByBigGoalIdContainDelte(this.context, Integer.parseInt(actId));
        if (subGoalIdArr != null) {
            XyCompator compator = new XyCompator();
            Iterator it = subGoalIdArr.iterator();
            while (it.hasNext()) {
                int subGoalId = ((Integer) it.next()).intValue();
                ArrayList<XYColumn> sub = DbUtils.getXseries(this.context, subGoalId + "", 6);
                Collections.sort(sub, compator);
                log("子目标：" + sub.toString());
                arr = addSubGoalToBigGoal(arr, sub);
                Collections.sort(arr, compator);
            }
        }
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        if (Val.col_Str2Int_Map == null) {
            Val.getCol_Str2Int_Map();
        }
        r.setColor(DbUtils.queryColorByActId(this.context, actId));
        renderer.addSeriesRenderer(r);
        renderer.setBarSpacing(0.2d);
        renderer.setBackgroundColor(-1);
        renderer.setXAxisMin(0.0d);
        renderer.setXAxisMax(8.0d);
        renderer.setYAxisMin(1.0d);
        renderer.setGridColor(-16777216);
        renderer.setZoomEnabled(false);
        renderer.setMarginsColor(getResources().getColor(R.color.white));
        renderer.setPanEnabled(false, false);
        renderer.setAxesColor(-1);
        renderer.setShowLegend(false);
        renderer.setLabelsTextSize(15.0f);
        XYSeries series = new XYSeries("投入");
        double i = 0.0d;
        this.spendInSevenDay = 0.0d;
        Iterator it2 = arr.iterator();
        while (it2.hasNext()) {
            XYColumn c = (XYColumn) it2.next();
            series.add((double) c.getX(), (double) c.getY());
            renderer.addXTextLabel((double) c.getX(), c.getValue());
            if (i < ((double) c.getY())) {
                i = (double) c.getY();
            }
            this.spendInSevenDay += (double) c.getY();
        }
        renderer = setTextLabel(renderer, (int) i);
        renderer.setYLabels(0);
        renderer.setXLabels(0);
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        this.barChart = new BarChart(dataset, renderer, Type.STACKED);
        Message msg = new Message();
        msg.arg1 = this.MESSAGE_ACTION_UPDATE_DATA_BARChart;
        this.myHandler.sendMessage(msg);
    }

    private XYMultipleSeriesRenderer setTextLabel(XYMultipleSeriesRenderer renderer, int second) {
        if (second <= 1800) {
            renderer.addYTextLabel(600.0d, "10min");
            renderer.addYTextLabel(1200.0d, "20min");
            renderer.addYTextLabel(1800.0d, "30min");
            renderer.setYAxisMax(1800.0d);
        } else if (second > 1800 && second <= 3600) {
            renderer.addYTextLabel(900.0d, "15min");
            renderer.addYTextLabel(1800.0d, "30min");
            renderer.addYTextLabel(2700.0d, "45min");
            renderer.addYTextLabel(3600.0d, "60min");
            renderer.setYAxisMax(3600.0d);
        } else if (second > 3600 && second <= 7200) {
            renderer.addYTextLabel(1800.0d, "30min");
            renderer.addYTextLabel(3600.0d, "1h");
            renderer.addYTextLabel(5400.0d, "1.5h");
            renderer.addYTextLabel(7200.0d, "2h");
            renderer.setYAxisMax(7200.0d);
        } else if (second > 3600 && second <= 10800) {
            renderer.addYTextLabel(2700.0d, "45min");
            renderer.addYTextLabel(5400.0d, "1.5h");
            renderer.addYTextLabel(7200.0d, "2h");
            renderer.addYTextLabel(10800.0d, "3h");
            renderer.setYAxisMax(10800.0d);
        } else if (second > 10800 && second <= 28800) {
            renderer.addYTextLabel(7200.0d, "2h");
            renderer.addYTextLabel(14400.0d, "4h");
            renderer.addYTextLabel(21600.0d, "6h");
            renderer.addYTextLabel(28800.0d, "8h");
            renderer.setYAxisMax(28800.0d);
        } else if (second > 28800 && second <= 43200) {
            renderer.addYTextLabel(10800.0d, "3h");
            renderer.addYTextLabel(21600.0d, "6h");
            renderer.addYTextLabel(32400.0d, "9h");
            renderer.addYTextLabel(43200.0d, "12h");
            renderer.setYAxisMax(43200.0d);
        } else if (second > 43200) {
            renderer.addYTextLabel(14400.0d, "4h");
            renderer.addYTextLabel(28800.0d, "8h");
            renderer.addYTextLabel(43200.0d, "12h");
            renderer.addYTextLabel(86400.0d, "24h");
            renderer.setYAxisMax(86400.0d);
        }
        return renderer;
    }

    private void updateDb(String id) {
        Cursor cur = DbUtils.getDb2(this.context).rawQuery("select * from t_act_item  where userId is ? and actId is " + id, new String[]{DbUtils.queryUserId(this.context)});
        if (cur.getCount() > 0) {
            int hadSpend = 0;
            while (cur.moveToNext()) {
                hadSpend += cur.getInt(cur.getColumnIndex("take"));
            }
            ContentValues values = new ContentValues();
            values.put("hadSpend", Integer.valueOf(hadSpend));
            DbUtils.getDb(this.context).update("t_act", values, " Id is ?", new String[]{id});
        }
        DbUtils.close(cur);
    }

    private void initGoalUI(String id) {
        Cursor cur = DbUtils.getDb(this.context).rawQuery("Select * from t_act where id is " + id + " and userId is " + DbUtils.queryUserId(this.context), null);
        if (cur.getCount() > 0) {
            this.rl_goal_progressbar.removeAllViews();
            cur.moveToNext();
            String actName = cur.getString(cur.getColumnIndex("actName"));
            String image = cur.getString(cur.getColumnIndex("image"));
            String color = cur.getString(cur.getColumnIndex("color"));
            String startTime = cur.getString(cur.getColumnIndex("startTime"));
            String createTime = cur.getString(cur.getColumnIndex("createTime"));
            this.isFinish = cur.getInt(cur.getColumnIndex("isFinish"));
            int isDelete = cur.getInt(cur.getColumnIndex("isDelete"));
            if (this.isFinish > 0 || isDelete > 0) {
                this.btn_goal_change.setVisibility(View.GONE);
                this.btn_goal_finish.setVisibility(View.GONE);
            }
            if (createTime != null) {
                try {
                    if (createTime.length() > 0) {
                        if (DateTime.compare_date(DateTime.beforeNDays2Str(-2) + " 00:00:00", createTime) > 0) {
                            this.isShowCorrection = true;
                        } else {
                            this.isShowCorrection = false;
                        }
                        if (Val.col_Str2xml_circle_Int_Map == null || Val.col_Str2xml_circle_Int_Map.size() == 0) {
                            Val.setMap();
                        }
                        this.iv_goal_show_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
                        this.tv_goal_invest.setTextColor(getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()));
                        this.iv_goal_show_label.setImageResource(Val.getLabelIntByName(image));
                        this.tv_goal_name.setText(actName);
                        if (startTime != null || startTime.length() <= 0) {
                            this.tv_goal_startTime.setText(DateTime.convertTsToYMD(createTime));
                        } else {
                            this.tv_goal_startTime.setText(DateTime.convertTsToYMD(startTime));
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (DateTime.compare_date(DateTime.beforeNDays2Str(-2) + " 00:00:00", startTime) > 0) {
                this.isShowCorrection = true;
            } else {
                this.isShowCorrection = false;
            }
            Val.setMap();
            this.iv_goal_show_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
            this.tv_goal_invest.setTextColor(getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()));
            this.iv_goal_show_label.setImageResource(Val.getLabelIntByName(image));
            this.tv_goal_name.setText(actName);
            if (startTime != null) {
            }
            this.tv_goal_startTime.setText(DateTime.convertTsToYMD(createTime));
        }
    }

    public void isSubGoal(Context context, int id) {
    }

    public static int[] getDayBetween(String startTime, String deadline) {
        Date today = Calendar.getInstance().getTime();
        Date start = DateTime.pars2Calender(startTime).getTime();
        int use = DateTime.getBetweenDayNumber(start, today) + 1;
        int tatal = DateTime.getBetweenDayNumber(start, DateTime.pars2Calender(deadline).getTime()) + 1;
        return new int[]{tatal, use};
    }

    private RelativeLayout getGoalUI(int rlId, String[] goalStrArr, int[] invest, int[] waste, int[] deadline) {
        LayoutParams params = new LayoutParams(-1, -2);
        params.addRule(3, rlId - 1);
        RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.template_goal, null);
        rl.setId(rlId);
        rl.setLayoutParams(params);
        TextView tv_goal = (TextView) rl.findViewById(R.id.tv_goal);
        TextView tv_goal_invest = (TextView) rl.findViewById(R.id.tv_goal_invest);
        TextView tv_goal_waste = (TextView) rl.findViewById(R.id.tv_goal_waste);
        TextView tv_goal_deadline = (TextView) rl.findViewById(R.id.tv_goal_deadline);
        ProgressBar pb_goal_invest = (ProgressBar) rl.findViewById(R.id.pb_goal_invest);
        ProgressBar pb_goal_waste = (ProgressBar) rl.findViewById(R.id.pb_goal_waste);
        ProgressBar pb_goal_deadline = (ProgressBar) rl.findViewById(R.id.pb_goal_deadline);
        if (invest[1] > invest[0]) {
            invest[1] = invest[0];
        }
        if (waste[1] > waste[0]) {
            waste[1] = waste[0];
        }
        if (deadline[1] > deadline[0]) {
            deadline[1] = deadline[0];
        }
        tv_goal.setText(goalStrArr[0]);
        tv_goal_invest.setText(goalStrArr[1]);
        tv_goal_waste.setText(goalStrArr[2]);
        tv_goal_deadline.setText(goalStrArr[3]);
        pb_goal_invest.setMax(invest[0]);
        pb_goal_invest.setProgress(invest[1]);
        pb_goal_waste.setMax(waste[0]);
        pb_goal_waste.setProgress(waste[1]);
        pb_goal_deadline.setMax(deadline[0]);
        pb_goal_deadline.setProgress(deadline[1]);
        return rl;
    }

    private RelativeLayout getGoalUI_v2(int rlId, String[] goalStrArr, double[] invest, double[] waste, double[] deadline) {
        LayoutParams params = new LayoutParams(-1, -2);
        params.addRule(3, rlId - 1);
        RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.template_goal, null);
        rl.setId(rlId);
        rl.setLayoutParams(params);
        TextView tv_goal = (TextView) rl.findViewById(R.id.tv_goal);
        TextView tv_goal_invest = (TextView) rl.findViewById(R.id.tv_goal_invest);
        TextView tv_goal_waste = (TextView) rl.findViewById(R.id.tv_goal_waste);
        TextView tv_goal_deadline = (TextView) rl.findViewById(R.id.tv_goal_deadline);
        ProgressBar pb_goal_invest = (ProgressBar) rl.findViewById(R.id.pb_goal_invest);
        ProgressBar pb_goal_waste = (ProgressBar) rl.findViewById(R.id.pb_goal_waste);
        ProgressBar pb_goal_deadline = (ProgressBar) rl.findViewById(R.id.pb_goal_deadline);
        if (invest[1] >= invest[0]) {
            invest[1] = invest[0];
            tv_goal_invest.setTextColor(-1);
        }
        if (waste[1] >= waste[0]) {
            waste[1] = waste[0];
            tv_goal_waste.setTextColor(-1);
        }
        if (deadline[1] >= deadline[0]) {
            deadline[1] = deadline[0];
            tv_goal_deadline.setTextColor(-1);
        }
        tv_goal.setText(goalStrArr[0]);
        tv_goal_invest.setText(goalStrArr[1]);
        tv_goal_waste.setText(goalStrArr[2]);
        tv_goal_deadline.setText(goalStrArr[3]);
        pb_goal_invest.setMax((int) invest[0]);
        pb_goal_invest.setProgress((int) invest[1]);
        pb_goal_waste.setMax((int) waste[0]);
        pb_goal_waste.setProgress((int) waste[1]);
        pb_goal_deadline.setMax((int) deadline[0]);
        pb_goal_deadline.setProgress((int) deadline[1]);
        return rl;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 5) {
            return;
        }
        if (resultCode == 7) {
            setResult(7);
            initGoalUI(this.actId);
            new Thread(new updateDataRunnble()).start();
        } else if (resultCode == 8) {
            setResult(7);
            finish();
        }
    }

    private void goAddActAcitivity() {
        Intent it = new Intent(this.context, AddActActivity_v2.class);
        it.putExtra("ActId", this.actId);
        startActivityForResult(it, 5);
    }

    private String getShareContent() {
        String content = SHARE_STRING.weiboDefault2;
        try {
            Act2 act = DbUtils.getAct2ByActId(this.context, this.actId + "");
            if (act.getDeadline() == null || act.getDeadline().length() <= 0) {
                content = SHARE_STRING.weiboGoalNoDeadline;
            } else {
                content = SHARE_STRING.weiboGoal.replace(SHARE_STRING.WEIBO_FILED_GOAL_DEADLINE, FormatUtils.format_1fra((double) getDayBetween(act.getStartTime(), act.getDeadline())[0]) + "天");
            }
            return content.replace(SHARE_STRING.WEIBO_FILED_GOAL_NAME, act.getActName()).replace(SHARE_STRING.WEIBO_FILED_GOAL_SEVEN, FormatUtils.format_1fra((this.spendInSevenDay / 3600.0d) / 7.0d) + "小时").replace(SHARE_STRING.WEIBO_FILED_GOAL_HAD_INVEST, FormatUtils.format_1fra(((double) act.getHadSpend()) / 3600.0d) + "小时");
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            return content;
        }
    }

    private void showResetGoalDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage(getString(R.string.str_reset_goal_prompt)).setPositiveButton(getString(R.string.str_reset), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int count = DbUtils.queryResetCountByGoalId2(GoalActivity.this.context, GoalActivity.this.actId);
                ContentValues values = new ContentValues();
                values.put("startTime", DateTime.getTimeString());
                values.put("resetCount", Integer.valueOf(count + 1));
                if (DbUtils.querysGoalIdByActId(GoalActivity.this.context, Integer.parseInt(GoalActivity.this.actId)) > 0) {
                    values.put("endUpdateTime", DateTime.getTimeString());
                }
                DbUtils.getDb(GoalActivity.this.context).update("t_act", values, "id = " + GoalActivity.this.actId, null);
                GeneralUtils.toastShort(GoalActivity.this.context, GoalActivity.this.getString(R.string.str_reset_successfully));
                GoalActivity.this.initUI();
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showFinishGoalDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_is_finish_goal)).setMessage(getString(R.string.str_is_finish_goal_note)).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton(getString(R.string.str_confirm_finished), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final ArrayList<Integer> arr = DbUtils.querySubGoalIdByBigGoalId(GoalActivity.this.context, Integer.parseInt(GoalActivity.this.actId));
                if (arr == null || arr.size() <= 0) {
                    dialog.cancel();
                    GoalActivity.this.finishGoal(Integer.parseInt(GoalActivity.this.actId));
                    GoalActivity.this.finish();
                    return;
                }
                new Builder(GoalActivity.this.context).setTitle(GoalActivity.this.getString(R.string.str_prompt)).setMessage(GoalActivity.this.getString(R.string.str_finish_goal_but_it_contain_sub).replace("{几个}", arr.size() + "")).setPositiveButton(GoalActivity.this.getString(R.string.str_confirm_finished), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GoalActivity.this.finishGoal(Integer.parseInt(GoalActivity.this.actId));
                        Iterator it = arr.iterator();
                        while (it.hasNext()) {
                            GoalActivity.this.finishGoal(((Integer) it.next()).intValue());
                        }
                        dialog.cancel();
                        GoalActivity.this.finish();
                    }
                }).setNegativeButton(GoalActivity.this.getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        }).create().show();
    }

    public void finishGoal(int actId) {
        try {
            if (TimerService.timer != null && Act.getInstance().getId() == actId) {
                sendBroadcast(new Intent(Val.INTENT_ACTION_STOP_COUNTER));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put("isFinish", Integer.valueOf(1));
        values.put("finishTime", DateTime.getTimeString());
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_act", values, " id is " + actId, null);
        setResult(7);
    }

    private void showSimpleUi() {
        this.tv_goal_correction_info.setVisibility(View.VISIBLE);
        this.ll_goal_correction_inner.setVisibility(View.GONE);
        this.rl_goal_correction_reset.setVisibility(View.GONE);
        this.v_goal_correction_line2.setVisibility(View.GONE);
        this.tv_goal_correction_advance.setBackgroundResource(R.drawable.x_white_bg_black_frame);
        this.tv_goal_correction_simple.setBackgroundResource(R.drawable.x_blue_bg_black_frame);
        this.tv_goal_correction_simple.setTextColor(getResources().getColor(R.color.white));
        this.tv_goal_correction_advance.setTextColor(getResources().getColor(R.color.black_tran_es));
    }

    private void showAdvance() {
        this.tv_goal_correction_info.setVisibility(View.GONE);
        this.ll_goal_correction_inner.setVisibility(View.VISIBLE);
        this.rl_goal_correction_reset.setVisibility(View.VISIBLE);
        this.v_goal_correction_line2.setVisibility(View.VISIBLE);
        this.tv_goal_correction_advance.setBackgroundResource(R.drawable.x_blue_bg_black_frame);
        this.tv_goal_correction_simple.setBackgroundResource(R.drawable.x_white_bg_black_frame);
        this.tv_goal_correction_simple.setTextColor(getResources().getColor(R.color.black_tran_es));
        this.tv_goal_correction_advance.setTextColor(getResources().getColor(R.color.white));
    }

    public static void startActivity(Activity activity, String id) {
        Intent it = new Intent(activity, GoalActivity.class);
        it.putExtra("id", id);
        activity.startActivityForResult(it, 14);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void log(String str) {
        Log.i("override", ":" + str);
    }
}
