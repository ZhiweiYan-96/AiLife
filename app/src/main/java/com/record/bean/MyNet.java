package com.record.bean;

import com.record.view.pullrefresh.view.PullToRefreshBase;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class MyNet {
    static int checkNet = 0;
    long Timeout = 3000;
    Thread checkNetThread = null;
    Thread timeoutThread = null;

    class CheckNetThread extends Thread {
        CheckNetThread() {
        }

        public void run() {
            try {
                HttpGet request = new HttpGet("http://www.baidu.com");
                HttpClient client = new DefaultHttpClient();
                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 2000);
                HttpConnectionParams.setSoTimeout(params, 2000);
                if (client.execute(request).getStatusLine().getStatusCode() == PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
                    MyNet.checkNet = 1;
                    MyNet.this.timeoutThread = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyNet.checkNet = 0;
            }
        }
    }

    class TimeoutThread extends Thread {
        TimeoutThread() {
        }

        public void run() {
            try {
                Thread.sleep(MyNet.this.Timeout);
                MyNet.checkNet = 2;
                MyNet.this.timeoutThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkNet() {
        checkNet = 0;
        if (this.timeoutThread == null && this.checkNetThread == null) {
            this.checkNetThread = new CheckNetThread();
            this.checkNetThread.start();
            this.timeoutThread = new TimeoutThread();
            this.timeoutThread.start();
            while (checkNet == 0) {
                if (checkNet == 2) {
                    break;
                }
            }
        }
        if (checkNet == 1) {
            return true;
        }
        return false;
    }

    public boolean checkNet(long timeout) {
        this.Timeout = timeout;
        checkNet = 0;
        if (this.timeoutThread == null && this.checkNetThread == null) {
            this.checkNetThread = new CheckNetThread();
            this.checkNetThread.start();
            this.timeoutThread = new TimeoutThread();
            this.timeoutThread.start();
            while (checkNet == 0) {
                if (checkNet == 2) {
                    break;
                }
            }
        }
        if (checkNet == 1) {
            return true;
        }
        return false;
    }
}
