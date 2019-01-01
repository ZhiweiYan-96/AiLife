package com.record.bean.net;

import com.google.gson.Gson;

public class ParamsBean {
    public String base_url = "";
    public String data_from = "";
    public String is_return_params = "";
    public String requset_fail = "";
    public String requset_success = "";

    public static ParamsBean getParamsBean(String str) {
        return (ParamsBean) new Gson().fromJson(str, ParamsBean.class);
    }
}
