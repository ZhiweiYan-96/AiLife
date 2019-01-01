package com.record.service;

import android.content.Context;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import java.util.HashMap;

public class VersionCodeRunnable2 implements Runnable {
    Context context;

    public VersionCodeRunnable2(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            Thread.sleep(5000);
            HttpRequestProxy.doPost("http://javalxf.sinaapp.com/syndata.php", new HashMap(), "UTF-8");
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }
}
