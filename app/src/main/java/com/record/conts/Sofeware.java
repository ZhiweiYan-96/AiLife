package com.record.conts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.record.bean.HttpParams;
import com.record.utils.Val;
import com.record.utils.net.HttpRequestProxy;
import java.util.HashMap;

public class Sofeware {
    public static String AUTH = "Api/auth";
    public static String DOWNLOAD_STATICS = "Api/getGoalStatics";
    public static String HTTP_BASE = "http://api.itodayss.com/";
    public static String GET_DATE_RECORD = (HTTP_BASE + "Api/getDateRecord");
    public static String GET_PROMPT = (HTTP_BASE + "Api/getPrompt");
    public static String GET_SERVER_TIME = (HTTP_BASE + "Api/getServerTime");
    public static String GET_TODAY_RANK = (HTTP_BASE + "Api/getTodayRank");
    public static String HELLO = "Api/hello";
    public static String IS_NICKNAME_EXIST = "Api/isNickNameExist";
    public static String LOGIN = "Api/login";
    public static String RESET_PASSWORD = "User/resetPw";
    public static String SEND_VERRIFY_CODE = "User/sendVerifyCode";
    public static String SIGN_UP = "Api/register";
    public static String UPLOAD_ALLOCATION = "Api/uploadAllocation";
    public static String UPLOAD_DELIBERATE_RECORD = "Api/uploadDeliberateRecord";
    public static String UPLOAD_ERROR_DATA = "Api/uploadErrorData";
    public static String UPLOAD_GOALS = "Api/uploadGoals";
    public static String UPLOAD_ITEM_ONE_BY_ONE = "Api/uploadItemOneByOne";
    public static String UPLOAD_LABEL = "Api/uploadLabel";
    public static String UPLOAD_LABEL_LINK = "Api/uploadLabelLink";
    public static String UPLOAD_USER_INFO = "Api/uploadUserInfo";
    public static HashMap<String, String> httpMap;
    public static String token = "123456789";

    public static HashMap<String, String> getHttpMap() {
        if (httpMap != null) {
            return httpMap;
        }
        httpMap = new HashMap();
        httpMap.put("t_act", UPLOAD_GOALS);
        return httpMap;
    }

    public static String getHttpByTable(String tableName) {
        return HTTP_BASE + ((String) getHttpMap().get(tableName));
    }

    public static String getToken() {
        try {
            if (token != null && token.length() > 0) {
                return token;
            }
            String http = HTTP_BASE + AUTH;
            HashMap<String, String> map = new HashMap();
            map.put("phoneid", "1234567898979");
            map.put("scope", "android");
            map.put("passwd", Val.getAuthPassword());
            token = ((JSONObject) JSON.parse(HttpRequestProxy.doPost(http, map, "UTF-8"))).get("token") + "";
            System.out.println("token:" + token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static HashMap<String, String> getTokenMap() {
        return new HttpParams().put("token", getToken());
    }
}
