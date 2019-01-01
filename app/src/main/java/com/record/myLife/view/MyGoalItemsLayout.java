package com.record.myLife.view;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.utils.LogUtils;
import com.record.utils.Sql;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;

public class MyGoalItemsLayout {
    Activity context;
    String defaultCheckId;
    LayoutInflater inflater;
    OnClickListener myclClickListener = new OnClickListener() {
        public void onClick(View v) {
            String desc = v.getContentDescription() + "";
            if (MyGoalItemsLayout.this.viewContentDescription.equals(desc)) {
                MyGoalItemsLayout.this.resetViewBgWhenIsItem(v);
            } else if (MyGoalItemsLayout.this.subViewContentDescription.equals(desc)) {
                MyGoalItemsLayout.this.resetViewBgWhenIsSubView(v);
            } else {
                LogUtils.log("自定义控出错啦！");
            }
            ((RelativeLayout) v).getChildAt(1).setBackgroundResource(R.drawable.x_white_bg_blue_frame3);
            String checkActId = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            if (MyGoalItemsLayout.this.myclClickListenerOutSide != null) {
                MyGoalItemsLayout.this.myclClickListenerOutSide.onClick(v, checkActId);
            }
        }
    };
    MyOnItemsClickListener myclClickListenerOutSide;
    LinearLayout parentLayout;
    String subViewContentDescription = "subItemsView";
    String viewContentDescription = "itemsView";

    public interface MyOnItemsClickListener {
        void onClick(View view, String str);
    }

    public void cancelAllSelect() {
        if (this.parentLayout != null && this.parentLayout.getChildCount() > 0) {
            ViewGroup v = (ViewGroup) this.parentLayout.getChildAt(0);
            String desc = v.getContentDescription() + "";
            if (this.viewContentDescription.equals(desc)) {
                resetViewBgWhenIsItem(v);
            } else if (this.subViewContentDescription.equals(desc)) {
                resetViewBgWhenIsSubView(v);
            } else {
                LogUtils.log("自定义控出错啦！");
            }
        }
    }

    private void resetViewBgWhenIsItem(View v) {
        LinearLayout ll = (LinearLayout) v.getParent();
        int count = ll.getChildCount();
        for (int i = 0; i < count; i++) {
            ViewGroup vg = (ViewGroup) ll.getChildAt(i);
            String tempDes = vg.getContentDescription() + "";
            if (this.viewContentDescription.equals(tempDes) || this.subViewContentDescription.equals(tempDes)) {
                vg.getChildAt(1).setBackgroundColor(this.context.getResources().getColor(R.color.translate));
            } else {
                int tempCount = vg.getChildCount();
                for (int j = 0; j < tempCount; j++) {
                    ((ViewGroup) vg.getChildAt(j)).getChildAt(1).setBackgroundColor(this.context.getResources().getColor(R.color.translate));
                }
            }
        }
    }

    private void resetViewBgWhenIsSubView(View v) {
        LinearLayout ll = (LinearLayout) v.getParent();
        int count = ll.getChildCount();
        for (int i = 0; i < count; i++) {
            ((ViewGroup) ll.getChildAt(i)).getChildAt(1).setBackgroundColor(this.context.getResources().getColor(R.color.translate));
        }
        ViewGroup bigViewGroup = (ViewGroup) ll.getParent();
        int childCount = bigViewGroup.getChildCount();
        for (int j = 0; j < childCount; j++) {
            ViewGroup item = (ViewGroup) bigViewGroup.getChildAt(j);
            String contentDesc = item.getContentDescription() + "";
            if (this.viewContentDescription.equals(contentDesc) || this.subViewContentDescription.equals(contentDesc)) {
                item.getChildAt(1).setBackgroundColor(this.context.getResources().getColor(R.color.translate));
            } else {
                int subChildCount = item.getChildCount();
                for (int k = 0; k < subChildCount; k++) {
                    ((ViewGroup) item.getChildAt(k)).getChildAt(1).setBackgroundColor(this.context.getResources().getColor(R.color.translate));
                }
            }
        }
    }

    public MyGoalItemsLayout(Activity context, LinearLayout parentLayout, MyOnItemsClickListener goalItemsIdClickLister) {
        this.context = context;
        this.parentLayout = parentLayout;
        this.myclClickListenerOutSide = goalItemsIdClickLister;
        this.inflater = context.getLayoutInflater();
    }

    public MyGoalItemsLayout(Activity context, LinearLayout parentLayout, MyOnItemsClickListener goalItemsIdClickLister, String defaultCheckId) {
        this.context = context;
        this.parentLayout = parentLayout;
        this.myclClickListenerOutSide = goalItemsIdClickLister;
        this.inflater = context.getLayoutInflater();
        this.defaultCheckId = defaultCheckId;
    }

