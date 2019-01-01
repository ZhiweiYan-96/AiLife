package com.record.myLife.goal;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.record.bean.XYColumn;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.main.AddActActivity_v2;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.DensityUtil;
import com.record.utils.GeneralHelper;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.share.QuickShareUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class TypeActivity extends BaseActivity {
    static int index = 4;
    AnimationController aController;
    Button btn_goal_back;
    Button btn_goal_change;
    Context context;
    LayoutInflater inflater;
    ImageView iv_goal_share;
    ImageView iv_goal_show_color;
    ImageView iv_goal_show_label;
    OnClickListener myListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_goal_back) {
                TypeActivity.this.finish();
            } else if (id == R.id.btn_goal_edit) {
                Intent it = new Intent(TypeActivity.this.context, AddActActivity_v2.class);
                it.putExtra("ActId", TypeActivity.this.tranmitId);
                TypeActivity.this.startActivityForResult(it, 5);
            } else if (id == R.id.btn_goal_finish) {
                new Builder(TypeActivity.this.context).setTitle((CharSequence) "是否完成目标？").setNegativeButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton((CharSequence) "完成", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values = new ContentValues();
                        values.put("isFinish", Integer.valueOf(1));
                        DbUtils.getDb(TypeActivity.this.context).update("t_act", values, " Id is ?", new String[]{TypeActivity.this.tranmitId});
                        TypeActivity.this.setResult(7);
                        dialog.cancel();
                        TypeActivity.this.finish();
                    }
                }).create().show();
            } else if (id == R.id.iv_goal_share) {
                new QuickShareUtil(TypeActivity.this.context).SceenCutAndShare(DbUtils.queryWeibo(TypeActivity.this.context, DbUtils.queryUserId(TypeActivity.this.context), "3", "1"));
            } else if (id == R.id.btn_goal_change) {
                TypeActivity.this.addModifyLabel();
            }
        }
    };
    OnLongClickListener myLongListener = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            String actIdStr = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            Intent it = new Intent(TypeActivity.this.context, AddActActivity_v2.class);
            it.putExtra("ActId", actIdStr);
            TypeActivity.this.startActivityForResult(it, 5);
            return false;
        }
    };
    RelativeLayout rl_goal_show_label_bg;
    LinearLayout rl_goal_statistics;
    double spendInSevenDay = 0.0d;
    String tranmitId;
    int tranmitType = 0;
    TextView tv_goal_invest;
    TextView tv_goal_name;
    TextView tv_goal_startTime;
    TextView tv_goal_struction;
    String typeName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        SystemBarTintManager.setMIUIbar(this);
        this.inflater = getLayoutInflater();
        this.context = this;
        this.aController = new AnimationController();
        this.rl_goal_show_label_bg = (RelativeLayout) findViewById(R.id.rl_goal_show_label_bg);
        this.rl_goal_statistics = (LinearLayout) findViewById(R.id.rl_goal_statistics);
        this.iv_goal_show_label = (ImageView) findViewById(R.id.iv_goal_show_label);
        this.iv_goal_share = (ImageView) findViewById(R.id.iv_goal_share);
        this.iv_goal_show_color = (ImageView) findViewById(R.id.iv_goal_show_color);
        this.tv_goal_name = (TextView) findViewById(R.id.tv_goal_name);
        this.tv_goal_startTime = (TextView) findViewById(R.id.tv_goal_startTime);
        this.tv_goal_invest = (TextView) findViewById(R.id.tv_goal_invest);
        this.tv_goal_struction = (TextView) findViewById(R.id.tv_goal_struction);
        this.btn_goal_back = (Button) findViewById(R.id.btn_goal_back);
        this.btn_goal_change = (Button) findViewById(R.id.btn_goal_change);
        this.btn_goal_back.setOnClickListener(this.myListener);
        this.iv_goal_share.setOnClickListener(this.myListener);
        this.btn_goal_change.setOnClickListener(this.myListener);
        this.aController.slideFadeIn(this.rl_goal_show_label_bg, 400, 0);
        Intent it = getIntent();
        this.tranmitId = it.getStringExtra("id");
        this.tranmitType = it.getIntExtra(a.a, 0);
        if (this.tranmitType == 0) {
            finish();
            return;
        }
        log("tranmitId" + this.tranmitId);
        initGoalUI(this.tranmitType);
        initStatistics(this.tranmitType);
        showAddGoalDialog();
    }

    private void showAddGoalDialog() {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_act where " + DbUtils.getWhereUserId(this.context) + " limit 2", null);
        if (cursor.getCount() == 0) {
            new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage(getString(R.string.str_add_goal_prompt2)).setPositiveButton(getString(R.string.str_go_add), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    TypeActivity.this.startActivity(new Intent(TypeActivity.this.context, AddActActivity_v2.class));
                    dialog.cancel();
                }
            }).setNegativeButton(getString(R.string.str_next_time), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
        DbUtils.close(cursor);
    }

    private void initStatistics(int tranmitType) {
        ArrayList<XYColumn> arr = DbUtils.getTypeXseries(this.context, tranmitType, 6);
        log(arr.toString());
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        if (Val.col_Str2Int_Map == null) {
            Val.getCol_Str2Int_Map();
        }
        r.setColor(DbUtils.queryColorByActId(this.context, this.tranmitId));
        renderer.addSeriesRenderer(r);
        renderer.setBarSpacing(0.3d);
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
        renderer.setLabelsTextSize((float) DensityUtil.dip2px(this.context, 9.0f));
        XYSeries series = new XYSeries("投入");
        double i = 0.0d;
        this.spendInSevenDay = 0.0d;
        Iterator it = arr.iterator();
        while (it.hasNext()) {
            XYColumn c = (XYColumn) it.next();
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
        this.rl_goal_statistics.addView(new GraphicalView(this.context, new BarChart(dataset, renderer, Type.STACKED)));
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

    private void initGoalUI(int type) {
        Cursor cur = DbUtils.getDb(this.context).rawQuery("Select * from t_act where type is " + type + " and userId is " + DbUtils.queryUserId(this.context), null);
        if (cur.getCount() > 0) {
            cur.moveToNext();
            String actName = cur.getString(cur.getColumnIndex("actName"));
            this.typeName = actName;
            String image = cur.getString(cur.getColumnIndex("image"));
            String color = cur.getString(cur.getColumnIndex("color"));
            String intruction = cur.getString(cur.getColumnIndex("intruction"));
            this.iv_goal_show_label.setImageResource(Val.getLabelIntByName(image));
            this.iv_goal_show_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
            this.tv_goal_name.setText(actName);
            this.tv_goal_struction.setText(intruction);
            return;
        }
        finish();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addModifyLabel() {
        final EditText et = new EditText(this.context);
        et.setBackgroundColor(getResources().getColor(R.color.white));
        et.setHint(getResources().getString(R.string.str_please_input_label_name));
        et.setText(this.typeName);
        new Builder(this.context).setView(et).setTitle(getResources().getString(R.string.str_modify_name)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString();
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(TypeActivity.this.context, TypeActivity.this.getResources().getString(R.string.str_prompt_no_null));
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("actName", str);
                DbUtils.getDb(TypeActivity.this.context).update("t_act", values, " userId is ? and type is ?", new String[]{DbUtils.queryUserId(TypeActivity.this.context), TypeActivity.this.tranmitType + ""});
                GeneralHelper.toastShort(TypeActivity.this.context, "操作成功！");
                TypeActivity.this.initGoalUI(TypeActivity.this.tranmitType);
                TypeActivity.this.setResult(7);
                dialog.cancel();
            }
        }).create().show();
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
