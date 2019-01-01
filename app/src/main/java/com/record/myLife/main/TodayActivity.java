package com.record.myLife.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.mob.MobSDK;
import com.record.bean.Act;
import com.record.bean.ActUI;
import com.record.bean.User;
import com.record.bean.net.PromptBean;
import com.record.bean.net.ResponseBean;
import com.record.conts.Sofeware;
import com.record.myLife.BaseApplication;
import com.record.myLife.IActivity;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordActivity;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.add.AddRecordWheelActivity;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.base.BottomActivity.OnTabActivityResultListener;
import com.record.myLife.goal.GoalActivity;
import com.record.myLife.goal.GoalListActivity;
import com.record.myLife.goal.TypeActivity;
import com.record.myLife.history.HistoryActivity_v2;
import com.record.myLife.other.GuideActivity;
import com.record.myLife.settings.SetActivity;
import com.record.myLife.view.AddNoteActivity;
import com.record.myLife.view.AnimationController;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.RecalculateRunnable;
import com.record.service.SetActItemsTypeRunnable;
import com.record.service.SetRecordTypeRunnable;
import com.record.service.TimerService;
import com.record.service.UpdateServices;
import com.record.task.BaseTask;
import com.record.thread.UploadRunnable;
import com.record.thread.UploadThread;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.MyNotification;
import com.record.utils.NetUtils;
import com.record.utils.PreferUtils;
import com.record.utils.PushInitUtils;
import com.record.utils.RemindUtils;
import com.record.utils.ShowGuideImgUtils;
import com.record.utils.Sql;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils;
import com.record.view.floatingactionbutton.RapidFloatingActionButton;
import com.record.view.floatingactionbutton.RapidFloatingActionHelper;
import com.record.view.floatingactionbutton.RapidFloatingActionLayout;
import com.record.view.floatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.record.view.floatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.record.view.floatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener;
import com.record.view.pullrefresh.view.PullToRefreshBase;
import com.record.view.pullrefresh.view.PullToRefreshBase.OnRefreshListener;
import com.record.view.pullrefresh.view.PullToRefreshScrollView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
//import com.umeng.fb.FeedbackAgent;
import com.wangjie.androidbucket.utils.ABTextUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONObject;

import static java.util.Calendar.HOUR_OF_DAY;

