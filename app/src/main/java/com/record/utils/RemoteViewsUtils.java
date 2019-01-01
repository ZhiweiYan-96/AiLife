package com.record.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.record.myLife.R;
import com.record.myLife.base.BottomActivity;
import com.record.service.WidgetGoalItemsServices;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.List;
import org.achartengine.GraphicalView;
import org.achartengine.chart.DoughnutChart;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class RemoteViewsUtils {
    public static RemoteViews getRemoteDoughPie(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.tem_widget_doughpie);
        Intent intent = new Intent(context, BottomActivity.class);
        intent.putExtra("item", 1);
        remoteViews.setOnClickPendingIntent(R.id.rl_wagets_today_pie, PendingIntent.getActivity(context, 1, intent, 0));
        int bg = context.getResources().getColor(R.color.translate);
        int bg_green1 = context.getResources().getColor(R.color.bg_green1);
        int bg_blue1 = context.getResources().getColor(R.color.bg_blue1);
        int bg_red1 = context.getResources().getColor(R.color.bg_red1);
        int bg_yellow1 = context.getResources().getColor(R.color.bg_yellow1);
        String today = DateTime.getDateString();
        Cursor cursor = DbUtils.getDb2(context).rawQuery("Select * from t_allocation where userId is " + DbUtils.queryUserId(context) + " and time is '" + today + "' order by time", null);
        if (cursor.getCount() > 0) {
            String invest_Str;
            String waste_Str;
            String routine_Str;
            String sleep_Str;
            String unknow_Str;
            cursor.moveToNext();
            double invest = cursor.getDouble(cursor.getColumnIndex("invest"));
            double waste = cursor.getDouble(cursor.getColumnIndex("waste"));
            double routine = cursor.getDouble(cursor.getColumnIndex("routine"));
            double sleep = cursor.getDouble(cursor.getColumnIndex("sleep"));
            String Date = cursor.getString(cursor.getColumnIndex("time"));
            if (waste < 0.0d) {
                waste = 0.0d;
            }
            double unknow = (((86400.0d - invest) - waste) - routine) - sleep;
            if (unknow < 0.0d) {
                unknow = 0.0d;
            }
            if (context.getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_EVERY_DAY_DOUTHNUT_UINT, 0) == 0) {
                invest_Str = FormatUtils.format_1fra((invest / 86400.0d) * 100.0d) + "% ";
                waste_Str = FormatUtils.format_1fra((waste / 86400.0d) * 100.0d) + "% ";
                routine_Str = FormatUtils.format_1fra((routine / 86400.0d) * 100.0d) + "% ";
                sleep_Str = FormatUtils.format_1fra((sleep / 86400.0d) * 100.0d) + "% ";
                unknow_Str = FormatUtils.format_1fra((unknow / 86400.0d) * 100.0d) + "% ";
            } else {
                invest_Str = DateTime.calculateTime5(context, (long) invest);
                waste_Str = DateTime.calculateTime5(context, (long) waste);
                routine_Str = DateTime.calculateTime5(context, (long) routine);
                sleep_Str = DateTime.calculateTime5(context, (long) sleep);
                unknow_Str = DateTime.calculateTime5(context, (long) unknow);
            }
            List<double[]> values = new ArrayList();
            values.add(new double[]{invest, waste, routine, sleep, unknow});
            List<String[]> titles = new ArrayList();
            String str_invest_short = context.getResources().getString(R.string.str_invest_short);
            String str_waste_short = context.getResources().getString(R.string.str_waste_short);
            String str_Routine_short = context.getResources().getString(R.string.str_Routine_short);
            String str_Sleep_short = context.getResources().getString(R.string.str_Sleep_short);
            Cursor cursor2 = DbUtils.getDb(context).rawQuery("Select actName,type from (Select * from t_act where " + DbUtils.getWhereUserId(context) + " ) where type is 10 or type is 20 or type is 30 or type is 40", null);
            if (cursor2.getCount() > 0) {
                while (cursor2.moveToNext()) {
                    String actName = cursor2.getString(cursor2.getColumnIndex("actName"));
                    int type = cursor2.getInt(cursor2.getColumnIndex(a.a));
                    if (type == 10) {
                        str_invest_short = actName;
                    } else if (type == 20) {
                        str_Routine_short = actName;
                    } else if (type == 30) {
                        str_Sleep_short = actName;
                    } else if (type == 40) {
                        str_waste_short = actName;
                    }
                }
            }
            DbUtils.close(cursor2);
            if (Date.equals(today)) {
                String str_remain_short = context.getResources().getString(R.string.str_remain_short);
                titles.add(new String[]{invest_Str + str_invest_short, waste_Str + str_waste_short, routine_Str + str_Routine_short, sleep_Str + str_Sleep_short, unknow_Str + str_remain_short});
            } else {
                String str_unkown_short = context.getResources().getString(R.string.str_unknow_short);
                titles.add(new String[]{invest_Str + str_invest_short, waste_Str + str_waste_short, routine_Str + str_Routine_short, sleep_Str + str_Sleep_short, unknow_Str + str_unkown_short});
            }
            DefaultRenderer renderer = buildCategoryRenderer(new int[]{bg_green1, bg_red1, bg_yellow1, bg_blue1, -1});
            renderer.setApplyBackgroundColor(true);
            renderer.setBackgroundColor(bg);
            renderer.setLabelsColor(-1);
            renderer.setShowLegend(false);
            renderer.setPanEnabled(false);
            renderer.setZoomEnabled(false);
            renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.textSize_Micro_Small_9));
            renderer.setChartTitleTextSize(context.getResources().getDimension(R.dimen.textSize_Micro_Small_9));
            renderer.setLegendTextSize(context.getResources().getDimension(R.dimen.textSize_Micro_Small_9));
            remoteViews.setImageViewBitmap(R.id.iv_wagets_dough_pie, bitmapFromChartView(new GraphicalView(context, new DoughnutChart(buildMultipleCategoryDataset("时间分配", titles, values), renderer)), DensityUtil.dip2px(context, 274.0f), DensityUtil.dip2px(context, 274.0f)));
            Object[] arr = getRank(context, (int) invest);
            if (arr[0] != null) {
                remoteViews.setTextViewText(R.id.tv_wagets_dough_rank, (String) arr[0]);
                remoteViews.setTextColor(R.id.tv_wagets_dough_rank, ((Integer) arr[1]).intValue());
                remoteViews.setViewVisibility(R.id.tv_wagets_dough_rank, ((Integer) arr[2]).intValue());
            }
        }
        DbUtils.close(cursor);
        return remoteViews;
    }

    private static Object[] getRank(Context context, int invest) {
        Object[] arr = new Object[3];
        String rank = "E";
        int color = 0;
        int visible = 0;
        if (invest < 1) {
            rank = "E";
            color = context.getResources().getColor(R.color.black_tran_es);
            visible = 8;
        } else if (invest >= 1 && invest < 3600) {
            rank = "E";
            color = context.getResources().getColor(R.color.black_tran_es);
            visible = 0;
        } else if (invest >= 3600 && invest < 7200) {
            rank = "D";
            color = context.getResources().getColor(R.color.bg_blue1);
            visible = 0;
        } else if (invest >= 7200 && invest < 18000) {
            rank = "C";
            color = context.getResources().getColor(R.color.bg_blue2);
            visible = 0;
        } else if (invest >= 18000 && invest < 28800) {
            rank = "B";
            color = context.getResources().getColor(R.color.bg_green1);
            visible = 0;
        } else if (invest >= 28800 && invest <= 32400) {
            rank = "A";
            color = context.getResources().getColor(R.color.bg_green2);
            visible = 0;
        } else if (invest >= 32400 && invest <= 36000) {
            rank = "AA";
            color = context.getResources().getColor(R.color.bg_green3);
            visible = 0;
        } else if (invest >= 36000) {
            rank = "AAA";
            color = context.getResources().getColor(R.color.bg_green3);
            visible = 0;
        }
        arr[0] = rank;
        arr[1] = Integer.valueOf(color);
        arr[2] = Integer.valueOf(visible);
        return arr;
    }

    private static Bitmap bitmapFromChartView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, 0, 0);
        v.draw(c);
        return b;
    }

    protected static DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(15.0f);
        renderer.setLegendTextSize(15.0f);
        renderer.setMargins(new int[]{20, 30, 15, 0});
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected static MultipleCategorySeries buildMultipleCategoryDataset(String title, List<String[]> titles, List<double[]> values) {
        MultipleCategorySeries series = new MultipleCategorySeries(title);
        int k = 0;
        for (double[] value : values) {
            series.add((k + 2007) + "", (String[]) titles.get(k), value);
            k++;
        }
        return series;
    }

    public static RemoteViews getRemoteWidgetBar(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tem_widget_today_bar);
        Intent it = new Intent(context, BottomActivity.class);
        it.putExtra("item", 0);
        views.setOnClickPendingIntent(R.id.rl_wagets_today_bar, PendingIntent.getActivity(context, 1, it, 0));
        String[] arrStr = DbUtils.queryTodayAllocation(context);
        views.setTextViewText(R.id.tv_wagets_today_invest, arrStr[0] + " " + context.getResources().getString(R.string.str_invest));
        views.setTextViewText(R.id.tv_wagets_today_waste, arrStr[1] + " " + context.getResources().getString(R.string.str_waste));
        if (arrStr[2] != null) {
            views.setProgressBar(R.id.pb_wagets_today_invest, Integer.parseInt(arrStr[4]), Integer.parseInt(arrStr[2]), false);
            views.setProgressBar(R.id.pb_wagets_today_waste, Integer.parseInt(arrStr[4]), Integer.parseInt(arrStr[3]), false);
        }
        return views;
    }

    @SuppressLint({"NewApi"})
    public static RemoteViews getRemoteWidgetGoalItems(Context context) {
        if (VERSION.SDK_INT < 11) {
            return null;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tem_widget_today_main_goal_items);
        log("Goal部件onUpdate，setRemoteAdapter");
        views.setRemoteAdapter(R.id.lv_wagets_today_goal_items, new Intent(context, WidgetGoalItemsServices.class));
        Intent gridIntent = new Intent();
        gridIntent.setAction(Val.INTENT_ACTION_WIDGET_TEST1);
        views.setPendingIntentTemplate(R.id.lv_wagets_today_goal_items, PendingIntent.getBroadcast(context, 0, gridIntent, 134217728));
        return views;
    }

    private static void log(String str) {
        Log.i("override RemoteViewUtils", ":" + str);
    }
}
