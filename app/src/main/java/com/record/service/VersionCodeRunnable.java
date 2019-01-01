package com.record.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import java.util.HashMap;
import java.util.Map;

public class VersionCodeRunnable implements Runnable {
    Context context;

    public VersionCodeRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            String result = HttpRequestProxy.doPost(Val.GET_VERSION_CODE_URL, new HashMap(), "UTF-8");
            if (result != null) {
                Log.i("override VersionCode", result);
                String versionCode = (String) ((Map) JSON.parse(result)).get("versionCode");
                PackageInfo info = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
                Log.i("override", "服务版本号" + versionCode);
                if (Integer.parseInt(versionCode) > info.versionCode) {
                    new Thread(new VersionRunnable(this.context)).start();
                } else {
                    this.context.getSharedPreferences(Val.CONFIGURE_NAME, 2).edit().putInt(Val.CONFIGURE_IS_HAD_NEW_VERSION, 0).commit();
                }
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }
}
