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
import com.record.utils.GeneralHelper;
import com.record.utils.SoftwareUpdateHelper;
import com.record.utils.db.DbUtils;
import com.wangjie.androidbucket.services.network.http.HttpConstants;
import java.io.File;
import java.io.IOException;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class UpdateSoftService extends Service {
    public static final int NOTIFY_ID = 3;
    private int DOWNLOAD_OUT_TIME = 120;
    private boolean canceled = false;
    String fileName;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    UpdateSoftService.this.canceled = true;
                    return;
                case 1:
                    int rate = msg.arg1;
                    if (rate < 100) {
                        RemoteViews contentView = UpdateSoftService.this.mNotification.contentView;
                        contentView.setTextViewText(R.id.rate, rate + "%");
                        contentView.setProgressBar(R.id.progress, 100, rate, false);
                    } else if (rate == 100) {
                        UpdateSoftService.this.mNotification.icon = R.drawable.downloaded;
                        UpdateSoftService.this.mNotification.defaults = 1;
                        UpdateSoftService.this.mNotification.tickerText = "下载完成";
                        UpdateSoftService.this.mNotification.flags = 16;
                        UpdateSoftService.this.mNotification.contentView = null;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            DbUtils.exceptionHandler(e);
                        }
                        File resultFile = null;
                        try {
                            resultFile = UpdateSoftService.this.su.getLocalFile();
                            System.out.println(resultFile.getPath() + "--" + resultFile.getName() + "| file: " + resultFile.getAbsoluteFile() + "|length:" + resultFile.length());
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
//                        UpdateSoftService.this.mNotification.setLatestEventInfo(UpdateSoftService.this, "下载完成", "文件已下载完毕", PendingIntent.getActivity(UpdateSoftService.this, 0, UpdateSoftService.this.su.openFile(resultFile), 134217728));
                        Intent it = new Intent("android.intent.action.VIEW");
                        it.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        it.setDataAndType(Uri.fromFile(resultFile), HttpConstants.CONTENT_TYPE_APK);
                        UpdateSoftService.this.startActivity(it);
                    }
                    UpdateSoftService.this.mNotificationManager.notify(3, UpdateSoftService.this.mNotification);
                    return;
                case 2:
                    UpdateSoftService.this.mNotification.defaults = 1;
                    UpdateSoftService.this.mNotification.tickerText = "下载超时";
                    UpdateSoftService.this.mNotification.flags = 16;
                    UpdateSoftService.this.mNotification.contentView = null;
                    UpdateSoftService.this.canceled = true;
//                    UpdateSoftService.this.mNotification.setLatestEventInfo(UpdateSoftService.this, "下载超时", "请检查网络", null);
                    UpdateSoftService.this.mNotificationManager.notify(3, UpdateSoftService.this.mNotification);
                    return;
                default:
                    return;
            }
        }
    };
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    String path;
    SoftwareUpdateHelper su;
    private int time = 0;
    String url;

    public void onCreate() {
        super.onCreate();
        if (this.su == null) {
            this.su = new SoftwareUpdateHelper();
            initNotification();
            downloadSoft();
        }
    }

    private void initNotification() {
        this.mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.mNotification = new Notification(R.drawable.ic_launcher, "开始下载", System.currentTimeMillis());
        this.mNotification.flags = 2;
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.tem_download_notification);
        contentView.setTextViewText(R.id.fileName, "爱今天");
        this.mNotification.contentView = contentView;
        this.mNotification.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), FLAG_UPDATE_CURRENT);
        this.mNotificationManager.notify(3, this.mNotification);
    }

    private void downloadSoft() {
        GeneralHelper.d("downloadSoft...");
        new Thread(new Runnable() {
            public void run() {
                Message msg;
                int rate = 0;
                double tmpsize = 0.0d;
                while (!UpdateSoftService.this.canceled && rate < 100) {
                    double softSize = (double) UpdateSoftService.this.su.getDownloader().getSourceSize();
                    double localSize = (double) UpdateSoftService.this.su.getDownloader().getSize();
                    GeneralHelper.d(rate + ":" + softSize + ":" + localSize);
                    if (softSize > 0.0d && localSize > 0.0d) {
                        GeneralHelper.d(rate + "<-下载进度");
                        rate = (int) ((localSize / softSize) * 100.0d);
                        GeneralHelper.d("localSize: " + localSize + "--softSize:" + softSize + " /:" + (localSize / softSize));
                    }
                    if (localSize - tmpsize < 1.0d) {
                        UpdateSoftService.this.time = UpdateSoftService.this.time + 1;
                    }
                    tmpsize = localSize;
                    msg = UpdateSoftService.this.handler.obtainMessage();
                    GeneralHelper.d(UpdateSoftService.this.time + "<-时间");
                    msg.what = 1;
                    msg.arg1 = rate;
                    if (UpdateSoftService.this.time > UpdateSoftService.this.DOWNLOAD_OUT_TIME) {
                        msg.what = 2;
                    }
                    UpdateSoftService.this.handler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                msg = UpdateSoftService.this.handler.obtainMessage();
                msg.arg1 = rate;
                UpdateSoftService.this.handler.sendMessage(msg);
            }
        }).start();
    }

    public IBinder onBind(Intent intent) {
        this.path = intent.getStringExtra("url");
        this.url = intent.getStringExtra("url");
        this.fileName = intent.getStringExtra("fileName");
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        this.canceled = true;
        this.mNotificationManager.cancel(3);
    }
}
