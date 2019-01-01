package com.record.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.record.myLife.R;
import com.record.service.TimerService;
import com.record.utils.GeneralHelper;
import com.record.utils.RemoteViewsUtils;
import com.record.utils.db.DbUtils;

public class WidgetTodayMainGoalItemsProvider extends AppWidgetProvider {
    String TAG = "override WidgetTodayMainGoalItemsProvider";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        log("Goal部件onUpdate，length：" + appWidgetIds.length);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews RemoteViews = RemoteViewsUtils.getRemoteWidgetGoalItems(context);
            if (RemoteViews != null) {
                appWidgetManager.updateAppWidget(appWidgetId, RemoteViews);
            } else {
                GeneralHelper.toastLong(context, context.getResources().getString(R.string.str_your_sdk_version_low));
            }
        }
        try {
            context.startService(new Intent(context, TimerService.class));
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        log("Goal部件onReceive");
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        log("Goal部件onDeleted");
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
        log("Goal件onEnabled");
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
        log("Goal部件onDisabled");
    }

    public void log(String str) {
        Log.i(this.TAG, ":" + str);
    }
}
