package com.record.myLife.settings.label;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.asm.Opcodes;
import com.record.bean.IDemoChart;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.utils.ColorPickerDialog;
import com.record.utils.ColorPickerDialog.OnColorChangedListener;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

public class LabelSelectActivity extends BaseActivity {
    OnClickListener IvLabelListener = new OnClickListener() {
        public void onClick(View v) {
            String sel_color = "#3DD494";
            int id = v.getId();
            if (id == R.id.iv_color_bg_green1) {
                sel_color = "#3DD494";
            } else if (id == R.id.iv_color_bg_green2) {
                sel_color = "#00C18C";
            } else if (id == R.id.iv_color_bg_green3) {
                sel_color = "#00AD69";
            } else if (id == R.id.iv_color_bg_yellow1) {
                sel_color = "#FF9E1C";
            } else if (id == R.id.iv_color_bg_yellow2) {
                sel_color = "#FF7D21";
            } else if (id == R.id.iv_color_bg_yellow3) {
                sel_color = "#FF6B2B";
            } else if (id == R.id.iv_color_bg_blue1) {
                sel_color = "#00A8E2";
            } else if (id == R.id.iv_color_bg_blue2) {
                sel_color = "#0075C2";
            } else if (id == R.id.iv_color_bg_blue3) {
                sel_color = "#005686";
            } else if (id == R.id.iv_color_bg_red1) {
                sel_color = "#EC4E3B";
            } else if (id == R.id.iv_color_bg_red2) {
                sel_color = "#E13F41";
            } else if (id == R.id.iv_color_bg_red3) {
                sel_color = "#D53049";
            }
            if (sel_color.length() > 0) {
                LabelSelectActivity.this.labelColor = Color.parseColor(sel_color);
                LabelSelectActivity.this.curren_select_color_view.setBackgroundColor(LabelSelectActivity.this.labelColor);
                LabelSelectActivity.this.updateLabelColor(LabelSelectActivity.this.labelColor);
            }
            if (LabelSelectActivity.this.dialog_label_select.isShowing()) {
                LabelSelectActivity.this.dialog_label_select.cancel();
            }
        }
    };
    String LabelName = "";
    final int ORDER_LABEL_BY_COLOR_ACS = 1;
    final int ORDER_LABEL_BY_COLOR_DESC = 2;
    final int ORDER_LABEL_BY_USE_TIME_ASC = 3;
    final int ORDER_LABEL_BY_USE_TIME_DESC = 4;
    int actType = 0;
    Button btn_label_select_create;
    Context context;
    View curren_select_color_view = null;
    String current_select_id = "";
    Dialog dialog_label_select;
    EditText et_label_select_create;
    LayoutInflater inflater;
    int itemsId = 0;
    ImageView iv_add_note_close;
    ImageView iv_label_select_create_color;
    ImageView iv_label_select_sort_color;
    ImageView iv_label_select_sort_use_time;
    int labelColor = 0;
    LinearLayout ll_label_select_sort;
    LinearLayout ll_laber_info_items;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            int order;
            if (id == R.id.iv_add_note_close) {
                LabelSelectActivity.this.finish();
            } else if (id == R.id.btn_label_select_create) {
                String newLabel = LabelSelectActivity.this.et_label_select_create.getText().toString().trim();
                if (newLabel == null && newLabel.length() == 0) {
                    GeneralHelper.toastShort(LabelSelectActivity.this.context, LabelSelectActivity.this.getResources().getString(R.string.str_prompt_label_no_null));
                } else if (DbUtils.getDb(LabelSelectActivity.this.context).rawQuery("select Id from t_sub_type where " + DbUtils.getWhereUserId(LabelSelectActivity.this.context) + " and isDelete is not 1", null).getCount() > Opcodes.FCMPG) {
                    GeneralUtils.toastShort(LabelSelectActivity.this.context, "标签过多，请先删除不常用的标签，再尝试添加!");
                } else {
                    LabelSelectActivity.this.addModifyLabel(newLabel);
                }
            } else if (R.id.iv_label_select_create_color == id) {
                LabelSelectActivity.this.curren_select_color_view = LabelSelectActivity.this.iv_label_select_create_color;
                LabelSelectActivity.this.current_select_id = "";
                LabelSelectActivity.this.selectColor(v);
            } else if (R.id.btn_label_select_sort_color == id || R.id.rl_label_select_sort_color == id) {
                order = PreferUtils.getSP(LabelSelectActivity.this.context).getInt(Val.CONFIGURE_ORDER_SELECT_LABEL, 1);
                if (order >= 3) {
                    order = 2;
                } else if (order == 1) {
                    order = 2;
                } else if (order == 2) {
                    order = 1;
                }
                LabelSelectActivity.this.changeOrder(order);
                LabelSelectActivity.this.initArrow(order);
            } else if (R.id.btn_label_select_sort_use_time == id || R.id.rl_label_select_sort_use_time == id) {
                order = PreferUtils.getSP(LabelSelectActivity.this.context).getInt(Val.CONFIGURE_ORDER_SELECT_LABEL, 1);
                if (order <= 2) {
                    order = 4;
                } else if (order == 3) {
                    order = 4;
                } else if (order == 4) {
                    order = 3;
                }
                LabelSelectActivity.this.changeOrder(order);
                LabelSelectActivity.this.initArrow(order);
            }
        }
    };
    OnClickListener myClickListener2 = new OnClickListener() {
        public void onClick(View v) {
            String id = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            if (LabelSelectActivity.this.itemsId > 0) {
                LabelSelectActivity.this.linkLabel(id);
            } else {
                LabelSelectActivity.this.mySetResult(id);
            }
        }
    };
    OnClickListener myClickListener3 = new OnClickListener() {
        public void onClick(View v) {
            RelativeLayout rl = (RelativeLayout) v.getParent();
            String id = ((TextView) rl.getChildAt(0)).getText().toString();
            LabelSelectActivity.this.log("tv.getText():" + id);
            LabelSelectActivity.this.current_select_id = id;
            LabelSelectActivity.this.curren_select_color_view = rl;
            LabelSelectActivity.this.selectColor(LabelSelectActivity.this.curren_select_color_view);
        }
    };
    RelativeLayout rl_label_select_create_label;
    RelativeLayout rl_label_select_sort_color;
    RelativeLayout rl_label_select_sort_use_time;
    TextView tv_add_note_title;
    int type = 1;

    public static void startActivity(Activity activity, int type) {
        Intent it = new Intent(activity, LabelSelectActivity.class);
        it.putExtra("actType", type);
        activity.startActivityForResult(it, 25);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_select);
        this.context = this;
        this.inflater = getLayoutInflater();
        this.type = 1;
        Intent it = getIntent();
        this.itemsId = it.getIntExtra("itemsId", 0);
        this.actType = it.getIntExtra("actType", 0);
        if (this.actType > 0 && this.actType == 11) {
            this.actType = 10;
        }
        this.ll_laber_info_items = (LinearLayout) findViewById(R.id.ll_laber_info_items);
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.tv_add_note_title = (TextView) findViewById(R.id.tv_add_note_title);
        this.et_label_select_create = (EditText) findViewById(R.id.et_label_select_create);
        this.btn_label_select_create = (Button) findViewById(R.id.btn_label_select_create);
        this.rl_label_select_create_label = (RelativeLayout) findViewById(R.id.rl_label_select_create_label);
        this.iv_label_select_create_color = (ImageView) findViewById(R.id.iv_label_select_create_color);
        this.iv_label_select_sort_use_time = (ImageView) findViewById(R.id.iv_label_select_sort_use_time);
        this.iv_label_select_sort_color = (ImageView) findViewById(R.id.iv_label_select_sort_color);
        this.rl_label_select_sort_use_time = (RelativeLayout) findViewById(R.id.rl_label_select_sort_use_time);
        this.rl_label_select_sort_color = (RelativeLayout) findViewById(R.id.rl_label_select_sort_color);
        this.ll_label_select_sort = (LinearLayout) findViewById(R.id.ll_label_select_sort);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.btn_label_select_create.setOnClickListener(this.myClickListener);
        this.iv_label_select_create_color.setOnClickListener(this.myClickListener);
        this.rl_label_select_sort_color.setOnClickListener(this.myClickListener);
        this.rl_label_select_sort_use_time.setOnClickListener(this.myClickListener);
        log("itemsId:" + this.itemsId + ",type:" + this.type);
        int order = PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_ORDER_SELECT_LABEL, 1);
        initArrow(order);
        initUI_v2(this.type, getOrderWhere(order));
    }

    private void initArrow(int order) {
        if (order == 1) {
            this.iv_label_select_sort_color.setVisibility(0);
            this.iv_label_select_sort_color.setImageResource(R.drawable.ic_arrow_down);
            this.iv_label_select_sort_use_time.setVisibility(4);
        } else if (order == 2) {
            this.iv_label_select_sort_color.setVisibility(0);
            this.iv_label_select_sort_color.setImageResource(R.drawable.ic_arrow_up);
            this.iv_label_select_sort_use_time.setVisibility(4);
        } else if (order == 3) {
            this.iv_label_select_sort_use_time.setVisibility(0);
            this.iv_label_select_sort_use_time.setImageResource(R.drawable.ic_arrow_down);
            this.iv_label_select_sort_color.setVisibility(4);
        } else if (order == 4) {
            this.iv_label_select_sort_use_time.setVisibility(0);
            this.iv_label_select_sort_use_time.setImageResource(R.drawable.ic_arrow_up);
            this.iv_label_select_sort_color.setVisibility(4);
        }
    }

    private String getOrderWhere(int order) {
        String where = "";
        if (order == 1) {
            return " order by labelColor ";
        }
        if (order == 2) {
            return " order by labelColor desc ";
        }
        if (order == 3) {
            return " order by lastUseTime ";
        }
        if (order == 4) {
            return " order by lastUseTime desc ";
        }
        return where;
    }

    private void initUI_v2(int type, String order) {
        try {
            String sql = "select * from ( select * from t_sub_type where actType is " + this.actType + " or actType is 1 ) where " + DbUtils.getWhereUserId(this.context) + "  and isDelete is not 1 and labelType is " + type + order;
            if (this.actType == 10 || this.actType == 11) {
                sql = "select * from ( select * from t_sub_type where actType is 10 or actType is 1 or actType is 11 ) where " + DbUtils.getWhereUserId(this.context) + "  and isDelete is not 1 and labelType is " + type + order;
            }
            Cursor cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
            RelativeLayout rl;
            TextView tv;
            if (cursor.getCount() > 0) {
                this.ll_laber_info_items.removeAllViews();
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex("Id"));
                    String name = cursor.getString(cursor.getColumnIndex(IDemoChart.NAME));
                    int color = cursor.getInt(cursor.getColumnIndex("labelColor"));
                    rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_for_select_label_white_v2, null);
                    rl.setOnClickListener(this.myClickListener2);
                    tv = (TextView) rl.findViewById(R.id.tv_temp_text);
                    ((TextView) rl.findViewById(R.id.tv_temp_id)).setText(id + "");
                    tv.setText(name);
                    ((ImageView) rl.findViewById(R.id.iv_temp_circle)).setOnClickListener(this.myClickListener3);
                    if (color != 0) {
                        rl.setBackgroundColor(color);
                        tv.setTextColor(-1);
                    }
                    LayoutParams lp = new LayoutParams(-1, -2);
                    lp.topMargin = 2;
                    rl.setLayoutParams(lp);
                    this.ll_laber_info_items.addView(rl);
                }
            } else {
                rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_for_select_label_white_v2, null);
                tv = (TextView) rl.findViewById(R.id.tv_temp_text);
                ((ImageView) rl.findViewById(R.id.iv_temp_circle)).setVisibility(8);
                tv.setText(getString(R.string.str_no_label));
                this.ll_laber_info_items.addView(rl);
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void initLabelInfoUI(String typeId) {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_routine_link where " + DbUtils.getWhereUserId(this.context) + " and subTypeId is " + typeId + " order by Time desc", null);
        if (cursor.getCount() > 0) {
            this.ll_laber_info_items.removeAllViews();
            int total = 0;
            int last = 0;
            int current = 0;
            int min = 0;
            int i = 0;
            while (cursor.moveToNext()) {
                int dbTake = cursor.getInt(cursor.getColumnIndex("take"));
                total += dbTake;
                if (cursor.isFirst()) {
                    current = dbTake;
                }
                if (i == 1) {
                    last = dbTake;
                }
                if (min == 0) {
                    min = dbTake;
                }
                if (dbTake < min) {
                    min = dbTake;
                }
                i++;
            }
            LinearLayout rl = (LinearLayout) this.inflater.inflate(R.layout.tem_show_labe_info, null);
            TextView tv_temp_sum = (TextView) rl.findViewById(R.id.tv_temp_sum);
            TextView tv_temp_avg = (TextView) rl.findViewById(R.id.tv_temp_avg);
            TextView tv_temp_min = (TextView) rl.findViewById(R.id.tv_temp_min);
            TextView tv_temp_last = (TextView) rl.findViewById(R.id.tv_temp_last);
            TextView tv_temp_current = (TextView) rl.findViewById(R.id.tv_temp_current);
            ((TextView) rl.findViewById(R.id.tv_temp_label_info)).setText(getResources().getString(R.string.str_text_this_label_had).replace("{几条}", cursor.getCount() + ""));
            tv_temp_sum.setText(getResources().getString(R.string.str_sum_2) + "：" + DateTime.calculateTime6((long) total));
            tv_temp_avg.setText(getResources().getString(R.string.str_avg) + "：" + DateTime.calculateTime6((long) (total / cursor.getCount())));
            tv_temp_min.setText(getResources().getString(R.string.str_lowest) + "：" + DateTime.calculateTime6((long) min));
            if (last > 0) {
                tv_temp_last.setText(getResources().getString(R.string.str_last_time) + "：" + DateTime.calculateTime6((long) last));
            } else {
                tv_temp_last.setVisibility(8);
            }
            tv_temp_current.setText(getResources().getString(R.string.str_this_time) + "：" + DateTime.calculateTime6((long) current));
            this.rl_label_select_create_label.setVisibility(8);
            this.ll_label_select_sort.setVisibility(8);
            if (this.LabelName == null || this.LabelName.length() <= 0) {
                this.tv_add_note_title.setText(getResources().getString(R.string.str_info));
            } else {
                this.tv_add_note_title.setText(this.LabelName);
            }
            this.ll_laber_info_items.addView(rl);
        } else {
            finish();
        }
        DbUtils.close(cursor);
    }

    private void mySetResult(String labelIdStr) {
        Intent it = new Intent();
        it.putExtra("labelIdStr", labelIdStr);
        setResult(-1, it);
        finish();
    }

    private void linkLabel(String subTypeId) {
        String id = subTypeId;
        int take = 0;
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select take from t_act_item where id is " + this.itemsId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            take = cursor.getInt(cursor.getColumnIndex("take"));
        }
        DbUtils.close(cursor);
        if (take > 0) {
            Cursor cursor5 = DbUtils.getDb(this.context).rawQuery("select Id from t_routine_link where itemsId is " + this.itemsId + " and subTypeId is " + id, null);
            if (cursor5.getCount() > 0) {
                GeneralUtils.toastShort(this.context, getResources().getString(R.string.str_prompt_this_record_had_add_this_label));
                DbUtils.close(cursor5);
                finish();
                return;
            }
            DbUtils.close(cursor5);
            ContentValues values = new ContentValues();
            values.put("userId", DbUtils.queryUserId(this.context));
            values.put("itemsId", Integer.valueOf(this.itemsId));
            values.put("subTypeId", id);
            values.put("take", Integer.valueOf(take));
            Cursor cursor33 = DbUtils.getDb(this.context).rawQuery("select * from t_act_item where id is " + this.itemsId, null);
            if (cursor33.getCount() > 0) {
                cursor33.moveToNext();
                values.put("goalId", Integer.valueOf(cursor33.getInt(cursor33.getColumnIndex("actId"))));
                values.put("goalType", Integer.valueOf(cursor33.getInt(cursor33.getColumnIndex("actType"))));
            }
            DbUtils.close(cursor33);
            values.put("time", DateTime.getTimeString());
            DbUtils.getDb(this.context).insert("t_routine_link", null, values);
            values = new ContentValues();
            values.put("lastUseTime", DateTime.getTimeString());
            DbUtils.getDb(this.context).update("t_sub_type", values, " id is ? ", new String[]{id});
            Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select * from t_sub_type where id is " + id, null);
            if (cursor2.getCount() > 0) {
                cursor2.moveToNext();
                this.LabelName = cursor2.getString(cursor2.getColumnIndex(IDemoChart.NAME));
                String remarksLocal = "";
                Cursor cursor3 = DbUtils.getDb(this.context).rawQuery("select remarks from t_act_item where id is " + this.itemsId, null);
                if (cursor3.getCount() > 0) {
                    cursor3.moveToNext();
                    remarksLocal = cursor3.getString(cursor3.getColumnIndex("remarks"));
                }
                DbUtils.close(cursor3);
                ContentValues values2 = new ContentValues();
                if (remarksLocal != null) {
                    values2.put("remarks", remarksLocal + " [" + getResources().getString(R.string.str_label) + ":" + this.LabelName + "]");
                } else {
                    values2.put("remarks", "[" + getResources().getString(R.string.str_label) + ":" + this.LabelName + "]");
                }
                DbUtils.getDb(this.context).update("t_act_item", values2, " id is ? ", new String[]{"" + this.itemsId});
            }
            DbUtils.close(cursor2);
            initLabelInfoUI(id);
            return;
        }
        finish();
    }

    public void changeOrder(int order) {
        PreferUtils.getSP(this.context).edit().putInt(Val.CONFIGURE_ORDER_SELECT_LABEL, order).commit();
        initUI_v2(1, getOrderWhere(order));
    }

    private void selectColor(final View v) {
        View rl_color_select_12 = (RelativeLayout) this.inflater.inflate(R.layout.dialog_color_v3, null);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_green1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_green2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_green3)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_yellow1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_yellow2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_yellow3)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_blue1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_blue2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_blue3)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_red1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_red2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) rl_color_select_12.findViewById(R.id.iv_color_bg_red3)).setOnClickListener(this.IvLabelListener);
        this.dialog_label_select = new Builder(this.context).setTitle(getString(R.string.str_choose_color)).setView(rl_color_select_12).setPositiveButton(getString(R.string.str_more), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LabelSelectActivity.this.selectMoreColor(v);
                dialog.cancel();
            }
        }).create();
        this.dialog_label_select.show();
    }

    private void selectMoreColor(final View v) {
        ColorPickerDialog cd = new ColorPickerDialog(this.context, getResources().getString(R.string.str_choose_color), new OnColorChangedListener() {
            public void colorChanged(int color) {
                LabelSelectActivity.this.log("选择颜色：" + color);
                v.setBackgroundColor(color);
                LabelSelectActivity.this.labelColor = color;
                LabelSelectActivity.this.updateLabelColor(color);
            }
        });
        if (this.current_select_id != null && this.current_select_id.length() > 0) {
            int color = DbUtils.queryColorByLabelId(this.context, this.current_select_id);
            if (color != 0) {
                cd.setmInitialColor(color);
            }
        }
        cd.show();
    }

    private void updateLabelColor(int color) {
        if (this.current_select_id.length() > 0) {
            ContentValues values = new ContentValues();
            values.put("labelColor", Integer.valueOf(color));
            DbUtils.getDb(this.context).update("t_sub_type", values, " Id is ? ", new String[]{this.current_select_id});
        }
    }

    public void addModifyLabel(String editString) {
        String str = editString;
        if (str == null || str.trim().length() == 0) {
            GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_prompt_label_no_null));
            return;
        }
        if (this.actType == 11) {
            this.actType = 10;
        }
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select Id from t_sub_type where " + DbUtils.getWhereUserId(this.context) + " and name is '" + str + "' ", null);
        ContentValues values;
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String id = cursor.getString(cursor.getColumnIndex("Id"));
            values = new ContentValues();
            values.put("isDelete", Integer.valueOf(0));
            values.put("actType", Integer.valueOf(this.actType));
            if (this.labelColor != 0) {
                values.put("labelColor", Integer.valueOf(this.labelColor));
            }
            DbUtils.getDb(this.context).update("t_sub_type", values, " name is ? ", new String[]{str});
            if (this.itemsId > 0) {
                linkLabel("" + id);
                GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_prompt_label_had_exist));
                return;
            }
            mySetResult(id);
            return;
        }
        values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(this.context));
        values.put("actType", Integer.valueOf(this.actType));
        values.put("labelType", Integer.valueOf(1));
        values.put(IDemoChart.NAME, str);
        if (this.labelColor != 0) {
            values.put("labelColor", Integer.valueOf(this.labelColor));
        }
        values.put("time", DateTime.getTimeString());
        long id2 = DbUtils.getDb(this.context).insert("t_sub_type", null, values);
        if (this.itemsId > 0) {
            linkLabel("" + id2);
        } else {
            mySetResult(id2 + "");
        }
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
