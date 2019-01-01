package com.record.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.record.bean.Act;
import com.record.bean.NoSubmitTomato;
import com.record.bean.User;
import com.record.logic.AddSleepRecordByNotiHandler;
import com.record.myLife.R;
import com.record.myLife.main.TodayActivity;
import com.record.myLife.main.tomato.RemindTomatoActivity;
import com.record.myLife.main.tomato.TomatoActivity;
import com.record.myLife.settings.label.LabelSelectActivity;
import com.record.myLife.view.MyTextDialog;
import com.record.receiver.WidgetDoughPieProvider;
import com.record.receiver.WidgetTodayBarProvider;
import com.record.receiver.WidgetTodayMainGoalItemsProvider;
import com.record.utils.AndroidUtils;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.LogUtils;
import com.record.utils.MyNotification;
import com.record.utils.PowerMangerUtils;
import com.record.utils.PreferUtils;
import com.record.utils.RemindUtils;
import com.record.utils.RemoteViewsUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.sound.Sound;
import com.record.utils.tomato.TomatoController;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    public static double actCount = 0.0d;
    public static int actItemsId = 0;
    public static Context context;
    public static boolean isInsertDb = true;
    public static double lastActCount = 0.0d;
    static MyNotification myNoti;
    public static Timer timer;
    public static TimerService timerService;
    int HANDLER_UPDATE_PIE_UI = 1;
    BroadcastReceiver OnclickReceiver2;
    Thread calAllocatThread;
    long lastSeenOFFTime = 0;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (TimerService.this.HANDLER_UPDATE_PIE_UI == msg.what) {
                TimerService.this.updateWidgetPie();
            }
            TimerService.initNoti(TimerService.context);
        }
    };
    MyTextDialog myTextDialog;
    SharedPreferences sp;
    BroadcastReceiver systemActionReceiver = null;
    TomatoController tomatoController;

    class SystemActionReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        SystemActionReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_ON".equals(action)) {
                try {
                    long currentTimeMillis = System.currentTimeMillis();
                    if ((currentTimeMillis - TimerService.this.lastSeenOFFTime) / 1000 > 30) {
                        TimerService.this.lastSeenOFFTime = currentTimeMillis;
                        if (TimerService.this.calAllocatThread == null) {
                            TimerService.this.calAllocatThread = new Thread(new calAllocatRunnable(context));
                            TimerService.this.calAllocatThread.start();
                        }
                    }
                    RemindUtils.setUpdateWidgetUI(context);
                    DbUtils.saveScreenOn(context);
                    TimerService.this.notiAddSleepRecord();
                } catch (Exception e) {
                    DbUtils.exceptionHandler(context, e);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                TimerService.this.lastSeenOFFTime = System.currentTimeMillis();
                RemindUtils.cancelUpdateWidgetUI(context);
                DbUtils.saveScreenOff(context);
                if (TimerService.this.getTomatoController().isCounting()) {
                    TimerService.log("番茄计时中，保持设备不休眠");
                    PowerMangerUtils.getPrivateWakeLock(context, TomatoController.getTomatoController(context).getLeftSec());
                }
            }
        }
    }

    class calAllocatRunnable implements Runnable {
        Context context;
        int tempGoalId = 0;
        int tempGoalType = 0;
        int tempTake = 0;

        public calAllocatRunnable(Context context) {
            this.context = context;
            this.tempGoalId = 0;
            this.tempGoalType = 0;
            this.tempTake = 0;
        }

        public calAllocatRunnable(Context context, int tempGoalId, int tempGoalType, int tempTake) {
            this.context = context;
            this.tempGoalId = tempGoalId;
            this.tempGoalType = tempGoalType;
            this.tempTake = tempTake;
        }

        public void run() {
            try {
                if (11 == this.tempGoalType && this.tempTake >= 600) {
                    DbUtils.staticsGoalAllAutoUpdateBigGoalByGoalId(this.context, this.tempGoalId);
                }
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
            try {
                TimerService.updateCounter2Db(this.context);
                TodayActivity.cal_TodayAllocat(this.context);
                TimerService.this.updateWidgetBar(this.context);
                TimerService.updateWidgetGoal(this.context);
                TimerService.this.myHandler.sendEmptyMessage(TimerService.this.HANDLER_UPDATE_PIE_UI);
            } catch (Exception e2) {
                DbUtils.exceptionHandler(e2);
            }
            TimerService.this.calAllocatThread = null;
        }
    }

    class myBroadCastReceiver extends BroadcastReceiver {
        myBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            TimerService.this.logmy("Action：" + action);
            String id;
            String startTime;
            Intent it;
            Intent it2;
            if (Val.INTENT_ACTION_START_COUNTER.equals(action)) {
                try {
                    id = intent.getStringExtra("id");
                    startTime = intent.getStringExtra("startTime");
                    TimerService.log("START收到广播！id:" + id + ",isInsertDb:" + TimerService.isInsertDb);
                    TimerService.this.start_v3(id, intent.getBooleanExtra("isToast", false), intent.getIntExtra("isFromRemindRestActivity", 0), startTime);
                    TimerService.initNoti(context);
                    TimerService.updateWidgetGoal(context);
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
            } else if (Val.INTENT_ACTION_STOP_COUNTER.equals(action)) {
                id = intent.getStringExtra("id");
                it = new Intent(Val.INTENT_ACTION_UPDATE_UI_MAIN_STOP);
                it.putExtra("id", id + "");
                TimerService.this.sendBroadcast(it);
                TimerService.log("STOP收到广播！" + id);
                TimerService.this.stop(intent.getBooleanExtra("isNormal", true));
                String tomatoGoalId = intent.getStringExtra("tomatoGoalId");
                startTime = intent.getStringExtra("startTime");
                if (tomatoGoalId != null && tomatoGoalId.length() > 0 && startTime != null) {
                    it2 = new Intent(Val.INTENT_ACTION_START_COUNTER);
                    it2.putExtra("startTime", startTime);
                    it2.putExtra("id", tomatoGoalId);
                    TimerService.this.sendBroadcast(it2);
                }
            } else if (Val.INTENT_ACTION_WEIBO_SEND_STATUS.equals(action)) {
                try {
                    GeneralHelper.toastShort(context, intent.getStringExtra("msg"));
                } catch (Exception e2) {
                    DbUtils.exceptionHandler(e2);
                }
            } else if (Val.INTENT_ACTION_MODIFY_ACTCOUNT.equals(action)) {
                Cursor cursor = DbUtils.getDb(context).rawQuery("select * from t_act_item where id is " + TimerService.actItemsId, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    int tempTake = DateTime.cal_secBetween(cursor.getString(cursor.getColumnIndex("startTime")), DateTime.getTimeString());
                    if (tempTake > 0 && tempTake < 2592000) {
                        TimerService.actCount = (double) tempTake;
                    }
                }
                DbUtils.close(cursor);
            } else if (Val.INTENT_ACTION_WIDGET_UPDATE_BAR_UI.equals(action)) {
                try {
                    if (TimerService.this.calAllocatThread == null) {
                        TimerService.this.calAllocatThread = new Thread(new calAllocatRunnable(context));
                        TimerService.this.calAllocatThread.start();
                    }
                    if (TodayActivity.onResume) {
                        TimerService.this.sendBroadcast(new Intent(Val.INTENT_ACTION_UPDATE_UI_TODAY));
                    }
                } catch (Exception e22) {
                    DbUtils.exceptionHandler(e22);
                }
            } else if (Val.INTENT_ACTION_WIDGET_TEST1.equals(action)) {
                String action2 = intent.getStringExtra("action");
                TimerService.log("action2:" + action2);
                if (Val.INTENT_ACTION_WIDGET_START_STOP_TIMER.equals(action2)) {
                    int widgetid = intent.getIntExtra("appWidgetId", 0);
                    id = intent.getStringExtra("id");
                    TimerService.log("从插件启动计时更新部件,widgetId:" + widgetid + ",Id:" + id);
                    if (TimerService.timer == null) {
                        it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                        it.putExtra("id", id);
                        it.putExtra("isToast", true);
                        TimerService.this.sendBroadcast(it);
                        return;
                    }
                    it2 = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                    it2.putExtra("id", Act.getInstance().getId() + "");
                    TimerService.log("STOP发送广播：当前id：" + Act.getInstance().getId());
                    TimerService.this.sendBroadcast(it2);
                    if (!id.equals(Act.getInstance().getId() + "")) {
                        it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                        TimerService.log("Start发送广播：当前点击id：" + id);
                        it.putExtra("id", id);
                        it.putExtra("isToast", true);
                        TimerService.this.sendBroadcast(it);
                    }
                }
            } else if (Val.INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_ADD.equals(action)) {
                new AddSleepRecordByNotiHandler().addRecordByIntent(context, intent);
            } else if ("android.intent.action.PACKAGE_ADDED".equalsIgnoreCase(action)) {
                TimerService.log("正在安装！");
            } else if (Val.INTENT_ACTION_SHOW_TOAST.equalsIgnoreCase(action)) {
                ToastUtils.toastShort(context, intent.getStringExtra("Toast"));
            } else if (Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT.equals(action) || Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT.equals(action)) {
                TimerService.logfile("收到广播 番茄结束,action:" + action);
                NoSubmitTomato noSubmitTomato = TomatoActivity.getNoSubmitTomatoBean(context);
                TimerService.logfile("收到广播 番茄结束 nosubmittomato:" + noSubmitTomato.toString());
                RemindTomatoActivity.startActivityNewTask(context, noSubmitTomato.getTypeAction(), noSubmitTomato.getStartTime(), (double) noSubmitTomato.getTotalMin());
                TimerService.log(action + "" + noSubmitTomato.getTypeAction());
            } else if (Val.ACTION_TOMOTO_START.equals(action)) {
                int total = intent.getIntExtra(TomatoController.TOMATO_TOTAL, -1);
                int pass = intent.getIntExtra(TomatoController.TOMATO_PASS_SEC, -1);
                TimerService.logfile("收到广播ACTION_TOMOTO_START total" + total + ",pass" + pass);
                TimerService.this.getTomatoController().startTomato(total, pass);
            } else if (Val.ACTION_TOMOTO_STOP.equals(action)) {
                TimerService.this.getTomatoController().endTomato();
            }
        }
    }

    class timeTask extends TimerTask {
        String startTime = "";

        public timeTask(String startTime) {
            this.startTime = startTime;
        }

        public void run() {
            TimerService.this.counter(this.startTime);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        log("TimerService onCreate");
        context = this;
        if (this.OnclickReceiver2 == null) {
            this.OnclickReceiver2 = new myBroadCastReceiver();
            registerReceiver(this.OnclickReceiver2, getIntentFilter());
            RemindUtils.setUpdateWidgetUI(context);
        }
        if (this.systemActionReceiver == null) {
            this.systemActionReceiver = new SystemActionReceiver();
            registerReceiver(this.systemActionReceiver, getSystemIntentFilter());
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter(Val.INTENT_ACTION_START_COUNTER);
        filter.addAction(Val.INTENT_ACTION_STOP_COUNTER);
        filter.addAction(Val.INTENT_ACTION_WEIBO_SEND_STATUS);
        filter.addAction(Val.INTENT_ACTION_MODIFY_ACTCOUNT);
        filter.addAction(Val.INTENT_ACTION_WIDGET_UPDATE_BAR_UI);
        filter.addAction(Val.INTENT_ACTION_WIDGET_TEST1);
        filter.addAction(Val.INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_ADD);
        filter.addAction(Val.INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_EDIT);
        filter.addAction(Val.INTENT_ACTION_SHOW_TOAST);
        filter.addAction(Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT);
        filter.addAction(Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT);
        filter.addAction(Val.ACTION_TOMOTO_START);
        filter.addAction(Val.ACTION_TOMOTO_STOP);
        return filter;
    }

    private void notiAddSleepRecord() {
        if (VERSION.SDK_INT >= 11 && timer == null && PreferUtils.getInt(context, Val.CONFIGURE_IS_NOTI_ADD_SLEEP_DATE, 1) != 0) {
            String today = DateTime.getDateString();
            String lastNotiDate = PreferUtils.getString(context, Val.CONFIGURE_LAST_NOTI_ADD_SLEEP_DATE, "");
            if (lastNotiDate == null || lastNotiDate.length() == 0 || !today.equals(lastNotiDate)) {
                HashMap temp = DbUtils.querySleepLastRecordByType(context, "30");
                if (temp != null) {
                    String stopTime = (String) temp.get("stopTime");
                    if (stopTime != null && stopTime.contains(today)) {
                        PreferUtils.putString(context, Val.CONFIGURE_LAST_NOTI_ADD_SLEEP_DATE, today);
                        return;
                    }
                }
                AddSleepRecordByNotiHandler notiHandler = new AddSleepRecordByNotiHandler();
                if (notiHandler.isShowNoti(context)) {
                    String[] arr = notiHandler.getLastSleepTimeArr(context);
                    if (arr != null) {
                        try {
                            if (arr.length > 1 && DateTime.compare_date(arr[1], arr[0]) == 1) {
                                notiHandler.notiAddSleep(context, arr[0], arr[1], DbUtils.queryActId(context, "30"));
                                PreferUtils.putString(context, Val.CONFIGURE_LAST_NOTI_ADD_SLEEP_DATE, today);
                            }
                        } catch (Exception e) {
                            DbUtils.exceptionHandler(e);
                            notiHandler.notiAddSleep(context, "", "", "");
                        }
                    }
                    notiHandler.notiAddSleep(context, "", "", "");
                    PreferUtils.putString(context, Val.CONFIGURE_LAST_NOTI_ADD_SLEEP_DATE, today);
                }
            }
        }
    }

    private IntentFilter getSystemIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        return filter;
    }

    public static boolean isShowNoti(Context context) {
        return context.getSharedPreferences(Val.CONFIGURE_NAME, 0).getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true);
    }

    public TomatoController getTomatoController() {
        if (this.tomatoController == null) {
            this.tomatoController = TomatoController.getTomatoController(context);
        }
        return this.tomatoController;
    }

    private void updateWidgetBar(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] AppWidgetIds = manager.getAppWidgetIds(new ComponentName(context, WidgetTodayBarProvider.class));
        log("更新小插件--进度条插件！AppWidgetIds:" + AppWidgetIds.length);
        if (AppWidgetIds.length > 0) {
            manager.updateAppWidget(AppWidgetIds, RemoteViewsUtils.getRemoteWidgetBar(context));
        }
    }

    private void updateWidgetPie() {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] AppWidgetIds = manager.getAppWidgetIds(new ComponentName(context, WidgetDoughPieProvider.class));
        log("更新小插件--饼图插件！AppWidgetIds:" + AppWidgetIds.length);
        if (AppWidgetIds.length > 0) {
            manager.updateAppWidget(AppWidgetIds, RemoteViewsUtils.getRemoteDoughPie(context));
        }
    }

    @SuppressLint({"NewApi"})
    public static void updateWidgetGoal(Context context) {
        try {
            if (VERSION.SDK_INT >= 11) {
                ComponentName appWidget = new ComponentName(context, WidgetTodayMainGoalItemsProvider.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
                log("更新小插件--目标插件！appWidgetIds:" + appWidgetIds.length);
                if (appWidgetIds.length > 0) {
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_wagets_today_goal_items);
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void isContinue() {
        String tableName = "t_act_item";
        Cursor cursor = DbUtils.getDb2(getApplicationContext()).rawQuery("Select * from " + tableName + " where " + DbUtils.getWhereUserId(context) + " and isEnd is not 1 order by startTime desc limit 1", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String actId = cursor.getString(cursor.getColumnIndex("actId"));
            String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
            String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
            double take = cursor.getDouble(cursor.getColumnIndex("take"));
            int isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
            int actType = cursor.getInt(cursor.getColumnIndex("actType"));
            try {
                double start2 = (double) DateTime.pars2Calender(startTime).getTime().getTime();
                double now = (double) Calendar.getInstance().getTime().getTime();
                double tmpTake = (now - start2) / 1000.0d;
                if (start2 > now) {
                    log("开始时间大于当前，可能是换电池，时间重置了。");
                    setIsEnd(tableName, id, actId, startTime, stopTime, isUpload, 43200);
                    return;
                } else if (actType != 30 && tmpTake > ((double) 43200)) {
                    log("非睡眠类型，超过12个小时，视为忘记关闭");
                    setIsEnd(tableName, id, actId, startTime, stopTime, isUpload, 43200);
                    return;
                } else if (actType != 30 || tmpTake <= ((double) 57600)) {
                    if (tmpTake > 0.0d) {
                        take = tmpTake;
                    }
                    actCount = take;
                    actItemsId = Integer.parseInt(id);
                    isInsertDb = false;
                    Intent intent = new Intent(Val.INTENT_ACTION_START_COUNTER);
                    intent.putExtra("id", actId);
                    intent.putExtra("isToast", false);
                    sendBroadcast(intent);
                } else {
                    setIsEnd(tableName, id, actId, startTime, stopTime, isUpload, 57600);
                    return;
                }
            } catch (Exception e) {
                DbUtils.exceptionHandler(context, e);
            }
        }
        DbUtils.close(cursor);
    }

    private void setIsEnd(String tableName, String id, String actId, String startTime, String stopTime, int isUpload, int itemMaxSecond) {
        ContentValues values = new ContentValues();
        double tempTake = (double) DateTime.cal_secBetween(startTime, stopTime);
        if (tempTake > ((double) itemMaxSecond)) {
            tempTake = (double) itemMaxSecond;
            Calendar stopCal = DateTime.pars2Calender(startTime);
            stopCal.add(13, (int) tempTake);
            values.put("stopTime", DateTime.formatTime(stopCal));
        }
        values.put("take", Double.valueOf(tempTake));
        values.put("isEnd", Integer.valueOf(1));
        if (isUpload > 0) {
            values.put("endUpdateTime", DateTime.getTimeString());
        }
        DbUtils.getDb(context).update(tableName, values, " id = " + id, null);
        try {
            if (timer != null && actItemsId == Integer.parseInt(id)) {
                Intent it2 = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                it2.putExtra("id", actId);
                it2.putExtra("isNormal", false);
                sendBroadcast(it2);
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        log("检测到计时忘记关闭，已经修正！values" + values);
    }

    public static void isContinue2(Context context) {
        Cursor cursor = DbUtils.getDb2(context).rawQuery("Select * from t_act_item where " + DbUtils.getWhereUserId(context) + " and isEnd is not 1 order by startTime desc limit 1", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
            double take = cursor.getDouble(cursor.getColumnIndex("take"));
            try {
                double tmp = (((double) Calendar.getInstance().getTime().getTime()) - ((double) DateTime.pars2Calender(startTime).getTime().getTime())) / 1000.0d;
                if (tmp > 0.0d) {
                    take = tmp;
                }
                try {
                    actCount = (double) ((long) take);
                } catch (Exception e) {
                    DbUtils.exceptionHandler(context, e);
                    actCount = take;
                }
                actItemsId = Integer.parseInt(id);
                isInsertDb = false;
            } catch (Exception e2) {
                DbUtils.exceptionHandler(context, e2);
            }
        }
        DbUtils.close(cursor);
    }

    @SuppressLint({"WorldWriteableFiles"})
    private void start_v3(String id, boolean isToast, int isFromRemindRestActivity, String startTime) {
        DbUtils.getActAndSet_v2(context, id);
        log("设置当前目标id：" + id + ",是否在后台？" + AndroidUtils.isBackground2(context) + ",timer:" + timer);
        String str_start_counter = getString(R.string.str_start_counter).replace("{目标}", Act.getInstance().getActName());
        if (isToast) {
            ToastUtils.toastShort(context, str_start_counter);
        }
        sendBroadcast(new Intent(Val.INTENT_ACTION_UPDATE_UI_MAIN_START));
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }
        if (AndroidUtils.isBackground2(context)) {
            counter(startTime);
        } else {
            timer.schedule(new timeTask(startTime), 0, 1000);
        }
        if (isFromRemindRestActivity != 1) {
            try {
                int isRemindRest = getSp().getInt(Val.CONFIGURE_IS_REMIND_REST, 0);
                log("是否开启提醒，isRemindRest:" + isRemindRest + ",当前ActType：" + Act.getInstance().getType());
                if (isRemindRest > 0) {
                    if (this.sp.getInt(Val.CONFIGURE_IS_REMIND_REST_WHOLE, 1) > 0) {
                        if (Act.getInstance().getType() != 30) {
                            setRemindRest(this.sp);
                        }
                    } else if (Act.getInstance().getType() == 10 || Act.getInstance().getType() == 11) {
                        setRemindRest(this.sp);
                    }
                }
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                return;
            }
        }
        int restTime = getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_REMIND_REST_REST_TIME, 5);
        GeneralUtils.toastShort(context, "休息时间为" + restTime + "分钟哦！");
        RemindUtils.setRemindInvest(context, restTime);
        log("当前处于休息，休息时间为:" + restTime);
        if (isToast) {
            new Sound(context).tickOnStart();
        }
    }

    private void setRemindRest(SharedPreferences sp) {
        if (sp.getInt(Val.CONFIGURE_IS_HAD_SET_REMIND_REST, 0) <= 0) {
            int triTime = sp.getInt(Val.CONFIGURE_REMIND_REST_LEARN_TIME, 45);
            RemindUtils.setRemindRest(context, triTime);
            GeneralUtils.toastShort(context, "休息提醒开启，将在" + triTime + "分钟后提醒!");
            sp.edit().putInt(Val.CONFIGURE_IS_HAD_SET_REMIND_REST, 1).commit();
            RemindUtils.cancleRemindInvest(context);
        }
    }

    private void startLabelInfoActivity(int actType) {
        Intent it = new Intent(context, LabelSelectActivity.class);
        it.setFlags(268435456);
        it.putExtra("itemsId", actItemsId);
        it.putExtra("actType", actType);
        startActivity(it);
    }

    private void stop(boolean isNormalStop) {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        if (isNormalStop) {
            try {
                endAct();
                showLabel();
            } catch (Exception e) {
                DbUtils.exceptionHandler(context, e);
            }
        }
        try {
            if (this.calAllocatThread == null) {
                this.calAllocatThread = new Thread(new calAllocatRunnable(context, Act.getInstance().getId(), Act.getInstance().getType(), (int) actCount));
                this.calAllocatThread.start();
            }
            isInsertDb = true;
            actItemsId = 0;
            actCount = 0.0d;
        } catch (Exception e1) {
            DbUtils.exceptionHandler(context, e1);
            isInsertDb = true;
            actItemsId = 0;
            actCount = 0.0d;
        } catch (Throwable th) {
            isInsertDb = true;
            actItemsId = 0;
            actCount = 0.0d;
            throw th;
        }
        try {
            getSp().edit().putInt(Val.CONFIGURE_IS_HAD_SET_REMIND_REST, 0).commit();
            RemindUtils.cancelRemindRest(context);
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
        }
    }

    private void showLabel() {
        try {
            if (actCount > 60.0d) {
                int type = DbUtils.queryActTypeByItemsId(context, actItemsId);
                if (type == 11) {
                    type = 10;
                }
                int isOpen = 0;
                if (type == 10 || type == 11) {
                    isOpen = getSp().getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_INVEST_LABEL, 0);
                } else if (type == 20) {
                    isOpen = getSp().getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_ROUTINE_LABEL, 0);
                } else if (type == 30) {
                    isOpen = getSp().getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_SLEEP_LABEL, 0);
                } else if (type == 40) {
                    isOpen = getSp().getInt(Val.CONFIGURE_IS_REMIND_CHOOSE_WASTE_LABEL, 0);
                }
                if (isOpen > 0) {
                    startLabelInfoActivity(type);
                }
                log("是否提醒加标签,isOpen:" + isOpen + ",type:" + type);
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
        }
    }

    private void endAct() {
        isContinue2(context);
        log("结束计时，插入数据库------------------------------>actItemsId:" + actItemsId + "actCount:" + actCount);
        lastActCount = actCount;
        String now = DateTime.getTimeString();
        ContentValues values = new ContentValues();
        values.put("take", Double.valueOf(actCount));
        values.put("stoptime", now);
        values.put("isEnd", Integer.valueOf(1));
        int isUpload = DbUtils.queryIsUploadByActItemId(context, actItemsId + "");
        if (isUpload > 0) {
            values.put("endUpdateTime", DateTime.getTimeString());
        }
        if (actCount >= 60.0d) {
            DbUtils.getDb(context).update("t_act_item", values, " id is ? ", new String[]{"" + actItemsId});
            ToastUtils.toastShort(getApplicationContext(), Act.getInstance().getActName() + getString(R.string.str_timer_completed));
            DbUtils.addTimeAndSave2(context, DbUtils.queryStartTimebyItemsId(context, actItemsId + ""), now, " and id is not " + actItemsId);
        } else if (getIsFilter()) {
            if (isUpload > 0) {
                values.put("isDelete", Integer.valueOf(1));
                values.put("deleteTime", DateTime.getTimeString());
                DbUtils.getDb(context).update("t_act_item", values, " id is ? ", new String[]{"" + actItemsId});
            } else {
                DbUtils.getDb(context).delete("t_act_item", " id is ? ", new String[]{"" + actItemsId});
            }
            GeneralHelper.toastShort(context, getString(R.string.str_filter_open_unsave_less_one_minute_record));
        } else {
            DbUtils.getDb(context).update("t_act_item", values, " id is ? ", new String[]{"" + actItemsId});
            ToastUtils.toastShort(getApplicationContext(), Act.getInstance().getActName() + getString(R.string.str_timer_completed));
            DbUtils.addTimeAndSave2(context, DbUtils.queryStartTimebyItemsId(context, actItemsId + ""), now, " and id is not " + actItemsId);
        }
    }

    public boolean getIsFilter() {
        return true;
    }

    private void counter(String startTime) {
        actCount += 1.0d;
        if (TodayActivity.onResume) {
            sendBroadcast(new Intent(Val.INTENT_ACTION_UPDATE_UI_MAIN_COUNTER));
        }
        log("开始计时：" + actCount);
        if (isInsertDb) {
            String now = DateTime.getTimeString();
            ContentValues values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("actId", Integer.valueOf(Act.getInstance().getId()));
            values.put("actType", Integer.valueOf(Act.getInstance().getType()));
            if (startTime == null || startTime.length() <= 0) {
                values.put("startTime", now);
                values.put("take", Double.valueOf(actCount));
            } else {
                try {
                    actCount = (double) DateTime.cal_secBetween(startTime, now);
                    values.put("startTime", startTime);
                    values.put("take", Double.valueOf(actCount));
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                    values.put("startTime", now);
                    values.put("take", Double.valueOf(actCount));
                }
            }
            values.put("isRecord", Integer.valueOf(1));
            values.put("stoptime", now);
            actItemsId = (int) DbUtils.getDb(context).insert("t_act_item", null, values);
            isInsertDb = false;
        }
        if (AndroidUtils.isBackground2(context)) {
            timer.cancel();
        }
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:12:0x008f, code:
            r1 = move-exception;
     */
    /* JADX WARNING: Missing block: B:13:0x0090, code:
            com.record.utils.db.DbUtils.exceptionHandler(r11, r1);
     */
    public static void updateCounter2Db(android.content.Context r11) {
        /*
        isContinue2(r11);	 Catch:{ Exception -> 0x008f }
    L_0x0003:
        r4 = actItemsId;	 Catch:{ Exception -> 0x0095 }
        if (r4 <= 0) goto L_0x008e;
    L_0x0007:
        r3 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0095 }
        r3.<init>();	 Catch:{ Exception -> 0x0095 }
        r4 = "take";
        r6 = actCount;	 Catch:{ Exception -> 0x0095 }
        r5 = java.lang.Double.valueOf(r6);	 Catch:{ Exception -> 0x0095 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0095 }
        r4 = "stoptime";
        r5 = com.record.utils.DateTime.getTimeString();	 Catch:{ Exception -> 0x0095 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0095 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x009a }
        r4.<init>();	 Catch:{ Exception -> 0x009a }
        r5 = actItemsId;	 Catch:{ Exception -> 0x009a }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x009a }
        r5 = "";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x009a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x009a }
        r2 = com.record.utils.db.DbUtils.queryIsUploadByActItemId(r11, r4);	 Catch:{ Exception -> 0x009a }
        if (r2 <= 0) goto L_0x0044;
    L_0x003b:
        r4 = "endUpdateTime";
        r5 = com.record.utils.DateTime.getTimeString();	 Catch:{ Exception -> 0x009a }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x009a }
    L_0x0044:
        r4 = com.record.utils.db.DbUtils.getDb(r11);	 Catch:{ Exception -> 0x0095 }
        r5 = "t_act_item";
        r6 = " Id is ? ";
        r7 = 1;
        r7 = new java.lang.String[r7];	 Catch:{ Exception -> 0x0095 }
        r8 = 0;
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0095 }
        r9.<init>();	 Catch:{ Exception -> 0x0095 }
        r10 = "";
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x0095 }
        r10 = actItemsId;	 Catch:{ Exception -> 0x0095 }
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x0095 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0095 }
        r7[r8] = r9;	 Catch:{ Exception -> 0x0095 }
        r4.update(r5, r3, r6, r7);	 Catch:{ Exception -> 0x0095 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0095 }
        r4.<init>();	 Catch:{ Exception -> 0x0095 }
        r5 = "updateCounter2Db临时保存数据：actItemsId:";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0095 }
        r5 = actItemsId;	 Catch:{ Exception -> 0x0095 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0095 }
        r5 = ",,actCount:";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0095 }
        r6 = actCount;	 Catch:{ Exception -> 0x0095 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0095 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0095 }
        log(r4);	 Catch:{ Exception -> 0x0095 }
    L_0x008e:
        return;
    L_0x008f:
        r1 = move-exception;
        com.record.utils.db.DbUtils.exceptionHandler(r11, r1);	 Catch:{ Exception -> 0x0095 }
        goto L_0x0003;
    L_0x0095:
        r0 = move-exception;
        com.record.utils.db.DbUtils.exceptionHandler(r11, r0);
        goto L_0x008e;
    L_0x009a:
        r0 = move-exception;
        com.record.utils.db.DbUtils.exceptionHandler(r11, r0);	 Catch:{ Exception -> 0x0095 }
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.service.TimerService.updateCounter2Db(android.content.Context):void");
    }

    public void onStart(Intent intent, int startId) {
        log("onStart");
        if (this.OnclickReceiver2 == null) {
            this.OnclickReceiver2 = new myBroadCastReceiver();
            registerReceiver(this.OnclickReceiver2, getIntentFilter());
        }
        isContinue();
        initNoti(context);
    }

    public SharedPreferences getSp() {
        if (this.sp == null) {
            this.sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return this.sp;
    }

    public static void initNoti(Context context) {
        log("启动通知栏！TimerService.timer:" + timer);
        if (!isShowNoti(context)) {
            return;
        }
        if (timer == null) {
            getNoti(context).initNoti();
        } else {
            getNoti(context).initCountingNoti(Act.getInstance().getId() + "");
        }
    }

    private static MyNotification getNoti(Context context) {
        if (myNoti == null) {
            myNoti = new MyNotification(context);
        }
        return myNoti;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, 1, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            if (this.OnclickReceiver2 != null) {
                unregisterReceiver(this.OnclickReceiver2);
            }
            if (this.systemActionReceiver != null) {
                unregisterReceiver(this.systemActionReceiver);
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private static void log(String str) {
        Log.i("override TimerService", ":" + str);
    }

    private void logmy(String str) {
        Log.i(getClass().getSimpleName(), ":" + str);
    }

    public static void logfile(String str) {
        LogUtils.logfile(TimerService.class.getSimpleName() + ":" + str);
    }

    public static TimerService getInstance() {
        return timerService;
    }
}
