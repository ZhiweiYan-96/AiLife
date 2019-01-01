package com.record.service;

import android.content.Context;
import android.content.Intent;
import com.alibaba.fastjson.JSON;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import java.util.HashMap;
import java.util.Map;

public class VersionRunnable implements Runnable {
    Context context;

    public VersionRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            String result = HttpRequestProxy.doPost(Val.GET_VERSION_URL, new HashMap(), "UTF-8");
            if (result != null) {
                Map<String, String> map = (Map) JSON.parse(result);
                if (Integer.parseInt((String) map.get("versionCode")) > this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode) {
                    String versionName = (String) map.get("versionName");
                    String updateLog = (String) map.get("updateLog");
                    String url = (String) map.get("url");
                    Intent it = new Intent(Val.INTENT_ACTION_NEW_VERSION);
                    it.putExtra("versionName", versionName);
                    it.putExtra("updateLog", updateLog);
                    it.putExtra("url", url);
                    this.context.sendBroadcast(it);
                    this.context.getSharedPreferences(Val.CONFIGURE_NAME, 2).edit().putInt(Val.CONFIGURE_IS_HAD_NEW_VERSION, 1).commit();
                    return;
                }
                this.context.getSharedPreferences(Val.CONFIGURE_NAME, 2).edit().putInt(Val.CONFIGURE_IS_HAD_NEW_VERSION, 0).commit();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }
}
