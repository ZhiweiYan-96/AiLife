package com.record.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.record.service.TimerService;
import com.record.utils.RemoteViewsUtils;
import com.record.utils.db.DbUtils;

import static android.os.Build.VERSION_CODES.N;

public class WidgetDoughPieProvider extends AppWidgetProvider {
    String TAG = "override DoughPie";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        log("部件onUpdate，length：" + N);
        for (int updateAppWidget : appWidgetIds) {
            appWidgetManager.updateAppWidget(updateAppWidget, RemoteViewsUtils.getRemoteDoughPie(context));
        }
        try {
            context.startService(new Intent(context, TimerService.class));
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
        }
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        log("部件onDeleted");
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void log(String str) {
        Log.i(this.TAG, ":" + str);
    }
}