    public LinearLayout getAddItems() {
        LayoutInflater inflater = this.context.getLayoutInflater();
        Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.getBigGoalsWithOtherType2(this.context), null);
        View tempview = null;
        if (cur.getCount() > 0) {
            this.parentLayout.removeAllViews();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("id"));
                int type = cur.getInt(cur.getColumnIndex(a.a));
                if (cur.getCount() <= 4 || type != 10) {
                    String actName = cur.getString(cur.getColumnIndex("actName"));
                    String image = cur.getString(cur.getColumnIndex("image"));
                    String color = cur.getString(cur.getColumnIndex("color"));
                    String deadline = cur.getString(cur.getColumnIndex("deadline"));
                    int isSubGoal = cur.getInt(cur.getColumnIndex("isSubGoal"));
                    int isHided = cur.getInt(cur.getColumnIndex("isHided"));
                    Cursor cursor3 = DbUtils.getDb(this.context).rawQuery(Sql.getSubGoals(this.context, id), null);
                    View item;
                    if (type != 11 || cursor3.getCount() <= 0) {
                        item = getAddActItems_v2(id, actName, image, color, deadline, -1, isHided, type, this.viewContentDescription);
                        this.parentLayout.addView(item);
                        if (isSelect(id)) {
                            tempview = item;
                        }
                    } else {
                        LinearLayout childsll = (LinearLayout) inflater.inflate(R.layout.tem_ll_hori, null);
                        childsll.setBackgroundResource(R.drawable.x_translate_bg_black_frame);
                        item = getAddActItems_v2(id, actName, image, color, deadline, isSubGoal, isHided, type, this.subViewContentDescription);
                        if (isSelect(id)) {
                            tempview = item;
                        }
                        childsll.addView(item);
                        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery(Sql.getSubGoals(this.context, id), null);
                        if (cursor2.getCount() > 0) {
                            while (cursor2.moveToNext()) {
                                String id2 = getString(cursor2, "id");
                                int type2 = getInt(cursor2, a.a);
                                View item2 = getAddActItems_v2(id2, getString(cursor2, "actName"), getString(cursor2, "image"), getString(cursor2, "color"), getString(cursor2, "deadline"), getInt(cursor2, "isSubGoal"), getInt(cursor2, "isHided"), type2, this.subViewContentDescription);
                                childsll.addView(item2);
                                if (isSelect(id)) {
                                    tempview = item2;
                                }
                            }
                        }
                        DbUtils.close(cursor2);
                        if (childsll != null) {
                            this.parentLayout.addView(childsll);
                        }
                    }
                    DbUtils.close(cursor3);
                }
            }
        }
        DbUtils.close(cur);
        if (tempview != null) {
            tempview.performClick();
        }
        return this.parentLayout;
    }

    private boolean isSelect(String id) {
        if (this.defaultCheckId == null || this.defaultCheckId.length() <= 0 || !this.defaultCheckId.equals(id)) {
            return false;
        }
        return true;
    }

    private int getInt(Cursor cur, String column) {
        return cur.getInt(cur.getColumnIndex(column));
    }

    private String getString(Cursor cur, String column) {
        return cur.getString(cur.getColumnIndex(column));
    }

    private RelativeLayout getAddActItems(String id, String name, String label, String color) {
        RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.tem_sigle_act, null);
        rl_temp_show_outer.setOnClickListener(this.myclClickListener);
        TextView tv_temp_act_name = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_name);
        ImageView iv_temp_color = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_color);
        ImageView iv_temp_label = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_label);
        ((TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_id)).setText(id);
        tv_temp_act_name.setText(name);
        iv_temp_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
        iv_temp_label.setImageResource(Val.getLabelIntByName(label));
        return rl_temp_show_outer;
    }

    private RelativeLayout getAddActItems_v2(String id, String name, String label, String color, String deadline, int isSubGoal, int hided, int type, String contentDescription) {
        RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.tem_sigle_act, null);
        rl_temp_show_outer.setOnClickListener(this.myclClickListener);
        rl_temp_show_outer.setContentDescription(contentDescription);
        TextView tv_temp_act_name = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_name);
        ImageView iv_temp_color = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_color);
        ImageView iv_temp_label = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_label);
        ImageView iv_temp_top_left_corner = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_top_left_corner);
        RelativeLayout rl_temp_right_coner = (RelativeLayout) rl_temp_show_outer.findViewById(R.id.rl_temp_right_coner);
        ImageView iv_temp_right_coner_bg = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_right_coner_bg);
        TextView tv_temp_right_coner_text = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_right_coner_text);
        ((TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_id)).setText(id);
        tv_temp_act_name.setText(name);
        iv_temp_color.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
        iv_temp_label.setImageResource(Val.getLabelIntByName(label));
        if (type != 11) {
            iv_temp_top_left_corner.setVisibility(8);
        } else if (isSubGoal >= 0) {
            iv_temp_top_left_corner.setVisibility(0);
            if (deadline == null || deadline.length() <= 0) {
                if (isSubGoal == 0) {
                }
            } else if (isSubGoal == 0) {
                iv_temp_top_left_corner.setImageResource(R.drawable.ic_label_green_3);
            } else {
                iv_temp_top_left_corner.setImageResource(R.drawable.ic_subgoal_black);
            }
        } else {
            iv_temp_top_left_corner.setVisibility(8);
        }
        if (hided > 0) {
            rl_temp_right_coner.setVisibility(0);
            tv_temp_right_coner_text.setText("隐");
        } else {
            rl_temp_right_coner.setVisibility(8);
        }
        return rl_temp_show_outer;
    }
}
