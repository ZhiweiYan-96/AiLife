package com.record.myLife.settings.remind;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.record.bean.Act;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.utils.GeneralUtils;
import com.record.utils.PreferUtils;
import com.record.utils.RemindUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RemindRestActivity extends BaseActivity {
    String Date = "";
    Button btn_add_note_delay;
    Button btn_add_note_rest;
    Button btn_remind_rest_delay1;
    Button btn_remind_rest_delay2;
    Button btn_remind_rest_delay3;
    Context context;
    ImageView iv_add_note_close;
    ImageView iv_add_note_delay;
    MediaPlayer media = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_add_note_close) {
                GeneralUtils.toastLong(RemindRestActivity.this.context, "本次学习将不再提示休息！");
                RemindRestActivity.this.finish();
            } else if (id == R.id.btn_add_note_delay) {
                if (RemindRestActivity.this.rl_remind_rest_delay_items.getVisibility() == VISIBLE) {
                    RemindRestActivity.this.rl_remind_rest_delay_items.setVisibility(GONE);
                    RemindRestActivity.this.iv_add_note_delay.setImageResource(R.drawable.ic_right_arrow);
                    return;
                }
                RemindRestActivity.this.rl_remind_rest_delay_items.setVisibility(VISIBLE);
                RemindRestActivity.this.iv_add_note_delay.setImageResource(R.drawable.ic_down_arrow);
            } else if (id == R.id.btn_add_note_rest) {
                Intent it2 = new Intent(Val.INTENT_ACTION_STOP_COUNTER);
                Act act = Act.getInstance();
                if (act != null) {
                    it2.putExtra("id", act.getId() + "");
                }
                RemindRestActivity.this.sendBroadcast(it2);
                Intent it = new Intent(Val.INTENT_ACTION_START_COUNTER);
                it.putExtra("id", DbUtils.queryActId(RemindRestActivity.this.context, "20"));
                it.putExtra("isFromRemindRestActivity", 1);
                RemindRestActivity.this.sendBroadcast(it);
                RemindRestActivity.this.finish();
            } else if (id == R.id.btn_remind_rest_delay1) {
                GeneralUtils.toastLong(RemindRestActivity.this.context, "将在3分钟后提醒休息哦！");
                RemindUtils.setRemindRest(RemindRestActivity.this.context, 3);
                RemindRestActivity.this.finish();
            } else if (id == R.id.btn_remind_rest_delay2) {
                GeneralUtils.toastLong(RemindRestActivity.this.context, "将在5分钟后提醒休息哦！");
                RemindUtils.setRemindRest(RemindRestActivity.this.context, 5);
                RemindRestActivity.this.finish();
            } else if (id == R.id.btn_remind_rest_delay3) {
                GeneralUtils.toastLong(RemindRestActivity.this.context, "将在8分钟后提醒休息哦！");
                RemindUtils.setRemindRest(RemindRestActivity.this.context, 8);
                RemindRestActivity.this.finish();
            }
        }
    };
    RelativeLayout rl_remind_rest_delay_items;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        if (PreferUtils.getSP(this).getInt(Val.CONFIGURE_REMIND_REST_CLASS_OVER, 0) > 0) {
            playRing_v2();
        }
        setContentView(R.layout.activity_remind_rest);
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.iv_add_note_delay = (ImageView) findViewById(R.id.iv_add_note_delay);
        this.btn_add_note_rest = (Button) findViewById(R.id.btn_add_note_rest);
        this.btn_add_note_delay = (Button) findViewById(R.id.btn_add_note_delay);
        this.btn_remind_rest_delay1 = (Button) findViewById(R.id.btn_remind_rest_delay1);
        this.btn_remind_rest_delay2 = (Button) findViewById(R.id.btn_remind_rest_delay2);
        this.btn_remind_rest_delay3 = (Button) findViewById(R.id.btn_remind_rest_delay3);
        this.rl_remind_rest_delay_items = (RelativeLayout) findViewById(R.id.rl_remind_rest_delay_items);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.btn_add_note_rest.setOnClickListener(this.myClickListener);
        this.btn_add_note_delay.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay1.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay2.setOnClickListener(this.myClickListener);
        this.btn_remind_rest_delay3.setOnClickListener(this.myClickListener);
        initUI();
        try {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{100, 200, 500, 200, 100}, -1);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void playRing() {
//        SoundPool soundPool = new SoundPool(10, 1, 5);
//        final int SOUND_INTERVAL = soundPool.load(this.context, R.raw.itodayss_class_down_bell, 1);
//        log("休息响铃ID：" + SOUND_INTERVAL);
//        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                soundPool.play(SOUND_INTERVAL, 1.0f, 1.0f, 0, 0, 1.0f);
//                RemindRestActivity.this.log("休息响铃啦");
//            }
//        });
    }

    private void playRing_v2() {
        try {
            String uriStr = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_REMIND_REST_CLASS_OVER_RING, "");
            if (uriStr == null || uriStr.length() <= 0) {
//                this.media = MediaPlayer.create(this.context, R.raw.itodayss_class_down_bell);
            } else {
                try {
                    this.media = MediaPlayer.create(this.context, Uri.parse(uriStr));
                } catch (Exception e) {
                    DbUtils.exceptionHandler(this.context, e);
//                    this.media = MediaPlayer.create(this.context, R.raw.itodayss_class_down_bell);
                }
            }
            if (this.media == null) {
//                this.media = MediaPlayer.create(this.context, R.raw.itodayss_class_down_bell);
            }
            this.media.start();
            this.media.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
        }
    }

    private void initUI() {
        if (this.rl_remind_rest_delay_items.getVisibility() == VISIBLE) {
            this.iv_add_note_delay.setImageResource(R.drawable.ic_down_arrow);
            this.rl_remind_rest_delay_items.setVisibility(VISIBLE);
            return;
        }
        this.iv_add_note_delay.setImageResource(R.drawable.ic_right_arrow);
        this.rl_remind_rest_delay_items.setVisibility(GONE);
    }

    public void onBackPressed() {
        GeneralUtils.toastLong(this.context, "本次学习将不再提醒休息！");
        super.onBackPressed();
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
