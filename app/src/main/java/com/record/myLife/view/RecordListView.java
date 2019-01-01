package com.record.myLife.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.Act2;
import com.record.bean.TimePieRecord;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.add.AddRecordWheelActivity;
import com.record.myLife.history.ItemDetailActivity;
import com.record.myLife.settings.label.LabelSelectActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class RecordListView extends RelativeLayout {
    private Context context;
    String currentDate;
    LayoutInflater inflater;
    EditText isAddLabelEditText = null;
    long moonCal;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecordListView.this.inflaterView();
        }
    };
    OnClickListener myItemsClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_tem_history_record_add_label) {
                RecordListView.this.clickAddLabel(v);
            } else if (id == R.id.iv_tem_history_record_modify) {
                RecordListView.this.clickModify(v);
            } else if (id == R.id.rl_tem_history_record_item_outter) {
                RecordListView.this.clickAddRecordItems(v);
            } else if (id == R.id.rl_tem_history_record_color_label) {
                RecordListView.this.clickLabelIntoItemDetail(v);
            }
        }
    };
    String theDayBeforeYesterday = DateTime.beforeNDays2Str(-2);
    Thread thread;
    String today = DateTime.getDateString();
    long twentyFourCal;
    UiComponent uiComponent;
    ArrayList<View> viewList = new ArrayList();
    String yesterday = DateTime.beforeNDays2Str(-1);
    long zeroCal;

    class MyTextWatcher implements TextWatcher {
        EditText editText;

        public MyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        public void afterTextChanged(Editable s) {
            String str = s.toString().trim();
            if (str != null && str.length() > 0) {
                String itemsId = ((TextView) ((RelativeLayout) this.editText.getParent().getParent()).findViewById(R.id.tv_tem_history_record_id)).getText().toString();
                ContentValues values = new ContentValues();
                values.put("remarks", s.toString());
                DbUtils.getDb(RecordListView.this.context).update("t_act_item", values, "id = " + itemsId, null);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    class UiComponent {
        public LinearLayout ll_history_v2_items;

        UiComponent() {
        }

        private void setUiComponent(UiComponent uiComponent) {
            uiComponent.ll_history_v2_items = (LinearLayout) RecordListView.this.findViewById(R.id.ll_history_v2_items);
        }
    }

    private void inflaterView() {
        LinearLayout ll_history_v2_items = (LinearLayout) findViewById(R.id.ll_items);
        ll_history_v2_items.removeAllViews();
        Iterator it = this.viewList.iterator();
        while (it.hasNext()) {
            ll_history_v2_items.addView((View) it.next());
        }
    }

    public RecordListView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RecordListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.inflater.inflate(R.layout.temp_recordlist, this);
        init();
    }

    public RecordListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.inflater.inflate(R.layout.temp_recordlist, this);
        init();
    }

    private void init() {
        this.inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (this.uiComponent == null) {
            this.uiComponent = new UiComponent();
        }
        this.uiComponent.setUiComponent(this.uiComponent);
    }

    public void init(final String date) {
        init();
        this.currentDate = date;
        this.viewList = new ArrayList();
        if (this.thread == null || !this.thread.isAlive()) {
            this.thread = new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    RecordListView.this.initDate(date);
                    RecordListView.this.myHandler.sendEmptyMessage(1);
                }
            });
            this.thread.start();
        }
    }

    private View initDate(String date) {
        this.zeroCal = DateTime.pars2Calender(date + " 00:00:00").getTime().getTime();
        this.moonCal = DateTime.pars2Calender(date + " 12:00:00").getTime().getTime();
        this.twentyFourCal = DateTime.pars2Calender(date + " 23:59:59").getTime().getTime();
        String zeroTime = date + " 00:00:00";
        String twentyFourTime = date + " 24:00:00";
        addItems("Select * from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and startTime < '" + zeroTime + "' and stopTime > '" + zeroTime + "' and stopTime <= '" + twentyFourTime + "' order by startTime", 0);
        addItems("Select * from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and startTime >= '" + zeroTime + "' and startTime <= '" + twentyFourTime + "'  order by startTime", 1);
        if (this.viewList.size() == 0) {
            this.viewList.add(createEmptyItems("暂无记录哦！"));
        }
        return null;
    }

    private LinearLayout addItems(String sql, int findIntervalOnFirst) {
        Cursor cursor2 = DbUtils.getDb2(this.context).rawQuery(sql, null);
        if (cursor2.getCount() > 0) {
            String lastEndTime = null;
            int lastId = 0;
            while (cursor2.moveToNext()) {
                int id = cursor2.getInt(cursor2.getColumnIndex("id"));
                int goalId = cursor2.getInt(cursor2.getColumnIndex("actId"));
                String startTime = cursor2.getString(cursor2.getColumnIndex("startTime"));
                String stopTime = cursor2.getString(cursor2.getColumnIndex("stopTime"));
                String remark = cursor2.getString(cursor2.getColumnIndex("remarks"));
                if (findIntervalOnFirst > 0 && cursor2.isFirst()) {
                    Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select id,stopTime from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and stopTime <= '" + startTime + "'  order by stopTime desc limit 1", null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        String tempStopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                        int tempid = cursor.getInt(cursor.getColumnIndex("id"));
                        int range = DateTime.cal_secBetween(tempStopTime, startTime);
                        if (range > 180 && range < 43200) {
                            this.viewList.add(createItems(tempid, 0, tempStopTime, startTime, ""));
                        }
                    }
                    DbUtils.close(cursor);
                }
                if (lastId > 0 && ((startTime.contains(this.today) || startTime.contains(this.yesterday) || startTime.contains(this.theDayBeforeYesterday)) && DateTime.cal_secBetween(lastEndTime, startTime) > 180)) {
                    this.viewList.add(createItems(lastId, 0, lastEndTime, startTime, ""));
                }
                this.viewList.add(createItems(id, goalId, startTime, stopTime, remark));
                lastEndTime = stopTime;
                lastId = id;
            }
        }
        DbUtils.close(cursor2);
        return null;
    }

    public RelativeLayout createEmptyItems(String note) {
        RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.temp_hitory_item_empty, null);
        ((TextView) rl.findViewById(R.id.tp_tem_history_record_name)).setText(note);
        return rl;
    }

    public RelativeLayout createItems(int id, int goadId, String startTime, String endTime, String remark) {
        RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.temp_hitory_item, null);
        TextView tp_tem_history_record_name = (TextView) rl.findViewById(R.id.tp_tem_history_record_name);
        TimePie tp_tem_history_record_time_pie = (TimePie) rl.findViewById(R.id.tp_tem_history_record_time_pie);
        ImageView iv_tem_history_record_label = (ImageView) rl.findViewById(R.id.iv_tem_history_record_label);
        ImageView iv_tem_history_record_color = (ImageView) rl.findViewById(R.id.iv_tem_history_record_color);
        ImageView iv_tem_history_record_modify = (ImageView) rl.findViewById(R.id.iv_tem_history_record_modify);
        ImageView iv_tem_history_record_add_label = (ImageView) rl.findViewById(R.id.iv_tem_history_record_add_label);
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
                if (!startTime.contains(this.today)) {
                    if (!startTime.contains(this.yesterday)) {
                        if (!startTime.contains(this.theDayBeforeYesterday)) {
                            iv_tem_history_record_modify.setVisibility(4);
                        }
                    }
                }
                iv_tem_history_record_modify.setOnClickListener(this.myItemsClickListener);
            }
            iv_tem_history_record_add_label.setOnClickListener(this.myItemsClickListener);
            rl_tem_history_record_color_label.setOnClickListener(this.myItemsClickListener);
            et_tem_history_record_mark.addTextChangedListener(new MyTextWatcher(et_tem_history_record_mark));
            Act2 act = DbUtils.getAct2ByActId(this.context, goadId + "");
            tp_tem_history_record_time_pie.setRecord(new TimePieRecord(startTime, endTime, setTextColor(startTime, endTime, tv_tem_history_record_time_start, tv_tem_history_record_time_end)));
            iv_tem_history_record_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(act.getColor())).intValue());
            iv_tem_history_record_label.setImageResource(((Integer) Val.label2IntMap2.get(act.getImage())).intValue());
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
        } else {
            iv_tem_history_record_modify.setVisibility(8);
            iv_tem_history_record_add_label.setVisibility(8);
            et_tem_history_record_mark.setVisibility(8);
            tp_tem_history_record_name.setVisibility(8);
            tp_tem_history_record_take.setVisibility(8);
            int textColor = setTextColor(startTime, endTime, tv_tem_history_record_time_start, tv_tem_history_record_time_end);
            tp_tem_history_record_time_pie.setRecord(new TimePieRecord(startTime, endTime, getResources().getColor(R.color.black_tran_es)));
            rl_tem_history_record_item_outter.setOnClickListener(this.myItemsClickListener);
        }
        return rl;
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
        ((Activity) this.context).startActivityForResult(it, 2);
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

    private void goAddRecordActivity(String startTime, String stopTime) {
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
            ((Activity) this.context).startActivityForResult(it, 27);
            return;
        }
        it = new Intent(this.context, AddRecordWheelActivity.class);
        it.putExtra("startTime", startTime);
        it.putExtra("stopTime", stopTime);
        ((Activity) this.context).startActivityForResult(it, 27);
    }

    private void clickModify(final View v) {
        final String temp_id = ((TextView) ((RelativeLayout) v.getParent().getParent()).getChildAt(0)).getText().toString();
        new Builder(this.context).setTitle((int) R.string.str_choose).setPositiveButton((int) R.string.str_modify, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(RecordListView.this.context, AddRecordDigitActivity.class);
                it.putExtra("itemId", temp_id);
                ((Activity) RecordListView.this.context).startActivityForResult(it, 24);
                dialog.cancel();
            }
        }).setNeutralButton((int) R.string.str_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                RecordListView.this.deleteComfire(v, temp_id);
                dialog.cancel();
            }
        }).setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void clickAddLabel(View v) {
        RelativeLayout rl = (RelativeLayout) v.getParent().getParent();
        String temp_id = ((TextView) rl.getChildAt(0)).getText().toString();
        this.isAddLabelEditText = (EditText) rl.findViewById(R.id.tp_tem_history_record_mark);
        int actType = DbUtils.queryActTypeByItemsId(this.context, Integer.parseInt(temp_id));
        if (actType == 11) {
            actType = 10;
        }
        Intent it = new Intent(this.context, LabelSelectActivity.class);
        it.putExtra("itemsId", Integer.parseInt(temp_id));
        it.putExtra("actType", actType);
        this.context.startActivity(it);
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
                Cursor cursor2 = DbUtils.getDb(RecordListView.this.context).rawQuery("select * from t_act_item where " + DbUtils.getWhereUserId(RecordListView.this.context) + " and id = " + temp_id, null);
                if (cursor2.getCount() > 0) {
                    cursor2.moveToNext();
                    isUpload = cursor2.getInt(cursor2.getColumnIndex("isUpload"));
                    temp_actId = cursor2.getInt(cursor2.getColumnIndex("actId"));
                    startTime = cursor2.getString(cursor2.getColumnIndex("startTime"));
                    stopTime = cursor2.getString(cursor2.getColumnIndex("stopTime"));
                }
                DbUtils.close(cursor2);
                if (isUpload > 0) {
                    values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(1));
                    values.put("deleteTime", DateTime.getTimeString());
                    values.put("endUpdateTime", DateTime.getTimeString());
                    DbUtils.getDb(RecordListView.this.context).update("t_act_item", values, "id is ?", new String[]{temp_id});
                } else {
                    DbUtils.getDb(RecordListView.this.context).delete("t_act_item", "id is ?", new String[]{temp_id});
                }
                GeneralHelper.toastShort(RecordListView.this.context, "删除成功！");
                dialog.cancel();
                Cursor cursor = DbUtils.getDb(RecordListView.this.context).rawQuery("select * from t_routine_link where itemsId is " + temp_id, null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int isUpload2 = cursor.getInt(cursor.getColumnIndex("isUpload"));
                        int Id = cursor.getInt(cursor.getColumnIndex("Id"));
                        if (isUpload2 > 0) {
                            values = new ContentValues();
                            values.put("isDelete", Integer.valueOf(1));
                            values.put("deleteTime", DateTime.getTimeString());
                            values.put("endUpdateTime", DateTime.getTimeString());
                            DbUtils.getDb(RecordListView.this.context).update("t_routine_link", values, "Id is " + Id, null);
                        } else {
                            DbUtils.getDb(RecordListView.this.context).delete("t_routine_link", "Id is " + Id, null);
                        }
                    }
                }
                DbUtils.close(cursor);
                try {
                    String tempStartDate = "";
                    tempStartDate = startTime.substring(0, startTime.indexOf(" "));
                    String tempEndDate = stopTime.substring(0, stopTime.indexOf(" "));
                    TreeSet<String> changeDateArr = new TreeSet();
                    changeDateArr.add(tempStartDate);
                    changeDateArr.add(tempEndDate);
                    TreeSet<Integer> goalIdSet = new TreeSet();
                    goalIdSet.add(Integer.valueOf(temp_actId));
                    new Thread(new AllocationAndStaticsRunnable(RecordListView.this.context, goalIdSet, changeDateArr)).start();
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                RecordListView.this.init(RecordListView.this.currentDate);
                dialog.cancel();
            }
        }).create().show();
    }
}
