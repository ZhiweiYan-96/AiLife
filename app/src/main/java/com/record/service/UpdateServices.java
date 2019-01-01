package com.record.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import com.record.myLife.R;
import com.record.utils.FileUtils;
import com.record.utils.HttpDownloader;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.wangjie.androidbucket.services.network.http.HttpConstants;
import java.io.File;
import java.io.IOException;

public class UpdateServices extends Service {
    int DOWNLOADING = 1;
    int ERROR = 2;
    HttpDownloader dh;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int status = msg.arg2;
                if (status == UpdateServices.this.DOWNLOADING) {
                    int rate = msg.arg1;
                    UpdateServices.this.log("下载进度：" + rate);
                    if (rate < 100) {
                        RemoteViews contentView = UpdateServices.this.mNotification.contentView;
                        contentView.setTextViewText(R.id.rate, rate + "%");
                        contentView.setTextViewText(R.id.fileName, "下载中...");
                        contentView.setProgressBar(R.id.progress, 100, rate, false);
                        UpdateServices.this.mNotificationManager.notify(Val.NOTI_ID_DOWNLOAD, UpdateServices.this.mNotification);
                    } else if (rate >= 100) {
                        File resultFile = null;
                        try {
                            resultFile = FileUtils.getSDFile(Val.FileAPKName, Val.FilePath_SD2);
                        } catch (IOException e) {
                            DbUtils.exceptionHandler(e);
                        }
                        UpdateServices.this.mNotificationManager.cancel(Val.NOTI_ID_DOWNLOAD);
                        Intent it = new Intent("android.intent.action.VIEW");
                        it.addFlags(268435456);
                        it.setDataAndType(Uri.fromFile(resultFile), HttpConstants.CONTENT_TYPE_APK);
                        UpdateServices.this.startActivity(it);
                    }
                } else if (status == UpdateServices.this.ERROR) {
                    UpdateServices.this.mNotification.defaults = 1;
                    UpdateServices.this.mNotification.tickerText = "下载超时";
                    UpdateServices.this.mNotification.flags = 16;
//                    UpdateServices.this.mNotification.setLatestEventInfo(UpdateServices.this, "下载超时", "请稍候再试...", null);
                    UpdateServices.this.mNotificationManager.notify(Val.NOTI_ID_DOWNLOAD, UpdateServices.this.mNotification);
                }
            } catch (Exception e2) {
                DbUtils.exceptionHandler(e2);
            }
        }
    };
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    public IBinder onBind(Intent intent) {
        log("UpdateServices IBinder");
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    private void downloadSoft() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    UpdateServices.this.dh.download(Val.versionUrl, Val.FilePath_SD2, Val.FileAPKName);
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                int rate = 0;
                double localSize = 0.0d;
                double timeout = 0.0d;
                while (rate < 100) {
                    try {
                        double softSize = (double) UpdateServices.this.dh.getSourceSize();
                        double tmpsize = (double) UpdateServices.this.dh.getSize();
                        Message msg;
                        if (tmpsize > localSize) {
                            localSize = tmpsize;
                            rate = (int) ((localSize / softSize) * 100.0d);
                            msg = new Message();
                            msg.arg1 = rate;
                            msg.arg2 = UpdateServices.this.DOWNLOADING;
                            UpdateServices.this.handler.sendMessage(msg);
                        } else {
                            timeout += 1.0d;
                            if (timeout > 10.0d) {
                                msg = new Message();
                                msg.arg1 = rate;
                                msg.arg2 = UpdateServices.this.ERROR;
                                UpdateServices.this.handler.sendMessage(msg);
                                return;
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                        return;
                    }
                }
            }
        }).start();
    }

    private void initNotification() {
        this.mNotificationManager = (NotificationManager) getSystemService("notification");
        this.mNotification = new Notification(R.drawable.ic_launcher, "开始下载", System.currentTimeMillis());
        this.mNotification.flags = 2;
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.tem_download_notification);
        contentView.setTextViewText(R.id.fileName, "爱今天");
        contentView.setTextViewText(R.id.rate, "0%");
        contentView.setProgressBar(R.id.progress, 100, 0, false);
        this.mNotification.contentView = contentView;
        this.mNotification.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 134217728);
        this.mNotificationManager.notify(Val.NOTI_ID_DOWNLOAD, this.mNotification);
    }

    public Intent openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        System.out.println(getMIMEType(f) + "soft open type -----------------application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(f), HttpConstants.CONTENT_TYPE_APK);
        return intent;
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = HttpConstants.CONTENT_TYPE_APK;
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
            return type;
        }
        return type + "/*";
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        log("UpdateServices onStartCommand");
        try {
            log("UpdateServices onCreate");
            if (Val.versionUrl != null && Val.versionUrl.length() > 0) {
                this.dh = new HttpDownloader();
                initNotification();
                downloadSoft();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onTaskRemoved(Intent rootIntent) {
        log("UpdateServices onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    public void log(String str) {
    }
}
