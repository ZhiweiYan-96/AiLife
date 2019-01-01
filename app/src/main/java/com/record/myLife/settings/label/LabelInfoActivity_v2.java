package com.record.myLife.settings.label;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.asm.Opcodes;
import com.record.bean.IDemoChart;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.DraggableListView.DropListener;
import com.record.myLife.view.DraggableListView.RemoveListener;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.ColorPickerDialog;
import com.record.utils.ColorPickerDialog.OnColorChangedListener;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint({"CutPasteId"})
public class LabelInfoActivity_v2 extends BaseActivity {
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
                LabelInfoActivity_v2.this.selectColor = Color.parseColor(sel_color);
                LabelInfoActivity_v2.this.curren_select_color_view.setBackgroundColor(LabelInfoActivity_v2.this.selectColor);
                LabelInfoActivity_v2.this.updateLabelColor(LabelInfoActivity_v2.this.selectColor);
            }
            if (LabelInfoActivity_v2.this.dialog_label_select != null && LabelInfoActivity_v2.this.dialog_label_select.isShowing()) {
                LabelInfoActivity_v2.this.dialog_label_select.cancel();
            }
        }
    };
    private IconicAdapter adapter = null;
    private ArrayList<String> array = null;
    Button btn_label_back;
    Button btn_label_mood;
    Button btn_label_subtype;
    Context context;
    View curren_select_color_view = null;
    String current_select_id = "";
    Dialog dialog_label_select;
    EditText ed_tem_add_label;
    LayoutInflater inflater;
    ImageView iv_label_add;
    TextView iv_label_info_instruction;
    ImageView iv_laber_info_invest_isopen;
    ImageView iv_laber_info_items_defualt;
    ImageView iv_laber_info_items_invest;
    ImageView iv_laber_info_items_routine;
    ImageView iv_laber_info_items_sleep;
    ImageView iv_laber_info_items_waste;
    ImageView iv_laber_info_overall_isopen;
    ImageView iv_laber_info_routine_isopen;
    ImageView iv_laber_info_sleep_isopen;
    ImageView iv_laber_info_waste_isopen;
    LinearLayout ll_laber_info_invest_items;
    LinearLayout ll_laber_info_items;
    LinearLayout ll_laber_info_routine_items;
    LinearLayout ll_laber_info_sleep_items;
    LinearLayout ll_laber_info_waste_items;
    private ArrayList<itemsBean> mapArray = null;
    OnClickListener myClickListener = new OnClickListener() {
        @SuppressLint({"NewApi"})
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.set_btn_overall_noti) {
                GeneralHelper.toastLong(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getResources().getString(R.string.str_overrall_label_explain));
            } else if (id == R.id.set_btn_noti) {
                GeneralHelper.toastLong(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getResources().getString(R.string.str_label_explain));
            } else if (id == R.id.iv_laber_info_overall_isopen) {
                SharedPreferences sp = LabelInfoActivity_v2.this.getSharedPreferences(Val.CONFIGURE_NAME, 0);
                if (sp.getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_OVERRALL_LABEL, 0) > 0) {
                    sp.edit().putInt(Val.CONFIGURE_IS_REMIND_CHOOSE_OVERRALL_LABEL, 0).commit();
                    LabelInfoActivity_v2.this.iv_laber_info_overall_isopen.setImageResource(R.drawable.ic_off_v2);
                    GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, "全局关闭！");
                    return;
                }
                sp.edit().putInt(Val.CONFIGURE_IS_REMIND_CHOOSE_OVERRALL_LABEL, 1).commit();
                LabelInfoActivity_v2.this.iv_laber_info_overall_isopen.setImageResource(R.drawable.ic_on_v2);
                GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, "全局开启！");
            } else if (id == R.id.btn_label_back) {
                LabelInfoActivity_v2.this.finish();
                LabelInfoActivity_v2.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.iv_label_add) {
                if (DbUtils.getDb(LabelInfoActivity_v2.this.context).rawQuery("select Id from t_sub_type where " + DbUtils.getWhereUserId(LabelInfoActivity_v2.this.context) + " and isDelete is not 1", null).getCount() > Opcodes.FCMPG) {
                    GeneralUtils.toastShort(LabelInfoActivity_v2.this.context, "标签过多，请先删除不常用的标签，再尝试添加!");
                } else {
                    LabelInfoActivity_v2.this.addModifyLabel_v2("", "");
                }
            } else if (id == R.id.iv_label_info_instrution) {
                new Builder(LabelInfoActivity_v2.this.context).setTitle(LabelInfoActivity_v2.this.getResources().getString(R.string.str_label)).setMessage(LabelInfoActivity_v2.this.getResources().getString(R.string.str_label_instrution)).setPositiveButton(LabelInfoActivity_v2.this.getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            } else if (id == R.id.tv_label_info_title) {
            } else {
                if (id == R.id.iv_laber_info_routine_isopen) {
                    LabelInfoActivity_v2.this.openOrCloseLabel(LabelInfoActivity_v2.this.iv_laber_info_routine_isopen, Val.CONFIGURE_IS_REMIND_CHOOSE_ROUTINE_LABEL);
                } else if (id == R.id.iv_laber_info_sleep_isopen) {
                    LabelInfoActivity_v2.this.openOrCloseLabel(LabelInfoActivity_v2.this.iv_laber_info_sleep_isopen, Val.CONFIGURE_IS_REMIND_CHOOSE_SLEEP_LABEL);
                } else if (id == R.id.iv_laber_info_waste_isopen) {
                    LabelInfoActivity_v2.this.openOrCloseLabel(LabelInfoActivity_v2.this.iv_laber_info_waste_isopen, Val.CONFIGURE_IS_REMIND_CHOOSE_WASTE_LABEL);
                } else if (id == R.id.iv_laber_info_invest_isopen) {
                    LabelInfoActivity_v2.this.openOrCloseLabel(LabelInfoActivity_v2.this.iv_laber_info_invest_isopen, Val.CONFIGURE_IS_REMIND_CHOOSE_INVEST_LABEL);
                } else if (id == R.id.rl_laber_info_items_defualt) {
                    if (LabelInfoActivity_v2.this.getsp().getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0) == 1) {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(0, false);
                    } else {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(1, true);
                    }
                } else if (id == R.id.rl_laber_info_items_invest) {
                    if (LabelInfoActivity_v2.this.getsp().getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0) == 10) {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(0, false);
                    } else {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(10, true);
                    }
                } else if (id == R.id.rl_laber_info_items_routine) {
                    if (LabelInfoActivity_v2.this.getsp().getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0) == 20) {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(0, false);
                    } else {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(20, true);
                    }
                } else if (id == R.id.rl_laber_info_items_sleep) {
                    if (LabelInfoActivity_v2.this.getsp().getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0) == 30) {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(0, false);
                    } else {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(30, true);
                    }
                } else if (id != R.id.rl_laber_info_items_waste) {
                } else {
                    if (LabelInfoActivity_v2.this.getsp().getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0) == 40) {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(0, false);
                    } else {
                        LabelInfoActivity_v2.this.initLabelByTypeAll(40, true);
                    }
                }
            }
        }
    };
    OnClickListener myClickListener2 = new OnClickListener() {
        public void onClick(View v) {
            String id = ((TextView) ((RelativeLayout) v.getParent()).getChildAt(0)).getText().toString();
            Intent it = new Intent(LabelInfoActivity_v2.this.context, LabelDetailActivity.class);
            it.putExtra("id", id);
            LabelInfoActivity_v2.this.startActivity(it);
            LabelInfoActivity_v2.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
        }
    };
    OnClickListener myClickListener3 = new OnClickListener() {
        public void onClick(View v) {
            RelativeLayout rl = (RelativeLayout) v.getParent();
            String id = ((TextView) rl.getChildAt(0)).getText().toString();
            LabelInfoActivity_v2.this.log("tv.getText():" + id);
            LabelInfoActivity_v2.this.current_select_id = id;
            LabelInfoActivity_v2.this.curren_select_color_view = rl;
            LabelInfoActivity_v2.this.selectColor(LabelInfoActivity_v2.this.curren_select_color_view);
        }
    };
    OnLongClickListener myLongClickListener = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            RelativeLayout rl = (RelativeLayout) v.getParent();
            TextView nameTv = (TextView) rl.getChildAt(1);
            final String id = ((TextView) rl.getChildAt(0)).getText().toString();
            final String name = nameTv.getText().toString();
            new Builder(LabelInfoActivity_v2.this.context).setTitle((CharSequence) "是否删除？").setNegativeButton(LabelInfoActivity_v2.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setNeutralButton(LabelInfoActivity_v2.this.getResources().getString(R.string.str_delete), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (DbUtils.queryIsUploadBySubTypeId(LabelInfoActivity_v2.this.context, id) > 0) {
                        ContentValues values = new ContentValues();
                        values.put("isDelete", Integer.valueOf(1));
                        values.put("deleteTime", DateTime.getTimeString());
                        values.put("endUpdateTime", DateTime.getTimeString());
                        DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " Id is ? ", new String[]{id});
                    } else {
                        DbUtils.getDb(LabelInfoActivity_v2.this.context).delete("t_sub_type", " Id is ? ", new String[]{id});
                    }
                    GeneralHelper.toastLong(LabelInfoActivity_v2.this.context, "删除成功！");
                    LabelInfoActivity_v2.this.initUI_v2(LabelInfoActivity_v2.this.type);
                    dialog.cancel();
                }
            }).setPositiveButton(LabelInfoActivity_v2.this.getResources().getString(R.string.str_modify), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    LabelInfoActivity_v2.this.addModifyLabel_v2(name, id);
                }
            }).create().show();
            return false;
        }
    };
    private DropListener onDrop = new DropListener() {
        public void drop(int from, int to) {
            String item = (String) LabelInfoActivity_v2.this.adapter.getItem(from);
            LabelInfoActivity_v2.this.log("对调前位置：:from:" + from + ",to:" + to + ",item:" + item);
            ContentValues values;
            int i;
            String itemTemp;
            if (from == to + 1 || from == to - 1) {
                String toItem = (String) LabelInfoActivity_v2.this.adapter.getItem(to);
                values = new ContentValues();
                values.put("labelPosition", Integer.valueOf(from));
                DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{toItem});
                values = new ContentValues();
                values.put("labelPosition", Integer.valueOf(to));
                DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{item});
                LabelInfoActivity_v2.this.log("两个对调:item:" + item + "，from:" + from + "，toItem：" + toItem + ",to:" + to);
            } else if (from > to) {
                values = new ContentValues();
                values.put("labelPosition", Integer.valueOf(to));
                DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{item});
                for (i = to; i < from - 1; i++) {
                    itemTemp = (String) LabelInfoActivity_v2.this.adapter.getItem(i);
                    values = new ContentValues();
                    values.put("labelPosition", Integer.valueOf(i + 1));
                    DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{itemTemp});
                    LabelInfoActivity_v2.this.log("往前移动itemTemp:" + itemTemp + "  由" + i + "移到：" + (i + 1));
                }
            } else if (to > from) {
                for (i = from + 1; i < to + 1; i++) {
                    itemTemp = (String) LabelInfoActivity_v2.this.adapter.getItem(i);
                    values = new ContentValues();
                    values.put("labelPosition", Integer.valueOf(i - 1));
                    DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{itemTemp});
                    LabelInfoActivity_v2.this.log("往后移itemTemp:" + itemTemp + "  由" + i + "移到：" + (i - 1));
                }
                values = new ContentValues();
                values.put("labelPosition", Integer.valueOf(to));
                DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{item});
            }
            LabelInfoActivity_v2.this.adapter.remove(item);
            LabelInfoActivity_v2.this.adapter.insert(item, to);
            Toast.makeText(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.adapter.getList().toString(), 0).show();
        }
    };
    private RemoveListener onRemove = new RemoveListener() {
        public void remove(int which) {
            LabelInfoActivity_v2.this.log("onRemove:which" + which + "," + ((String) LabelInfoActivity_v2.this.adapter.getItem(which)));
            LabelInfoActivity_v2.this.adapter.remove(LabelInfoActivity_v2.this.adapter.getItem(which));
        }
    };
    RelativeLayout rl_laber_info_items_defualt;
    RelativeLayout rl_laber_info_items_invest;
    RelativeLayout rl_laber_info_items_routine;
    RelativeLayout rl_laber_info_items_sleep;
    RelativeLayout rl_laber_info_items_waste;
    RelativeLayout rl_lable_info_overall_noti;
    RelativeLayout rl_tem_add_label_color;
    int selectColor = 0;
    int selectType = 1;
    Button set_btn_overall_noti;
    SharedPreferences sp;
    RelativeLayout tem_rl;
    TextView tv_label_info_title;
    ImageView tv_tem_add_label_color_value;
    TextView tv_tem_add_label_type_value;
    int type = 1;

    class IconicAdapter extends ArrayAdapter<String> {
        IconicAdapter() {
            super(LabelInfoActivity_v2.this.context, R.layout.tem_label_row, LabelInfoActivity_v2.this.array);
        }

        public ArrayList<String> getList() {
            return LabelInfoActivity_v2.this.array;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            itemsBean bean = (itemsBean) LabelInfoActivity_v2.this.mapArray.get(position);
            LabelInfoActivity_v2.this.log("position:" + position + ",Id:" + bean.getId() + ",name:" + bean.getLabel() + ",color:" + bean.getColor());
            if (row == null) {
                row = LabelInfoActivity_v2.this.getLayoutInflater().inflate(R.layout.tem_label_row, parent, false);
            }
            TextView label = (TextView) row.findViewById(R.id.label);
            ((TextView) row.findViewById(R.id.id)).setText(bean.getId());
            label.setText((CharSequence) LabelInfoActivity_v2.this.array.get(position));
            ((ImageView) row.findViewById(R.id.circle)).setOnClickListener(LabelInfoActivity_v2.this.myClickListener3);
            RelativeLayout rl_row = (RelativeLayout) row.findViewById(R.id.rl_row);
            if (bean.getColor() != 0) {
                LabelInfoActivity_v2.this.log("设置颜色：" + bean.getColor());
                rl_row.setBackgroundColor(bean.getColor());
                label.setTextColor(LabelInfoActivity_v2.this.getResources().getColor(R.color.white));
            } else {
                label.setTextColor(LabelInfoActivity_v2.this.getResources().getColor(R.color.black));
                rl_row.setBackgroundColor(LabelInfoActivity_v2.this.getResources().getColor(R.color.white));
            }
            return row;
        }
    }

    class itemsBean {
        int color;
        String id;
        String label;

        public itemsBean(String id, String label, int color) {
            this.id = id;
            this.label = label;
            this.color = color;
        }

        public String toString() {
            return "id:" + this.id + ",label:" + this.label;
        }

        public String getId() {
            return this.id;
        }

        public String getLabel() {
            return this.label;
        }

        public int getColor() {
            return this.color;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_info_v2);
        this.context = this;
        this.inflater = getLayoutInflater();
        SystemBarTintManager.setMIUIbar(this);
        this.type = 1;
        initFind();
        initUI_v2(this.type);
        getSharedPreferences(Val.CONFIGURE_NAME_DOT, 2).edit().putInt(Val.CONFIGURE_IS_SHOW_LABEL_DOT, 3).commit();
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(Val.CONFIGURE_IS_SHOW_DIALOG_LABEL_INSTRUCTION, 0) < 2) {
            this.myClickListener.onClick(this.iv_label_info_instruction);
            sp.edit().putInt(Val.CONFIGURE_IS_SHOW_DIALOG_LABEL_INSTRUCTION, 2).commit();
        }
    }

    private void initFind() {
        this.ll_laber_info_items = (LinearLayout) findViewById(R.id.ll_laber_info_items);
        this.btn_label_back = (Button) findViewById(R.id.btn_label_back);
        this.set_btn_overall_noti = (Button) findViewById(R.id.set_btn_overall_noti);
        this.iv_label_add = (ImageView) findViewById(R.id.iv_label_add);
        this.iv_laber_info_invest_isopen = (ImageView) findViewById(R.id.iv_laber_info_invest_isopen);
        this.iv_laber_info_overall_isopen = (ImageView) findViewById(R.id.iv_laber_info_overall_isopen);
        this.iv_laber_info_routine_isopen = (ImageView) findViewById(R.id.iv_laber_info_routine_isopen);
        this.iv_laber_info_sleep_isopen = (ImageView) findViewById(R.id.iv_laber_info_sleep_isopen);
        this.iv_laber_info_waste_isopen = (ImageView) findViewById(R.id.iv_laber_info_waste_isopen);
        this.iv_laber_info_items_defualt = (ImageView) findViewById(R.id.iv_laber_info_items_defualt);
        this.iv_laber_info_items_invest = (ImageView) findViewById(R.id.iv_laber_info_items_invest);
        this.iv_laber_info_items_routine = (ImageView) findViewById(R.id.iv_laber_info_items_routine);
        this.iv_laber_info_items_sleep = (ImageView) findViewById(R.id.iv_laber_info_items_sleep);
        this.iv_laber_info_items_waste = (ImageView) findViewById(R.id.iv_laber_info_items_waste);
        this.rl_laber_info_items_defualt = (RelativeLayout) findViewById(R.id.rl_laber_info_items_defualt);
        this.rl_laber_info_items_invest = (RelativeLayout) findViewById(R.id.rl_laber_info_items_invest);
        this.rl_laber_info_items_routine = (RelativeLayout) findViewById(R.id.rl_laber_info_items_routine);
        this.rl_laber_info_items_sleep = (RelativeLayout) findViewById(R.id.rl_laber_info_items_sleep);
        this.rl_laber_info_items_waste = (RelativeLayout) findViewById(R.id.rl_laber_info_items_waste);
        this.iv_label_info_instruction = (TextView) findViewById(R.id.iv_label_info_instrution);
        this.tv_label_info_title = (TextView) findViewById(R.id.tv_label_info_title);
        this.rl_lable_info_overall_noti = (RelativeLayout) findViewById(R.id.rl_lable_info_overall_noti);
        this.ll_laber_info_invest_items = (LinearLayout) findViewById(R.id.ll_laber_info_invest_items);
        this.ll_laber_info_routine_items = (LinearLayout) findViewById(R.id.ll_laber_info_routine_items);
        this.ll_laber_info_sleep_items = (LinearLayout) findViewById(R.id.ll_laber_info_sleep_items);
        this.ll_laber_info_waste_items = (LinearLayout) findViewById(R.id.ll_laber_info_waste_items);
        this.btn_label_back.setOnClickListener(this.myClickListener);
        this.iv_label_add.setOnClickListener(this.myClickListener);
        this.iv_laber_info_invest_isopen.setOnClickListener(this.myClickListener);
        this.set_btn_overall_noti.setOnClickListener(this.myClickListener);
        this.iv_label_info_instruction.setOnClickListener(this.myClickListener);
        this.iv_laber_info_overall_isopen.setOnClickListener(this.myClickListener);
        this.tv_label_info_title.setOnClickListener(this.myClickListener);
        this.iv_laber_info_routine_isopen.setOnClickListener(this.myClickListener);
        this.iv_laber_info_sleep_isopen.setOnClickListener(this.myClickListener);
        this.iv_laber_info_invest_isopen.setOnClickListener(this.myClickListener);
        this.iv_laber_info_waste_isopen.setOnClickListener(this.myClickListener);
        this.rl_laber_info_items_defualt.setOnClickListener(this.myClickListener);
        this.rl_laber_info_items_invest.setOnClickListener(this.myClickListener);
        this.rl_laber_info_items_routine.setOnClickListener(this.myClickListener);
        this.rl_laber_info_items_sleep.setOnClickListener(this.myClickListener);
        this.rl_laber_info_items_waste.setOnClickListener(this.myClickListener);
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
                LabelInfoActivity_v2.this.selectMoreColor(v);
                dialog.cancel();
            }
        }).create();
        this.dialog_label_select.show();
    }

    private void selectMoreColor(final View v) {
        ColorPickerDialog cd = new ColorPickerDialog(this.context, getResources().getString(R.string.str_choose_color), new OnColorChangedListener() {
            public void colorChanged(int color) {
                LabelInfoActivity_v2.this.log("选择颜色：" + color);
                LabelInfoActivity_v2.this.selectColor = color;
                v.setBackgroundColor(color);
                LabelInfoActivity_v2.this.updateLabelColor(color);
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
        if (this.current_select_id != null && this.current_select_id.length() > 0) {
            ContentValues values;
            if (DbUtils.queryIsUploadBySubTypeId(this.context, this.current_select_id) > 0) {
                values = new ContentValues();
                values.put("labelColor", Integer.valueOf(color));
                values.put("endUpdateTime", DateTime.getTimeString());
                DbUtils.getDb(this.context).update("t_sub_type", values, " Id is ? ", new String[]{this.current_select_id});
                return;
            }
            values = new ContentValues();
            values.put("labelColor", Integer.valueOf(color));
            DbUtils.getDb(this.context).update("t_sub_type", values, " Id is ? ", new String[]{this.current_select_id});
        }
    }

    private void initLabel_v2() {
        String[] items;
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_sub_type where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and labelType is " + this.type + " order by labelPosition", null);
        this.mapArray = new ArrayList();
        if (cursor.getCount() > 0) {
            items = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(IDemoChart.NAME));
                String id = cursor.getString(cursor.getColumnIndex("Id"));
                int labelColor = cursor.getInt(cursor.getColumnIndex("labelColor"));
                items[i] = name;
                i++;
                this.mapArray.add(new itemsBean(id, name, labelColor));
            }
        } else {
            items = new String[]{"暂无标签！"};
        }
        DbUtils.close(cursor);
        this.array = new ArrayList(Arrays.asList(items));
        this.adapter = new IconicAdapter();
    }

    private void initLabelByType(int actType, LinearLayout ll) {
        String sql = "select * from t_sub_type where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and actType is " + actType + " order by labelColor";
        if (actType == 10 || actType == 11) {
            sql = "select * from ( select * from t_sub_type where actType is 10 or actType is 11 ) where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1  order by labelColor";
        }
        Cursor cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
        ll.removeAllViews();
        RelativeLayout rl;
        TextView tv;
        LayoutParams lp;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(IDemoChart.NAME));
                String id = cursor.getString(cursor.getColumnIndex("Id"));
                int color = cursor.getInt(cursor.getColumnIndex("labelColor"));
                rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_for_select_label_white_v2, null);
                tv = (TextView) rl.findViewById(R.id.tv_temp_text);
                tv.setOnClickListener(this.myClickListener2);
                tv.setOnLongClickListener(this.myLongClickListener);
                tv.setTextColor(-16777216);
                ((TextView) rl.findViewById(R.id.tv_temp_id)).setText(id + "");
                ((ImageView) rl.findViewById(R.id.iv_temp_circle)).setOnClickListener(this.myClickListener3);
                if (color != 0) {
                    rl.setBackgroundColor(color);
                    tv.setTextColor(-1);
                }
                tv.setText(name);
                lp = new LayoutParams(-1, -2);
                lp.topMargin = 2;
                rl.setLayoutParams(lp);
                ll.addView(rl);
            }
        } else {
            rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_text_for_select_label_white_v2, null);
            tv = (TextView) rl.findViewById(R.id.tv_temp_text);
            ((ImageView) rl.findViewById(R.id.iv_temp_circle)).setVisibility(8);
            tv.setText(getString(R.string.str_no_label));
            lp = new LayoutParams(-1, -2);
            lp.topMargin = 2;
            rl.setLayoutParams(lp);
            ll.addView(rl);
        }
        DbUtils.close(cursor);
    }

    private void initUI_v2(int type) {
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_INVEST_LABEL, 0) > 0) {
            this.iv_laber_info_invest_isopen.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_laber_info_invest_isopen.setImageResource(R.drawable.ic_off_v2);
        }
        if (sp.getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_ROUTINE_LABEL, 0) > 0) {
            this.iv_laber_info_routine_isopen.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_laber_info_routine_isopen.setImageResource(R.drawable.ic_off_v2);
        }
        if (sp.getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_SLEEP_LABEL, 0) > 0) {
            this.iv_laber_info_sleep_isopen.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_laber_info_sleep_isopen.setImageResource(R.drawable.ic_off_v2);
        }
        if (sp.getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_WASTE_LABEL, 0) > 0) {
            this.iv_laber_info_waste_isopen.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_laber_info_waste_isopen.setImageResource(R.drawable.ic_off_v2);
        }
        initLabelByTypeAll(sp.getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0), true);
    }

    private void initLabelByTypeAll(int type, boolean isReload) {
        getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, type).commit();
        if (type == 1) {
            this.ll_laber_info_items.setVisibility(0);
            this.ll_laber_info_invest_items.setVisibility(8);
            this.ll_laber_info_routine_items.setVisibility(8);
            this.ll_laber_info_sleep_items.setVisibility(8);
            this.ll_laber_info_waste_items.setVisibility(8);
            this.iv_laber_info_items_defualt.setImageResource(R.drawable.ic_down_triangle_2);
            this.iv_laber_info_items_invest.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_routine.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_sleep.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_waste.setImageResource(R.drawable.ic_right_triangle_2);
            if (this.ll_laber_info_items.getChildCount() == 0) {
                initLabelByType(1, this.ll_laber_info_items);
            } else if (isReload) {
                initLabelByType(1, this.ll_laber_info_items);
            }
        } else if (type == 10) {
            this.ll_laber_info_items.setVisibility(8);
            this.ll_laber_info_invest_items.setVisibility(0);
            this.ll_laber_info_routine_items.setVisibility(8);
            this.ll_laber_info_sleep_items.setVisibility(8);
            this.ll_laber_info_waste_items.setVisibility(8);
            this.iv_laber_info_items_defualt.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_invest.setImageResource(R.drawable.ic_down_triangle_2);
            this.iv_laber_info_items_routine.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_sleep.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_waste.setImageResource(R.drawable.ic_right_triangle_2);
            if (this.ll_laber_info_invest_items.getChildCount() == 0) {
                initLabelByType(type, this.ll_laber_info_invest_items);
            } else if (isReload) {
                initLabelByType(type, this.ll_laber_info_invest_items);
            }
        } else if (type == 20) {
            this.ll_laber_info_items.setVisibility(8);
            this.ll_laber_info_invest_items.setVisibility(8);
            this.ll_laber_info_routine_items.setVisibility(0);
            this.ll_laber_info_sleep_items.setVisibility(8);
            this.ll_laber_info_waste_items.setVisibility(8);
            this.iv_laber_info_items_defualt.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_invest.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_routine.setImageResource(R.drawable.ic_down_triangle_2);
            this.iv_laber_info_items_sleep.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_waste.setImageResource(R.drawable.ic_right_triangle_2);
            if (this.ll_laber_info_routine_items.getChildCount() == 0) {
                initLabelByType(type, this.ll_laber_info_routine_items);
            } else if (isReload) {
                initLabelByType(type, this.ll_laber_info_routine_items);
            }
        } else if (type == 30) {
            this.ll_laber_info_items.setVisibility(8);
            this.ll_laber_info_invest_items.setVisibility(8);
            this.ll_laber_info_routine_items.setVisibility(8);
            this.ll_laber_info_sleep_items.setVisibility(0);
            this.ll_laber_info_waste_items.setVisibility(8);
            this.iv_laber_info_items_defualt.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_invest.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_routine.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_sleep.setImageResource(R.drawable.ic_down_triangle_2);
            this.iv_laber_info_items_waste.setImageResource(R.drawable.ic_right_triangle_2);
            if (this.ll_laber_info_sleep_items.getChildCount() == 0) {
                initLabelByType(type, this.ll_laber_info_sleep_items);
            } else if (isReload) {
                initLabelByType(type, this.ll_laber_info_sleep_items);
            }
        } else if (type == 40) {
            this.ll_laber_info_items.setVisibility(8);
            this.ll_laber_info_invest_items.setVisibility(8);
            this.ll_laber_info_routine_items.setVisibility(8);
            this.ll_laber_info_sleep_items.setVisibility(8);
            this.ll_laber_info_waste_items.setVisibility(0);
            this.iv_laber_info_items_defualt.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_invest.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_routine.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_sleep.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_waste.setImageResource(R.drawable.ic_down_triangle_2);
            if (this.ll_laber_info_waste_items.getChildCount() == 0) {
                initLabelByType(type, this.ll_laber_info_waste_items);
            } else if (isReload) {
                initLabelByType(type, this.ll_laber_info_waste_items);
            }
        } else {
            this.ll_laber_info_items.setVisibility(8);
            this.ll_laber_info_invest_items.setVisibility(8);
            this.ll_laber_info_routine_items.setVisibility(8);
            this.ll_laber_info_sleep_items.setVisibility(8);
            this.ll_laber_info_waste_items.setVisibility(8);
            this.iv_laber_info_items_defualt.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_invest.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_routine.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_sleep.setImageResource(R.drawable.ic_right_triangle_2);
            this.iv_laber_info_items_waste.setImageResource(R.drawable.ic_right_triangle_2);
        }
    }

    private void openOrCloseLabel(ImageView iv, String preName) {
        if (getsp().getInt(preName, 0) > 0) {
            getsp().edit().putInt(preName, 0).commit();
            iv.setImageResource(R.drawable.ic_off_v2);
            GeneralHelper.toastShort(this.context, getString(R.string.str_close));
            return;
        }
        getsp().edit().putInt(preName, 1).commit();
        iv.setImageResource(R.drawable.ic_on_v2);
        GeneralHelper.toastShort(this.context, getString(R.string.str_open));
    }

    public void addModifyLabel_v2(String editString, final String labelId) {
        this.tem_rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_ed_label_add_modify_v2, null);
        this.ed_tem_add_label = (EditText) this.tem_rl.findViewById(R.id.ed_tem_add_label);
        this.tv_tem_add_label_type_value = (TextView) this.tem_rl.findViewById(R.id.tv_tem_add_label_type_value);
        this.rl_tem_add_label_color = (RelativeLayout) this.tem_rl.findViewById(R.id.rl_tem_add_label_color);
        this.tv_tem_add_label_color_value = (ImageView) this.tem_rl.findViewById(R.id.tv_tem_add_label_color_value);
        this.ed_tem_add_label.setHint(getResources().getString(R.string.str_please_input_label_name));
        this.ed_tem_add_label.setText(editString);
        this.selectType = 1;
        this.selectColor = 0;
        if (labelId != null && labelId.length() > 0) {
            this.selectType = DbUtils.queryActTypeByLabelId(this.context, labelId).intValue();
            if (this.selectType < 1) {
                this.selectType = 1;
            }
        }
        final String[] str = new String[]{getString(R.string.str_default), getString(R.string.str_invest), getString(R.string.str_routine), getString(R.string.str_sleep), getString(R.string.str_waste)};
        final int[] actTypeStr = new int[]{1, 10, 20, 30, 40};
        for (int i = 0; i < actTypeStr.length; i++) {
            if (actTypeStr[i] == this.selectType) {
                this.tv_tem_add_label_type_value.setText(str[i]);
                break;
            }
        }
        this.tv_tem_add_label_type_value.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new Builder(LabelInfoActivity_v2.this.context).setTitle(LabelInfoActivity_v2.this.getString(R.string.str_choose_type)).setItems(str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LabelInfoActivity_v2.this.tv_tem_add_label_type_value.setText(str[which]);
                        LabelInfoActivity_v2.this.selectType = actTypeStr[which];
                        dialog.cancel();
                    }
                }).setNegativeButton(LabelInfoActivity_v2.this.getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });
        if (labelId != null && labelId.length() > 0) {
            this.selectColor = DbUtils.queryLabelColorByLabelId(this.context, labelId).intValue();
            this.tv_tem_add_label_color_value.setBackgroundColor(this.selectColor);
        }
        this.rl_tem_add_label_color.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LabelInfoActivity_v2.this.current_select_id = "";
                LabelInfoActivity_v2.this.curren_select_color_view = LabelInfoActivity_v2.this.tv_tem_add_label_color_value;
                LabelInfoActivity_v2.this.selectColor(LabelInfoActivity_v2.this.curren_select_color_view);
            }
        });
        new Builder(this.context).setView(this.tem_rl).setTitle(getResources().getString(R.string.str_add_label)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = LabelInfoActivity_v2.this.ed_tem_add_label.getText().toString();
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getResources().getString(R.string.str_prompt_label_no_null));
                    return;
                }
                int lastType = LabelInfoActivity_v2.this.getsp().getInt(Val.CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE, 0);
                Cursor cursor = DbUtils.getDb(LabelInfoActivity_v2.this.context).rawQuery("select id,isUpload from t_sub_type where " + DbUtils.getWhereUserId(LabelInfoActivity_v2.this.context) + " and name is '" + str + "' ", null);
                ContentValues values;
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    int isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
                    values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(0));
                    values.put("actType", Integer.valueOf(LabelInfoActivity_v2.this.selectType));
                    values.put("labelColor", Integer.valueOf(LabelInfoActivity_v2.this.selectColor));
                    if (isUpload > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{str});
                    GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getString(R.string.str_successfully));
                    dialog.cancel();
                    LabelInfoActivity_v2.this.initLabelByTypeAll(lastType, true);
                    return;
                }
                if (labelId == null || labelId.length() == 0) {
                    values = new ContentValues();
                    values.put("userId", DbUtils.queryUserId(LabelInfoActivity_v2.this.context));
                    values.put("actType", Integer.valueOf(LabelInfoActivity_v2.this.selectType));
                    values.put("labelColor", Integer.valueOf(LabelInfoActivity_v2.this.selectColor));
                    values.put("labelType", Integer.valueOf(1));
                    values.put(IDemoChart.NAME, str);
                    values.put("time", DateTime.getTimeString());
                    DbUtils.getDb(LabelInfoActivity_v2.this.context).insert("t_sub_type", null, values);
                } else {
                    values = new ContentValues();
                    values.put(IDemoChart.NAME, str);
                    values.put("actType", Integer.valueOf(LabelInfoActivity_v2.this.selectType));
                    values.put("labelColor", Integer.valueOf(LabelInfoActivity_v2.this.selectColor));
                    if (DbUtils.queryIsUploadBySubTypeId(LabelInfoActivity_v2.this.context, labelId) > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " id is ? ", new String[]{labelId});
                }
                GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getString(R.string.str_successfully));
                LabelInfoActivity_v2.this.initLabelByTypeAll(lastType, true);
                dialog.cancel();
            }
        }).create().show();
    }

    public void addModifyLabel(String editString, final String labelId) {
        final EditText et = new EditText(this.context);
        et.setBackgroundColor(getResources().getColor(R.color.white));
        et.setHint(getResources().getString(R.string.str_please_input_label_name));
        et.setText(editString);
        new Builder(this.context).setView(et).setTitle(getResources().getString(R.string.str_add_label)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString();
                ContentValues values;
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getResources().getString(R.string.str_prompt_label_no_null));
                } else if (DbUtils.getDb(LabelInfoActivity_v2.this.context).rawQuery("select id from t_sub_type where " + DbUtils.getWhereUserId(LabelInfoActivity_v2.this.context) + " and name is '" + str + "' ", null).getCount() > 0) {
                    values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(0));
                    DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " name is ? ", new String[]{str});
                    GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, "操作成功！");
                    dialog.cancel();
                } else {
                    if (labelId == null || labelId.length() == 0) {
                        values = new ContentValues();
                        values.put("userId", DbUtils.queryUserId(LabelInfoActivity_v2.this.context));
                        values.put("actType", Integer.valueOf(1));
                        values.put("labelType", Integer.valueOf(1));
                        values.put(IDemoChart.NAME, str);
                        values.put("time", DateTime.getTimeString());
                        DbUtils.getDb(LabelInfoActivity_v2.this.context).insert("t_sub_type", null, values);
                    } else {
                        values = new ContentValues();
                        values.put(IDemoChart.NAME, str);
                        DbUtils.getDb(LabelInfoActivity_v2.this.context).update("t_sub_type", values, " id is ? ", new String[]{labelId});
                    }
                    GeneralHelper.toastShort(LabelInfoActivity_v2.this.context, LabelInfoActivity_v2.this.getString(R.string.str_successfully));
                    LabelInfoActivity_v2.this.initUI_v2(LabelInfoActivity_v2.this.type);
                    dialog.cancel();
                }
            }
        }).create().show();
    }

    public SharedPreferences getsp() {
        if (this.sp == null) {
            this.sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return this.sp;
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
        Log.i("override LabelInfo", ":" + str);
    }
}
