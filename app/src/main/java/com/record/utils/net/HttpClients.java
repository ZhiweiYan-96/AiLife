package com.record.utils.net;

import android.app.Activity;
import android.content.Context;
import com.record.view.pullrefresh.view.PullToRefreshBase;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClients {
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount >= 3) {
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                return true;
            }
            if ((exception instanceof SSLHandshakeException) || (((HttpRequest) context.getAttribute("http.request")) instanceof HttpEntityEnclosingRequest)) {
                return false;
            }
            return true;
        }
    };
    private Activity activity = null;
    private long contentLength;
    private Context context;
    private DefaultHttpClient httpClient;
    private HttpParams httpParams;
    private String strResult = "服务器无法连接，请检查网络";

    public HttpClients(Activity act) {
        this.context = act.getBaseContext();
        this.activity = act;
        getHttpClient();
    }

    public String doGet(String url, Map params) {
        String paramStr = "";
        if (params == null) {
            params = new HashMap();
        }
        for (Object entryobj : params.entrySet()) {
            Entry entry = (Entry)entryobj;
            paramStr = paramStr + ("&" + entry.getKey() + "=" + URLEncoder.encode(nullToString(entry.getValue())));
        }
        if (!paramStr.equals("")) {
            url = url + paramStr.replaceFirst("&", "?");
        }
        return doGet(url);
    }

    public String doGet(String url, List<NameValuePair> params) {
        String paramStr = "";
        if (params == null) {
            params = new ArrayList();
        }
        for (NameValuePair obj : params) {
            paramStr = paramStr + ("&" + obj.getName() + "=" + URLEncoder.encode(obj.getValue()));
        }
        if (!paramStr.equals("")) {
            url = url + paramStr.replaceFirst("&", "?");
        }
        return doGet(url);
    }

    public String doGet(String url) {
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.setHeaders(getHeader());
        try {
            MyHttpCookies li = new MyHttpCookies(this.context);
            if (li.getuCookie() != null) {
                this.httpClient.setCookieStore(li.getuCookie());
            }
            HttpResponse httpResponse = this.httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
                this.strResult = EntityUtils.toString(httpResponse.getEntity());
                li.setuCookie(this.httpClient.getCookieStore());
            } else {
                this.strResult = "Error Response: " + httpResponse.getStatusLine().toString();
            }
            httpRequest.abort();
            shutDownClient();
        } catch (ClientProtocolException e) {
            this.strResult = nullToString(e.getMessage());
            e.printStackTrace();
            httpRequest.abort();
            shutDownClient();
        } catch (IOException e2) {
            this.strResult = nullToString(e2.getMessage());
            e2.printStackTrace();
            httpRequest.abort();
            shutDownClient();
        } catch (Exception e3) {
            this.strResult = nullToString(e3.getMessage());
            e3.printStackTrace();
            httpRequest.abort();
            shutDownClient();
        } catch (Throwable th) {
            httpRequest.abort();
            shutDownClient();
            throw th;
        }
        return this.strResult;
    }

    public String doPost(String url, List<NameValuePair> params) {
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.setHeaders(getHeader());
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            MyHttpCookies li = new MyHttpCookies(this.context);
            if (li.getuCookie() != null) {
                this.httpClient.setCookieStore(li.getuCookie());
            }
            HttpResponse httpResponse = this.httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
                this.strResult = EntityUtils.toString(httpResponse.getEntity());
                li.setuCookie(this.httpClient.getCookieStore());
            } else {
                this.strResult = "Error Response: " + httpResponse.getStatusLine().toString();
            }
            httpRequest.abort();
            shutDownClient();
        } catch (ClientProtocolException e) {
            this.strResult = "";
            e.printStackTrace();
            httpRequest.abort();
            shutDownClient();
        } catch (IOException e2) {
            this.strResult = "";
            e2.printStackTrace();
            httpRequest.abort();
            shutDownClient();
        } catch (Exception e3) {
            this.strResult = "";
            e3.printStackTrace();
            httpRequest.abort();
            shutDownClient();
        } catch (Throwable th) {
            httpRequest.abort();
            shutDownClient();
            throw th;
        }
        return this.strResult;
    }

    public DefaultHttpClient getHttpClient() {
        this.httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(this.httpParams, 20000);
        HttpConnectionParams.setSoTimeout(this.httpParams, 20000);
        HttpConnectionParams.setSocketBufferSize(this.httpParams, 8192);
        HttpClientParams.setRedirecting(this.httpParams, true);
        String proxyStr = new MyHttpCookies(this.context).getHttpProxyStr();
        if (proxyStr != null && proxyStr.trim().length() > 0) {
            this.httpClient.getParams().setParameter("http.route.default-proxy", new HttpHost(proxyStr, 80));
        }
        this.httpClient = new DefaultHttpClient(this.httpParams);
        this.httpClient.setHttpRequestRetryHandler(requestRetryHandler);
        return this.httpClient;
    }

    private Header[] getHeader() {
        return new MyHttpCookies(this.context).getHttpHeader();
    }

    public void shutDownClient() {
        this.httpClient.getConnectionManager().shutdown();
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public static String nullToString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }
}
