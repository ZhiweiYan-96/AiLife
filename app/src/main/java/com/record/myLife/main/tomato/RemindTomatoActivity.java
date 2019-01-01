package com.record.myLife.main.tomato;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.IDemoChart;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.settings.label.LabelSelectActivity;
import com.record.myLife.view.DeliberateStarView;
import com.record.myLife.view.MyGoalItemsLayout;
import com.record.myLife.view.MyGoalItemsLayout.MyOnItemsClickListener;
import com.record.service.TimerService;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralUtils;
import com.record.utils.LogUtils;
import com.record.utils.PowerMangerUtils;
import com.record.utils.PreferUtils;
import com.record.utils.Sql;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.log.MyLog;
import com.record.utils.sound.VibratorHelper;
import com.record.utils.tomato.TomatoController;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RemindTomatoActivity extends BaseActivity {
    public static HashMap<Integer, Integer> Act2TypeMap;
    String Date = "";
    int SOUND_INTERVAL = 0;
    final int TYPE_LEARN_TIME_OUT = 1;
    final int TYPE_REST_TIME_OUT = 2;
    String action = "";
    Button btn_add_note_delay;
    Button btn_add_note_rest;
    Button btn_remind_rest_covert_to_counter;
    Button btn_remind_rest_delay1;
    Button btn_remind_rest_delay2;
    Button btn_remind_rest_delay3;
    String checkActId;
    Context context;
    DeliberateStarView de_rating_star;
    DeliverBean deliverBean;
    MyOnItemsClickListener goalItemsIdClickLister = new MyOnItemsClickListener() {
        public void onClick(View v, String id) {
            RemindTomatoActivity.this.checkActId = id;
            RemindTomatoActivity.this.log("checkActId:" + RemindTomatoActivity.this.checkActId);
        }
    };
    HorizontalScrollView hs_remind_tomato_items;
    LayoutInflater inflater;
    ImageView iv_add_note_close;
    ImageView iv_add_note_delay;
    ImageView iv_remind_rest_drag_down;
    int labelId = 0;
    LinearLayout ll_remind_tomato_items;
    MediaPlayer media;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_add_note_close) {
                RemindTomatoActivity.this.onBackPressed();
            } else if (id == R.id.btn_add_note_delay) {
                if (RemindTomatoActivity.this.rl_remind_rest_delay_items.getVisibility() == View.VISIBLE) {
                    RemindTomatoActivity.this.rl_remind_rest_delay_items.setVisibility(View.GONE);
                    RemindTomatoActivity.this.iv_add_note_delay.setImageResource(R.drawable.ic_right_arrow);
                    return;
                }
                RemindTomatoActivity.this.rl_remind_rest_delay_items.setVisibility(View.VISIBLE);
                RemindTomatoActivity.this.iv_add_note_delay.setImageResource(R.drawable.ic_down_arrow);
            } else if (id == R.id.btn_add_note_rest) {
                RemindTomatoActivity.this.clickStart();
            } else if (id == R.id.btn_remind_rest_delay1) {
                RemindTomatoActivity.this.clickDelay(3);
            } else if (id == R.id.btn_remind_rest_delay2) {
                RemindTomatoActivity.this.clickDelay(5);
            } else if (id == R.id.btn_remind_rest_delay3) {
                RemindTomatoActivity.this.clickDelay(8);
            } else if (id == R.id.iv_remind_rest_drag_down || id == R.id.rl_remind_rest_convert_to_counter) {
                if (RemindTomatoActivity.this.btn_remind_rest_covert_to_counter.isShown()) {
                    RemindTomatoActivity.this.btn_remind_rest_covert_to_counter.setVisibility(View.GONE);
                    RemindTomatoActivity.this.iv_remind_rest_drag_down.setImageResource(R.drawable.sel_down_arrow);
                    return;
                }
                RemindTomatoActivity.this.btn_remind_rest_covert_to_counter.setVisibility(View.VISIBLE);
                RemindTomatoActivity.this.iv_remind_rest_drag_down.setImageResource(R.drawable.sel_up_arrow);
            } else if (id == R.id.btn_remind_rest_covert_to_counter) {
                RemindTomatoActivity.this.clickConvertToCounter();
            } else if (id == R.id.tv_remind_tomato_label) {
                Intent it = new Intent(RemindTomatoActivity.this.context, LabelSelectActivity.class);
                if (RemindTomatoActivity.this.checkActId == null || RemindTomatoActivity.this.checkActId.length() <= 0) {
                    it.putExtra("actType", 20);
                } else {
                    int actType = DbUtils.queryActTypeById(RemindTomatoActivity.this.context, RemindTomatoActivity.this.checkActId).intValue();
                    if (actType == 11) {
                        actType = 10;
                    }
                    it.putExtra("actType", actType);
                }
                RemindTomatoActivity.this.startActivityForResult(it, 25);
            }
        }
    };
    RelativeLayout rl_delay;
    RelativeLayout rl_remind_rest_convert_to_counter;
    RelativeLayout rl_remind_rest_delay_items;
    HashMap<Integer, TempActBean> tempActMap = null;
    TextView tv_remind_tomato_label;
    TextView tv_remind_tomato_message;
    TextView tv_remind_tomato_title;

    class DeliverBean {
        double isClearPre = 0.0d;
        double rangeMin;
        String startTime;

        public DeliverBean(String startTime, double rangeMin, int isClearPre) {
            this.startTime = startTime;
            this.rangeMin = rangeMin;
            this.isClearPre = (double) isClearPre;
        }
    }

    class TempActBean {
        String actName;
        String color;
        int id;
        String image;

        public TempActBean(int id, String actName, String image, String color) {
            this.id = id;
            this.actName = actName;
            this.image = image;
            this.color = color;
        }

        public int getId() {
            return this.id;
        }

        public String getActName() {
            return this.actName;
        }

        public String getImage() {
            return this.image;
        }

        public String getColor() {
            return this.color;
        }
    }

    public static void startActivityLastTomato(Context context, String action, String startTime, double rangeMin, int isClearPre) {
        Intent it = new Intent(context, RemindTomatoActivity.class);
        it.setAction(action);
        it.putExtra("startTime", startTime);
        it.putExtra("rangeMin", rangeMin);
        it.putExtra("isClearPre", isClearPre);
        context.startActivity(it);
    }

    public static void startActivity(Context context, String action, String startTime, double rangeMin) {
        Intent it = new Intent(context, RemindTomatoActivity.class);
        it.setAction(action);
        it.putExtra("startTime", startTime);
        it.putExtra("rangeMin", rangeMin);
        context.startActivity(it);
    }

    public static void startActivityNewTask(Context context, String action, String startTime, double rangeMin) {
        Intent it = new Intent(context, RemindTomatoActivity.class);
        it.setAction(action);
        it.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
        logfile("startActivityNewTask番茄结束" + action);
    }

    private void setBean() {
        Intent it = getIntent();
        this.deliverBean = new DeliverBean(it.getStringExtra("startTime"), it.getDoubleExtra("rangeMin", 0.0d), it.getIntExtra("isClearPre", 0));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.action = getIntent().getAction();
        logfile("onCreate");
        log(this.action);
        if (this.action == null) {
            logfile("onCreate finish");
            finish();
            return;
        }
        setBean();
        logfile("onCreate setBean");
        PowerMangerUtils.acquireWhenRemind(this.context);
        if (!isHasDeliver()) {
            playRing_v2();
        }
        setContentView(R.layout.activity_remind_tomato_time_out);
        initView();
        initUI();
        if (!isHasDeliver()) {
            vibrate();
        }
        PowerMangerUtils.releaseAfterRemind(this.context);
    }

    private void initView() {
        this.inflater = getLayoutInflater();
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.iv_add_note_delay = (ImageView) findViewById(R.id.iv_add_note_delay);
        this.iv_remind_rest_drag_down = (ImageView) findViewById(R.id.iv_remind_rest_drag_down);
        this.tv_remind_tomato_title = (TextView) findViewById(R.id.tv_remind_tomato_title);
        this.tv_remind_tomato_message = (TextView) findViewById(R.id.tv_remind_tomato_message);
        this.tv_remind_tomato_label = (TextView) findViewById(R.id.tv_remind_tomato_label);
        this.btn_add_note_rest = (Button) findViewById(R.id.btn_add_note_rest);
        this.btn_add_note_delay = (Button) findViewById(R.id.btn_add_note_delay);
        this.btn_remind_rest_delay1 = (Button) findViewById(R.id.btn_remind_rest_delay1);
        this.btn_remind_rest_delay2 = (Button) findViewById(R.id.btn_remind_rest_delay2);
        this.btn_remind_rest_delay3 = (Button) findViewById(R.id.btn_remind_rest_delay3);
        this.btn_remind_rest_covert_to_counter = (Button) findViewById(R.id.btn_remind_rest_covert_to_counter);
        this.rl_remind_rest_delay_items = (RelativeLayout) findViewById(R.id.rl_remind_rest_delay_items);
        this.hs_remind_tomato_items = (HorizontalScrollView) findViewById(R.id.hs_remind_tomato_items);
        this.ll_remind_tomato_items = (LinearLayout) findViewById(R.id.ll_remind_tomato_items);
        this.rl_remind_rest_convert_to_counter = (RelativeLayout) findViewById(R.id.rl_remind_rest_convert_to_counter);
        this.rl_delay = (RelativeLayout) findViewById(R.id.rl_delay);
        this.de_rating_star = (DeliberateStarView) findViewById(R.id.de_rating_star);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.btn_add_note_rest.setOnClickListener(this.myClickListener);
        this.btn_add_note_delay.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay1.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay2.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay3.setOnClickListener(this.myClickListener);
        this.iv_remind_rest_drag_down.setOnClickListener(this.myClickListener);
        this.rl_remind_rest_convert_to_counter.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_covert_to_counter.setOnClickListener(this.myClickListener);
        this.tv_remind_tomato_label.setOnClickListener(this.myClickListener);
    }

    private boolean isHasDeliver() {
        if (this.deliverBean == null || this.deliverBean.rangeMin <= 0.0d) {
            return false;
        }
        return true;
    }

    private void playRing_v2() {
        if (PreferUtils.getSP(this.context).getInt(Val.CONFIGURE_TOMATO_IS_RING, 1) <= 0) {
            return;
        }
        if (Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT.equals(this.action)) {
            if (PreferUtils.getSP(this).getInt(Val.CONFIGURE_TOMATO_IS_STUDY_TIME_OUT_RING, 1) > 0) {
                mediaPlay(1);
            }
        } else if (Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT.equals(this.action) && PreferUtils.getSP(this).getInt(Val.CONFIGURE_TOMATO_IS_REST_TIME_OUT_RING, 1) > 0) {
            mediaPlay(2);
        }
    }

    private void vibrate() {
        if (PreferUtils.getSP(this).getInt(Val.CONFIGURE_TOMATO_IS_VIBRATOR, 1) > 0) {
            VibratorHelper.getInstance(this).vibrateTwice();
        }
    }

    private void mediaPlay(int type) {
        String uriStr;
        if (type == 1) {
            uriStr = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_TOMATO_LEARN_TIME_OUT_RINGTONE, "");
            if (uriStr == null || uriStr.length() <= 0) {
//                this.media = MediaPlayer.create(this.context, R.raw.itodayss_tomato_study_time_out);
            } else {
                try {
                    this.media = MediaPlayer.create(this.context, Uri.parse(uriStr));
                    if (this.media == null) {
//                        this.media = MediaPlayer.create(this.context, R.raw.itodayss_tomato_study_time_out);
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(this.context, e);
//                    this.media = MediaPlayer.create(this.context, R.raw.itodayss_tomato_study_time_out);
                }
            }
        } else {
            uriStr = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_TOMATO_REST_TIME_OUT_RINGTONE, "");
            if (uriStr == null || uriStr.length() <= 0) {
//                this.media = MediaPlayer.create(this.context, R.raw.itodayss_tomato_rest_time_out);
            } else {
                try {
                    this.media = MediaPlayer.create(this.context, Uri.parse(uriStr));
                    if (this.media == null) {
//                        this.media = MediaPlayer.create(this.context, R.raw.itodayss_tomato_rest_time_out);
                    }
                } catch (Exception e2) {
                    DbUtils.exceptionHandler(this.context, e2);
//                    this.media = MediaPlayer.create(this.context, R.raw.itodayss_tomato_rest_time_out);
                }
            }
        }
        try {
            if (this.media != null) {
                AudioManager manager = (AudioManager) this.context.getSystemService(AUDIO_SERVICE);
                int mode = manager.getRingerMode();
                int volume = manager.getStreamVolume(3);
                int maxvolume = manager.getStreamMaxVolume(3);
                this.media.setVolume((float) volume, (float) volume);
                this.media.start();
                this.media.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
            }
        } catch (Exception e22) {
            DbUtils.exceptionHandler(e22);
        }
    }

    private void initUI() {
        if (Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT.equals(this.action)) {
            this.tv_remind_tomato_title.setText(getString(R.string.str_tomato_study_time_out_title));
            this.tv_remind_tomato_message.setText(getString(R.string.str_tomato_study_time_out));
            this.btn_add_note_rest.setText(getString(R.string.str_submit));
            this.btn_add_note_delay.setText(getString(R.string.str_overtime));
            this.tv_remind_tomato_message.setVisibility(View.GONE);
            this.hs_remind_tomato_items.setVisibility(View.VISIBLE);
            this.de_rating_star.setVisibility(View.VISIBLE);
            this.ll_remind_tomato_items = new MyGoalItemsLayout((Activity) this.context, this.ll_remind_tomato_items, this.goalItemsIdClickLister).getAddItems();
            logfile("onCreate-initUI 恭喜！您完成番茄时钟，是否提交");
            if (this.deliverBean != null && this.deliverBean.rangeMin > 0.0d) {
                String str = getString(R.string.str_tomato_study_forget);
                String str_time = getString(R.string.str_time);
                str = str.replace(str_time, this.deliverBean.startTime).replace(getString(R.string.str_time_length), FormatUtils.format_0fra(this.deliverBean.rangeMin));
                this.tv_remind_tomato_message.setText(str);
                this.rl_remind_rest_convert_to_counter.setVisibility(8);
                this.rl_delay.setVisibility(View.GONE);
                logfile("onCreate-initUI " + str);
            }
        } else if (Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT.equals(this.action)) {
            this.tv_remind_tomato_title.setText(getString(R.string.str_prompt));
            this.tv_remind_tomato_message.setText(getString(R.string.str_tomato_rest_time_out));
            this.btn_add_note_rest.setText(getString(R.string.str_start));
            this.tv_remind_tomato_message.setVisibility(View.VISIBLE);
            this.hs_remind_tomato_items.setVisibility(View.GONE);
            this.de_rating_star.setVisibility(View.VISIBLE);
            logfile("onCreate-initUI 休息时间结束了，是否开始下一个番茄 ");
        } else {
            logfile("onCreate-initUI else!!!!!!!!!! action:" + this.action);
        }
        if (this.rl_remind_rest_delay_items.getVisibility() == View.VISIBLE) {
            this.iv_add_note_delay.setImageResource(R.drawable.ic_down_arrow);
            this.rl_remind_rest_delay_items.setVisibility(View.VISIBLE);
            return;
        }
        this.iv_add_note_delay.setImageResource(R.drawable.ic_right_arrow);
        this.rl_remind_rest_delay_items.setVisibility(View.GONE);
    }

    private void clickConvertToCounter() {
        String start = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        String countingActId;
        Intent it;
        if (Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT.equals(this.action)) {
            if (this.checkActId == null || this.checkActId.length() == 0) {
                GeneralUtils.toastShort(this.context, getString(R.string.str_please_choose_type));
                return;
            } else if (TimerService.timer != null) {
                countingActId = DbUtils.queryActId(this.context);
                it = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                it.putExtra("startTime", start);
                it.putExtra("tomatoGoalId", this.checkActId);
                it.putExtra("id", countingActId);
                sendBroadcast(it);
            } else {
                it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                it.putExtra("startTime", start);
                it.putExtra("id", this.checkActId);
                sendBroadcast(it);
            }
        } else if (Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT.equals(this.action)) {
            if (TimerService.timer != null) {
                countingActId = DbUtils.queryActId(this.context);
                it = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                it.putExtra("startTime", start);
                it.putExtra("tomatoGoalId", DbUtils.queryActId(this.context, "20"));
                it.putExtra("id", countingActId);
                sendBroadcast(it);
            } else {
                it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                it.putExtra("startTime", start);
                it.putExtra("id", DbUtils.queryActId(this.context, "20"));
                sendBroadcast(it);
            }
        }
        TomatoActivity.resetPre(this.context);
        finish();
    }

    private void clickDelay(int minute) {
        logfile("clickDelay-点击延迟minute:" + minute);
        SharedPreferences sp = PreferUtils.getSP(this.context);
        int delaySecond = sp.getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
        int defualtMinute = 0;
        if (Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT.equals(this.action)) {
            defualtMinute = sp.getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25);
        } else if (Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT.equals(this.action)) {
            defualtMinute = sp.getInt(Val.CONFIGURE_TOMATO_REST_TIME, 5);
        }
        String startTime = sp.getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        if (startTime.length() > 0) {
            int actualSecond = DateTime.cal_secBetween(startTime, DateTime.getTimeString());
            if (actualSecond > 0) {
                delaySecond = actualSecond - (defualtMinute * 60);
            }
        }
        sp.edit().putInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, (minute * 60) + delaySecond).commit();
        logfile("clickDelay : minute:" + minute + " ,defualtMinute :" + defualtMinute);
        TomatoController.getTomatoController(this.context).startTomato(minute * 60, 0);
        finish();
    }

    private void clickStart() {
        logfile("clickStart-点击提交");
        SharedPreferences sp = PreferUtils.getSP(this.context);
        int delaySecond = sp.getInt(Val.CONFIGURE_TOMATO_DELAY_SECOND, 0);
        int studyMinute = sp.getInt(Val.CONFIGURE_TOMATO_STUDY_TIME, 25);
        int restMinute = sp.getInt(Val.CONFIGURE_TOMATO_REST_TIME, 5);
        String start = sp.getString(Val.CONFIGURE_TOMATO_START_TIME, "");
        if (this.deliverBean != null && this.deliverBean.rangeMin > 0.0d) {
            start = this.deliverBean.startTime;
        }
        logfile("clickStart-action:" + this.action + ",start:" + start + ",delaySecond:" + delaySecond + ",studyMinute:" + studyMinute + ",restMinute:" + restMinute);
        Calendar c;
        long tempItemsId;
        if (Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT.equals(this.action)) {
            if (this.checkActId == null || this.checkActId.length() == 0) {
                GeneralUtils.toastShort(this.context, getString(R.string.str_please_choose_type));
                logfile("clickStart-return checkActId:" + this.checkActId);
                return;
            } else if (start.length() > 0) {
                String endTime;
                if (this.deliverBean == null || this.deliverBean.rangeMin <= 0.0d) {
                    c = DateTime.pars2Calender(start);
                    c.add(Calendar.SECOND, (studyMinute * 60) + delaySecond);
                    endTime = DateTime.formatTime(c);
                    logfile("clickStart-保存番茄时间");
                    tempItemsId = DbUtils.addTimeAndSave(this.context, start, endTime, 3, this.checkActId);
                    TomatoActivity.setPre(this.context, restMinute * 60, 2, Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT);
                    TomatoController.getTomatoController(this.context).startTomato(restMinute * 60, 0);
                    log(getClass().getSimpleName() + " clickStart action:" + this.action + ", starttime:" + start + ",restMinute:" + restMinute);
                } else {
                    c = DateTime.pars2Calender(start);
                    c.add(Calendar.SECOND, (int) (this.deliverBean.rangeMin * 60.0d));
                    endTime = DateTime.formatTime(c);
                    logfile("clickStart-保存未提交番茄");
                    tempItemsId = DbUtils.addTimeAndSave(this.context, start, endTime, 3, this.checkActId);
                    DbUtils.deleteUnhandlerTomato(this.context, start);
                    if (this.deliverBean.isClearPre == 1.0d) {
                        TomatoActivity.resetPre(this.context);
                    }
                }
                try {
                    if (this.labelId > 0) {
                        addLabelLink((int) tempItemsId);
                    }
                } catch (NumberFormatException e) {
                    DbUtils.exceptionHandler(e);
                }
                try {
                    addDeliberateRecord(tempItemsId);
                } catch (Exception e2) {
                    DbUtils.exceptionHandler(e2);
                }
            } else {
                logfile("clickStart-开始时间为空串，无法开始 :" + start + "!!!!!!!!!!!!!");
                ToastUtils.toastShort(this.context, "出错啦！RemindTomato clickStart start" + start);
            }
        } else if (Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT.equals(this.action)) {
            if (start.length() > 0) {
                String goalId = DbUtils.queryActId(this.context, "20");
                c = DateTime.pars2Calender(start);
                c.add(Calendar.SECOND, (restMinute * 60) + delaySecond);
                tempItemsId = DbUtils.addTimeAndSave(this.context, start, DateTime.formatTime(c), 3, goalId);
                logfile("clickStart-保存学习番茄时间");
                try {
                    addLabelLink((int) tempItemsId);
                } catch (NumberFormatException e3) {
                    DbUtils.exceptionHandler(e3);
                }
            } else {
                logfile("clickStart-未保存番茄时间,start:" + start);
            }
            logfile("clickStart-开始新番茄,studyMinute:" + (studyMinute * 60) + "，Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT：" + Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT);
            TomatoActivity.setPre(this.context, studyMinute * 60, 1, Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT);
            TomatoController.getTomatoController(this.context).startTomato(studyMinute * 60, 0);
            logfile(" clickStart action:" + this.action + ", starttime:" + start + ",restMinute:" + restMinute);
        }
        logfile("clickStart action:" + this.action + ", starttime:" + start + ",deliverBean:" + this.deliverBean);
        onBackPressed();
    }

    private void addDeliberateRecord(long itemsId) {
        float rating_goal = this.de_rating_star.getRatingByGoal();
        float rating_feedback = this.de_rating_star.getRatingByFeedback();
        float rating_focus = this.de_rating_star.getRatingByFocus();
        float rating_uncomfortable = this.de_rating_star.getRatingByUncomforable();
        String total_val_str = FormatUtils.format_2fra(this.de_rating_star.getTotalRating());
        ContentValues values = new ContentValues();
        values.put("itemsId", Long.valueOf(itemsId));
        values.put("userId", DbUtils.queryUserId(this.context));
        values.put("keyVal1", Float.valueOf(rating_goal));
        values.put("keyVal2", Float.valueOf(rating_feedback));
        values.put("keyVal3", Float.valueOf(rating_focus));
        values.put("keyVal4", Float.valueOf(rating_uncomfortable));
        values.put("totalVal", total_val_str);
        values.put("createTime", DateTime.getTimeString());
        values.put("sDeliberateRecordId", Integer.valueOf(0));
        values.put("isDelete", Integer.valueOf(0));
        values.put("isUpload", Integer.valueOf(0));
        DbUtils.getDb(this.context).insert("t_deliberate_record", null, values);
    }

    private void addLabelLink(int itemsId) {
        if (this.labelId > 0) {
            String id = this.labelId + "";
            int take = 0;
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select take from t_act_item where id is " + itemsId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                take = cursor.getInt(cursor.getColumnIndex("take"));
            }
            DbUtils.close(cursor);
            if (take > 0) {
                Cursor cursor5 = DbUtils.getDb(this.context).rawQuery("select Id from t_routine_link where itemsId is " + itemsId + " and subTypeId is " + id, null);
                if (cursor5.getCount() > 0) {
                    GeneralUtils.toastShort(this.context, getResources().getString(R.string.str_prompt_this_record_had_add_this_label));
                    DbUtils.close(cursor5);
                    finish();
                    return;
                }
                DbUtils.close(cursor5);
                ContentValues values = new ContentValues();
                values.put("userId", DbUtils.queryUserId(this.context));
                values.put("itemsId", Integer.valueOf(itemsId));
                values.put("subTypeId", id);
                values.put("take", Integer.valueOf(take));
                Cursor cursor33 = DbUtils.getDb(this.context).rawQuery("select * from t_act_item where id is " + itemsId, null);
                if (cursor33.getCount() > 0) {
                    cursor33.moveToNext();
                    values.put("goalId", Integer.valueOf(cursor33.getInt(cursor33.getColumnIndex("actId"))));
                    values.put("goalType", Integer.valueOf(cursor33.getInt(cursor33.getColumnIndex("actType"))));
                }
                DbUtils.close(cursor33);
                values.put("time", DateTime.getTimeString());
                DbUtils.getDb(this.context).insert("t_routine_link", null, values);
                values = new ContentValues();
                values.put("lastUseTime", DateTime.getTimeString());
                DbUtils.getDb(this.context).update("t_sub_type", values, " id is ? ", new String[]{id});
                Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select * from t_sub_type where id is " + id, null);
                if (cursor2.getCount() > 0) {
                    cursor2.moveToNext();
                    String LabelName = cursor2.getString(cursor2.getColumnIndex(IDemoChart.NAME));
                    String remarksLocal = "";
                    Cursor cursor3 = DbUtils.getDb(this.context).rawQuery("select remarks from t_act_item where id is " + itemsId, null);
                    if (cursor3.getCount() > 0) {
                        cursor3.moveToNext();
                        remarksLocal = cursor3.getString(cursor3.getColumnIndex("remarks"));
                    }
                    DbUtils.close(cursor3);
                    ContentValues values2 = new ContentValues();
                    if (remarksLocal != null) {
                        values2.put("remarks", remarksLocal + " [" + getResources().getString(R.string.str_label) + ":" + LabelName + "]");
                    } else {
                        values2.put("remarks", "[" + getResources().getString(R.string.str_label) + ":" + LabelName + "]");
                    }
                    DbUtils.getDb(this.context).update("t_act_item", values2, " id is ? ", new String[]{"" + itemsId});
                }
                DbUtils.close(cursor2);
            }
        }
    }

    private LinearLayout getAddItems(LinearLayout ll) {
        this.tempActMap = new HashMap();
        Cursor cur = DbUtils.getDb(this.context).rawQuery(Sql.GoalsList(this.context), null);
        if (cur.getCount() > 0) {
            ll.removeAllViews();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("id"));
                if (Act2TypeMap == null || Act2TypeMap.get(id) == null) {
                    Act2TypeMap = getAct2TypeMap();
                }
                if (cur.getCount() <= 4 || ((Integer) Act2TypeMap.get(Integer.valueOf(Integer.parseInt(id)))).intValue() != 10) {
                    String actName = cur.getString(cur.getColumnIndex("actName"));
                    String image = cur.getString(cur.getColumnIndex("image"));
                    String color = cur.getString(cur.getColumnIndex("color"));
                    this.tempActMap.put(Integer.valueOf(Integer.parseInt(id)), new TempActBean(Integer.parseInt(id), actName, image, color));
                    ll.addView(getAddActItems(id, actName, image, color));
                }
            }
        }
        DbUtils.close(cur);
        return ll;
    }

    private RelativeLayout getAddActItems(String id, String name, String label, String color) {
        RelativeLayout rl_temp_show_outer = (RelativeLayout) this.inflater.inflate(R.layout.tem_sigle_act, null);
        rl_temp_show_outer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) v.getParent();
                int count = ll.getChildCount();
                for (int i = 0; i < count; i++) {
                    ll.getChildAt(i).setBackgroundColor(RemindTomatoActivity.this.getResources().getColor(R.color.gray));
                }
                v.setBackgroundColor(RemindTomatoActivity.this.getResources().getColor(R.color.black));
                RelativeLayout rl = (RelativeLayout) v;
                RemindTomatoActivity.this.checkActId = ((TextView) rl.getChildAt(0)).getText().toString();
                RemindTomatoActivity.this.log("checkActId:" + RemindTomatoActivity.this.checkActId);
            }
        });
        TextView tv_temp_act_name = (TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_name);
        ImageView iv_temp_color = (ImageView) rl_temp_show_outer.findViewById(R.id.iv_temp_color);
        ((TextView) rl_temp_show_outer.findViewById(R.id.tv_temp_act_id)).setText(id);
        tv_temp_act_name.setText(name);
        iv_temp_color.setImageResource(Val.getLabelIntByName(label));
        iv_temp_color.setBackgroundColor(getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()));
        return rl_temp_show_outer;
    }

    public HashMap<Integer, Integer> getAct2TypeMap() {
        HashMap<Integer, Integer> map = new HashMap();
        Cursor cur = DbUtils.getDb2(this.context).rawQuery("select id,type from t_act where " + DbUtils.getWhereUserId(this.context), null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                map.put(Integer.valueOf(cur.getInt(cur.getColumnIndex("id"))), Integer.valueOf(cur.getInt(cur.getColumnIndex(a.a))));
            }
        }
        DbUtils.close(cur);
        return map;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (25 == requestCode && resultCode == -1) {
            String labelId = data.getStringExtra("labelIdStr");
            this.labelId = Integer.parseInt(labelId);
            this.tv_remind_tomato_label.setText(DbUtils.queryLabelNameByLabelId(this.context, labelId));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onDestroy() {
        try {
            VibratorHelper.getInstance(this).cancel();
        } catch (Exception e) {
            DbUtils.exceptionHandler(this.context, e);
        }
        try {
            if (this.media != null) {
                this.media.release();
            }
        } catch (Exception e2) {
            DbUtils.exceptionHandler(this.context, e2);
        }
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void log(String str) {
        MyLog.d(getClass().getSimpleName(), str);
    }

    public static void logfile(String str) {
        LogUtils.logfile(RemindTomatoActivity.class.getSimpleName() + ":" + str);
    }
}
