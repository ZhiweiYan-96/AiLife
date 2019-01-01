package com.record.utils.net;

import com.record.utils.db.DbUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestProxy {
    private static int connectTimeOut = 5000;
    private static int readTimeOut = 10000;
    private static String requestEncoding = "UTF-8";

    public static String doGet(String reqUrl, Map parameters, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Object elementObj : parameters.entrySet()) {
                Entry element = (Entry)elementObj;
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(), requestEncoding));
                params.append("&");
            }
            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }
            url_con = (HttpURLConnection) new URL(reqUrl).openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connectTimeOut));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(readTimeOut));
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            StringBuffer temp = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            for (String tempLine = rd.readLine(); tempLine != null; tempLine = rd.readLine()) {
                temp.append(tempLine);
                temp.append(crlf);
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
            if (url_con != null) {
                url_con.disconnect();
            }
        } catch (IOException e) {
            if (url_con != null) {
                url_con.disconnect();
            }
        } catch (Throwable th) {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    public static String doGet(String reqUrl, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            String queryUrl = reqUrl;
            int paramIndex = reqUrl.indexOf("?");
            if (paramIndex > 0) {
                queryUrl = reqUrl.substring(0, paramIndex);
                String[] paramArray = reqUrl.substring(paramIndex + 1, reqUrl.length()).split("&");
                for (String string : paramArray) {
                    int index = string.indexOf("=");
                    if (index > 0) {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1, string.length());
                        params.append(parameter);
                        params.append("=");
                        params.append(URLEncoder.encode(value, requestEncoding));
                        params.append("&");
                    }
                }
                params = params.deleteCharAt(params.length() - 1);
            }
            url_con = (HttpURLConnection) new URL(queryUrl).openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connectTimeOut));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(readTimeOut));
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            StringBuffer temp = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            for (String tempLine = rd.readLine(); tempLine != null; tempLine = rd.readLine()) {
                temp.append(tempLine);
                temp.append(crlf);
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
            if (url_con != null) {
                url_con.disconnect();
            }
        } catch (IOException e) {
            System.out.println(e);
            if (url_con != null) {
                url_con.disconnect();
            }
        } catch (Throwable th) {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    public static String doPost(String reqUrl, Map parameters, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Object elementObj : parameters.entrySet()) {
                Entry element = (Entry)elementObj;
                Object value = element.getValue();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(value == null ? "" : value.toString(), requestEncoding));
                params.append("&");
            }
            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }
            url_con = (HttpURLConnection) new URL(reqUrl).openConnection();
            url_con.setRequestMethod("POST");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connectTimeOut));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(readTimeOut));
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            for (String tempLine = rd.readLine(); tempLine != null; tempLine = rd.readLine()) {
                tempStr.append(tempLine);
                tempStr.append(crlf);
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
            if (url_con != null) {
                url_con.disconnect();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            return responseContent;
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    public static String doPost(String reqUrl, Map parameters) {
        return doPost(reqUrl, parameters, "UTF-8");
    }

    public static int getConnectTimeOut() {
        return connectTimeOut;
    }

    public static int getReadTimeOut() {
        return readTimeOut;
    }

    public static String getRequestEncoding() {
        return requestEncoding;
    }

    public static void setConnectTimeOut(int connectTimeOut) {
        connectTimeOut = connectTimeOut;
    }

    public static void setReadTimeOut(int readTimeOut) {
        readTimeOut = readTimeOut;
    }

    public static void setRequestEncoding(String requestEncoding) {
        requestEncoding = requestEncoding;
    }

    public static void main(String[] args) {
        System.out.println(doGet("http://www.dfyl-luxgen.com/GetCityList.ashx", "utf-8"));
    }
}
