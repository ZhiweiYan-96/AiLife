package com.record.myLife.history;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.Act2;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.settings.label.LabelSelectActivity;
import com.record.myLife.view.DeliberateStarView;
import com.record.myLife.view.MyGoalItemsLayout;
import com.record.myLife.view.MyGoalItemsLayout.MyOnItemsClickListener;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils;
import com.record.view.wheel.widget.NumericWheelAdapter;
import com.record.view.wheel.widget.OnWheelChangedListener;
import com.record.view.wheel.widget.WheelView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class ItemDetailActivity extends BaseActivity {
    public static HashMap<Integer, Integer> Act2TypeMap;
    static Context context;
    private int MODIFY_END_TIME = 1;
    private int MODIFY_START_TIME = 0;
    private int MODIFY_TYPE = 2;
    String addEndDate = "";
    String addEndTime = "";
    String addStartDate = "";
    String addStartTime = "";
    OnClickListener addTimeOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_tem_time_cancel) {
                ItemDetailActivity.this.popup.dismiss();
            } else if (id == R.id.btn_tem_time_save) {
                DecimalFormat df = new DecimalFormat("00");
                if (ItemDetailActivity.this.MODIFY_TYPE == ItemDetailActivity.this.MODIFY_START_TIME) {
                    ItemDetailActivity.this.addStartTime = ItemDetailActivity.this.addStartDate + " " + df.format((long) ItemDetailActivity.this.hour2) + ":" + df.format((long) ItemDetailActivity.this.min2) + ":00";
                    ItemDetailActivity.this.addEndTime = ItemDetailActivity.this.stopTime;
                } else if (ItemDetailActivity.this.MODIFY_TYPE == ItemDetailActivity.this.MODIFY_END_TIME) {
                    ItemDetailActivity.this.addStartTime = ItemDetailActivity.this.startTime;
                    ItemDetailActivity.this.addEndTime = ItemDetailActivity.this.addEndDate + " " + df.format((long) ItemDetailActivity.this.hour2) + ":" + df.format((long) ItemDetailActivity.this.min2) + ":00";
                }
                try {
                    ItemDetailActivity.this.addStartTime = ItemDetailActivity.this.addStartTime.substring(0, ItemDetailActivity.this.addStartTime.lastIndexOf(":")) + ":00";
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                ItemDetailActivity.this.log("addStartTime:" + ItemDetailActivity.this.addStartTime + ",addEndTime:" + ItemDetailActivity.this.addEndTime);
                long addStartTimeInt = DateTime.pars2Calender(ItemDetailActivity.this.addStartTime).getTime().getTime();
                long addEndTimeInt = DateTime.pars2Calender(ItemDetailActivity.this.addEndTime).getTime().getTime();
                long nowInt = Calendar.getInstance().getTime().getTime();
                if (addEndTimeInt < addStartTimeInt) {
                    ToastUtils.toastShort(ItemDetailActivity.context, ItemDetailActivity.this.getString(R.string.str_starttime_should_less_than_endtime));
                } else if ((addEndTimeInt - addStartTimeInt) / 1000 <= 60) {
                    ToastUtils.toastShort(ItemDetailActivity.context, ItemDetailActivity.this.getString(R.string.str_add_time_too_short));
                } else if (addEndTimeInt > nowInt) {
                    ToastUtils.toastLong(ItemDetailActivity.context, ItemDetailActivity.this.getString(R.string.str_endtime_should_less_than_now));
                } else {
                    ItemDetailActivity.this.showIsContainOtherDialog();
                }
            }
        }
    };
    private Button btn_itemdetail_del;
    private OnWheelChangedListener changedListener3 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            ItemDetailActivity.this.hour2 = newValue;
            ItemDetailActivity.this.isOverrideOther();
        }
    };
    private OnWheelChangedListener changedListener4 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            ItemDetailActivity.this.min2 = newValue;
            ItemDetailActivity.this.isOverrideOther();
        }
    };
    String checkActId = "";
    private DeliberateStarView dl_item_detail;
    private TextView et_itemdetail_remark;
    String firstStartTime;
    MyOnItemsClickListener goalItemsIdClickLister = new MyOnItemsClickListener() {
        public void onClick(View v, String id) {
            ItemDetailActivity.this.checkActId = id;
            ItemDetailActivity.this.log("checkActId:" + ItemDetailActivity.this.checkActId);
        }
    };
    int hour2 = 0;
    LayoutInflater inflater;
    String isEnd = "";
    private ImageView iv_itemdetail__label_add;
    private ImageView iv_itemdetail_color;
    private ImageView iv_itemdetail_label;
    int min2 = 0;
    OnClickListener myListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_itemdetail_back) {
                ItemDetailActivity.this.onBackPressed();
            } else if (id == R.id.iv_itemdetail__label_add) {
                int actType = DbUtils.queryActTypeByItemsId(ItemDetailActivity.context, Integer.parseInt(ItemDetailActivity.this.temp_id));
                if (actType == 11) {
                    actType = 10;
                }
                Intent it = new Intent(ItemDetailActivity.context, LabelSelectActivity.class);
                it.putExtra("itemsId", Integer.parseInt(ItemDetailActivity.this.temp_id));
                it.putExtra("actType", actType);
                ItemDetailActivity.this.startActivity(it);
            } else if (id == R.id.tv_itemdetail_change || id == R.id.tv_itemdetail_date) {
                ItemDetailActivity.this.showChangeType_v2();
            } else if (id == R.id.btn_itemdetail_del) {
                new Builder(ItemDetailActivity.context).setTitle((CharSequence) "是否删除").setMessage((CharSequence) "删除后相关信息也会被删除!").setNegativeButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton((CharSequence) "删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values;
                        int isUpload = DbUtils.queryIsUploadByActItemId(ItemDetailActivity.context, ItemDetailActivity.this.temp_id);
                        ItemDetailActivity.this.log("查询所删除是否上传,isUpload：" + isUpload);
                        if (isUpload > 0) {
                            values = new ContentValues();
                            values.put("isDelete", Integer.valueOf(1));
                            values.put("deleteTime", DateTime.getTimeString());
                            values.put("endUpdateTime", DateTime.getTimeString());
                            DbUtils.getDb(ItemDetailActivity.context).update("t_act_item", values, "id is ?", new String[]{ItemDetailActivity.this.temp_id});
                        } else {
                            DbUtils.getDb(ItemDetailActivity.context).delete("t_act_item", "id is ?", new String[]{ItemDetailActivity.this.temp_id});
                            ItemDetailActivity.this.log("删除记录");
                        }
                        GeneralHelper.toastShort(ItemDetailActivity.context, "删除成功！");
                        dialog.cancel();
                        Cursor cursor = DbUtils.getDb(ItemDetailActivity.context).rawQuery("select * from t_routine_link where itemsId is " + ItemDetailActivity.this.temp_id, null);
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                int isUpload2 = cursor.getInt(cursor.getColumnIndex("isUpload"));
                                int Id = cursor.getInt(cursor.getColumnIndex("Id"));
                                if (isUpload2 > 0) {
                                    values = new ContentValues();
                                    values.put("isDelete", Integer.valueOf(1));
                                    values.put("deleteTime", DateTime.getTimeString());
                                    values.put("endUpdateTime", DateTime.getTimeString());
                                    DbUtils.getDb(ItemDetailActivity.context).update("t_routine_link", values, "Id is " + Id, null);
                                } else {
                                    DbUtils.getDb(ItemDetailActivity.context).delete("t_routine_link", "Id is " + Id, null);
                                }
                            }
                        }
                        DbUtils.close(cursor);
                        String tempStartDate = "";
                        try {
                            tempStartDate = ItemDetailActivity.this.startTime.substring(0, ItemDetailActivity.this.startTime.indexOf(" "));
                            String tempEndDate = ItemDetailActivity.this.stopTime.substring(0, ItemDetailActivity.this.stopTime.indexOf(" "));
                            TreeSet<String> changeDateArr = new TreeSet();
                            changeDateArr.add(tempStartDate);
                            changeDateArr.add(tempEndDate);
                            TreeSet<Integer> goalIdSet = new TreeSet();
                            goalIdSet.add(Integer.valueOf(ItemDetailActivity.this.temp_actid));
                            new Thread(new AllocationAndStaticsRunnable(ItemDetailActivity.context, goalIdSet, changeDateArr)).start();
                        } catch (Exception e) {
                            DbUtils.exceptionHandler(e);
                        }
                        Intent it = new Intent();
                        it.putExtra("id", ItemDetailActivity.this.temp_id);
                        it.putExtra("take", ItemDetailActivity.this.take);
                        ItemDetailActivity.this.setResult(11, it);
                        ItemDetailActivity.this.finish();
                    }
                }).create().show();
            } else if (id == R.id.rl_itemdetail_label_bg) {
                ItemDetailActivity.this.showSelectActDialog();
            }
        }
    };
    TextWatcher myTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            String string = ItemDetailActivity.this.et_itemdetail_remark.getText().toString();
            if (string != null && string.length() > 0) {
                ContentValues values = new ContentValues();
                values.put("remarks", string.trim());
                if (DbUtils.queryIsUploadByActItemId(ItemDetailActivity.context, ItemDetailActivity.this.temp_id) > 0) {
                    values.put("endUpdateTime", DateTime.getTimeString());
                }
                DbUtils.getDb(ItemDetailActivity.context).update("t_act_item", values, "id is " + ItemDetailActivity.this.temp_id, null);
            }
        }
    };
    PopupWindow popup = null;
    boolean refleshUI = false;
    private RelativeLayout rl_itemdetail_label_bg;
    String startTime;
    String stopTime;
    int take;
    HashMap<Integer, TempActBean> tempActMap = null;
    int temp_actid = 0;
    int temp_goalType = 0;
    String temp_id = "";
    String temp_remark = "";
    String theDayBefore = DateTime.beforeNDays2Str(-2);
    String today = DateTime.getDateString();
    private Button tv_itemdetail_back;
    private Button tv_itemdetail_change;
    private TextView tv_itemdetail_date;
    private TextView tv_itemdetail_from;
    private TextView tv_itemdetail_take;
    private TextView tv_itemdetail_title;
    private TextView tv_tem_time_info;
    String yesterday = DateTime.beforeNDays2Str(-1);

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);
        SystemBarTintManager.setMIUIbar(this);
        context = this;
        this.inflater = getLayoutInflater();
        this.tv_itemdetail_title = (TextView) findViewById(R.id.tv_itemdetail_title);
        this.tv_itemdetail_take = (TextView) findViewById(R.id.tv_itemdetail_take);
        this.tv_itemdetail_date = (TextView) findViewById(R.id.tv_itemdetail_date);
        this.tv_itemdetail_from = (TextView) findViewById(R.id.tv_itemdetail_from);
        this.et_itemdetail_remark = (TextView) findViewById(R.id.et_itemdetail_remark);
        this.rl_itemdetail_label_bg = (RelativeLayout) findViewById(R.id.rl_itemdetail_label_bg);
        this.iv_itemdetail_label = (ImageView) findViewById(R.id.iv_itemdetail_label);
        this.iv_itemdetail__label_add = (ImageView) findViewById(R.id.iv_itemdetail__label_add);
        this.iv_itemdetail_color = (ImageView) findViewById(R.id.iv_itemdetail_color);
        this.tv_itemdetail_back = (Button) findViewById(R.id.tv_itemdetail_back);
        this.tv_itemdetail_change = (Button) findViewById(R.id.tv_itemdetail_change);
        this.btn_itemdetail_del = (Button) findViewById(R.id.btn_itemdetail_del);
        this.dl_item_detail = (DeliberateStarView) findViewById(R.id.dl_item_detail);
        this.et_itemdetail_remark.addTextChangedListener(this.myTextWatcher);
        this.tv_itemdetail_back.setOnClickListener(this.myListener);
        this.btn_itemdetail_del.setOnClickListener(this.myListener);
        this.tv_itemdetail_change.setOnClickListener(this.myListener);
        this.iv_itemdetail__label_add.setOnClickListener(this.myListener);
        this.btn_itemdetail_del.setVisibility(8);
        this.tv_itemdetail_change.setVisibility(8);
        Intent it = getIntent();
        this.temp_id = it.getStringExtra("Id");
        log("temp_id:" + this.temp_id);
        if (it.getAction().equals(Val.INTENT_ACTION_ITEMS)) {
            updateUI();
        }
    }

    private void updateUI() {
        Cursor cur = DbUtils.getDb(context).rawQuery("Select a.*,d.keyVal1,d.keyVal2,d.keyVal3,d.keyVal4,d.totalVal from t_act_item a left join t_deliberate_record as d on a.id = d.itemsId where a.id is " + this.temp_id, null);
        if (cur.getCount() > 0) {
            cur.moveToNext();
            int actid = cur.getInt(cur.getColumnIndex("actId"));
            this.temp_actid = actid;
            this.temp_goalType = DbUtils.queryActTypeById(context, this.temp_actid + "").intValue();
            this.take = cur.getInt(cur.getColumnIndex("take"));
            this.startTime = cur.getString(cur.getColumnIndex("startTime"));
            this.stopTime = cur.getString(cur.getColumnIndex("stopTime"));
            this.temp_remark = cur.getString(cur.getColumnIndex("remarks"));
            this.isEnd = cur.getString(cur.getColumnIndex("isEnd"));
            float keyVal1 = cur.getFloat(cur.getColumnIndex("keyVal1"));
            float keyVal2 = cur.getFloat(cur.getColumnIndex("keyVal2"));
            float keyVal3 = cur.getFloat(cur.getColumnIndex("keyVal3"));
            float keyVal4 = cur.getFloat(cur.getColumnIndex("keyVal4"));
            float totalVal = cur.getFloat(cur.getColumnIndex("totalVal"));
            if (totalVal > 0.0f) {
                this.dl_item_detail.setAllRating(keyVal1, keyVal2, keyVal3, keyVal4, totalVal);
                this.dl_item_detail.setVisibility(0);
            } else {
                this.dl_item_detail.setVisibility(8);
            }
            if (this.firstStartTime == null) {
                this.firstStartTime = this.startTime;
            }
            this.tv_itemdetail_date.setText(DateTime.getMonDayWeekFromTimestamp2(this.startTime) + "　" + DateTime.getHourAndMinFromTimestamp(this.startTime) + "-" + DateTime.getHourAndMinFromTimestamp(this.stopTime));
            this.addStartDate = this.startTime.substring(0, this.startTime.indexOf(" "));
            this.addEndDate = this.stopTime.substring(0, this.stopTime.indexOf(" "));
            if (this.temp_remark == null) {
                this.temp_remark = "";
            }
            log("today:" + this.today + ",yesterday:" + this.yesterday);
            if (this.startTime.contains(this.today) || this.startTime.contains(this.yesterday) || this.startTime.contains(this.theDayBefore)) {
                this.tv_itemdetail_change.setVisibility(0);
                this.btn_itemdetail_del.setVisibility(0);
                this.tv_itemdetail_date.setOnClickListener(this.myListener);
                this.rl_itemdetail_label_bg.setOnClickListener(this.myListener);
            }
            Act2 act = DbUtils.getAct2ByActId(context, actid + "");
            this.tv_itemdetail_title.setText(act.getActName());
            this.iv_itemdetail_label.setImageResource(Val.getLabelIntByName(act.getImage()));
            this.iv_itemdetail_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(act.getColor())).intValue());
            this.tv_itemdetail_take.setText(DateTime.calculateTime2((long) this.take));
            this.et_itemdetail_remark.setText(this.temp_remark);
            this.iv_itemdetail__label_add.setVisibility(0);
            if (!"1".equals(this.isEnd) && this.stopTime.contains(this.today)) {
                this.tv_itemdetail_take.setText(DateTime.calculateTime2((long) this.take) + " (进行中)");
                this.btn_itemdetail_del.setVisibility(8);
                return;
            }
            return;
        }
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (24 == requestCode && resultCode == -1) {
            Intent it;
            if (data.getIntExtra("isDeleteItemsId", 0) > 0) {
                it = new Intent();
                it.putExtra("id", this.temp_id);
                it.putExtra("take", this.take);
                setResult(11, it);
                return;
            }
            it = new Intent();
            it.putExtra("id", this.temp_id);
            it.putExtra("take", this.take);
            if (this.MODIFY_TYPE == this.MODIFY_START_TIME || this.MODIFY_TYPE == this.MODIFY_END_TIME) {
                setResult(11, it);
            }
            if (this.refleshUI) {
                setResult(11, it);
            }
            if (!"1".equals(this.isEnd) && this.stopTime.contains(DateTime.getDateString())) {
                sendBroadcast(new Intent(Val.INTENT_ACTION_MODIFY_ACTCOUNT));
            }
            showUploadToUpdateGoalTimes2();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (this.popup == null || !this.popup.isShowing()) {
            if (this.temp_id != null && this.temp_id.length() > 0) {
                String remark = this.et_itemdetail_remark.getText().toString();
                if (!remark.equals(this.temp_remark)) {
                    ContentValues values = new ContentValues();
                    values.put("remarks", remark);
                    DbUtils.getDb(context).update("t_act_item", values, " Id is ?", new String[]{this.temp_id});
                    GeneralHelper.toastShort(context, "保存成功！");
                    setResult(11);
                }
            }
            super.onBackPressed();
            return;
        }
        this.popup.dismiss();
    }

    private void showChangeType_v2() {
        Intent it = new Intent(context, AddRecordDigitActivity.class);
        it.putExtra("itemId", this.temp_id);
        startActivityForResult(it, 24);
    }

    private void showSelectActDialog() {
        View hs = (HorizontalScrollView) this.inflater.inflate(R.layout.tem_horizontal, null);
        LinearLayout ll = new MyGoalItemsLayout((Activity) context, (LinearLayout) hs.findViewById(R.id.ll_tem_time_items), this.goalItemsIdClickLister).getAddItems();
        new Builder(context).setTitle(getResources().getString(R.string.str_change_type)).setView(hs).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (ItemDetailActivity.this.checkActId == null || ItemDetailActivity.this.checkActId.length() == 0) {
                    GeneralHelper.toastShort(ItemDetailActivity.context, ItemDetailActivity.this.getResources().getString(R.string.str_please_choose));
                } else if (ItemDetailActivity.this.temp_actid == Integer.parseInt(ItemDetailActivity.this.checkActId)) {
                    dialog.cancel();
                } else {
                    int type = DbUtils.queryActTypeById(ItemDetailActivity.context, ItemDetailActivity.this.checkActId).intValue();
                    ContentValues values = new ContentValues();
                    values.put("actId", ItemDetailActivity.this.checkActId);
                    values.put("actType", Integer.valueOf(type));
                    if (DbUtils.queryIsUploadByActItemId(ItemDetailActivity.context, ItemDetailActivity.this.temp_id) > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    DbUtils.getDb(ItemDetailActivity.context).update("t_act_item", values, " id is ? ", new String[]{ItemDetailActivity.this.temp_id});
                    TreeSet<Integer> goalIdArr = new TreeSet();
                    goalIdArr.add(Integer.valueOf(Integer.parseInt(ItemDetailActivity.this.checkActId)));
                    goalIdArr.add(Integer.valueOf(ItemDetailActivity.this.temp_actid));
                    TreeSet<String> DateArr = new TreeSet();
                    DateArr.add(ItemDetailActivity.this.startTime.substring(0, ItemDetailActivity.this.startTime.indexOf(" ")));
                    DateArr.add(ItemDetailActivity.this.stopTime.substring(0, ItemDetailActivity.this.stopTime.indexOf(" ")));
                    new Thread(new AllocationAndStaticsRunnable(ItemDetailActivity.context, goalIdArr, DateArr)).start();
                    ItemDetailActivity.this.showUploadToUpdateGoalTimes();
                    ItemDetailActivity.this.updateUI();
                    ItemDetailActivity.this.refleshUI = true;
                    ItemDetailActivity.this.setResult(11);
                    dialog.cancel();
                    ItemDetailActivity.this.checkActId = "";
                }
            }
        }).setNegativeButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showUploadToUpdateGoalTimes() {
        if (DbUtils.queryIsUploadByActItemId(context, this.temp_id) > 0) {
            DialogUtils.showPromptWithNoShow(context, getString(R.string.str_modify_items_had_upload), Val.CONFIGURE_IS_SHOW_NEED_UPLOADING_TO_UPDATE_GOAL_TIMES);
        }
    }

    private void showUploadToUpdateGoalTimes2() {
        if (DbUtils.queryIsUploadByActItemId(context, this.temp_id) > 0) {
            DialogUtils.showPromptWithNoShow(context, getString(R.string.str_modify_items_had_upload), Val.CONFIGURE_IS_SHOW_NEED_UPLOADING_TO_UPDATE_GOAL_TIMES2);
        }
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

    public void showPopu(String type) {
        RelativeLayout rl = (RelativeLayout) getLayoutInflater().inflate(R.layout.tem_time_single, null);
        TextView tv_tem_time_title = (TextView) rl.findViewById(R.id.tv_tem_time_title);
        this.tv_tem_time_info = (TextView) rl.findViewById(R.id.tv_tem_time_info);
        tv_tem_time_title.setText(type);
        WheelView hours2 = (WheelView) rl.findViewById(R.id.wv_tem_hour2);
        hours2.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        hours2.setLabel("时");
        hours2.addChangingListener(this.changedListener3);
        hours2.setCyclic(true);
        WheelView mins2 = (WheelView) rl.findViewById(R.id.wv_tem_min2);
        mins2.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        mins2.setLabel("分");
        mins2.addChangingListener(this.changedListener4);
        mins2.setCyclic(true);
        int space;
        int first;
        int last;
        String hour;
        String min;
        if (this.MODIFY_TYPE == this.MODIFY_START_TIME) {
            space = this.startTime.indexOf(" ");
            first = this.startTime.indexOf(":");
            last = this.startTime.lastIndexOf(":");
            hour = this.startTime.substring(space + 1, first);
            min = this.startTime.substring(first + 1, last);
            hours2.setCurrentItem(Integer.parseInt(hour));
            mins2.setCurrentItem(Integer.parseInt(min));
        } else if (this.MODIFY_TYPE == this.MODIFY_END_TIME) {
            space = this.stopTime.indexOf(" ");
            first = this.stopTime.indexOf(":");
            last = this.stopTime.lastIndexOf(":");
            hour = this.stopTime.substring(space + 1, first);
            min = this.stopTime.substring(first + 1, last);
            hours2.setCurrentItem(Integer.parseInt(hour));
            mins2.setCurrentItem(Integer.parseInt(min));
        } else {
            GeneralUtils.toastShort(context, "请先选择修改类型...");
            return;
        }
        Button btn_tem_time_cancel = (Button) rl.findViewById(R.id.btn_tem_time_cancel);
        ((Button) rl.findViewById(R.id.btn_tem_time_save)).setOnClickListener(this.addTimeOnClickListener);
        btn_tem_time_cancel.setOnClickListener(this.addTimeOnClickListener);
        if (this.popup == null || !this.popup.isShowing()) {
            this.popup = new PopupWindow(rl, -1, -2);
            this.popup.setOutsideTouchable(true);
            if (!this.popup.isShowing()) {
                this.popup.showAtLocation(this.tv_itemdetail_back, 80, 0, 0);
            }
        } else if (this.popup == null && this.popup.isShowing()) {
            this.popup.dismiss();
        }
    }

    private void showIsContainOtherDialog() {
        Cursor cursor = DbUtils.getDb(context).rawQuery("select Id from t_act_item where " + DbUtils.getWhereUserId(context) + " and  isEnd is 1 and startTime > '" + this.addStartTime + "' and stopTime < '" + this.addEndTime + "' and id is not " + this.temp_id, null);
        if (cursor.getCount() > 0) {
            new Builder(context).setTitle(getResources().getString(R.string.str_is_modify)).setMessage(getResources().getString(R.string.str_add_time_contain_other_items_1).replace("{几个}", "" + cursor.getCount())).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setPositiveButton(getResources().getString(R.string.str_modify), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ItemDetailActivity.this.saveChange();
                    dialog.cancel();
                }
            }).create().show();
        } else {
            saveChange();
        }
        DbUtils.close(cursor);
    }

    private void saveChange() {
        TreeSet<Integer> goalIdSet1 = DbUtils.updateDbActItem_ChangeEndTime(context, this.addStartTime, " and id is not " + this.temp_id);
        TreeSet<Integer> goalIdSet2 = DbUtils.updateDbActItem_ChangeStartTime(context, this.addEndTime, " and id is not " + this.temp_id);
        TreeSet<Integer> goalIdSet3 = DbUtils.deleteActItem_deleteRecords(context, this.addStartTime, this.addEndTime, " and id is not " + this.temp_id);
        int take = DateTime.cal_secBetween(this.addStartTime, this.addEndTime);
        ContentValues values = new ContentValues();
        values.put("startTime", this.addStartTime);
        values.put("take", Integer.valueOf(take));
        values.put("stopTime", this.addEndTime);
        if (DbUtils.queryIsUploadByActItemId(context, this.temp_id) > 0) {
            values.put("endUpdateTime", DateTime.getTimeString());
        }
        DbUtils.getDb(context).update("t_act_item", values, "id is ?", new String[]{this.temp_id});
        ToastUtils.toastShort(context, "修改成功！");
        values = new ContentValues();
        values.put("take", Integer.valueOf(take));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(context).update("t_routine_link", values, " itemsId is ? ", new String[]{this.temp_id});
        try {
            TreeSet<String> changeDateArr = new TreeSet();
            changeDateArr.add(this.addEndDate);
            changeDateArr.add(this.addStartDate);
            Iterator it = goalIdSet1.iterator();
            while (it.hasNext()) {
                goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
            }
            it = goalIdSet2.iterator();
            while (it.hasNext()) {
                goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
            }
            goalIdSet3.add(Integer.valueOf(Integer.parseInt(this.temp_id)));
            new Thread(new AllocationAndStaticsRunnable(context, goalIdSet3, changeDateArr)).start();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        this.refleshUI = true;
        Intent it2 = new Intent();
        it2.putExtra("id", this.temp_id);
        it2.putExtra("take", take);
        if (this.MODIFY_TYPE == this.MODIFY_START_TIME) {
            setResult(11, it2);
        } else if (this.MODIFY_TYPE == this.MODIFY_END_TIME) {
            setResult(3, it2);
        }
        if (this.refleshUI) {
            setResult(11, it2);
        }
        if (!"1".equals(this.isEnd) && this.stopTime.contains(DateTime.getDateString())) {
            sendBroadcast(new Intent(Val.INTENT_ACTION_MODIFY_ACTCOUNT));
        }
        if (this.popup != null && this.popup.isShowing()) {
            this.popup.dismiss();
        }
        updateUI();
        showUploadToUpdateGoalTimes2();
    }

    public boolean isOverrideOther() {
        log("是否与其它时间重叠1---addStartTime:" + this.addStartTime + ",addEndTime:" + this.addEndTime);
        DecimalFormat df = new DecimalFormat("00");
        if (this.addStartDate.equals("")) {
            this.addStartDate = DateTime.getDateString();
        }
        if (this.addEndDate.equals("")) {
            this.addEndDate = DateTime.getDateString();
        }
        if (this.MODIFY_TYPE == this.MODIFY_START_TIME) {
            this.addStartTime = this.addStartDate + " " + df.format((long) this.hour2) + ":" + df.format((long) this.min2) + ":59";
            this.addEndTime = this.stopTime;
        } else if (this.MODIFY_TYPE == this.MODIFY_END_TIME) {
            this.addStartTime = this.startTime;
            this.addEndTime = this.addEndDate + " " + df.format((long) this.hour2) + ":" + df.format((long) this.min2) + ":00";
        }
        log("是否与其它时间重叠2---addStartTime:" + this.addStartTime + ",addEndTime:" + this.addEndTime);
        if (DateTime.pars2Calender(this.addStartTime).getTime().getTime() > DateTime.pars2Calender(this.addEndTime).getTime().getTime()) {
            this.tv_tem_time_info.setText(getString(R.string.str_starttime_should_less_than_endtime));
        } else if (DateTime.pars2Calender(this.addStartTime).getTime().getTime() > Calendar.getInstance().getTime().getTime()) {
            this.tv_tem_time_info.setText(getString(R.string.str_starttime_should_less_than_now));
        } else if (DateTime.pars2Calender(this.addEndTime).getTime().getTime() > Calendar.getInstance().getTime().getTime()) {
            this.tv_tem_time_info.setText(getString(R.string.str_endtime_should_less_than_now));
        } else {
            Cursor cursor = DbUtils.getDb(context).rawQuery("select Id from t_act_item where " + DbUtils.getWhereUserId(context) + " and  isEnd is 1 and startTime > '" + this.addStartTime + "' and stopTime < '" + this.addEndTime + "' and id is not " + this.temp_id, null);
            if (cursor.getCount() > 0) {
                this.tv_tem_time_info.setText(getString(R.string.str_contain) + " " + cursor.getCount() + " " + getString(R.string.str_records_ok_will_delete_it));
                DbUtils.close(cursor);
            } else {
                this.tv_tem_time_info.setText("");
                DbUtils.close(cursor);
                cursor = DbUtils.getDb(context).rawQuery("select Id from t_act_item where " + DbUtils.getWhereUserId(context) + " and isEnd is 1 and startTime < '" + this.addStartTime + "' and stopTime > '" + this.addStartTime + "' and id is not " + this.temp_id, null);
                if (cursor.getCount() > 0) {
                    this.tv_tem_time_info.setText(getString(R.string.str_starttime_contain_other_ok_will_override_other));
                    DbUtils.close(cursor);
                } else {
                    this.tv_tem_time_info.setText("");
                    DbUtils.close(cursor);
                    cursor = DbUtils.getDb(context).rawQuery("select Id from t_act_item where " + DbUtils.getWhereUserId(context) + " and isEnd is not 1 and startTime < '" + this.addStartTime + "' and stopTime > '" + this.addStartTime + "' and id is not " + this.temp_id, null);
                    if (cursor.getCount() > 0) {
                        this.tv_tem_time_info.setText(getString(R.string.str_starttime_contain_now_ok_will_override_now));
                        DbUtils.close(cursor);
                    } else {
                        this.tv_tem_time_info.setText("");
                        DbUtils.close(cursor);
                        cursor = DbUtils.getDb(context).rawQuery("select Id from t_act_item where " + DbUtils.getWhereUserId(context) + " and  isEnd is 1 and startTime < '" + this.addEndTime + "' and stopTime > '" + this.addEndTime + "' and id is not " + this.temp_id, null);
                        if (cursor.getCount() > 0) {
                            this.tv_tem_time_info.setText(getString(R.string.str_endtime_contain_other_ok_will_override_other));
                            DbUtils.close(cursor);
                        } else {
                            this.tv_tem_time_info.setText("");
                            DbUtils.close(cursor);
                            cursor = DbUtils.getDb(context).rawQuery("select Id from t_act_item where " + DbUtils.getWhereUserId(context) + " and  isEnd is not 1 and startTime < '" + this.addEndTime + "' and stopTime > '" + this.addEndTime + "' and id is not " + this.temp_id, null);
                            if (cursor.getCount() > 0) {
                                this.tv_tem_time_info.setText(getString(R.string.str_endtime_contain_now_ok_will_override_now));
                                DbUtils.close(cursor);
                            } else {
                                this.tv_tem_time_info.setText("");
                                DbUtils.close(cursor);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
        updateUI();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void log(String str) {
        Log.i("override ItemDetail", ":" + str);
    }
}
