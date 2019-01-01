package com.record.utils.net;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import com.record.utils.NetUtils;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicHeader;

public class MyHttpCookies {
    public static String baseurl = "http://4dian.sinaapp.com/";
    private static Header[] httpHeader;
    private static String httpProxyStr;
    private static int pageSize = 10;
    private static CookieStore uCookie = null;
    Context context;

    public MyHttpCookies(Context context) {
        this.context = context;
        httpHeader = new Header[]{new BasicHeader("PagingRows", String.valueOf(pageSize))};
    }

    public void initHTTPProxy() {
        Context context = this.context;
        Context context2 = this.context;
        if (((WifiManager) context.getSystemService(NetUtils.WIFI_NET)).isWifiEnabled()) {
            httpProxyStr = null;
            return;
        }
        Cursor mCursor = this.context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToNext();
            httpProxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        pageSize = pageSize;
    }

    public CookieStore getuCookie() {
        return uCookie;
    }

    public void setuCookie(CookieStore uCookie) {
        uCookie = uCookie;
    }

    public Header[] getHttpHeader() {
        return httpHeader;
    }

    public String getHttpProxyStr() {
        return httpProxyStr;
    }
}
