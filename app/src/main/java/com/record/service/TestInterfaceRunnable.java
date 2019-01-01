package com.record.service;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import java.util.HashMap;
import java.util.Map;

public class TestInterfaceRunnable implements Runnable {
    Context context;

    public TestInterfaceRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {
            login(auth());
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    public void login(String token) {
        HashMap<String, String> map2 = new HashMap();
        map2.put("email", "925715564@qq.com");
        map2.put("token", token);
        map2.put("passwd", "123456");
        String result2 = HttpRequestProxy.doPost("http://2today.sinaapp.com/index.php/Api/login/", map2, "UTF-8");
        log(result2);
        log(JSON.parse(result2).toString());
    }

    public void reg(String token) {
        HashMap<String, String> map2 = new HashMap();
        map2.put("nick", "爱今天");
        map2.put("email", "624604006@qq.com");
        map2.put("token", token);
        map2.put("passwd", "123456");
        String result2 = HttpRequestProxy.doPost("http://2today.sinaapp.com/index.php/Api/register/", map2, "UTF-8");
        log(result2);
        log(JSON.parse(result2).toString());
    }

    public String auth() {
        TelephonyManager tm = (TelephonyManager) this.context.getSystemService("phone");
        HashMap<String, String> map2 = new HashMap();
        map2.put("phoneid", tm.getDeviceId());
        map2.put("scope", "android");
        map2.put("passwd", "1qazxsw2");
        String result2 = HttpRequestProxy.doPost("http://2today.sinaapp.com/index.php/Api/auth/", map2, "UTF-8");
        Log.i("override TestInterface", result2);
        String token = ((Map) JSON.parse(result2)).get("token") + "";
        log(token);
        return token;
    }

    public void log(String str) {
        Log.i("override TestInterface", ":" + str);
    }
}
