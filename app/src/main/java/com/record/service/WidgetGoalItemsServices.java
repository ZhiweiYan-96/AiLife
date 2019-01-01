package com.record.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import com.record.bean.Act;
import com.record.bean.Act2;
import com.record.myLife.R;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.Sql;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;

@SuppressLint({"NewApi"})
public class WidgetGoalItemsServices extends RemoteViewsService {

    class myRemoteViewsFactory implements RemoteViewsFactory {
        ArrayList<Act2> actList;
        int appWidgetId;
        Context context;
        Intent tranmitIntent;

        public myRemoteViewsFactory(Context widgetGoalItemsServices, Intent intent) {
            this.context = widgetGoalItemsServices;
        }

        public void onCreate() {
            WidgetGoalItemsServices.this.log("目标插件--onCreate！");
            this.actList = new ArrayList();
            Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.getBigGoalsWithOtherType(this.context), null);
            initActList(cur);
            DbUtils.close(cur);
        }

        public void initActList(Cursor cur) {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    int id = cur.getInt(cur.getColumnIndex("id"));
                    int dbType = cur.getInt(cur.getColumnIndex(a.a));
                    if (dbType != 10) {
                        int isSubGoal = cur.getInt(cur.getColumnIndex("isSubGoal"));
                        int position = cur.getInt(cur.getColumnIndex("position"));
                        int isHided = cur.getInt(cur.getColumnIndex("isHided"));
                        int timeOfEveryday = cur.getInt(cur.getColumnIndex("timeOfEveryday"));
                        int expectSpend = cur.getInt(cur.getColumnIndex("expectSpend"));
                        String actName = cur.getString(cur.getColumnIndex("actName"));
                        String image = cur.getString(cur.getColumnIndex("image"));
                        String color = cur.getString(cur.getColumnIndex("color"));
                        String intruction = cur.getString(cur.getColumnIndex("intruction"));
                        if (dbType == 10) {
                            intruction = WidgetGoalItemsServices.this.getResources().getString(R.string.str_help_time);
                        } else if (dbType == 20) {
                            intruction = WidgetGoalItemsServices.this.getResources().getString(R.string.str_oblige_helpless_time);
                        } else if (dbType == 30) {
                            intruction = WidgetGoalItemsServices.this.getResources().getString(R.string.str_sleep_time);
                        } else if (dbType == 40) {
                            intruction = WidgetGoalItemsServices.this.getResources().getString(R.string.str_helpless_time);
                        }
                        Cursor cursor = DbUtils.getDb(this.context).rawQuery(Sql.getSubGoals(this.context, id + ""), null);
                        if (cursor.getCount() > 0) {
                            if (isHided == 0) {
                                this.actList.add(new Act2(id, image, color, actName, intruction, 1, 0, position, dbType, isSubGoal, isHided, expectSpend, timeOfEveryday));
                            }
                            while (cursor.moveToNext()) {
                                int id2 = WidgetGoalItemsServices.getInt(cursor, "id");
                                int dbType2 = WidgetGoalItemsServices.getInt(cursor, a.a);
                                int isSubGoal2 = WidgetGoalItemsServices.getInt(cursor, "isSubGoal");
                                int position2 = WidgetGoalItemsServices.getInt(cursor, "position");
                                int isHided2 = WidgetGoalItemsServices.getInt(cursor, "isHided");
                                int timeOfEveryday2 = WidgetGoalItemsServices.getInt(cursor, "timeOfEveryday");
                                int expectSpend2 = WidgetGoalItemsServices.getInt(cursor, "expectSpend");
                                String actName2 = WidgetGoalItemsServices.getStr(cursor, "actName");
                                String image2 = WidgetGoalItemsServices.getStr(cursor, "image");
                                String color2 = WidgetGoalItemsServices.getStr(cursor, "color");
                                String intruction2 = WidgetGoalItemsServices.getStr(cursor, "intruction");
                                int isLast = 0;
                                if (cursor.isLast()) {
                                    isLast = 1;
                                }
                                if (isHided2 == 0) {
                                    this.actList.add(new Act2(id2, image2, color2, actName2, intruction2, 0, isLast, position2, dbType2, isSubGoal2, isHided2, expectSpend2, timeOfEveryday2));
                                }
                            }
                            DbUtils.close(cursor);
                        } else {
                            DbUtils.close(cursor);
                            if (isHided == 0) {
                                this.actList.add(new Act2(id, image, color, actName, intruction, 0, 0, position, dbType, isSubGoal, isHided, expectSpend, timeOfEveryday));
                            }
                        }
                    }
                }
            }
            DbUtils.close(cur);
            WidgetGoalItemsServices.this.log(this.actList.toString());
        }

        public void onDataSetChanged() {
            try {
                WidgetGoalItemsServices.this.log("目标插件--onDataSetChanged！");
                Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.goalGetGoalsList(this.context), null);
                WidgetGoalItemsServices.this.log("目标插件--size" + this.actList.size() + ",cur.getCount()" + cur.getCount());
                if (this.actList == null) {
                    this.actList = new ArrayList();
                    DbUtils.close(cur);
                    cur = DbUtils.getDb(this.context).rawQuery(Sql.getBigGoalsWithOtherType(this.context), null);
                    initActList(cur);
                } else if (this.actList.size() != cur.getCount()) {
                    this.actList.clear();
                    DbUtils.close(cur);
                    cur = DbUtils.getDb(this.context).rawQuery(Sql.getBigGoalsWithOtherType(this.context), null);
                    initActList(cur);
                }
                DbUtils.close(cur);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onDestroy() {
            if (this.actList != null) {
                this.actList.clear();
            }
        }

        public int getCount() {
            return this.actList.size();
        }

        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.template_act_rect_show_for_widget_v3);
            Act2 act = (Act2) this.actList.get(position);
            if (act.getIsHided() > 0) {
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_show_outer, 8);
            } else {
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_show_outer, 0);
            }
            remoteViews.setImageViewResource(R.id.iv_temp_show_label_pre, ((Integer) Val.col_Str2Ic_72_Map.get(act.getColor())).intValue());
            remoteViews.setImageViewResource(R.id.iv_temp_show_label, Val.getLabelIntByName(act.getImage()));
            remoteViews.setImageViewResource(R.id.iv_temp_show_start, R.drawable.ic_start_white);
            remoteViews.setTextViewText(R.id.tv_temp_show_actName, act.getActName());
            remoteViews.setTextViewText(R.id.tv_temp_show_remark, act.getIntruction());
            remoteViews.setImageViewResource(R.id.iv_temp_show_label_outer_bg, R.drawable.ic_color_rect_tran);
            if (act.getType() == 11) {
                double expectSpend = (double) act.getExpectSpend2();
                remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 0);
                remoteViews.setImageViewResource(R.id.iv_temp_top_left_corner, R.drawable.ic_label_green_3);
                double hadIvest = DbUtils.queryStaticsHadInvestByGoalId(this.context, act.getId());
                if (hadIvest >= 120.0d) {
                    remoteViews.setViewVisibility(R.id.tv_temp_show_hours, 0);
                    String hadInvestStr = FormatUtils.format_1fra(hadIvest / 3600.0d);
                    remoteViews.setTextViewText(R.id.tv_temp_show_hours, hadInvestStr + "/" + FormatUtils.format_0fra(expectSpend / 3600.0d) + "h");
                } else {
                    remoteViews.setViewVisibility(R.id.tv_temp_show_hours, 4);
                }
                double todayHadIvest = DbUtils.queryStaticsHadInvestByDateGoalId(this.context, act.getId(), DateTime.getDateString());
                double timeOfEveryday = (double) act.getTimeOfEveryday();
                if (timeOfEveryday > 0.0d) {
                    remoteViews.setViewVisibility(R.id.tv_temp_show_today_hours, 0);
                    String todayHadIvestStr = FormatUtils.format_1fra(todayHadIvest / 3600.0d);
                    remoteViews.setTextViewText(R.id.tv_temp_show_today_hours, todayHadIvestStr + "/" + FormatUtils.format_1fra(timeOfEveryday / 3600.0d) + "h");
                } else if (todayHadIvest <= 0.0d || timeOfEveryday > 0.0d) {
                    remoteViews.setViewVisibility(R.id.tv_temp_show_today_hours, 4);
                } else {
                    remoteViews.setViewVisibility(R.id.tv_temp_show_today_hours, 0);
                    remoteViews.setTextViewText(R.id.tv_temp_show_today_hours, FormatUtils.format_1fra(todayHadIvest / 3600.0d) + "h");
                }
            } else {
                remoteViews.setViewVisibility(R.id.tv_temp_show_hours, 4);
                remoteViews.setViewVisibility(R.id.tv_temp_show_today_hours, 4);
            }
            int isSubGoal = act.getIsSubGoal();
            int isTopShow = act.getExpectSpend();
            int isBottomShow = act.getHadSpend();
            if (isSubGoal == 0 && isTopShow > 0) {
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_top, 0);
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_bottom, 8);
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_left, 0);
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_right, 0);
                remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 0);
                remoteViews.setImageViewResource(R.id.iv_temp_top_left_corner, R.drawable.ic_label_green_3);
            } else if (isSubGoal <= 0 || isBottomShow <= 0) {
                int i;
                int i2;
                if (isSubGoal > 0) {
                    i = 1;
                } else {
                    i = 0;
                }
                if (isTopShow == 0) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                if ((i2 & i) == 0 || isBottomShow != 0) {
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_top, 8);
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_bottom, 8);
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_left, 8);
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_right, 8);
                    remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 8);
                    remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 8);
                } else {
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_top, 8);
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_bottom, 8);
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_left, 0);
                    remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_right, 0);
                    remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 8);
                    remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 0);
                    remoteViews.setImageViewResource(R.id.iv_temp_top_left_corner, R.drawable.ic_subgoal_white);
                }
            } else {
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_top, 8);
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_bottom, 0);
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_left, 0);
                remoteViews.setViewVisibility(R.id.rl_temp_rect_widget_line_right, 0);
                remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 8);
                remoteViews.setViewVisibility(R.id.iv_temp_top_left_corner, 0);
                remoteViews.setImageViewResource(R.id.iv_temp_top_left_corner, R.drawable.ic_subgoal_white);
            }
            if (TimerService.timer != null && act.getId() == Act.getInstance().getId()) {
                remoteViews.setImageViewResource(R.id.iv_temp_show_label_outer_bg, ((Integer) Val.col_Str2Int_Map.get(act.getColor())).intValue());
                remoteViews.setImageViewResource(R.id.iv_temp_show_label_pre, ((Integer) Val.col_Str2xml_circle_Int_Map.get(act.getColor())).intValue());
                WidgetGoalItemsServices.this.log("更新小插件--设置目标插件背景！");
                remoteViews.setImageViewResource(R.id.iv_temp_show_start, R.drawable.ic_stop_white);
                remoteViews.setTextViewText(R.id.tv_temp_show_actName, DateTime.calculateTime5(this.context, (long) TimerService.actCount));
                remoteViews.setTextViewText(R.id.tv_temp_show_remark, act.getActName());
            }
            Intent it = new Intent();
            it.putExtra("action", Val.INTENT_ACTION_WIDGET_START_STOP_TIMER);
            it.putExtra("id", act.getId() + "");
            remoteViews.setOnClickFillInIntent(R.id.iv_temp_show_start, it);
            return remoteViews;
        }

        public RemoteViews getLoadingView() {
            return null;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public boolean hasStableIds() {
            return false;
        }
    }

    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        log("GridWidgetService");
        return new myRemoteViewsFactory(this, intent);
    }

    public static String getStr(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    public void log(String str) {
        Log.i("override WidgetGoalItemsServices", ":" + str);
    }
}
