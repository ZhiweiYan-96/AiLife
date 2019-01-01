package com.record.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.record.thread.BackupDataRunnable;
import com.record.utils.Val;

public class AutoBackupService extends Service {
    BroadcastReceiver OnclickReceiver2;
    Thread autoBackupThread = null;
    Context context;

    class myBroadCastReceiver extends BroadcastReceiver {
        myBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!Val.INTENT_ACTION_AUTO_BACKUP_ERROR.equals(action) && Val.INTENT_ACTION_AUTO_BACKUP_FINISH.equals(action)) {
                AutoBackupService.log("关闭服务onDestroy");
                AutoBackupService.this.stopSelf();
            }
        }
    }

    public IBinder onBind(Intent intent) {
        log("fromAutoBackup:" + intent.getIntExtra("fromAutoBackup", 0));
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.context = this;
        if (this.OnclickReceiver2 == null) {
            this.OnclickReceiver2 = new myBroadCastReceiver();
            registerReceiver(this.OnclickReceiver2, getIntentFilter());
        }
        if (this.autoBackupThread == null || !this.autoBackupThread.isAlive()) {
            this.autoBackupThread = new Thread(new BackupDataRunnable(this.context));
            this.autoBackupThread.start();
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter(Val.INTENT_ACTION_AUTO_BACKUP_ERROR);
        filter.addAction(Val.INTENT_ACTION_AUTO_BACKUP_FINISH);
        return filter;
    }

    public void onDestroy() {
        try {
            if (this.OnclickReceiver2 != null) {
                unregisterReceiver(this.OnclickReceiver2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log("服务onDestroy");
        super.onDestroy();
    }

    private static void log(String str) {
        Log.i("override AutoBackupService", ":" + str);
    }
}