public class TodayActivity extends BaseActivity implements OnTabActivityResultListener, IActivity, OnRapidFloatingActionContentLabelListListener {
    static String TAG = "override";
    static Context context;
    static ArrayList<Integer> earnMoney;
    static int index = 4;
    public static ArrayList<Integer> invest_int = new ArrayList();
    public static boolean onResume = false;
    public static ArrayList<Integer> routine_int = new ArrayList();
    public static ArrayList<Integer> sleep_int = new ArrayList();
    static int todayMaxSecond = 0;
    public static HashMap<String, ActUI> uiMap = new HashMap();
    public static ArrayList<Integer> waste_int = new ArrayList();
    int ACTIVITY_FLAG = ((int) System.currentTimeMillis());
    Thread CalTodayThread = null;
    private int GOAL_MAX_NUMBER = 20;
    private int GOAL_NUMBER = 10;
    private final int UPDATE_UI_ALLOCATION = 2;
    AnimationController aController;
    String addEndTime = "";
    OnClickListener addOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_today_add_goal) {
                TodayActivity.this.AddGoalActivity();
            } else if (id == R.id.btn_today_manage_goal) {
                TodayActivity.this.goGoasListActivity();
            } else if (id == R.id.btn_today_add_time) {
                TodayActivity.this.goAddRecordActivity();
            } else if (id == R.id.btn_today_time_line) {
                TodayActivity.this.goHistoryActivity();
            }
        }
    };
    String addStartDate = "";
    String addStartTime = "";
    Button btn_act_back;
    String checkActId = "";
    private RapidFloatingActionButton fab_floatingButton_add;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                TodayActivity.this.initTodayBarUI();
            }
        }
    };
    int hour1 = 0;
    int hour2 = 0;
    int i = 0;
    LayoutInflater inflater;
    ImageView iv_act_add_items;
    int min1 = 0;
    int min2 = 0;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action = msg.arg1;
            if (action == 100) {
                GeneralUtils.toastShort(TodayActivity.context, msg.obj.toString());
            } else if (action == 101) {
                GeneralUtils.toastShort(TodayActivity.context, msg.obj.toString());
                TodayActivity.setTodayLeftHour(TodayActivity.context, TopActivity.tv_today_upload_progress);
            } else if (action == 102) {
                GeneralUtils.toastShort(TodayActivity.context, msg.obj.toString());
                TodayActivity.setTodayLeftHour(TodayActivity.context, TopActivity.tv_today_upload_progress);
            } else if (action == 103 && UploadRunnable.uploadCount > 0.0d) {
                TopActivity.tv_today_upload_progress.setText(TodayActivity.this.progressPre + msg.obj);
            }
        }
    };
    OnClickListener myIvListener = new OnClickListener() {
        public void onClick(View v) {
            String id = ((TextView) ((RelativeLayout) v.getParent()).getChildAt(0)).getText().toString();
            Intent it;
            if (TimerService.timer == null) {
                int type = DbUtils.queryActTypeById(TodayActivity.context, id + "").intValue();
                if (type == 10) {
                    TodayActivity.this.showAddActDialog();
                    return;
                }
                it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                it.putExtra("id", id);
                it.putExtra("isToast", true);
                TodayActivity.this.sendBroadcast(it);
                TodayActivity.this.staticsClickGoalStart(type);
                return;
            }
            Intent it2 = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
            it2.putExtra("id", Act.getInstance().getId() + "");
            TodayActivity.log("STOP发送广播：当前id：" + Act.getInstance().getId());
            TodayActivity.this.sendBroadcast(it2);
            if (!id.equals(Act.getInstance().getId() + "")) {
                it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                TodayActivity.log("Start发送广播：当前点击id：" + id);
                it.putExtra("id", id);
                it.putExtra("isToast", true);
                TodayActivity.this.sendBroadcast(it);
            }
            TodayActivity.this.staticsClickGoalStart(Act.getInstance().getType());
        }
    };
    OnClickListener myListener2 = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.fab_floatingButton_add) {
                TodayActivity.this.goAddRecordActivity();
                MobclickAgent.onEvent(TodayActivity.this.getApplicationContext(), "today_today_add_record_btn");
            } else if (id == R.id.pb_today_invest) {
                MobclickAgent.onEvent(TodayActivity.this.getApplicationContext(), "today_today_pb_invest");
                GeneralHelper.toastShort(TodayActivity.context, TodayActivity.this.getString(R.string.str_today_invest_bar_promt));
            } else if (id == R.id.pb_today_waste) {
                MobclickAgent.onEvent(TodayActivity.this.getApplicationContext(), "today_today_pb_waste");
                GeneralHelper.toastShort(TodayActivity.context, TodayActivity.this.getString(R.string.str_today_waste_bar_promt));
            } else if (id == R.id.tv_today_upload_progress) {
                MobclickAgent.onEvent(TodayActivity.this.getApplicationContext(), "today_today_pb_time");
                GeneralUtils.toastShort(TodayActivity.context, TodayActivity.this.getString(R.string.str_today_left_hours_or_upload_progress));
            }
        }
    };
    OnClickListener myRlListener = new OnClickListener() {
        public void onClick(View v) {
            String idStr = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            int id = Integer.parseInt(idStr);
            int type = DbUtils.queryActTypeById(TodayActivity.context, id + "").intValue();
            Intent it;
            if (type == 20 || type == 30 || type == 40 || type == 10) {
                it = new Intent(TodayActivity.context, TypeActivity.class);
                it.putExtra("id", idStr);
                it.putExtra(a.a, type);
                TodayActivity.this.getParent().startActivityForResult(it, 1);
                TodayActivity.this.staticsClickGoal(type);
            } else if (type == 11) {
                it = new Intent(TodayActivity.context, GoalActivity.class);
                it.putExtra("id", idStr);
                it.putExtra(a.a, type);
                TodayActivity.this.getParent().startActivityForResult(it, 1);
                TodayActivity.this.staticsClickGoal(type);
            } else if (id != -1) {
            } else {
                if (TodayActivity.this.getGoalNumber() < TodayActivity.this.GOAL_NUMBER) {
                    TodayActivity.this.getParent().startActivityForResult(new Intent(TodayActivity.context, AddActActivity_v2.class), 4);
                    return;
                }
                GeneralHelper.toastShort(TodayActivity.context, "太多目标会分散精力哦！请先完成目标后再添加！");
            }
        }
    };
    ProgressBar pb_today_invest;
    ProgressBar pb_today_waste;
    PopupWindow popup;
    String progressPre = "";
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionHelper rfabHelper;
    RelativeLayout rl_today_items;
    PullToRefreshScrollView sv_today_items_library;
    TextView tv_today_addtime_info;
    TextView tv_today_addtime_start_to;
    TextView tv_today_addtime_start_yes;
    TextView tv_today_invest;
    int tv_today_title_counter = 0;
    TextView tv_today_upload_progress;
    TextView tv_today_waste;
    BroadcastReceiver updateUIBReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            TodayActivity.log("TodayActivity BroadcastReceiver Action：" + action);
            String id;
            if (Val.INTENT_ACTION_ALARM_SEND.equals(action)) {
                if (TodayActivity.uiMap.size() > 0) {
                    ((ActUI) TodayActivity.uiMap.get(Act.getInstance().getId() + "")).getTv_temp_show_actName().setText(DateTime.calculateTime2((long) Val.actCount));
                }
            } else if (Val.INTENT_ACTION_UPDATE_UI_MAIN_COUNTER.equals(action)) {
                try {
                    if (TodayActivity.uiMap.size() > 0) {
                        String time = DateTime.calculateTime2((long) TimerService.actCount);
                        id = Act.getInstance().getId() + "";
                        if (TodayActivity.uiMap.get(id) != null) {
                            ((ActUI) TodayActivity.uiMap.get(id)).getTv_temp_show_actName().setText(time);
                        }
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
            } else if (Val.INTENT_ACTION_UPDATE_UI_MAIN_START.equals(action)) {
                TodayActivity.this.updateUI_Start_v2(Act.getInstance().getId() + "");
            } else if (Val.INTENT_ACTION_UPDATE_UI_MAIN_STOP.equals(action)) {
                id = intent.getStringExtra("id");
                TodayActivity.this.updateUI_Stop_v3(intent.getStringExtra("id"));
            } else if (Val.INTENT_ACTION_LOGIN.equals(action)) {
                try {
                    if (TimerService.timer != null) {
                        TodayActivity.this.sendBroadcast(new Intent(Val.INTENT_ACTION_STOP_COUNTER));
                    }
                    TodayActivity.this.initAfterLogin();
                    TimerService.initNoti(context);
                } catch (Exception e2) {
                    DbUtils.exceptionHandler(e2);
                }
                try {
                    TodayActivity.cal_TodayAllocat(context);
                    TodayActivity.this.initTodayBarUI();
                    DialogUtils.showPrompt(context, "因记录数据较多，默认只同步近三天的数据，您可通过时间轴界面右上方的同步按钮同步数据！");
                } catch (Exception e22) {
                    DbUtils.exceptionHandler(e22);
                }
            } else if (Val.INTENT_ACTION_NEW_VERSION.equals(action)) {
                TodayActivity.this.showNewVersion(intent);
            } else if (Val.INTENT_ACTION_UPDATE_UI_TODAY.equals(action)) {
                try {
                    TodayActivity.this.initTodayBarUI();
                } catch (Exception e222) {
                    DbUtils.exceptionHandler(e222);
                }
            } else if (Val.INTENT_ACTION_UPDATE_UI_GOAL.equals(action)) {
                try {
                    TodayActivity.log("广播更新主页");
                    TodayActivity.this.updateUiGoalsList();
                } catch (Exception e2222) {
                    DbUtils.exceptionHandler(e2222);
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_v4);
        BaseApplication.getInstance().getControllerManager().addIActivity(this);
        initView();
        init();
    }

    public void init() {
        context = getParent();
        TAG = "override " + getClass().getSimpleName();
        this.inflater = getLayoutInflater();
        this.aController = new AnimationController();
        try {
            MobSDK.init(context);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        isNewUser();
        initAfterLogin();
        doOnfirst();
        isRequestServerTime();
        if (NetUtils.isWiFiAvailable(context)) {
            requestPrompt();
        }
    }

    private void isRequestServerTime() {
        String ids = TimeZone.getDefault().getID();
        if (("Asia/Shanghai".equals(ids) || "Asia/Hong_Kong".equals(ids)) && NetUtils.isNetworkAvailable2noToast(context)) {
            requestServerTime();
        }
    }

    private void requestPrompt() {
        int maxsId = 0;
        try {
            Cursor cursor = DbUtils.getDb(context).rawQuery("select max(sId) from t_prompt ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                maxsId = cursor.getInt(cursor.getColumnIndex("max(sId)"));
            }
            DbUtils.close(cursor);
            JSONObject object = new JSONObject();
            object.put(BaseTask.COMPLETED_ID_FIELD, 5);
            object.put(BaseTask.FAILED_ID_FIELD, 6);
            object.put(BaseTask.REQUEST_URL_FIELD, Sofeware.GET_PROMPT);
            object.put(BaseTask.ACTIVITY_FLAG, this.ACTIVITY_FLAG);
            object.put("s_id", maxsId);
            BaseApplication.getInstance().getControllerManager().startTask(object);
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
        }
    }

    private void requestServerTime() {
        try {
            JSONObject object = new JSONObject();
            object.put(BaseTask.COMPLETED_ID_FIELD, 3);
            object.put(BaseTask.FAILED_ID_FIELD, 4);
            object.put(BaseTask.REQUEST_URL_FIELD, Sofeware.GET_SERVER_TIME);
            object.put(BaseTask.ACTIVITY_FLAG, this.ACTIVITY_FLAG);
            BaseApplication.getInstance().getControllerManager().startTask(object);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void initAfterLogin() {
        UserUtils.isLoginUser(context);
        if (User.getInstance().getUserId() == 0) {
            UserUtils.initTryUser(context);
        }
        DbUtils.insertDb_goalType(context);
        DbUtils.insertDb_LabelType(context);
        updateUiGoalsList();
    }

    private void doOnfirst() {
//        if (NetUtils.isWiFiAvailable(context)) {
//            new FeedbackAgent(context).sync();
//        }
        new Thread(new Runnable() {
            public void run() {
                try {
//                    MobclickAgent.updateOnlineConfig(TodayActivity.context);
                    SharedPreferences sp = TodayActivity.this.getSharedPreferences(Val.CONFIGURE_NAME, 0);
                    if (sp.getInt(Val.CONFIGURE_IS_RECALCULATE_TAKE, 0) < 1) {
                        new Thread(new RecalculateRunnable(TodayActivity.context)).start();
                    }
                    if (sp.getInt(Val.CONFIGURE_IS_SHOW_GUIDE, 0) < 4) {
                        TodayActivity.this.getParent().startActivityForResult(new Intent(TodayActivity.context, GuideActivity.class), 16);
                    } else {
                        ShowGuideImgUtils.showImage(TodayActivity.context, Val.CONFIGURE_IS_SHOW_ADD_GOAL_GUIDE, 2, R.drawable.guide_add_goal);
                    }
                    try {
                        PackageInfo info = TodayActivity.this.getPackageManager().getPackageInfo(TodayActivity.this.getPackageName(), 0);
                        if (sp.getInt(Val.CONFIGURE_IS_RESET_REMIND_ADD_NOTE, 0) < info.versionCode || User.getInstance().getUserName().contains("624604006@qq.com")) {
                            sp.edit().putInt(Val.CONFIGURE_IS_RESET_REMIND_ADD_NOTE, info.versionCode).commit();
                            ((AlarmManager) TodayActivity.context.getSystemService(ALARM_SERVICE)).cancel(RemindUtils.getRemindIntervalPi_Old(TodayActivity.context));
                        }
                        RemindUtils.quickSetRetroSpection(TodayActivity.context);
                        RemindUtils.quickSetRemindMorningVoice(TodayActivity.context);
                        if (sp.getInt(Val.CONFIGURE_IS_RESET_AUTO_BACKUP_WHILE_START, 0) < info.versionCode) {
                            RemindUtils.quicksetAutoBackup(TodayActivity.context);
                            sp.edit().putInt(Val.CONFIGURE_IS_RESET_AUTO_BACKUP_WHILE_START, info.versionCode).commit();
                        }
                        RemindUtils.setRemindInterval(TodayActivity.context);
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_SET_RECORD_BY_TIMER, 0) < 1) {
                            Cursor cursor = DbUtils.getDb(TodayActivity.context).rawQuery("select id from t_act_item limit 1", null);
                            if (cursor.getCount() > 0) {
                                new Thread(new SetRecordTypeRunnable(TodayActivity.context)).start();
                            }
                            DbUtils.close(cursor);
                            sp.edit().putInt(Val.CONFIGURE_IS_SET_RECORD_BY_TIMER, 1).commit();
                        }
                    } catch (Exception e2) {
                        DbUtils.exceptionHandler(e2);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_SET_RECORD_ACT_TYPE, 0) < 5) {
                            new Thread(new SetActItemsTypeRunnable(TodayActivity.context)).start();
                            sp.edit().putInt(Val.CONFIGURE_IS_SET_RECORD_ACT_TYPE, 5).commit();
                        }
                    } catch (Exception e22) {
                        DbUtils.exceptionHandler(e22);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_ADD_CREATETIME_INTO_GOAL, 0) < 1) {
                            DbUtils.addCreateTimeIntoGoal(TodayActivity.context);
                            sp.edit().putInt(Val.CONFIGURE_IS_ADD_CREATETIME_INTO_GOAL, 1).commit();
                        }
                    } catch (Exception e222) {
                        DbUtils.exceptionHandler(e222);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_STATISTICS_ALL_GOAL, 0) < 5) {
                            TodayActivity.log("开始统计----------------");
                            DbUtils.staticsGoalAll(TodayActivity.context);
                            TodayActivity.log("结束统计----------------");
                            sp.edit().putInt(Val.CONFIGURE_IS_STATISTICS_ALL_GOAL, 5).commit();
                        }
                    } catch (Exception e2222) {
                        DbUtils.exceptionHandler(e2222);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_ADD_DATA_TO_LABEL_LINK, 0) < 3) {
                            DbUtils.queryUpdateLabelAddGoalIdData(TodayActivity.context);
                            sp.edit().putInt(Val.CONFIGURE_IS_ADD_DATA_TO_LABEL_LINK, 3).commit();
                        }
                    } catch (Exception e22222) {
                        DbUtils.exceptionHandler(e22222);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_CHANGE_LABEL_ACTTYPE, 0) < 1) {
                            DbUtils.queryUpdateLabelActType(TodayActivity.context);
                            sp.edit().putInt(Val.CONFIGURE_IS_CHANGE_LABEL_ACTTYPE, 1).commit();
                        }
                    } catch (Exception e222222) {
                        DbUtils.exceptionHandler(e222222);
                    }
                    try {
                        if (sp.getInt(Val.CONFIGURE_IS_CHANGE_ICON_BROOM_TO_TRASH, 0) < 1) {
                            DbUtils.updateGoalIconBroomTOTrash(TodayActivity.context);
                            sp.edit().putInt(Val.CONFIGURE_IS_CHANGE_ICON_BROOM_TO_TRASH, 1).commit();
                        }
                    } catch (Exception e2222222) {
                        DbUtils.exceptionHandler(e2222222);
                    }
                    DbUtils.deleteData(TodayActivity.context);
                } catch (Exception e22222222) {
                    DbUtils.exceptionHandler(e22222222);
                }
            }
        }).start();
    }

    private void isNewUser() {
        int CONFIGURE_SHOW_GUIDE = PreferUtils.getSP(context).getInt(Val.CONFIGURE_IS_SHOW_GUIDE, 0);
        if (getGoalNumber() <= 0 && CONFIGURE_SHOW_GUIDE < 4) {
            new Builder(context).setTitle(getString(R.string.str_prompt)).setMessage((CharSequence) "你是新手吗？").setPositiveButton((CharSequence) "我是新手", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    TodayActivity.this.AddGoalActivity();
                    dialog.cancel();
                }
            }).setNegativeButton((CharSequence) "我已会使用", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    private void showNewVersion(Intent it) {
        try {
            String versionName = it.getStringExtra("versionName");
            String updateLog = it.getStringExtra("updateLog");
            Val.versionUrl = it.getStringExtra("url");
            String updateLog2 = "";
            if (updateLog != null) {
                for (String str : updateLog.split(";")) {
                    updateLog2 = updateLog2 + "\n" + str;
                }
                if (updateLog2.length() == 0) {
                    updateLog2 = updateLog;
                }
            }
            new Builder(context).setTitle((CharSequence) "发现新版本").setMessage("版本号:" + versionName + "\n更新内容：\n" + updateLog2).setPositiveButton((CharSequence) "更新", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    TodayActivity.this.startService(new Intent(TodayActivity.context, UpdateServices.class));
                    dialog.cancel();
                    GeneralHelper.toastShort(TodayActivity.context, "开始下载...");
                }
            }).setNegativeButton((CharSequence) "下次", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public static synchronized void cal_TodayAllocat(Context context) {
        synchronized (TodayActivity.class) {
            Cursor cur = DbUtils.getDb(context).rawQuery(Sql.cal_TodayAllocat(context), null);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                String time = cur.getString(cur.getColumnIndex("time"));
                int differDay = DateTime.cal_daysBetween(time, Calendar.getInstance().getTime());
                if (differDay > 0) {
                    log("每日分配 ：与现在相差" + differDay);
                    for (int i = 0; i <= differDay; i++) {
                        Calendar cal = DateTime.pars2Calender(time + " 00:00:00");
                        cal.add(Calendar.DAY_OF_MONTH, i);
                        String Date_former = DateTime.format(cal, DateTime.DATE_FORMAT_LINE);
                        log("每日分配 ：开始计算" + Date_former + "号数据");
                        int invest = cur.getInt(cur.getColumnIndex("invest"));
                        int waste = cur.getInt(cur.getColumnIndex("waste"));
                        int routine = cur.getInt(cur.getColumnIndex("routine"));
                        if (86400 != ((invest + waste) + routine) + cur.getInt(cur.getColumnIndex("sleep"))) {
                            log("每日分配 ：" + Date_former + "号没分配完成!");
                            queryAndUpdateDb_Allocation_v2(Date_former + " 00:00:00");
                        }
                    }
                } else {
                    log("每日分配 ：查询并更新今天的时间分配");
                    queryAndUpdateDb_Allocation_v2(DateTime.getTimeString());
                }
            } else {
                DbUtils.insertDB_todayAllocation(context);
            }
            DbUtils.close(cur);
        }
    }

    public static synchronized void queryAndUpdateDb_Allocation_v2(String time) {
        synchronized (TodayActivity.class) {
            clearAllocationArr();
            earnMoney = new ArrayList();
            String Date = time.substring(0, time.indexOf(" "));
            Cursor cursor = Sql.queryAndUpdateDb_Allocation_v2(context, Date);
            String stopTime;
            int actId;
            if (cursor.getCount() > 0) {
                log("每日分配 ：有" + Date + "的记录");
                String zeroTime = Date + " 00:00:00";
                String tentyTime = Date + " 23:59:59";
                while (cursor.moveToNext()) {
                    int counter;
                    String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                    stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                    actId = cursor.getInt(cursor.getColumnIndex("actId"));
                    String start_str = startTime.substring(0, startTime.indexOf(" "));
                    if (stopTime.substring(0, stopTime.indexOf(" ")).equals(start_str)) {
                        counter = DateTime.cal_secBetween(startTime, stopTime);
                    } else {
                        counter = DateTime.cal_secBetween(startTime, tentyTime);
                    }
                    try {
                        int actType = cursor.getInt(cursor.getColumnIndex("actType"));
                        int take = cursor.getInt(cursor.getColumnIndex("take"));
                        if (take >= 60 && take < 36000 && (actType == 10 || actType == 11)) {
                            log("计算挣钱,counter：" + counter);
                            earnMoney.add(Integer.valueOf(counter));
                        }
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                    }
                    addAllocation(actId, counter);
                    if (cursor.isFirst()) {
                        queryDb_stopTime1(zeroTime, startTime);
                    }
                    if (cursor.isLast()) {
                        ContentValues values;
                        if (start_str.equals(DateTime.getDateString())) {
                            values = cal_allocation(DateTime.getTimeString());
                            log("每日分配 ：values" + values.toString());
                        } else {
                            values = cal_allocation(start_str + " 23:59:59");
                        }
                        try {
                            values.put("earnMoney", FormatUtils.format_1fra(calEarnmoney()));
                        } catch (Exception e2) {
                            DbUtils.exceptionHandler(e2);
                        }
                        insertOrUpdateDb_allocation(Date, values);
                    }
                }
            } else {
                boolean falg;
                DbUtils.close(cursor);
                if (Date.equals(DateTime.getDateString())) {
                    falg = queryDb_stopTime(true, Date + " 00:00:00", DateTime.getTimeString());
                } else {
                    falg = queryDb_stopTime(true, Date + " 00:00:00", Date + " 23:59:59");
                }
                if (!falg) {
                    String time_temp = Date + " 23:59:59";
                    cursor = Sql.queryAndUpdateDb_Allocation_v2_2(context, Date);
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        String start1 = cursor.getString(cursor.getColumnIndex("startTime"));
                        stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                        actId = cursor.getInt(cursor.getColumnIndex("actId"));
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        try {
                            Date dt1 = df.parse(start1);
                            Date dt2 = df.parse(stopTime);
                            Date dt3 = df.parse(time_temp);
                            if (dt3.getTime() > dt1.getTime() && dt3.getTime() < dt2.getTime()) {
                                addAllocation(actId, 86399);
                                insertOrUpdateDb_allocation(Date, cal_allocation(time_temp));
                            }
                        } catch (ParseException e3) {
                            e3.printStackTrace();
                        }
                    } else {
                        try {
                            if (DateTime.getDateString().equals(Date)) {
                                addAllocation(((Integer) DbUtils.queryActIdByType(context, "40").get(0)).intValue(), DateTime.cal_secBetween(Date + " 00:00:00", DateTime.getTimeString()));
                                insertOrUpdateDb_allocation(Date, cal_allocation(DateTime.getTimeString()));
                            }
                        } catch (Exception e22) {
                            DbUtils.exceptionHandler(e22);
                        }
                    }
                    DbUtils.close(cursor);
                }
            }
            DbUtils.close(cursor);
        }
        return;
    }

    private static double calEarnmoney() {
        try {
            if (earnMoney != null && earnMoney.size() > 0) {
                double second = 0.0d;
                Iterator it = earnMoney.iterator();
                while (it.hasNext()) {
                    second += (double) ((Integer) it.next()).intValue();
                }
                return second / 1800.0d;
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0.0d;
    }

    private static boolean queryDb_stopTime1(String zeroTime, String endTime) {
        Cursor cursor2 = DbUtils.getDb(context).rawQuery("select * from t_act_item where userId is ? and startTime < '" + zeroTime + "' and stopTime >= '" + zeroTime + "' order by stopTime", new String[]{User.getInstance().getUserId() + ""});
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                int actId2 = cursor2.getInt(cursor2.getColumnIndex("actId"));
                String stopTime2 = cursor2.getString(cursor2.getColumnIndex("stopTime"));
                String startTime2 = cursor2.getString(cursor2.getColumnIndex("startTime"));
                if (!startTime2.substring(0, startTime2.indexOf(" ")).equals(stopTime2.substring(0, stopTime2.indexOf(" ")))) {
                    int counter = DateTime.cal_secBetween(zeroTime, stopTime2);
                    addAllocation(actId2, counter);
                    int actType = cursor2.getInt(cursor2.getColumnIndex("actType"));
                    int take = cursor2.getInt(cursor2.getColumnIndex("take"));
                    log("计算跨天挣的钱,此跨天共：" + take);
                    if (take >= 60 && take < 36000) {
                        if (actType == 10 || actType == 11) {
                            log("计算跨天挣的钱,此跨天部分：" + counter);
                            earnMoney.add(Integer.valueOf(counter));
                        }
                    }
                }
            }
            DbUtils.close(cursor2);
            return true;
        }
        DbUtils.close(cursor2);
        return false;
    }

    private static boolean queryDb_stopTime(boolean isClearArr, String zeroTime, String endTime) {
        if (isClearArr) {
            clearAllocationArr();
        }
        Cursor cursor2 = DbUtils.getDb(context).rawQuery("select * from t_act_item where userId is ? and isDelete is not 1 and stopTime > '" + zeroTime + "' and stopTime <= '" + endTime + "'", new String[]{User.getInstance().getUserId() + ""});
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            int actId2 = cursor2.getInt(cursor2.getColumnIndex("actId"));
            int counter = DateTime.cal_secBetween(zeroTime, cursor2.getString(cursor2.getColumnIndex("stopTime")));
            addAllocation(actId2, counter);
            try {
                int actType = cursor2.getInt(cursor2.getColumnIndex("actType"));
                int take = cursor2.getInt(cursor2.getColumnIndex("take"));
                if (take >= 60 && take < 36000 && (actType == 10 || actType == 11)) {
                    log("计算挣钱,counter：" + counter);
                    earnMoney.add(Integer.valueOf(counter));
                }
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
            ContentValues values = cal_allocation(endTime);
            try {
                values.put("earnMoney", FormatUtils.format_1fra(calEarnmoney()));
            } catch (Exception e2) {
                DbUtils.exceptionHandler(e2);
            }
            insertOrUpdateDb_allocation(zeroTime.substring(0, zeroTime.indexOf(" ")), values);
            DbUtils.close(cursor2);
            return true;
        }
        DbUtils.close(cursor2);
        return false;
    }

    private static void clearAllocationArr() {
        invest_int = new ArrayList();
        routine_int = new ArrayList();
        sleep_int = new ArrayList();
        waste_int = new ArrayList();
    }

    private static ContentValues cal_allocation(String time) {
        String date = time.substring(0, time.indexOf(" "));
        int totalCount = DateTime.cal_secBetween(date + " 00:00:00", time);
        int vest = 0;
        Iterator it = invest_int.iterator();
        while (it.hasNext()) {
            vest += ((Integer) it.next()).intValue();
        }
        int routine = 0;
        it = routine_int.iterator();
        while (it.hasNext()) {
            routine += ((Integer) it.next()).intValue();
        }
        int sleep = 0;
        it = sleep_int.iterator();
        while (it.hasNext()) {
            sleep += ((Integer) it.next()).intValue();
        }
        int waste = totalCount - ((vest + routine) + sleep);
        ContentValues values = new ContentValues();
        values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
        values.put("invest", Integer.valueOf(vest));
        values.put("routine", Integer.valueOf(routine));
        values.put("sleep", Integer.valueOf(sleep));
        values.put("waste", Integer.valueOf(waste));
        values.put("time", date);
        return values;
    }

    public static void insertOrUpdateDb_allocation(String date, ContentValues values) {
        String whereUserId = DbUtils.getWhereUserId(context);
        Cursor cursor = DbUtils.getDb(context).rawQuery("Select id,isUpload from t_allocation where " + whereUserId + " and time is '" + date + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("isUpload")) > 0) {
                values.put("endUpdateTime", DateTime.getTimeString());
            }
            DbUtils.getDb(context).update("t_allocation", values, whereUserId + " and  time is  '" + date + "'", null);
        } else {
            DbUtils.getDb(context).insert("t_allocation", null, values);
        }
        DbUtils.close(cursor);
    }

    private static void addAllocation(int actId, int counter) {
        int type = DbUtils.queryActTypeById(context, actId + "").intValue();
        if (type == 10 || type == 11) {
            invest_int.add(Integer.valueOf(counter));
        } else if (type == 20) {
            routine_int.add(Integer.valueOf(counter));
        } else if (type == 30) {
            sleep_int.add(Integer.valueOf(counter));
        } else if (type == 40) {
            waste_int.add(Integer.valueOf(counter));
        }
    }

    private void initGoalUI_v2() {
        if (uiMap == null) {
            uiMap = new HashMap();
        }
        uiMap.clear();
        Cursor cur = DbUtils.getDb(context).rawQuery(Sql.getBigGoalsWithOtherType2(context), null);
        int rlId = 400;
        String today = DateTime.getDateString();
        if (cur.getCount() > 0) {
            this.rl_today_items.removeAllViews();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("id"));
                int type = cur.getInt(cur.getColumnIndex(a.a));
                if (cur.getCount() <= 4 || type != 10) {
                    String actName = cur.getString(cur.getColumnIndex("actName"));
                    String image = cur.getString(cur.getColumnIndex("image"));
                    String color = cur.getString(cur.getColumnIndex("color"));
                    String intruction = cur.getString(cur.getColumnIndex("intruction"));
                    int dbType = cur.getInt(cur.getColumnIndex(a.a));
                    int isSubGoal = cur.getInt(cur.getColumnIndex("isSubGoal"));
                    int isHided = cur.getInt(cur.getColumnIndex("isHided"));
                    double timeOfEveryday = cur.getDouble(cur.getColumnIndex("timeOfEveryday"));
                    double expectSpend = cur.getDouble(cur.getColumnIndex("expectSpend"));
                    int hadSpend = 0;
                    int todayHadSpend = 0;
                    if (dbType == 11) {
                        try {
                            hadSpend = (int) DbUtils.queryStaticsHadInvestByGoalId(context, Integer.parseInt(id));
                            todayHadSpend = (int) DbUtils.queryStaticsHadInvestByDateGoalId(context, Integer.parseInt(id), today);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    double[] spendarr = new double[]{(double) hadSpend, expectSpend, (double) todayHadSpend, timeOfEveryday};
                    if (dbType == 10) {
                        intruction = getResources().getString(R.string.str_help_time);
                    } else if (dbType == 20) {
                        intruction = getResources().getString(R.string.str_oblige_helpless_time);
                    } else if (dbType == 30) {
                        intruction = getResources().getString(R.string.str_sleep_time);
                    } else if (dbType == 40) {
                        intruction = getResources().getString(R.string.str_helpless_time);
                    }
                    String deadline = cur.getString(cur.getColumnIndex("deadline"));
                    String[] strArr = new String[]{id, actName, color, image, intruction, deadline};
                    int isShowConnerLabel = 0;
                    if (isSubGoal > 0 && dbType == 11) {
                        isShowConnerLabel = 2;
                    } else if (dbType == 11) {
                        isShowConnerLabel = 1;
                    }
                    if (dbType == 11) {
                        Cursor cursor2 = DbUtils.getDb(context).rawQuery(Sql.getSubGoals(context, id), null);
                        if (cursor2.getCount() > 0) {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
                            layoutParams.addRule(3, rlId - 1);
                            layoutParams.topMargin = 5;
                            int bigGoalRlId = 500;
                            RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_rl_big_goal, null);
                            rl.setLayoutParams(layoutParams);
                            rl.setId(rlId);
                            rl.addView(getGoalItems(500, strArr, isShowConnerLabel, isHided, spendarr, dbType));
                            while (true) {
                                bigGoalRlId++;
                                if (!cursor2.moveToNext()) {
                                    break;
                                }
                                String id2 = cursor2.getString(cursor2.getColumnIndex("id"));
                                String actName2 = cursor2.getString(cursor2.getColumnIndex("actName"));
                                String image2 = cursor2.getString(cursor2.getColumnIndex("image"));
                                String color2 = cursor2.getString(cursor2.getColumnIndex("color"));
                                String intruction2 = cursor2.getString(cursor2.getColumnIndex("intruction"));
                                int dbType2 = cursor2.getInt(cursor2.getColumnIndex(a.a));
                                int isSubGoal2 = cursor2.getInt(cursor2.getColumnIndex("isSubGoal"));
                                int isHided2 = cursor2.getInt(cursor2.getColumnIndex("isHided"));
                                int hadSpend2 = cursor2.getInt(cursor2.getColumnIndex("hadSpend"));
                                int expectSpend2 = cursor2.getInt(cursor2.getColumnIndex("expectSpend"));
                                int timeOfEveryday2 = cursor2.getInt(cursor2.getColumnIndex("timeOfEveryday"));
                                String startTime2 = cursor2.getString(cursor2.getColumnIndex("startTime"));
                                String deadline2 = cursor2.getString(cursor2.getColumnIndex("deadline"));
                                int todayHadSpend2 = 0;
                                if (dbType2 == 11) {
                                    hadSpend2 = (int) DbUtils.queryStaticsHadInvestByGoalId(context, Integer.parseInt(id2));
                                    todayHadSpend2 = (int) DbUtils.queryStaticsHadInvestByDateGoalId(context, Integer.parseInt(id2), today);
                                }
                                String[] strArr2 = new String[]{id2, actName2, color2, image2, intruction2, deadline2};
                                isShowConnerLabel = 0;
                                if (isSubGoal2 > 0 && dbType2 == 11) {
                                    isShowConnerLabel = 2;
                                } else if (dbType2 == 11) {
                                    isShowConnerLabel = 1;
                                }
                                RelativeLayout relativeLayout = rl;
                                relativeLayout.addView(getGoalItems(bigGoalRlId, strArr2, isShowConnerLabel, isHided2, new double[]{(double) hadSpend2, (double) expectSpend2, (double) todayHadSpend2, (double) timeOfEveryday2}, dbType2));
                            }
                            this.rl_today_items.addView(rl);
                        } else {
                            this.rl_today_items.addView(getGoalItems(rlId, strArr, 4, isHided, spendarr, dbType));
                        }
                        DbUtils.close(cursor2);
                    } else {
                        this.rl_today_items.addView(getGoalItems(rlId, strArr, isShowConnerLabel, isHided, spendarr, dbType));
                    }
                    rlId++;
                    if (cur.isLast()) {
                        this.rl_today_items.addView(getAddView(rlId));
                    }
                }
            }
        }
        DbUtils.close(cur);
    }

    private LinearLayout getAddView(int rlId) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        params.addRule(3, rlId - 1);
        params.topMargin = 3;
        LinearLayout ll = (LinearLayout) this.inflater.inflate(R.layout.tem_ll_tran_60dp, null);
        ll.setLayoutParams(params);
        return ll;
    }

    private void goAddRecordActivity() {
        String[] timeArr = DbUtils.queryLastRecordStopTime(context, DateTime.getDateString());
        String startTime = timeArr[0];
        String stopTime = timeArr[1];
        int type = PreferUtils.getSP(context).getInt(Val.CONFIGURE_ADD_RECORD_TYPE, 1);
        Intent it;
        if (type == 1) {
            it = new Intent(context, AddRecordDigitActivity.class);
            it.putExtra("startTime", startTime);
            it.putExtra("stopTime", stopTime);
            startActivity(it);
        } else if (type == 2) {
            it = new Intent(context, AddRecordWheelActivity.class);
            it.putExtra("startTime", startTime);
            it.putExtra("stopTime", stopTime);
            startActivity(it);
        } else if (type == 3) {
            startActivity(new Intent(context, AddRecordActivity.class));
        }
    }

    private void goHistoryActivity() {
        Cursor cursor = DbUtils.getDb(context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(context) + " limit 10", null);
        if (cursor.getCount() < 1) {
            GeneralHelper.toastLong(context, "点击 带'+'的记录或者 '+'(底部) 可进行添加！");
        }
        DbUtils.close(cursor);
        startActivity(new Intent(context, HistoryActivity_v2.class));
    }

    private void goGoasListActivity() {
        getParent().startActivityForResult(new Intent(context, GoalListActivity.class), 13);
    }

    private void AddGoalActivity() {
        int currentNumber = getGoalNumber();
        if (currentNumber < this.GOAL_NUMBER) {
            getParent().startActivityForResult(new Intent(context, AddActActivity_v2.class), 4);
        } else if (currentNumber < this.GOAL_NUMBER || currentNumber > this.GOAL_MAX_NUMBER) {
            GeneralHelper.toastShort(context, (int) R.string.str_goal_too_more);
        } else if (!isFinishing()) {
            new Builder(context).setTitle(getString(R.string.str_prompt)).setMessage((int) R.string.str_goal_over_10).setPositiveButton((int) R.string.str_let_me_thinking, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setNeutralButton((int) R.string.str_comfirm_add, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    TodayActivity.this.getParent().startActivityForResult(new Intent(TodayActivity.context, AddActActivity_v2.class), 4);
                    dialog.cancel();
                }
            }).create().show();
        }
    }

    private RelativeLayout getGoalItems(int rlId, String[] strArr, int imgType, int isHided, double[] hadInvest, int type) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        params.addRule(3, rlId - 1);
        params.topMargin = 5;
        RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.template_act_rect_show_v3, null);
        rl_temp_show_outer.setId(rlId);
        rl_temp_show_outer.setLayoutParams(params);
        rl_temp_show_outer.setBackgroundColor(getResources().getColor(R.color.gray_f5));
        RelativeLayout rl_temp_show_label_bg = (RelativeLayout) rl_temp_show_outer.findViewById(R.id.rl_temp_show_label_bg);
        rl_temp_show_outer.setOnClickListener(this.myRlListener);
        if (isHided > 0) {
            rl_temp_show_outer.setVisibility(View.GONE);
        } else {
            TextView tv_temp_show_id = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_show_id);
            TextView tv_temp_show_actName = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_show_actName);
            TextView tv_temp_show_remark = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_show_remark);
            TextView tv_temp_show_hours = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_show_hours);
            TextView tv_temp_show_today_hours = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_show_today_hours);
            ImageView iv_temp_top_left_corner = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_top_left_corner);
            ((ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_show_label_pre)).setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(strArr[2])).intValue());
            ImageView iv_temp_show_label = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_show_label);
            ImageView iv_temp_show_start = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_show_start);
            iv_temp_show_start.setOnClickListener(this.myIvListener);
            tv_temp_show_id.setText(strArr[0]);
            tv_temp_show_actName.setText(strArr[1]);
            if (strArr[4] == null || strArr[4].length() <= 0) {
                tv_temp_show_remark.setText("");
            } else {
                tv_temp_show_remark.setText(strArr[4]);
            }
            if (strArr[3] == null || strArr[3].length() <= 0) {
                iv_temp_show_label.setVisibility(View.GONE);
                iv_temp_show_start.setVisibility(View.INVISIBLE);
                tv_temp_show_remark.setVisibility(View.GONE);
            } else {
                iv_temp_show_label.setImageResource(Val.getLabelIntByName(strArr[3]));
            }
            if (imgType == 0) {
                iv_temp_top_left_corner.setVisibility(View.INVISIBLE);
            } else if (imgType == 4) {
                iv_temp_top_left_corner.setVisibility(View.INVISIBLE);
            } else if (imgType == 1) {
                iv_temp_top_left_corner.setVisibility(View.VISIBLE);
                iv_temp_top_left_corner.setImageResource(R.drawable.ic_label_green_3);
            } else if (imgType == 2) {
                iv_temp_top_left_corner.setVisibility(View.VISIBLE);
                iv_temp_top_left_corner.setImageResource(R.drawable.ic_subgoal_black);
            }
            if (type == 11) {
                if (hadInvest == null || hadInvest.length <= 0 || hadInvest[0] < 120.0d) {
                    tv_temp_show_hours.setVisibility(View.INVISIBLE);
                } else {
                    tv_temp_show_hours.setVisibility(View.VISIBLE);
                    String spend = FormatUtils.format_1fra(hadInvest[0] / 3600.0d);
                    if (hadInvest[1] > 0.0d) {
                        tv_temp_show_hours.setText(spend + "/" + FormatUtils.format_0fra(hadInvest[1] / 3600.0d) + "h");
                    } else {
                        tv_temp_show_hours.setText(spend + "h");
                    }
                }
                if (hadInvest != null) {
                    try {
                        if (hadInvest[3] > 0.0d) {
                            String todayspend = FormatUtils.format_1fra(hadInvest[2] / 3600.0d);
                            tv_temp_show_today_hours.setText(todayspend + "/" + FormatUtils.format_1fra(hadInvest[3] / 3600.0d) + "h");
                            tv_temp_show_today_hours.setVisibility(View.VISIBLE);
                            if (hadInvest[2] > 0.0d) {
                                tv_temp_show_today_hours.setTextColor(getResources().getColor(R.color.bg_green1));
                            } else {
                                tv_temp_show_today_hours.setTextColor(getResources().getColor(R.color.bg_red1));
                            }
                        }
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                    }
                }
                if (hadInvest != null) {
                    if (hadInvest[2] > 0.0d) {
                        tv_temp_show_today_hours.setText(FormatUtils.format_1fra(hadInvest[2] / 3600.0d) + "h");
                        tv_temp_show_today_hours.setVisibility(View.VISIBLE);
                    }
                }
                tv_temp_show_today_hours.setVisibility(View.INVISIBLE);
            } else {
                tv_temp_show_hours.setVisibility(View.INVISIBLE);
                tv_temp_show_today_hours.setVisibility(View.INVISIBLE);
            }
            ActUI actUI = new ActUI(rl_temp_show_outer, tv_temp_show_id, rl_temp_show_label_bg, iv_temp_show_label, iv_temp_show_start, tv_temp_show_actName, tv_temp_show_remark, iv_temp_top_left_corner, tv_temp_show_hours);
            uiMap.put(strArr[0], actUI);
        }
        return rl_temp_show_outer;
    }

    public void initTodayBarUI() {
        try {
            String[] arrStr = DbUtils.queryTodayAllocation(context);
            this.tv_today_invest.setText(arrStr[0] + " " + getResources().getString(R.string.str_invest));
            this.tv_today_waste.setText(arrStr[1] + " " + getResources().getString(R.string.str_waste));
            if (arrStr[2] != null) {
                todayMaxSecond = Integer.parseInt(arrStr[4]);
                updateUI_todayPb(todayMaxSecond, Integer.parseInt(arrStr[2]), Integer.parseInt(arrStr[3]));
            }
            setTodayLeftHour(context, TopActivity.tv_today_upload_progress);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public static void setTodayLeftHour(Context context, TextView tv) {
        if (tv != null) {
            try {
                double left2;
                long now = Calendar.getInstance().getTime().getTime();
                int startHour = PreferUtils.getInt(context, Val.CONFIGURE_COUNTER_DOWN_START_TIME, 0);
                Calendar c = Calendar.getInstance();
                if (startHour - 1 >= 0) {
                    c.set(HOUR_OF_DAY, startHour - 1);
                } else {
                    c.set(HOUR_OF_DAY, 23);
                }
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                long end2 = c.getTime().getTime();
                if (end2 < now) {
                    left2 = (86400.0d - (((double) (now - end2)) / 1000.0d)) / 3600.0d;
                } else {
                    left2 = (((double) (end2 - now)) / 1000.0d) / 3600.0d;
                }
                tv.setText(FormatUtils.format_1fra(left2) + "h");
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
        }
    }

    public void updateUI_todayPb(int max, int invest, int waste) {
        this.pb_today_invest.setMax(max);
        this.pb_today_waste.setMax(max);
        this.pb_today_invest.setProgress(invest);
        this.pb_today_waste.setProgress(waste);
    }

    private void updateUiGoalsList() {
        initGoalUI_v2();
        if (TimerService.timer != null) {
            updateUI_Start_v2(Act.getInstance().getId() + "");
        }
    }

    public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 || resultCode == 7) {
            log("onActivityResult 更新所有界面。");
            updateUiGoalsList();
            try {
                if (getSharedPreferences(Val.CONFIGURE_NAME, MODE_WORLD_READABLE).getBoolean(Val.CONFIGURE_IS_SHOW_NOTI, true)) {
                    if (TimerService.timer == null) {
                        new MyNotification(context).initNoti();
                    } else {
                        new MyNotification(context).initCountingNoti(Act.getInstance().getId() + "");
                    }
                }
                ShowGuideImgUtils.showImage(context, Val.CONFIGURE_IS_SHOW_CLICK_GOAL_GUIDE, 2, R.drawable.guide_goal);
                TimerService.updateWidgetGoal(context);
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
        } else if (requestCode == 4) {
            log("onActivityResult 重新打开添加界面");
            if (resultCode == 15) {
                Intent it = new Intent(context, AddActActivity_v2.class);
                it.putExtra("isNotMantscript", 1);
                getParent().startActivityForResult(it, 4);
            }
        } else if (requestCode == 16) {
            ShowGuideImgUtils.showImage(context, Val.CONFIGURE_IS_SHOW_ADD_GOAL_GUIDE, 2, R.drawable.guide_add_goal);
        }
    }

    public static void startHistoryActivity(Activity activity) {
        activity.startActivity(new Intent(activity, HistoryActivity_v2.class));
        activity.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    private void onclickCloudUpload() {
        try {
            Cursor cursor = DbUtils.getDb(context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(context) + " and isUpload = 1 limit 1", null);
            if (cursor.getCount() > 0) {
                this.progressPre = "";
            } else {
                this.progressPre = getString(R.string.str_upload);
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (User.getInstance().getUserId() == DbUtils.queryTryUserId(context)) {
            uploadComplete();
            GeneralUtils.toastShort(context, getString(R.string.str_login_for_upload));
            return;
        }
        if (NetUtils.isNetworkAvailable(context)) {
            Thread upload = UploadThread.getInstance(context, this.myHandler);
            if (upload == null || !upload.isAlive()) {
                this.tv_today_upload_progress.setText("0%");
                this.tv_today_upload_progress.setVisibility(View.VISIBLE);
                upload.start();
                GeneralUtils.toastShort(context, getString(R.string.str_uploading));
            } else {
                GeneralUtils.toastShort(context, getString(R.string.str_uploading2));
            }
        }
        uploadComplete();
        MobclickAgent.onEvent(getApplicationContext(), "today_today_pulldown");
    }

    private void uploadComplete() {
        this.sv_today_items_library.onRefreshComplete();
    }

    private void staticsClickGoalStart(int type) {
        if (11 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_other_activate_btn");
        } else if (20 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_fixed_activate_btn");
        } else if (30 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_sleep_activate_btn");
        } else if (40 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_waste_activate_btn");
        }
    }

    private void staticsClickGoal(int type) {
        if (11 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_other_view_the_details_btn");
        } else if (20 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_fixed_view_the_details_btn");
        } else if (30 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_sleep_view_the_details_btn");
        } else if (40 == type) {
            MobclickAgent.onEvent(getApplicationContext(), "today_today_click_waste_view_the_details_btn");
        }
    }

    private void showAddActDialog() {
        if (!((Activity) context).isFinishing()) {
            new Builder(context).setTitle(getString(R.string.str_add_goal2)).setMessage(getString(R.string.str_add_goal_prompt)).setPositiveButton(getString(R.string.str_add), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    TodayActivity.this.getParent().startActivityForResult(new Intent(TodayActivity.context, AddActActivity_v2.class), 4);
                }
            }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
    }

    private int getGoalNumber() {
        Cursor cursor = DbUtils.getDb(context).rawQuery("Select id from t_act where " + DbUtils.getWhereUserId(context) + " and type is 11 and isDelete is not 1 and isfinish is not 1 and isManuscript is not 1", null);
        int count = cursor.getCount();
        DbUtils.close(cursor);
        return count;
    }

    private void updateUI_Stop_v3(String id) {
        if (uiMap == null || uiMap.size() == 0) {
            initGoalUI_v2();
        }
        Cursor cursor = DbUtils.getDb(context).rawQuery("select * from t_act where id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String actName = cursor.getString(cursor.getColumnIndex("actName"));
            String instruction = cursor.getString(cursor.getColumnIndex("intruction"));
            int type = cursor.getInt(cursor.getColumnIndex(a.a));
            if (type == 10) {
                instruction = getResources().getString(R.string.str_help_time);
            } else if (type == 20) {
                instruction = getResources().getString(R.string.str_oblige_helpless_time);
            } else if (type == 30) {
                instruction = getResources().getString(R.string.str_sleep_time);
            } else if (type == 40) {
                instruction = getResources().getString(R.string.str_helpless_time);
            }
            log("id:" + id + ",uiMap:" + uiMap.toString());
            try {
                ((ActUI) uiMap.get(id)).getRl_temp_show_outer().setBackgroundColor(getResources().getColor(R.color.gray_f5));
                TextView tv_temp_show_actName = ((ActUI) uiMap.get(id)).getTv_temp_show_actName();
                if (actName == null) {
                    actName = "";
                }
                tv_temp_show_actName.setText(actName);
                tv_temp_show_actName = ((ActUI) uiMap.get(id)).getTv_temp_show_remark();
                if (instruction == null) {
                    instruction = "";
                }
                tv_temp_show_actName.setText(instruction);
                ((ActUI) uiMap.get(id)).getIv_temp_show_start().setImageResource(R.drawable.ic_start);
                ((ActUI) uiMap.get(id)).getTv_temp_show_actName().setTextColor(getResources().getColor(R.color.black));
                ((ActUI) uiMap.get(id)).getTv_temp_show_remark().setTextColor(getResources().getColor(R.color.black_tran_fs));
                ((ActUI) uiMap.get(id)).getTv_temp_show_hours().setTextColor(getResources().getColor(R.color.black_tran_fs));
            } catch (Exception e1) {
                DbUtils.exceptionHandler(context, e1, "id:" + id + ",uiMap:" + uiMap.toString());
            }
            if (type == 11) {
                try {
                    if (TimerService.lastActCount > 600.0d) {
                        int lastId = Integer.parseInt(id);
                        double hadInvest = DbUtils.queryStaticsHadInvestByGoalId(context, lastId);
                        ((ActUI) uiMap.get(id)).getTv_temp_show_hours().setText(FormatUtils.format_1fra(hadInvest / 3600.0d) + "h");
                        int bigGoalId = DbUtils.queryBigGoalIdBySubGoalId(context, lastId);
                        if (bigGoalId > 0) {
                            double BigGoalhadInvest = DbUtils.queryStaticsHadInvestByGoalId(context, bigGoalId);
                            ActUI ui = (ActUI) uiMap.get(Integer.valueOf(bigGoalId));
                            if (ui != null) {
                                ui.getTv_temp_show_hours().setText(FormatUtils.format_1fra(BigGoalhadInvest / 3600.0d) + "h");
                            }
                            log("定止计时，更新主页目标列表updateUI_Stop_v3，，hadInvest：" + BigGoalhadInvest + ",子目标：" + hadInvest);
                        }
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(context, e);
                }
            }
        }
        DbUtils.close(cursor);
    }

    private void updateUI_Start_v2(String id) {
        try {
            if (uiMap == null || uiMap.size() == 0) {
                initGoalUI_v2();
            }
            ActUI actui = (ActUI) uiMap.get(id);
            String colorStr = Act.getInstance().getColor();
            if (Val.col_Str2Int_Map == null) {
                Val.setMap();
            }
            actui.getRl_temp_show_outer().setBackgroundColor(getResources().getColor(((Integer) Val.col_Str2Int_Map.get(colorStr)).intValue()));
            actui.getTv_temp_show_actName().setText("00:00:00");
            actui.getTv_temp_show_remark().setText(Act.getInstance().getActName());
            actui.getIv_temp_show_start().setImageResource(R.drawable.ic_stop_white);
            actui.getTv_temp_show_remark().setTextColor(getResources().getColor(R.color.white2));
            actui.getTv_temp_show_actName().setTextColor(getResources().getColor(R.color.white2));
            actui.getTv_temp_show_hours().setTextColor(getResources().getColor(R.color.white2));
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void initView() {
        this.pb_today_invest = (ProgressBar) findViewById(R.id.pb_today_invest);
        this.pb_today_waste = (ProgressBar) findViewById(R.id.pb_today_waste);
        this.tv_today_invest = (TextView) findViewById(R.id.tv_today_invest);
        this.tv_today_waste = (TextView) findViewById(R.id.tv_today_waste);
        this.tv_today_upload_progress = (TextView) findViewById(R.id.tv_today_upload_progress);
        this.btn_act_back = (Button) findViewById(R.id.btn_act_back);
        this.rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.rfal_layout);
        this.rfaButton = (RapidFloatingActionButton) findViewById(R.id.fab_floatingButton);
        this.fab_floatingButton_add = (RapidFloatingActionButton) findViewById(R.id.fab_floatingButton_add);
        this.tv_today_upload_progress.setVisibility(View.VISIBLE);
        this.rl_today_items = (RelativeLayout) findViewById(R.id.rl_today_items);
        this.sv_today_items_library = (PullToRefreshScrollView) findViewById(R.id.sv_today_items_library);
        this.sv_today_items_library.setPullLabel("下拉上传");
        this.sv_today_items_library.setRefreshingLabel("启动上传线程");
        this.sv_today_items_library.setReleaseLabel("松开上传");
        this.sv_today_items_library.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
            public void onRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                TodayActivity.this.onclickCloudUpload();
            }
        });
        this.pb_today_invest.setOnClickListener(this.myListener2);
        this.pb_today_waste.setOnClickListener(this.myListener2);
        this.tv_today_upload_progress.setOnClickListener(this.myListener2);
        this.fab_floatingButton_add.setOnClickListener(this.myListener2);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Val.INTENT_ACTION_UPDATE_UI_MAIN_COUNTER);
        filter.addAction(Val.INTENT_ACTION_UPDATE_UI_MAIN_START);
        filter.addAction(Val.INTENT_ACTION_UPDATE_UI_MAIN_STOP);
        filter.addAction(Val.INTENT_ACTION_ALARM_SEND);
        filter.addAction(Val.INTENT_ACTION_LOGIN);
        filter.addAction(Val.INTENT_ACTION_NEW_VERSION);
        filter.addAction(Val.INTENT_ACTION_UPDATE_UI_TODAY);
        filter.addAction(Val.INTENT_ACTION_UPDATE_UI_GOAL);
        registerReceiver(this.updateUIBReceiver, filter);
        initFloatActionButton();
    }

    private void initFloatActionButton() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList();
        items.add(new RFACLabelItem().setLabel("管理目标").setResId(R.drawable.ic_label_globe).setIconNormalColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setIconPressedColor(Integer.valueOf(getResources().getColor(R.color.bg_blue2))).setLabelColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setWrapper(Integer.valueOf(0)));
        items.add(new RFACLabelItem().setLabel("添加目标").setResId(R.drawable.ic_label_targit).setIconNormalColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setIconPressedColor(Integer.valueOf(getResources().getColor(R.color.bg_blue2))).setLabelColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setWrapper(Integer.valueOf(1)));
        items.add(new RFACLabelItem().setLabel("查看记录").setResId(R.drawable.ic_label_page).setIconNormalColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setIconPressedColor(Integer.valueOf(getResources().getColor(R.color.bg_blue2))).setLabelColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setWrapper(Integer.valueOf(2)));
        items.add(new RFACLabelItem().setLabel("晨音计划").setResId(R.drawable.ic_label_idea).setIconNormalColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setIconPressedColor(Integer.valueOf(getResources().getColor(R.color.bg_blue2))).setLabelColor(Integer.valueOf(getResources().getColor(R.color.bg_blue1))).setWrapper(Integer.valueOf(3)));
        rfaContent.setItems(items).setIconShadowRadius(ABTextUtil.dip2px(this, 4.0f)).setIconShadowColor(-7829368).setIconShadowDy(ABTextUtil.dip2px(this, 4.0f));
        this.rfabHelper = new RapidFloatingActionHelper(this, this.rfaLayout, this.rfaButton, rfaContent).build();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        onResume = true;
        Message msg = new Message();
        msg.what = 2;
        this.handler.sendMessage(msg);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.str_set));
        menu.add(getResources().getString(R.string.str_exit));
        menu.add(getResources().getString(R.string.str_add_goal));
        menu.add(getResources().getString(R.string.str_add_history));
        menu.add(getResources().getString(R.string.str_goal));
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.str_set))) {
            log("点击设置");
            startActivity(new Intent(context, SetActivity.class));
        } else if (item.getTitle().equals(getResources().getString(R.string.str_exit))) {
            log("点击退出");
            showExitDialog();
        } else if (item.getTitle().equals(getResources().getString(R.string.str_add_goal))) {
            AddGoalActivity();
        } else if (item.getTitle().equals(getResources().getString(R.string.str_add_history))) {
            goHistoryActivity();
        } else if (item.getTitle().equals(getResources().getString(R.string.str_goal))) {
            goGoasListActivity();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void showExitDialog() {
        try {
            new Builder(context).setTitle(getResources().getString(R.string.str_is_exit)).setMessage(getResources().getString(R.string.str_exit_prompt)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setPositiveButton(getResources().getString(R.string.str_exit), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    TodayActivity.this.exitApp();
                }
            }).create().show();
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public static String getDeviceInfo(Context context) {
        try {
            JSONObject json = new JSONObject();
            String device_id = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            String mac = ((WifiManager) context.getSystemService(WIFI_SERVICE)).getConnectionInfo().getMacAddress();
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = Secure.getString(context.getContentResolver(), "android_id");
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void exitApp() {
        stopService(new Intent(context, TimerService.class));
        RemindUtils.cancelUpdateWidgetUI(context);
        MobclickAgent.onKillProcess(context);
        finish();
        System.exit(0);
    }

    public void onBackPressed() {
        try {
            if (this.popup != null && this.popup.isShowing()) {
                this.popup.dismiss();
            }
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            BaseApplication.getInstance().getControllerManager().removeIActivity(this);
            MobSDK.init(context);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        try {
            unregisterReceiver(this.updateUIBReceiver);
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
        }
    }

    protected void onPause() {
        super.onPause();
        onResume = false;
        MobclickAgent.onPause(this);
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }

    public void refresh(Message msg) {
        if (msg.arg2 == this.ACTIVITY_FLAG) {
            switch (msg.what) {
                case 3:
                    parseServerTime((ResponseBean) msg.obj);
                    return;
                case 5:
                    parsePromptContent((ResponseBean) msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    private void parsePromptContent(ResponseBean bean) {
        try {
            if (bean.status == 1) {
                savePrompt(PromptBean.getBeanArrs(new JSONObject(bean.data).getString("items")));
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(context, e);
        }
    }

    private void savePrompt(ArrayList<PromptBean> items) {
        if (items != null && items.size() != 0) {
            String talbeName = "t_prompt";
            Iterator it = items.iterator();
            while (it.hasNext()) {
                PromptBean tempBean = (PromptBean) it.next();
                ContentValues values = new ContentValues();
                values.put("sId", Integer.valueOf(tempBean.s_id));
                values.put(PushInitUtils.RESPONSE_CONTENT, tempBean.content);
                DbUtils.getDb(context).insert(talbeName, null, values);
            }
        }
    }

    private void parseServerTime(ResponseBean bean) {
        if (bean.status != 1 || bean.data == null || bean.data.length() <= 0 || bean.data.equals("null") || Math.abs(DateTime.cal_secBetween(DateTime.getTimeString(), bean.data)) > 1800) {
        }
    }

    private void showResetTimeDialog(String time, String serverTime) {
        DialogUtils.showPromptWithHandler(context, "亲，您手机时间不准确哦！\n现在北京时间为:\n" + serverTime + "\n是否重新设定时间？", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TodayActivity.this.startActivity(new Intent("android.settings.DATE_SETTINGS"));
                dialog.cancel();
            }
        });
    }

    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        onRFACItemClick(position);
    }

    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        onRFACItemClick(position);
    }

    private void onRFACItemClick(int position) {
        switch (position) {
            case 0:
                MobclickAgent.onEvent(getApplicationContext(), "today_today_fab_goal_list");
                goGoasListActivity();
                break;
            case 1:
                MobclickAgent.onEvent(getApplicationContext(), "today_today_fab_add_goal");
                AddGoalActivity();
                break;
            case 2:
                MobclickAgent.onEvent(getApplicationContext(), "today_today_fab_time_line");
                goHistoryActivity();
                break;
            case 3:
                MobclickAgent.onEvent(getApplicationContext(), "today_today_fab_morning_voice");
                showMorningVoice();
                break;
        }
        MobclickAgent.onEvent(getApplicationContext(), "today_today_add_left_one_btn");
    }

    private void showMorningVoice() {
        Intent it = new Intent(context, AddNoteActivity.class);
        it.setAction(Val.INTENT_ACTION_NOTI_MORNING_VOICE);
        startActivityForResult(it, 12);
    }
}
