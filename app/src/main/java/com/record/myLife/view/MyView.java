package com.record.myLife.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.utils.DateTime;
import com.record.utils.Lunar;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import java.util.Calendar;

public class MyView {
    Context context;
    LayoutInflater inflater;
    String today = null;

    class myRunnable implements Runnable {
        LinearLayout ll;

        public myRunnable(LinearLayout ll) {
            this.ll = ll;
        }

        public void run() {
            this.ll.setLayoutParams(new LayoutParams(-1, -2));
        }
    }

    public MyView(Activity activity) {
        this.context = activity;
        this.inflater = activity.getLayoutInflater();
    }

    public void addWeekTitle(RelativeLayout ll_my_calendar_title_items) {
        LinearLayout ll = (LinearLayout) this.inflater.inflate(R.layout.temp_my_calendar_title_items, null);
        TextView tv_my_calendar_column_1 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_1);
        TextView tv_my_calendar_column_2 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_2);
        TextView tv_my_calendar_column_3 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_3);
        TextView tv_my_calendar_column_4 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_4);
        TextView tv_my_calendar_column_5 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_5);
        TextView tv_my_calendar_column_6 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_6);
        TextView tv_my_calendar_column_7 = (TextView) ll.findViewById(R.id.tv_my_calendar_column_7);
        if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2) == 1) {
            tv_my_calendar_column_1.setText("日");
            tv_my_calendar_column_2.setText("一");
            tv_my_calendar_column_3.setText("二");
            tv_my_calendar_column_4.setText("三");
            tv_my_calendar_column_5.setText("四");
            tv_my_calendar_column_6.setText("五");
            tv_my_calendar_column_7.setText("六");
            tv_my_calendar_column_1.setTextColor(this.context.getResources().getColor(R.color.bg_red1));
            tv_my_calendar_column_7.setTextColor(this.context.getResources().getColor(R.color.bg_red1));
        } else {
            tv_my_calendar_column_6.setTextColor(this.context.getResources().getColor(R.color.bg_red1));
            tv_my_calendar_column_7.setTextColor(this.context.getResources().getColor(R.color.bg_red1));
        }
        ll_my_calendar_title_items.addView(ll);
        ll_my_calendar_title_items.post(new myRunnable(ll));
    }

    public LinearLayout setCalendarItems(LinearLayout ll, Calendar cal, int startDayOfWeek, int currentMonth, OnClickListener myClickListener2, String selectDate, boolean isShowMonth) {
        int dayOfWeek = cal.get(7);
        if (this.today == null) {
            this.today = DateTime.getDateString();
        }
        RelativeLayout itemRl = null;
        TextView idTv = null;
        TextView numberTv = null;
        ImageView circleIv = null;
        ImageView upCircleIv = null;
        TextView infoTv = null;
        if (startDayOfWeek == 1) {
            if (dayOfWeek == 1) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_1);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_1);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_1);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1_1);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_1);
            } else if (dayOfWeek == 2) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_2);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_2);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_2);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2_2);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_2);
            } else if (dayOfWeek == 3) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_3);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_3);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_3);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3_3);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_3);
            } else if (dayOfWeek == 4) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_4);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_4);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_4);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4_4);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_4);
            } else if (dayOfWeek == 5) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_5);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_5);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_5);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5_5);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_5);
            } else if (dayOfWeek == 6) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_6);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_6);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_6);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6_6);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_6);
            } else if (dayOfWeek == 7) {
                itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_7);
                idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_7);
                numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_7);
                circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7);
                upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7_7);
                infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_7);
            }
        } else if (dayOfWeek == 2) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_1);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_1);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_1);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_1_1);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_1);
        } else if (dayOfWeek == 3) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_2);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_2);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_2);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_2_2);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_2);
        } else if (dayOfWeek == 4) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_3);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_3);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_3);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_3_3);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_3);
        } else if (dayOfWeek == 5) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_4);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_4);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_4);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_4_4);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_4);
        } else if (dayOfWeek == 6) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_5);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_5);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_5);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_5_5);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_5);
        } else if (dayOfWeek == 7) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_6);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_6);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_6);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_6_6);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_6);
        } else if (dayOfWeek == 1) {
            itemRl = (RelativeLayout) ll.findViewById(R.id.rl_tem_calendar_item_7);
            idTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_id_7);
            numberTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_7);
            circleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7);
            upCircleIv = (ImageView) ll.findViewById(R.id.iv_tem_calendar_circle_7_7);
            infoTv = (TextView) ll.findViewById(R.id.tv_tem_calendar_text_info_7);
        }
        if (myClickListener2 != null) {
            itemRl.setOnClickListener(myClickListener2);
        }
        String date = DateTime.formatDate(cal);
        idTv.setText(date);
        int month = cal.get(2);
        if (currentMonth != month) {
            numberTv.setTextColor(this.context.getResources().getColor(R.color.black_tran_es));
        }
        numberTv.setText(cal.get(5) + "");
        boolean isShowCircle = false;
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and stopTime >= '" + date + " 00:00:00' and stopTime <= '" + date + " 23:59:59' limit 1 ", null);
        if (cursor.getCount() > 0) {
            isShowCircle = true;
        }
        DbUtils.close(cursor);
        boolean isHadInvest = false;
        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and stopTime >= '" + date + " 00:00:00' and stopTime <= '" + date + " 23:59:59' and actType = 11 limit 1 ", null);
        if (cursor2.getCount() > 0) {
            isHadInvest = true;
        }
        DbUtils.close(cursor2);
        boolean isHadSummary = false;
        Cursor cursor3 = DbUtils.getDb(this.context).rawQuery("select id from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time = '" + date + "' and length(remarks) > 0", null);
        if (cursor3.getCount() > 0) {
            isHadSummary = true;
        }
        DbUtils.close(cursor3);
        String info = "";
        if (this.today != null && this.today.equals(date)) {
            info = this.context.getResources().getString(R.string.str_today);
            itemRl.setBackgroundResource(R.drawable.x_gray_bg_red_frame_middle);
        }
        if (currentMonth == month) {
            info = new Lunar(cal).getFestival();
        }
        if (info != null && info.length() > 0) {
            infoTv.setText(info);
            infoTv.setVisibility(0);
            infoTv.setTextColor(this.context.getResources().getColor(R.color.black_tran_fs));
        }
        if (isHadSummary && info.length() > 0) {
            circleIv.setVisibility(4);
            upCircleIv.setVisibility(0);
        } else if (isHadSummary) {
            circleIv.setVisibility(4);
            upCircleIv.setVisibility(0);
        } else {
            circleIv.setVisibility(4);
            upCircleIv.setVisibility(4);
        }
        if (isHadInvest) {
            numberTv.setTextColor(this.context.getResources().getColor(R.color.bg_green1));
        } else if (isShowCircle) {
            numberTv.setTextColor(this.context.getResources().getColor(R.color.bg_red1));
        }
        if (selectDate != null && selectDate.equals(date)) {
            itemRl.setBackgroundResource(R.drawable.x_white_bg_red_frame_middle);
        }
        if (isShowMonth) {
            if (startDayOfWeek == 1) {
                if (dayOfWeek == 1 && (info == null || info.length() == 0)) {
                    if (isShowCircle) {
                        circleIv.setVisibility(4);
                        upCircleIv.setVisibility(0);
                        infoTv.setText(DateTime.getMonth2(cal));
                    } else {
                        infoTv.setText(DateTime.getMonth2(cal));
                    }
                    infoTv.setVisibility(0);
                }
            } else if (dayOfWeek == 2 && (info == null || info.length() == 0)) {
                if (isShowCircle) {
                    circleIv.setVisibility(4);
                    upCircleIv.setVisibility(0);
                    infoTv.setText(DateTime.getMonth2(cal));
                } else {
                    infoTv.setText(DateTime.getMonth2(cal));
                }
                infoTv.setVisibility(0);
            }
        }
        return ll;
    }
}
