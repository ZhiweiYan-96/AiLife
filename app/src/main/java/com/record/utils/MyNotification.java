package com.record.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.util.Log;
import android.widget.RemoteViews;
import com.record.bean.User;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.base.BottomActivity;
import com.record.myLife.settings.remind.SetRemindActivity;
import com.record.myLife.view.AddNoteActivity;
import com.record.service.TimerService;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;
import java.util.Calendar;

public class MyNotification {
    static String TAG = "override";
    Context context;
    Notification noti;

    public MyNotification(Context context) {
        this.context = context;
        TAG = "override " + getClass().getName();
    }

    public void initNoti() {
        if (VERSION.SDK_INT >= 11) {
            initNoti2_v2();
        }
    }

    public void initCountingNoti(String id) {
        log("通知栏--目标正在计时initCountingNoti");
        if (VERSION.SDK_INT >= 11) {
            try {
                int idInt = Integer.parseInt(id);
                if (idInt < 1) {
                    Log.i("overrride MyNotification", "actId:" + idInt);
                    return;
                }
            } catch (NumberFormatException e) {
                DbUtils.exceptionHandler(e);
            }
            initCountingNoti2(id);
        }
    }

    @SuppressLint({"NewApi"})
    private void initNoti2_v2() {
        try {
            Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.widgetGoalsList(this.context), null);
            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.tem_noti_items_v2);
            Intent it = new Intent(this.context, BottomActivity.class);
            it.setFlags(536870912);
            it.addFlags(268435456);
            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_set, PendingIntent.getActivity(this.context, 0, it, 0));
            Intent it1 = new Intent(this.context, AddRecordDigitActivity.class);
            it1.setFlags(536870912);
            it1.addFlags(268435456);
            it1.putExtra("startTime", "");
            it1.putExtra("stopTime", "");
            it1.putExtra("defaultCheckActId", "");
            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_0, PendingIntent.getActivity(this.context, 11, it1, 134217728));
            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_0, 0);
            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_0, ((Integer) Val.col_Str2Ic_72_Map.get("bg_blue1")).intValue());
            Log.i("override MyNoti", "通知栏--目标列表initNoti2 cur.getCount()：" + cur.getCount());
            if (cur.getCount() > 0) {
                if (cur.getCount() > 5) {
                    remoteViews.setViewVisibility(R.id.iv_noti_show_set, 8);
                }
                int i = 1;
                int notiNumber = PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_NOTI_ITEMS_NUMBER, 6);
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex("id"));
                    int type = cur.getInt(cur.getColumnIndex(a.a));
                    if (cur.getCount() <= 4 || type != 10) {
                        String actName = cur.getString(cur.getColumnIndex("actName"));
                        String image = cur.getString(cur.getColumnIndex("image"));
                        String color = cur.getString(cur.getColumnIndex("color"));
                        it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                        it.putExtra("id", id);
                        it.putExtra("isToast", true);
                        PendingIntent pi = PendingIntent.getBroadcast(this.context, Integer.parseInt(id), it, 134217728);
                        if (i == 1) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_1, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_1, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_1, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_1, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 2) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_2, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_2, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_2, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_2, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 3) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_3, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_3, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_3, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_3, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 4) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_4, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_4, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_4, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_4, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 5) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_5, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_5, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_5, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_5, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 6) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_6, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_6, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_6, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_6, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 7) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_7, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_7, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_7, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_7, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        } else if (i == 8) {
                            remoteViews.setOnClickPendingIntent(R.id.iv_noti_show_label_pre_8, pi);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_8, Val.getLabelIntByName(image));
                            remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_8, 0);
                            remoteViews.setImageViewResource(R.id.iv_noti_show_label_pre_8, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                        }
                        if (i < notiNumber && !cur.isLast()) {
                            i++;
                        } else if (i < 8) {
                            for (int j = i + 1; j <= 8; j++) {
                                if (j == 4) {
                                    remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_4, 8);
                                } else if (j == 5) {
                                    remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_5, 8);
                                } else if (j == 6) {
                                    remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_6, 8);
                                } else if (j == 7) {
                                    remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_7, 8);
                                } else if (j == 8) {
                                    remoteViews.setViewVisibility(R.id.rl_noti_show_label_bg_8, 8);
                                }
                            }
                        }
                    }
                }
            }
            DbUtils.close(cur);
            this.noti = new Notification();
            this.noti.contentView = remoteViews;
            Notification notification = this.noti;
            notification.flags |= 2;
            notification = this.noti;
            notification.flags |= 32;
            CharSequence tickerText = this.context.getResources().getString(R.string.app_name);
            long when = System.currentTimeMillis();
            this.noti.tickerText = tickerText;
            this.noti.icon = R.drawable.ic_launcher;
            this.noti.when = when;
            if (VERSION.SDK_INT >= 16) {
                this.noti.priority = -2;
            }
            ((NotificationManager) this.context.getSystemService("notification")).notify(1, this.noti);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    @SuppressLint({"NewApi"})
    private void initCountingNoti2(String id) {
        try {
            Cursor cur = DbUtils.getDb(this.context).rawQuery("select * from t_act where userId is " + User.getInstance().getUserId() + " and id is " + id, null);
            RemoteViews remoteView = new RemoteViews(this.context.getPackageName(), R.layout.tem_noti_rect_show_v2);
            Intent it = new Intent(this.context, BottomActivity.class);
            it.setFlags(536870912);
            it.addFlags(268435456);
            remoteView.setOnClickPendingIntent(R.id.rl_noti_show_outer, PendingIntent.getActivity(this.context, 0, it, 0));
            if (cur.getCount() > 0) {
                cur.moveToNext();
                String actName = cur.getString(cur.getColumnIndex("actName"));
                String image = cur.getString(cur.getColumnIndex("image"));
                String color = cur.getString(cur.getColumnIndex("color"));
                int isHided = cur.getInt(cur.getColumnIndex("isHided"));
                remoteView.setTextViewText(R.id.tv_noti_show_time, DateTime.calculateTime5(this.context, (long) TimerService.actCount));
                remoteView.setTextViewText(R.id.tv_noti_show_actName, actName);
                remoteView.setImageViewResource(R.id.iv_noti_show_label, Val.getLabelIntByName(image));
                remoteView.setImageViewResource(R.id.iv_noti_show_label_pre, ((Integer) Val.col_Str2Ic_72_Map.get(color)).intValue());
                if (isHided > 0) {
                    remoteView.setViewVisibility(R.id.rl_noti_right_coner, 0);
                } else {
                    remoteView.setViewVisibility(R.id.rl_noti_right_coner, 8);
                }
                it = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                it.putExtra("id", id);
                it.putExtra("isToast", true);
                remoteView.setOnClickPendingIntent(R.id.iv_noti_show_start, PendingIntent.getBroadcast(this.context, Integer.parseInt(id), it, 134217728));
            }
            DbUtils.close(cur);
            this.noti = new Notification();
            this.noti.contentView = remoteView;
            Notification notification = this.noti;
            notification.flags |= 2;
            notification = this.noti;
            notification.flags |= 32;
            long when = System.currentTimeMillis();
            this.noti.tickerText = "爱今天";
            this.noti.icon = R.drawable.ic_itodayss_circle2;
            this.noti.when = when;
            if (VERSION.SDK_INT >= 16) {
                this.noti.priority = -2;
            }
            ((NotificationManager) this.context.getSystemService("notification")).notify(1, this.noti);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void initMorningVoiceNoti() {
        String[] arr = getMorningTitle();
        initRetrospect(this.context.getString(R.string.str_Morning_voice), arr[0], arr[1], Val.INTENT_ACTION_NOTI_MORNING_VOICE, AddNoteActivity.class);
    }

    public void initRetrospectNoti() {
        String tickerText = this.context.getString(R.string.str_summarize_everyday);
        String[] arr = getTitle();
        initRetrospect(tickerText, arr[0], arr[1], Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER, AddNoteActivity.class);
    }

    private String[] getMorningTitle() {
        String[] arr = new String[2];
        String title = this.context.getString(R.string.str_record_now_idea);
        int hour = DateTime.getHour(Calendar.getInstance());
        if (hour >= 4 && hour < 12) {
            title = this.context.getString(R.string.str_good_morning) + "," + this.context.getString(R.string.str_record_now_idea);
        }
        String msg = this.context.getString(R.string.str_click_add);
        int ra = (int) (Math.random() * 15.0d);
        if (ra == 1) {
            msg = "保持饥饿。保持愚蠢。--乔布斯";
        } else if (ra == 2) {
            msg = "活着就是为了改变世界，难道还有其他原因吗？--乔布斯";
        } else if (ra == 3) {
            msg = "梦想，她真的是会实现的。我们终将成为我们想成为的样子，只是，她来临前，一切都是静悄悄。";
        } else if (ra == 4 || ra == 5 || ra == 6) {
            msg = "记录今天最重要的三件事，并努力完成！";
        } else if (ra == 7 || ra == 8) {
            msg = "试着将你的密码设置成自己的目标或格言，不断提醒自己！";
        } else if (ra == 11 || ra == 12) {
            msg = "你的身体语言会影响你的状态，让自己保持抬头挺胸收腹。";
        } else if (ra == 9) {
            msg = "今天，给你见到的人一个微笑并主动打个招呼！";
        } else if (ra == 10) {
            msg = "缓缓深吸一口气（至少7秒），紧握拳头，感受力量！";
        } else {
            msg = "永远不要给自己设限！即使刚刚跌倒了。";
        }
        Calendar cal = Calendar.getInstance();
        String festival = "";
        if ("Asia/Shanghai".equals(cal.getTimeZone().getID())) {
            Lunar lunar = new Lunar(cal);
            festival = lunar.LunarFestival();
            if (festival == null || festival.length() == 0) {
                festival = lunar.Festival();
            }
        }
        if (festival != null && festival.length() > 0) {
            title = "今天是" + festival + "哦！";
            msg = "感受这一时刻，开始新的生活。";
        }
        arr[0] = title;
        arr[1] = msg;
        return arr;
    }

    private String[] getTitle() {
        String[] arr = new String[2];
        String title = this.context.getString(R.string.str_summarize_everyday);
        String msg = this.context.getString(R.string.str_click_add);
        int ra = (int) (Math.random() * 17.0d);
        if (ra == 1) {
            msg = "反躬自省是通向美德和上帝的途径。--瓦茨";
        } else if (ra == 2) {
            msg = "知错就改，永远是不嫌迟的。--莎士比亚";
        } else if (ra == 3) {
            msg = "最伟大的胜利，就是战胜自己。--高尔基";
        } else if (ra == 4) {
            msg = "不会评价自己，就不会评价别人。";
        } else if (ra == 5) {
            msg = "最困难的事情就是认识自己。";
        } else if (ra == 6) {
            msg = "要想了解自己，最好问问别人。";
        } else if (ra == 7) {
            msg = "人不能没有批评和自我批评，那样一个人就不能进步。--毛泽东";
        } else if (ra == 8) {
            msg = "被人揭下面具是一种失败，自己揭下面具是一种胜利。--雨果";
        } else if (ra == 9) {
            msg = "不会从失败中寻找教训的人，他们的成功之路是遥远的。--拿破仑";
        } else if (ra == 10) {
            msg = "自重、自觉、自制，此三者可以引至生命的崇高境域。--丁尼生";
        } else if (ra == 11) {
            msg = "每个人都会犯错，但是，只有愚人才会执过不改。——西塞罗";
        } else if (ra == 12) {
            msg = "自己的鞋子，自己知道紧在哪里。";
        } else if (ra == 13) {
            msg = "成功的起始点乃自我分析，成功的秘诀则是自我反省。——陈安之";
        } else if (ra == 14) {
            msg = "吾日三省吾身。——孔子";
        } else if (ra == 15) {
            msg = "若有恒，何必三更眠五更起；最无益，莫过一日曝十日寒。--";
        } else {
            msg = this.context.getString(R.string.str_summarize_today_gain);
        }
        if (ra < 5) {
            title = "每日反省";
        } else if (ra <= 5 || ra >= 10) {
            title = this.context.getString(R.string.str_summarize_everyday);
        } else {
            title = "每日总结";
        }
        Calendar cal = Calendar.getInstance();
        String festival = "";
        if ("Asia/Shanghai".equals(cal.getTimeZone().getID())) {
            Lunar lunar = new Lunar(cal);
            festival = lunar.LunarFestival();
            if (festival == null || festival.length() == 0) {
                festival = lunar.Festival();
            }
        }
        if (festival != null && festival.length() > 0) {
            title = festival + "快乐!";
            msg = this.context.getString(R.string.str_summarize_today_gain);
        }
        arr[0] = title;
        arr[1] = msg;
        return arr;
    }

    @SuppressLint({"NewApi"})
    private void initRetrospect(String tickerText, String title, String msg, String action, Class<?> cls) {
        try {
            this.noti = new Notification();
            Notification notification = this.noti;
            notification.flags |= 8;
            notification = this.noti;
            notification.flags |= 16;
            notification = this.noti;
            notification.defaults |= 2;
            long when = System.currentTimeMillis();
            RemoteViews remoteView = new RemoteViews(this.context.getPackageName(), R.layout.tem_noti_retrospection);
            remoteView.setTextViewText(R.id.tv_noti_show_actName, title);
            remoteView.setTextViewText(R.id.tv_noti_show_remark, msg);
            if (Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(action)) {
                remoteView.setImageViewResource(R.id.iv_noti_show_label, R.drawable.ic_itodayss_circle2_48);
            } else if (Val.INTENT_ACTION_NOTI_MORNING_VOICE.equals(action)) {
                remoteView.setImageViewResource(R.id.iv_noti_show_label, R.drawable.ic_morning_voice_icon);
            }
            Intent it = new Intent(this.context, SetRemindActivity.class);
            it.setFlags(536870912);
            it.addFlags(268435456);
            remoteView.setOnClickPendingIntent(R.id.iv_noti_show_set, PendingIntent.getActivity(this.context, 0, it, 0));
            Intent it2 = new Intent(this.context, cls);
            it2.setFlags(536870912);
            it2.addFlags(268435456);
            it2.setAction(action);
            PendingIntent pi2 = PendingIntent.getActivity(this.context, 0, it2, 0);
            remoteView.setOnClickPendingIntent(R.id.rl_noti_show_outer, pi2);
            this.noti.contentView = remoteView;
            this.noti.tickerText = tickerText;
            this.noti.contentIntent = pi2;
            this.noti.icon = R.drawable.ic_itodayss_circle2;
            this.noti.when = when;
            if (!Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(action)) {
                if (Val.INTENT_ACTION_NOTI_MORNING_VOICE.equals(action)) {
                    this.noti.icon = R.drawable.ic_morning_voice_icon;
                }
            }
            ((NotificationManager) this.context.getSystemService("notification")).notify(2, this.noti);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
