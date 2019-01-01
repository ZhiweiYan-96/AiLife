package com.record.utils.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;
import com.record.myLife.R;
import com.record.utils.Val;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class Sound {
    int SOUND_INTERVAL;
    int SOUND_INTERVAL2;
    int SOUND_START_COUNTER;
    Context context;
    SoundPool soundPool;

    public Sound(Context context) {
        this.context = context;
        initSoundPool();
    }

    public void tickOnStart() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Sound.this.isRingOrShake();
            }
        }, 300);
    }

    private void isRingOrShake() {
        if (this.context.getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_IS_RING_WHILE_START_COUNTER, 1) == 1) {
            this.soundPool.play(this.SOUND_START_COUNTER, 1.0f, 1.0f, 0, 0, 1.0f);
        }
        if (this.context.getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_IS_SHAKE_WHILE_START_COUNTER, 1) == 1) {
            ((Vibrator) this.context.getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{100, 200, 500}, -1);
        }
    }

    private void initSoundPool() {
        if (this.soundPool == null) {
            this.soundPool = new SoundPool(10, 1, 5);
//            this.SOUND_INTERVAL = this.soundPool.load(this.context, R.raw.itodayss_strike_one, 1);
//            this.SOUND_INTERVAL2 = this.soundPool.load(this.context, R.raw.itodayss_strike, 1);
//            this.SOUND_START_COUNTER = this.soundPool.load(this.context, R.raw.tick_v2, 1);
        }
    }

    private void getSysMusic() {
        AudioManager manager = (AudioManager) this.context.getSystemService(AUDIO_SERVICE);
        manager.getRingerMode();
        manager.getStreamVolume(3);
    }

    public boolean isMute() {
        if (((AudioManager) this.context.getSystemService(AUDIO_SERVICE)).getStreamVolume(3) == 0) {
            return true;
        }
        return false;
    }
}
