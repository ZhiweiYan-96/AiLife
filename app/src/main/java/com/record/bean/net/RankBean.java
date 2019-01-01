package com.record.bean.net;

import com.google.gson.Gson;

public class RankBean {
    public String rank_str;

    public static RankBean getBean(String json) {
        return (RankBean) new Gson().fromJson(json, RankBean.class);
    }
}
