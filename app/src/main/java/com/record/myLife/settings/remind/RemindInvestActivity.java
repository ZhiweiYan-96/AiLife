package com.record.myLife.settings.remind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.Act;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.MyGoalItemsLayout;
import com.record.myLife.view.MyGoalItemsLayout.MyOnItemsClickListener;
import com.record.utils.GeneralUtils;
import com.record.utils.PreferUtils;
import com.record.utils.RemindUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

public class RemindInvestActivity extends BaseActivity {
    String Date = "";
    Button btn_add_note_delay;
    Button btn_add_note_rest;
    Button btn_remind_invest_start_learn;
    Button btn_remind_rest_delay1;
    Button btn_remind_rest_delay2;
    Button btn_remind_rest_delay3;
    String checkActId = "";
    Context context;
    MyOnItemsClickListener goalItemsIdClickLister = new MyOnItemsClickListener() {
        public void onClick(View v, String id) {
            RemindInvestActivity.this.checkActId = id;
            RemindInvestActivity.this.log("checkActId:" + RemindInvestActivity.this.checkActId);
        }
    };
    LayoutInflater inflater;
    ImageView iv_add_note_close;
    ImageView iv_add_note_delay;
    ImageView iv_add_note_rest;
    LinearLayout ll_tem_time_items;
    MediaPlayer media = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_add_note_close) {
                GeneralUtils.toastLong(RemindInvestActivity.this.context, "本次将不再提示!");
                RemindInvestActivity.this.finish();
            } else if (id == R.id.btn_add_note_delay) {
                if (RemindInvestActivity.this.rl_remind_rest_delay_items.getVisibility() == View.VISIBLE) {
                    RemindInvestActivity.this.rl_remind_rest_delay_items.setVisibility(View.GONE);
                    RemindInvestActivity.this.iv_add_note_delay.setImageResource(R.drawable.ic_right_arrow);
                    return;
                }
                RemindInvestActivity.this.rl_remind_rest_delay_items.setVisibility(View.VISIBLE);
                RemindInvestActivity.this.iv_add_note_delay.setImageResource(R.drawable.ic_down_arrow);
                RemindInvestActivity.this.rl_remind_invest_items.setVisibility(View.GONE);
            } else if (id == R.id.btn_add_note_rest) {
                if (RemindInvestActivity.this.rl_remind_invest_items.getVisibility() == View.VISIBLE) {
                    RemindInvestActivity.this.rl_remind_invest_items.setVisibility(View.GONE);
                    RemindInvestActivity.this.iv_add_note_rest.setImageResource(R.drawable.ic_right_arrow);
                    return;
                }
                RemindInvestActivity.this.rl_remind_invest_items.setVisibility(View.VISIBLE);
                RemindInvestActivity.this.iv_add_note_rest.setImageResource(R.drawable.ic_down_arrow);
                RemindInvestActivity.this.rl_remind_rest_delay_items.setVisibility(View.GONE);
            } else if (id == R.id.btn_remind_invest_start_learn) {
                if (RemindInvestActivity.this.checkActId.length() == 0) {
                    GeneralUtils.toastShort(RemindInvestActivity.this.context, "请先选择投入目标哦！");
                    return;
                }
                RemindInvestActivity.this.log("选择id为：" + RemindInvestActivity.this.checkActId);
                if (DbUtils.getDb(RemindInvestActivity.this.context).rawQuery("select id from t_act_item where " + DbUtils.getWhereUserId(RemindInvestActivity.this.context) + " and isEnd is not 1", null).getCount() > 0) {
                    Intent it2 = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                    Act act = Act.getInstance();
                    if (act != null) {
                        it2.putExtra("id", act.getId() + "");
                    }
                    RemindInvestActivity.this.sendBroadcast(it2);
                }
                Intent it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                it.putExtra("id", RemindInvestActivity.this.checkActId);
                RemindInvestActivity.this.sendBroadcast(it);
                RemindInvestActivity.this.finish();
            } else if (id == R.id.btn_remind_rest_delay1) {
                GeneralUtils.toastLong(RemindInvestActivity.this.context, "将在5分钟后提醒学习哦！");
                RemindUtils.setRemindInvest(RemindInvestActivity.this.context, 5);
                RemindInvestActivity.this.finish();
            } else if (id == R.id.btn_remind_rest_delay2) {
                GeneralUtils.toastLong(RemindInvestActivity.this.context, "将在10分钟后提醒学习哦！");
                RemindUtils.setRemindInvest(RemindInvestActivity.this.context, 10);
                RemindInvestActivity.this.finish();
            } else if (id == R.id.btn_remind_rest_delay3) {
                GeneralUtils.toastLong(RemindInvestActivity.this.context, "将在30分钟后提醒学习哦！");
                RemindUtils.setRemindInvest(RemindInvestActivity.this.context, 30);
                RemindInvestActivity.this.finish();
            }
        }
    };
    RelativeLayout rl_remind_invest_items;
    RelativeLayout rl_remind_rest_delay_items;
    TextView tv_remind_invest_content;
    TextView tv_remind_invest_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        if (PreferUtils.getSP(this).getInt(Val.CONFIGURE_REMIND_REST_CLASS_START, 0) > 0) {
            playRing_v2();
        }
        setContentView(R.layout.activity_remind_invest);
        this.inflater = getLayoutInflater();
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.iv_add_note_delay = (ImageView) findViewById(R.id.iv_add_note_delay);
        this.iv_add_note_rest = (ImageView) findViewById(R.id.iv_add_note_rest);
        this.btn_add_note_rest = (Button) findViewById(R.id.btn_add_note_rest);
        this.btn_add_note_delay = (Button) findViewById(R.id.btn_add_note_delay);
        this.btn_remind_rest_delay1 = (Button) findViewById(R.id.btn_remind_rest_delay1);
        this.btn_remind_rest_delay2 = (Button) findViewById(R.id.btn_remind_rest_delay2);
        this.btn_remind_rest_delay3 = (Button) findViewById(R.id.btn_remind_rest_delay3);
        this.btn_remind_invest_start_learn = (Button) findViewById(R.id.btn_remind_invest_start_learn);
        this.tv_remind_invest_content = (TextView) findViewById(R.id.tv_remind_invest_content);
        this.tv_remind_invest_title = (TextView) findViewById(R.id.tv_remind_invest_title);
        this.rl_remind_rest_delay_items = (RelativeLayout) findViewById(R.id.rl_remind_rest_delay_items);
        this.rl_remind_invest_items = (RelativeLayout) findViewById(R.id.rl_remind_invest_items);
        this.ll_tem_time_items = (LinearLayout) findViewById(R.id.ll_tem_time_items);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.btn_add_note_rest.setOnClickListener(this.myClickListener);
        this.btn_add_note_delay.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay1.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay2.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay3.setOnClickListener(this.myClickListener);
        this.btn_remind_invest_start_learn.setOnClickListener(this.myClickListener);
        initUI();
        try {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{100, 200, 500, 200, 100}, -1);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void playRing() {
        SoundPool soundPool = new SoundPool(10, 1, 5);
//        final int SOUND_INTERVAL = soundPool.load(this.context, R.raw.itodayss_class_start_bell, 1);
//        log("响铃ID：" + SOUND_INTERVAL);
//        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                soundPool.play(SOUND_INTERVAL, 0.5f, 0.5f, 0, 0, 1.0f);
//                RemindInvestActivity.this.log("响铃啦！");
//            }
//        });
    }

    private void playRing_v2() {
        try {
            String uriStr = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_REMIND_REST_CLASS_START_RING, "");
            if (uriStr == null || uriStr.length() <= 0) {
//                this.media = MediaPlayer.create(this.context, R.raw.itodayss_class_start_bell);
            } else {
                try {
                    this.media = MediaPlayer.create(this.context, Uri.parse(uriStr));
                } catch (Exception e) {
                    DbUtils.exceptionHandler(this.context, e);
//                    this.media = MediaPlayer.create(this.context, R.raw.itodayss_class_start_bell);
                }
            }
            if (this.media == null) {
//                this.media = MediaPlayer.create(this.context, R.raw.itodayss_class_start_bell);
            }
            this.media.start();
            this.media.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        } catch (Exception e2) {
            DbUtils.exceptionHandler(this.context, e2);
        }
    }

    private void initUI() {
        this.tv_remind_invest_title.setText("开始学习");
        this.tv_remind_invest_content.setText("休息完毕，精力恢复，是否开始投入学习？");
        this.btn_add_note_rest.setText("学习");
        new MyGoalItemsLayout((Activity) this.context, this.ll_tem_time_items, this.goalItemsIdClickLister).getAddItems();
        this.rl_remind_invest_items.setVisibility(View.GONE);
        this.rl_remind_rest_delay_items.setVisibility(View.GONE);
    }

    protected void onDestroy() {
        try {
            if (this.media != null) {
                this.media.release();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(this.context, e);
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        GeneralUtils.toastLong(this.context, "本次将不再提示!");
        super.onBackPressed();
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
        Log.i("override Login", ":" + str);
    }
}
