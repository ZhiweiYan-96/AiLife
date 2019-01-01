package com.record.utils;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PowerMangerUtils {
    public static WakeLock getPowerManager(Context context) {
        WakeLock wl = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "itodayss_record_mylife");
        wl.setReferenceCounted(false);
        return wl;
    }

    public static void getPrivateWakeLock(Context context, int heldsec) {
        try {
            WakeLock wl = getPowerManager(context);
            try {
                if (wl.isHeld()) {
                    LogUtils.log("itodayss WakeLock 1 isheld : true ");
                    wl.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (heldsec > 0) {
                LogUtils.log("itodayss WakeLock heldsec :  " + heldsec);
                wl.acquire((long) heldsec);
                return;
            }
            LogUtils.log("itodayss WakeLock acquire");
            wl.acquire();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void releasePrivateWakeLock(Context context) {
        try {
            WakeLock wl = getPowerManager(context);
            if (wl != null) {
                if (!wl.isHeld()) {
                    LogUtils.log("itodayss WakeLock isheld : false ");
                }
                if (wl.isHeld()) {
                    LogUtils.log("itodayss WakeLock isheld : true ");
                    wl.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WakeLock getWakeLockForRemind(Context context) {
        WakeLock wl = ((PowerManager) context.getSystemService("power")).newWakeLock(805306374, "remind_tomato");
        wl.setReferenceCounted(false);
        return wl;
    }

    public static void acquireWhenRemind(Context context) {
        try {
            WakeLock wl = getWakeLockForRemind(context);
            try {
                if (wl.isHeld()) {
                    LogUtils.log("itodayss WakeLock 1 isheld : true ");
                    wl.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LogUtils.log("itodayss WakeLock acquire");
            wl.acquire();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void releaseAfterRemind(Context context) {
        try {
            WakeLock wl = getWakeLockForRemind(context);
            if (wl != null) {
                if (!wl.isHeld()) {
                    LogUtils.log("itodayss WakeLock isheld : false ");
                }
                if (wl.isHeld()) {
                    LogUtils.log("itodayss WakeLock isheld : true ");
                    wl.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
