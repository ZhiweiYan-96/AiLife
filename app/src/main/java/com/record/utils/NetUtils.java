package com.record.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import com.record.conts.Nums;
import com.record.myLife.R;
import com.record.view.pullrefresh.view.PullToRefreshBase;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

public class NetUtils {
    public static final String MOBILE_NET = "mobile";
    public static final String NETWORK_ERROR = "Can_not_get_ConnectionManager";
    public static final String NETWORK_NONE = "none";
    public static final String WIFI_NET = "wifi";
    static int checkNet = 0;

    class checkNetThread1 extends Thread {
        checkNetThread1() {
        }

        public void run() {
            try {
                Thread.sleep(4000);
                NetUtils.checkNet = 2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class checkNetThread2 extends Thread {
        checkNetThread2() {
        }

        public void run() {
            try {
                HttpGet request = new HttpGet("http://www.baidu.com");
                HttpClient client = new DefaultHttpClient();
                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 2000);
                HttpConnectionParams.setSoTimeout(params, 2000);
                if (client.execute(request).getStatusLine().getStatusCode() == PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
                    NetUtils.checkNet = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                NetUtils.checkNet = 0;
            }
        }
    }

    public static boolean isNetAvailable(Context context) {
        try {
            if (InetAddress.getByName("www.baidu.com").isReachable(Nums.BACKPRESSED_FINISH_DELAY) || InetAddress.getByName("www.sohu.com").isReachable(2000) || InetAddress.getByName("www.sina.com").isReachable(2000)) {
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
            System.out.println(e);
            return false;
        } catch (IOException e2) {
            System.out.println(e2);
            return false;
        }
    }

    public static boolean openUrl() {
        String myString = "";
        try {
            HttpURLConnection urlCon = (HttpURLConnection) new URL("HTTP://www.baidu.com/index.html").openConnection();
            urlCon.setConnectTimeout(2000);
            urlCon.setReadTimeout(Nums.BACKPRESSED_FINISH_DELAY);
            urlCon.connect();
            InputStream is = urlCon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            while (true) {
                int current = bis.read();
                if (current == -1) {
                    break;
                }
                baf.append((byte) current);
            }
            myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
            bis.close();
            is.close();
            if (myString.indexOf("www.baidu.com") > -1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkNet() {
        checkNet = 0;
        new checkNetThread2().start();
        new checkNetThread1().start();
        while (checkNet == 0) {
            if (checkNet == 2) {
                break;
            }
        }
        if (checkNet == 1) {
            return true;
        }
        return false;
    }

    public static String getActiveNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity == null) {
            return NETWORK_ERROR;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return NETWORK_NONE;
        }
        return info.getTypeName().toLowerCase();
    }

    public static boolean isNetworkAvailable2noToast(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null || info.getState() != State.CONNECTED) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity == null) {
            GeneralUtils.toastShort(context, context.getString(R.string.str_net_fail));
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.getState() == State.CONNECTED) {
            return true;
        }
        GeneralUtils.toastShort(context, context.getString(R.string.str_net_fail));
        return false;
    }

    public static boolean isWiFiAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getState() == State.CONNECTED && info.getType() == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWiFiAvailable_Toast(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getState() == State.CONNECTED && info.getType() == 1) {
                return true;
            }
        }
        GeneralUtils.toastShort(context, "请先检查网络连接...");
        return false;
    }
}
