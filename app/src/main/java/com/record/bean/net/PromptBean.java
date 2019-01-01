package com.record.bean.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class PromptBean {
    public String content;
    public int s_id;

    public static ArrayList<PromptBean> getBeanArrs(String jsonArr) {
        ArrayList<PromptBean> items = new ArrayList();
        return (ArrayList) new Gson().fromJson(jsonArr, new TypeToken<ArrayList<PromptBean>>() {
        }.getType());
    }
}
