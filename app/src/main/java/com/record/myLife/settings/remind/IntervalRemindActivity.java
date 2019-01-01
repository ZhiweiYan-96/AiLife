package com.record.myLife.settings.remind;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.utils.DateTime;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class IntervalRemindActivity extends BaseActivity {
    static String TAG = "override";
    Button btn_add_note_content;
    Button btn_add_note_down_count;
    int closeDialogCounter = 0;
    Context context;
    EditText et_add_note_content;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IntervalRemindActivity.this.btn_add_note_down_count.setText(IntervalRemindActivity.this.getString(R.string.str_down_count_close) + IntervalRemindActivity.this.closeDialogCounter);
        }
    };
    ImageView iv_add_note_close;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_add_note_content) {
                IntervalRemindActivity.this.saveAdnFininsh();
            } else if (id == R.id.iv_add_note_close) {
                IntervalRemindActivity.this.closeTimer();
            } else if (id == R.id.btn_add_note_down_count) {
                if (IntervalRemindActivity.this.timer != null) {
                    IntervalRemindActivity.this.timer.cancel();
                    IntervalRemindActivity.this.timer = null;
                }
                IntervalRemindActivity.this.btn_add_note_down_count.setVisibility(View.GONE);
            }
        }
    };
    SharedPreferences sp;
    Timer timer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        TAG += getClass().getSimpleName();
        if (getSp().getInt(Val.CONFIGURE_IS_REMIND_INTERVAL, 0) == 0) {
            finish();
            return;
        }
        Calendar c = Calendar.getInstance();
        if (getSp().getString(Val.CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR, "0,1,2,3,4,5,6,7,23").contains(c.get(Calendar.HOUR_OF_DAY) + "")) {
            finish();
            return;
        }
        SharedPreferences sp = getSp();
        int isShake = sp.getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHAKE, 1);
        int isSound = sp.getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SOUND, 1);
        int isShowDialog = sp.getInt(Val.CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG, 0);
        log("间隔提醒isShake" + isShake + ",isSound:" + isSound + ",isShowDialog:" + isShowDialog);
        if (c.get(Calendar.MINUTE) == 0) {
            if (isSound > 0) {
                playTwoSound();
            }
            if (isShake > 0) {
                vibrateTwice();
            }
        } else {
            if (isSound > 0) {
                playOneSound();
            }
            if (isShake > 0) {
                vibrateOne();
            }
        }
        if (isShowDialog <= 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_interval_remind);
        initFind();
        this.et_add_note_content.setHint(getSp().getString(Val.CONFIGURE_REMIND_INTERVAL_PROMPT, getString(R.string.str_interval_prmopt)).replace("{现在时间}", DateTime.getTimeStr1229()));
        this.closeDialogCounter = 60;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                if (IntervalRemindActivity.this.closeDialogCounter <= 0) {
                    IntervalRemindActivity.this.closeTimer();
                }
                IntervalRemindActivity.this.handler.sendEmptyMessage(1);
                IntervalRemindActivity intervalRemindActivity = IntervalRemindActivity.this;
                intervalRemindActivity.closeDialogCounter--;
            }
        }, 0, 1000);
    }

    private void initFind() {
        this.btn_add_note_content = (Button) findViewById(R.id.btn_add_note_content);
        this.btn_add_note_down_count = (Button) findViewById(R.id.btn_add_note_down_count);
        this.et_add_note_content = (EditText) findViewById(R.id.et_add_note_content);
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.btn_add_note_content.setOnClickListener(this.myClickListener);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.btn_add_note_down_count.setOnClickListener(this.myClickListener);
        this.et_add_note_content.setOnClickListener(this.myClickListener);
    }

    private void closeTimer() {
        try {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAdnFininsh() {
        String time = DateTime.getTimeStr1229();
        String str = this.et_add_note_content.getText().toString();
        if (str != null && str.length() > 0) {
            ContentValues values = new ContentValues();
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time is '" + DateTime.getDateString() + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String lastRemark = cursor.getString(cursor.getColumnIndex("remarks"));
                if (lastRemark == null || lastRemark.length() <= 0) {
                    values.put("remarks", "1," + str + "[" + time + "];");
                    insertOrUpdateDb_allocation(DateTime.getDateString(), values);
                } else {
                    String[] arr = lastRemark.split(";");
                    if (arr != null) {
                        values.put("remarks", lastRemark + ("\n" + (arr.length + 1) + "," + str + "[" + time + "];"));
                        insertOrUpdateDb_allocation(DateTime.getDateString(), values);
                    } else {
                        values.put("remarks", lastRemark + ("\n1," + str + "[" + time + "];"));
                        insertOrUpdateDb_allocation(DateTime.getDateString(), values);
                    }
                }
            } else {
                values.put("remarks", "1," + str + "[" + time + "];");
                insertOrUpdateDb_allocation(DateTime.getDateString(), values);
            }
            DbUtils.close(cursor);
        }
        closeTimer();
    }

    public void insertOrUpdateDb_allocation(String date, ContentValues values) {
        log(values.toString());
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where userid is ? and time is '" + date + "'", new String[]{DbUtils.queryUserId(this.context)});
        if (cursor.getCount() > 0) {
            DbUtils.getDb(this.context).update("t_allocation", values, " userid is ? and  time is  '" + date + "'", new String[]{DbUtils.queryUserId(this.context)});
        } else {
            DbUtils.getDb(this.context).insert("t_allocation", null, values);
        }
        DbUtils.close(cursor);
    }

    private void playTwoSound() {
        SoundPool soundPool = new SoundPool(10, 1, 5);
//        soundPool.load(this.context, R.raw.itodayss_strike, 1);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
                }
            }
        });
    }

    private void playOneSound() {
        SoundPool soundPool = new SoundPool(10, 1, 5);
//        soundPool.load(this.context, R.raw.itodayss_strike_one, 1);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
                }
            }
        });
    }

    private void vibrateOne() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{100, 200, 500}, -1);
    }

    private void vibrateTwice() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{100, 200, 500, 200, 100}, -1);
    }

    public SharedPreferences getSp() {
        if (this.sp == null) {
            this.sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        }
        return this.sp;
    }

    public void onBackPressed() {
        super.onBackPressed();
        closeTimer();
    }

    protected void onDestroy() {
        super.onDestroy();
        closeTimer();
    }

    protected void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
