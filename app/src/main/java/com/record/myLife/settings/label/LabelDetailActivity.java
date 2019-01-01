package com.record.myLife.settings.label;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.IDemoChart;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.MyTextDialog.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

import static android.view.View.GONE;

public class LabelDetailActivity extends BaseActivity {
    String LabelName = "";
    Button btn_label_back;
    Button btn_label_mood;
    Button btn_label_subtype;
    Context context;
    LayoutInflater inflater;
    LinearLayout ll_laber_info_items;
    OnClickListener myClickListener = new OnClickListener() {
        @SuppressLint({"NewApi"})
        public void onClick(View v) {
            int id = v.getId();
            if (id != R.id.btn_label_subtype && id == R.id.btn_label_back) {
                LabelDetailActivity.this.finish();
                LabelDetailActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            }
        }
    };
    OnLongClickListener myLongClickListener = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            final String id = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            new Builder(LabelDetailActivity.this.context).setTitle("是否删除？").setPositiveButton(LabelDetailActivity.this.getResources().getString(R.string.str_delete), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Cursor cursor2 = DbUtils.getDb(LabelDetailActivity.this.context).rawQuery("select itemsId from t_routine_link where " + DbUtils.getWhereUserId(LabelDetailActivity.this.context) + " and id is " + id, null);
                    if (cursor2.getCount() > 0) {
                        cursor2.moveToNext();
                        Cursor cursor3 = DbUtils.getDb(LabelDetailActivity.this.context).rawQuery("select remarks from t_act_item where " + DbUtils.getWhereUserId(LabelDetailActivity.this.context) + " and id is " + cursor2.getString(cursor2.getColumnIndex("itemsId")), null);
                        if (cursor3.getCount() > 0) {
                            cursor3.moveToNext();
                            String remarks = cursor3.getString(cursor3.getColumnIndex("remarks"));
                            String label = "[" + LabelDetailActivity.this.getResources().getString(R.string.str_label) + ":" + LabelDetailActivity.this.LabelName + "]";
                            if (remarks != null && remarks.contains(label)) {
                                remarks = remarks.replace(label, "");
                                ContentValues values = new ContentValues();
                                values.put("remarks", remarks);
//                                DbUtils.getDb(LabelDetailActivity.this.context).update("t_act_item", values, " id is ? ", new String[]{itemsId});
                            }
                        }
                        DbUtils.close(cursor3);
                    }
                    DbUtils.close(cursor2);
                    DbUtils.getDb(LabelDetailActivity.this.context).delete("t_routine_link", " id is ? ", new String[]{id});
                    GeneralHelper.toastLong(LabelDetailActivity.this.context, "删除成功！");
                    LabelDetailActivity.this.initLabelInfoUI(LabelDetailActivity.this.tranmitId);
                    dialog.cancel();
                }
            }).setNegativeButton(LabelDetailActivity.this.getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
            return false;
        }
    };
    Button set_btn_noti;
    String tranmitId = "";
    TextView tv_label_detail_avg;
    TextView tv_label_detail_min;
    TextView tv_label_detail_msg;
    TextView tv_label_detail_sum;
    TextView tv_label_detail_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_detail);
        this.context = this;
        this.inflater = getLayoutInflater();
        SystemBarTintManager.setMIUIbar(this);
        this.tranmitId = getIntent().getStringExtra("id");
        if (this.tranmitId == null || this.tranmitId.length() == 0) {
            finish();
        }
        this.ll_laber_info_items = (LinearLayout) findViewById(R.id.ll_laber_info_items);
        this.btn_label_back = (Button) findViewById(R.id.btn_label_back);
        this.set_btn_noti = (Button) findViewById(R.id.set_btn_noti);
        this.tv_label_detail_title = (TextView) findViewById(R.id.tv_label_detail_title);
        this.tv_label_detail_sum = (TextView) findViewById(R.id.tv_label_detail_sum);
        this.tv_label_detail_avg = (TextView) findViewById(R.id.tv_label_detail_avg);
        this.tv_label_detail_min = (TextView) findViewById(R.id.tv_label_detail_min);
        this.tv_label_detail_msg = (TextView) findViewById(R.id.tv_label_detail_msg);
        this.btn_label_back.setOnClickListener(this.myClickListener);
        initLabelInfoUI(this.tranmitId);
    }

    private void initLabelInfoUI(String tranmitId) {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_routine_link where " + DbUtils.getWhereUserId(this.context) + " and subTypeId is " + tranmitId + " order by Time desc", null);
        this.ll_laber_info_items.removeAllViews();
        if (cursor.getCount() > 0) {
            int total = 0;
            int min = 0;
            int max = 0;
            String today = DateTime.getDateString();
            while (cursor.moveToNext()) {
                int dbTake = cursor.getInt(cursor.getColumnIndex("take"));
                total += dbTake;
                if (cursor.isFirst()) {
                    int current = dbTake;
                }
                if (min == 0) {
                    min = dbTake;
                }
                if (dbTake < min) {
                    min = dbTake;
                }
                if (dbTake > max) {
                    max = dbTake;
                }
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String id = cursor.getString(cursor.getColumnIndex("Id"));
                RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_label_detail, null);
                rl.setOnLongClickListener(this.myLongClickListener);
                LayoutParams params = new LayoutParams(-1, -2);
                params.topMargin = 3;
                rl.setLayoutParams(params);
                TextView tv_temp_label_detail_date = (TextView) rl.findViewById(R.id.tv_temp_label_detail_date);
                TextView tv_temp_label_detail_take = (TextView) rl.findViewById(R.id.tv_temp_label_detail_take);
                ((TextView) rl.findViewById(R.id.tv_temp_label_detail_id)).setText("" + id);
                tv_temp_label_detail_date.setText(time);
                tv_temp_label_detail_take.setText(DateTime.calculateTime6((long) dbTake));
                this.ll_laber_info_items.addView(rl);
            }
            this.tv_label_detail_sum.setText("累记花费：" + DateTime.calculateTime6((long) total));
            this.tv_label_detail_avg.setText("平均花费：" + DateTime.calculateTime6((long) (total / cursor.getCount())));
            this.tv_label_detail_min.setText("最少时间：" + DateTime.calculateTime6((long) min));
        } else {
            this.tv_label_detail_sum.setVisibility(View.VISIBLE);
            this.tv_label_detail_avg.setVisibility(GONE);
            this.tv_label_detail_sum.setText("暂无记录哦！");
            this.tv_label_detail_min.setVisibility(GONE);
            this.tv_label_detail_msg.setVisibility(GONE);
        }
        DbUtils.close(cursor);
        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select * from t_sub_type where id is " + tranmitId, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            String name = cursor2.getString(cursor2.getColumnIndex(IDemoChart.NAME));
            this.LabelName = name;
            this.tv_label_detail_title.setText(name);
        } else {
            this.LabelName = getResources().getString(R.string.str_info);
            this.tv_label_detail_title.setText(this.LabelName);
        }
        DbUtils.close(cursor2);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void log(String str) {
        Log.i("override Login", ":" + str);
    }
}
