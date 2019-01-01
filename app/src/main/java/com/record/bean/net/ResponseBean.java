package com.record.bean.net;

import com.google.gson.Gson;
import org.json.JSONObject;

public class ResponseBean {
    public String data;
    public Object excObject;
    public String info;
    public JSONObject params;
    public int status;

    public static ResponseBean getResponBean(String res) {
        return (ResponseBean) new Gson().fromJson(res, ResponseBean.class);
    }

    public static ResponseBean getFailResponBean() {
        ResponseBean bean = new ResponseBean();
        bean.status = -1;
        bean.info = "";
        bean.data = "";
        return bean;
    }
}
