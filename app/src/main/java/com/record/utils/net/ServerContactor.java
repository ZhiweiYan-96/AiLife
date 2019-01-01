package com.record.utils.net;

import com.record.bean.RequestFailExeption;
import com.record.conts.Nums;
import com.record.view.pullrefresh.view.PullToRefreshBase;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class ServerContactor {
    public static String UploadImageForResult(String strUrl, FileInputStream fStream) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(strUrl).openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setConnectTimeout(Nums.SOC_TIME_OUT);
        con.setReadTimeout(Nums.SOC_TIME_OUT);
        con.setRequestMethod("POST");
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());
        byte[] buffer = new byte[1024];
        while (true) {
            int length = fStream.read(buffer);
            if (length == -1) {
                break;
            }
            ds.write(buffer, 0, length);
        }
        fStream.close();
        ds.flush();
        ds.close();
        InputStream is = con.getInputStream();
        StringBuffer b = new StringBuffer();
        while (true) {
            int ch = is.read();
            if (ch == -1) {
                return b.toString();
            }
            b.append((char) ch);
        }
    }

    public static String getResponseStringWithHttpPost(String strRequestBaseUrl, List<NameValuePair> params) throws Exception {
        HttpPost httpPost = new HttpPost(strRequestBaseUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 20000);
        HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpPost);
        if (httpResponse.getStatusLine().getStatusCode() == PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
            return EntityUtils.toString(httpResponse.getEntity());
        }
        throw new RequestFailExeption(400);
    }

    public static void directHttpPost(String strRequestBaseUrl, List<NameValuePair> params) throws Exception {
        HttpPost httpPost = new HttpPost(strRequestBaseUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 20000);
        new DefaultHttpClient(httpParameters).execute(httpPost);
    }

    public static String getResponseStringWithHttpGet(String strRequestBaseUrl, List<NameValuePair> params) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(extractRequestUrl(strRequestBaseUrl, params));
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 20000);
        HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpGet);
        if (httpResponse.getStatusLine().getStatusCode() != PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
            return null;
        }
        StringBuilder builder = new StringBuilder(128);
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return builder.toString();
            }
            builder.append(line).append(10);
        }
    }

    private static String extractRequestUrl(String strRequestBaseUrl, List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(strRequestBaseUrl);
        if (params == null || params.size() == 0) {
            return sb.toString();
        }
        int len = params.size();
        sb.append('?');
        for (int i = 0; i < len; i++) {
            NameValuePair nameValuePair = (NameValuePair) params.get(i);
            sb.append(nameValuePair.getName()).append('=').append(nameValuePair.getValue());
            sb.append('&');
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static File getAmrFromServer(String strAmrUrl, String path, String fileName) {
        ClientProtocolException e;
        Throwable th;
        IOException e2;
        Exception e3;
        HttpClient httpClient = new DefaultHttpClient();
        File file = null;
        try {
            HttpResponse httpResponse = httpClient.execute(new HttpGet(strAmrUrl));
            if (httpResponse.getStatusLine().getStatusCode() == PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS) {
                File file2 = new File(path + fileName);
                try {
                    FileOutputStream outputStream = new FileOutputStream(file2);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    byte[] b = new byte[1024];
                    while (true) {
                        int j = inputStream.read(b);
                        if (j == -1) {
                            break;
                        }
                        outputStream.write(b, 0, j);
                    }
                    outputStream.flush();
                    outputStream.close();
                    file = file2;
                } catch (ClientProtocolException e4) {
                    e = e4;
                    file = file2;
                    try {
                        e.printStackTrace();
                        httpClient.getConnectionManager().shutdown();
                        return file;
                    } catch (Throwable th2) {
                        th = th2;
                        httpClient.getConnectionManager().shutdown();
//                        throw th;
                    }
                } catch (IOException e5) {
                    e2 = e5;
                    file = file2;
                    e2.printStackTrace();
                    httpClient.getConnectionManager().shutdown();
                    return file;
                } catch (Exception e6) {
                    e3 = e6;
                    file = file2;
                    e3.printStackTrace();
                    httpClient.getConnectionManager().shutdown();
                    return file;
                } catch (Throwable th3) {
                    th = th3;
                    file = file2;
                    httpClient.getConnectionManager().shutdown();
//                    throw th;
                }
            }
            httpClient.getConnectionManager().shutdown();
        } catch (ClientProtocolException e7) {
            e = e7;
            e.printStackTrace();
            httpClient.getConnectionManager().shutdown();
            return file;
        } catch (IOException e8) {
            e2 = e8;
            e2.printStackTrace();
            httpClient.getConnectionManager().shutdown();
            return file;
        } catch (Exception e9) {
            e3 = e9;
            e3.printStackTrace();
            httpClient.getConnectionManager().shutdown();
            return file;
        }
        return file;
    }
}
