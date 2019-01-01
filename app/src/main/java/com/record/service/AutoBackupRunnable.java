package com.record.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.record.myLife.R;
import com.record.utils.DateTime;
import com.record.utils.StringUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import java.io.File;

public class AutoBackupRunnable implements Runnable {
    Context context;

    public AutoBackupRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            log("启动自动备份线程---->");
            backupData_v2();
            log("自动备份线程完成！");
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            sentBroadcast(this.context.getResources().getString(R.string.str_back_up_fail));
            log("自动备份数据--失败！");
        }
    }

    private File backupDbFileToLocal_v2(File srcfile) throws Exception {
        String desFilePath = Environment.getExternalStorageDirectory() + File.separator + Val.SD_BACKUP_DIR;
        String desFileName = Val.SD_BACKUP_NAME + DateTime.getTimeString2();
        StringUtils.encryptFile(srcfile.getPath(), Val.getPassword(), desFilePath, desFileName);
        log("备份成功" + desFilePath + File.separator + desFileName);
        File file = new File(desFilePath, Val.SD_BACKUP_NAME);
        if (file.exists()) {
            file.delete();
            log("备份成功,删除这前备份文件：" + file.getPath());
        }
        File file2 = new File(desFilePath + File.separator + desFileName);
        if (file2.exists()) {
            file2.renameTo(file);
            log("备份成功,改名成功：" + file.getPath());
        } else {
            log("备份失败！！");
        }
        return file;
    }

    public void backupData_v2() throws Exception {
        if (!isSdExist()) {
            sentBroadcast(this.context.getResources().getString(R.string.str_no_detect_sd));
        } else if (isSdCanWrite()) {
            long freeSize = getSDFreeSize();
            File file = new File(DbUtils.getDb(this.context).getPath());
            if (!file.exists()) {
                file = getDbFile();
            }
            if (file.exists()) {
                long fileSize = (file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                log("freeSize" + freeSize + ",fileSize:" + fileSize);
                if (getSDFreeSize() > fileSize) {
                    String pathWithoutSdPath = backupDbFileToLocal_v2(file).getPath().replace(Environment.getExternalStorageDirectory().toString(), "");
                    sentBroadcast(this.context.getResources().getString(R.string.str_back_up_success));
                    return;
                }
                sentBroadcast(this.context.getResources().getString(R.string.str_space_no_enough));
                return;
            }
            sentBroadcast(this.context.getResources().getString(R.string.str_read_db_error));
        } else {
            sentBroadcast(this.context.getResources().getString(R.string.str_sd_cannot_write));
        }
    }

    private File getDbFile() {
        String packageName = "";
        try {
            PackageInfo info = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
            if (info.packageName != null) {
                packageName = info.packageName;
                log(packageName);
            }
        } catch (NameNotFoundException e) {
            DbUtils.exceptionHandler(e);
        }
        File dbFile = new File(Environment.getDataDirectory() + "/data/" + packageName + "/databases/" + Val.DB_NAME);
        if (dbFile.exists()) {
            return dbFile;
        }
        return null;
    }

    public long getSDFreeSize() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((((long) sf.getAvailableBlocks()) * ((long) sf.getBlockSize())) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public boolean isSdCanWrite() {
        if (Environment.getExternalStorageDirectory().canWrite()) {
            return true;
        }
        return false;
    }

    public boolean isSdExist() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    private static void sentBroadcast(String str) {
        new Intent(Val.INTENT_ACTION_BACKUP_TOAST).putExtra("toast", str);
    }

    public void log(String str) {
        Log.i("override AutoBackupRunnable", ":" + str);
    }
}
