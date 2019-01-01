package com.record.utils.share;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.mob.MobSDK;
import com.record.utils.CaptureUtil_v2;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.LogUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import java.util.HashMap;

public class QuickShareUtil {
    static Thread capture = null;
    private static String content = "";
    private static Context context;
    boolean captureSuccess = true;
    public Handler myHandlerUpdate = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String imagePath = msg.obj.toString();   // by Ren
            MobSDK.init(QuickShareUtil.context);     // by Ren
            OnekeyShare oks = new OnekeyShare();
            oks.setTitle("爱今天");
            oks.setText(QuickShareUtil.content);
            oks.setImagePath(imagePath);
            oks.setLatitude(0.0f);
            oks.setLongitude(0.0f);
            oks.setSilent(false);
            oks.setCallback(new PlatformActionListener() {
                public void onError(Platform arg0, int arg1, Throwable arg2) {
                    LogUtils.log("分享，出错啦！");
                    arg2.printStackTrace();
                }

                public void onComplete(Platform arg0, int arg1, HashMap<String, Object> hashMap) {
                    ToastUtils.toastShort(QuickShareUtil.context, "分享成功！");
                }

                public void onCancel(Platform arg0, int arg1) {
                    LogUtils.log("取消分享");
                }
            });
            oks.show(QuickShareUtil.context);
        }
    };

    public QuickShareUtil(Context context) {
        context = context;
    }

    public void SceenCutAndShare(String content) {
        content = content;
        if (GeneralHelper.checkNetworkConnectionAndToast(context)) {
            log("网络没开启，不能分享！");
            return;
        }
        GeneralUtils.toastShort(context, "正在截图,请稍后...");
        if (this.captureSuccess) {
            screenCaptrueFileGUI();
        } else {
            GeneralUtils.toastShort(context, "正在截图,请稍后...");
        }
    }

    private void screenCaptrueFileGUI() {
        if (this.captureSuccess) {
            this.captureSuccess = false;
            String path = Environment.getExternalStorageDirectory() + Val.FilePath_SD + "img_capture/";
            String fileName = CaptureUtil_v2.shootWithWater((Activity) context, path, "");
            Message msg = new Message();
            msg.obj = path + fileName;
            this.myHandlerUpdate.sendMessage(msg);
            this.captureSuccess = true;
            return;
        }
        GeneralUtils.toastShort(context, "正在截图，请稍后...");
    }

    public static void log(String str) {
        Log.i("override", "QuickShareUtil:" + str);
    }
}
