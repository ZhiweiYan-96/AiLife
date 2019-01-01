package com.record.utils.noti;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.base.BottomActivity;
import com.record.utils.DateTime;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;

public class RemindNotification {
    String actid = "";
    Context context;
    String endtime = "";
    String starttime = "";

    public RemindNotification(Context context, String starttime, String endtime, String actid) {
        this.context = context;
        this.starttime = starttime;
        this.endtime = endtime;
        this.actid = actid;
    }

    public Notification getRemindNofication() {
        Notification noti = new Notification();
        noti.contentView = getMyRemoteViews();
        noti.flags |= 16;
        CharSequence tickerText = this.context.getResources().getString(R.string.str_remind_add_record_title1);
        if (this.starttime == null || this.starttime.length() <= 0 || this.endtime == null || this.endtime.length() <= 0 || this.actid == null || this.actid.length() <= 0) {
            switch ((int) (Math.random() * 2.0d)) {
                case 1:
                    tickerText = this.context.getResources().getString(R.string.str_remind_add_record_title2);
                    break;
                case 2:
                    tickerText = this.context.getResources().getString(R.string.str_remind_add_record_title3);
                    break;
            }
        } else if (DbUtils.queryActTypeById(this.context, this.actid).intValue() == 30) {
            tickerText = this.context.getResources().getString(R.string.str_remind_add_sleep_record);
        }
        long when = System.currentTimeMillis();
        noti.tickerText = tickerText;
        noti.icon = R.drawable.ic_launcher;
        noti.when = when;
        return noti;
    }

    private RemoteViews getMyRemoteViews() {
        try {
            if (this.starttime.length() <= 0 || DateTime.pars2Calender(this.starttime) == null) {
                return getMyRemoteViewsWithoutTime();
            }
            return getMyRemoteViewWithTime();
        } catch (Exception e) {
            e.printStackTrace();
            return getMyRemoteViewsWithoutTime();
        }
    }

    private RemoteViews getMyRemoteViewWithTime() {
        RemoteViews remoteView = new RemoteViews(this.context.getPackageName(), R.layout.tem_noti_remind_add_sleep);
        String showStartTime = this.starttime.substring(this.starttime.indexOf(" ") + 1, this.starttime.lastIndexOf(":"));
        String showEndtime = this.endtime.substring(this.endtime.indexOf(" ") + 1, this.endtime.lastIndexOf(":"));
        CharSequence tickerText = this.context.getResources().getString(R.string.str_remind_add_record_title1);
        CharSequence content = showStartTime + "-" + showEndtime;
        if (this.starttime == null || this.starttime.length() <= 0 || this.endtime == null || this.endtime.length() <= 0 || this.actid == null || this.actid.length() <= 0) {
            switch ((int) (Math.random() * 2.0d)) {
                case 1:
                    tickerText = this.context.getResources().getString(R.string.str_remind_add_record_title2);
                    break;
                case 2:
                    tickerText = this.context.getResources().getString(R.string.str_remind_add_record_title3);
                    break;
            }
        } else if (DbUtils.queryActTypeById(this.context, this.actid).intValue() == 30) {
            tickerText = this.context.getResources().getString(R.string.str_remind_add_sleep_record);
            content = this.context.getString(R.string.str_sleep) + "：" + showStartTime + "-" + showEndtime;
        }
        remoteView.setTextViewText(R.id.tv_title, tickerText);
        remoteView.setTextViewText(R.id.tv_time, content);
        Intent it = new Intent(this.context, BottomActivity.class);
        it.setFlags(536870912);
        it.addFlags(268435456);
        remoteView.setOnClickPendingIntent(R.id.iv_noti_show_set, PendingIntent.getActivity(this.context, 0, it, 0));
        Intent it1 = new Intent(this.context, AddRecordDigitActivity.class);
        it1.setFlags(536870912);
        it1.addFlags(268435456);
        it1.putExtra("startTime", this.starttime + "");
        it1.putExtra("stopTime", this.endtime + "");
        it1.putExtra("defaultCheckActId", this.actid + "");
        it1.putExtra("clearNotification", 3);
        remoteView.setOnClickPendingIntent(R.id.tv_action_edit, PendingIntent.getActivity(this.context, 1, it1, 134217728));
        it = new Intent(Val.INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_ADD);
        it.putExtra("startTime", this.starttime);
        it.putExtra("endTime", this.endtime);
        it.putExtra("actId", this.actid);
        remoteView.setOnClickPendingIntent(R.id.tv_action_add, PendingIntent.getBroadcast(this.context, 2, it, 134217728));
        return remoteView;
    }

    private RemoteViews getMyRemoteViewsWithoutTime() {
        RemoteViews remoteView = new RemoteViews(this.context.getPackageName(), R.layout.tem_noti_remind_add_sleep);
        remoteView.setTextViewText(R.id.tv_title, "点击添加记录");
        remoteView.setTextViewText(R.id.tv_time, "常记录是一个好习惯:-)");
        remoteView.setViewVisibility(R.id.tv_action_add, 8);
        remoteView.setViewVisibility(R.id.tv_action_edit, 8);
        Intent it = new Intent(Val.INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_EDIT);
        it.putExtra("startTime", "");
        it.putExtra("endTime", "");
        it.putExtra("actId", "");
        remoteView.setOnClickPendingIntent(R.id.rl_noti_add_time_outer, PendingIntent.getBroadcast(this.context, 1, it, 134217728));
        return remoteView;
    }
}
