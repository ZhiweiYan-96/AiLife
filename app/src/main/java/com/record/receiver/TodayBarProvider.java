package com.record.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.record.myLife.R;
import com.record.myLife.main.TodayActivity;
import com.record.utils.RemindUtils;
import com.record.utils.db.DbUtils;

import static android.os.Build.VERSION_CODES.N;

public class TodayBarProvider extends AppWidgetProvider {
    String TAG = "override TodayBarProvider";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        log("部件onUpdate：" + N);
        for (int updateAppWidget : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tem_today_bar);
            views.setOnClickPendingIntent(R.id.rl_wagets_today_bar, PendingIntent.getActivity(context, 1, new Intent(context, TodayActivity.class), 0));
            String[] arrStr = DbUtils.queryTodayAllocation(context);
            log(arrStr[0] + arrStr[1]);
            views.setTextViewText(R.id.tv_wagets_today_invest, arrStr[0] + " " + context.getResources().getString(R.string.str_invest));
            views.setTextViewText(R.id.tv_wagets_today_waste, arrStr[1] + " " + context.getResources().getString(R.string.str_waste));
            if (arrStr[2] != null) {
                log(arrStr[0] + arrStr[1] + arrStr[2]);
                views.setProgressBar(R.id.pb_wagets_today_invest, Integer.parseInt(arrStr[4]), Integer.parseInt(arrStr[2]), false);
                views.setProgressBar(R.id.pb_wagets_today_waste, Integer.parseInt(arrStr[4]), Integer.parseInt(arrStr[3]), false);
            }
            appWidgetManager.updateAppWidget(updateAppWidget, views);
        }
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        log("部件onReceive");
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        log("部件onDeleted");
        RemindUtils.cancelUpdateWidgetUI(context);
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
        log("部件onEnabled");
        RemindUtils.setUpdateWidgetUI(context);
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
        log("部件onDisabled");
    }

    public void log(String str) {
        Log.i(this.TAG, ":" + str);
    }
}
