package com.record.bean.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class RecordBean {
    ArrayList<RecordBean> arrayList = new ArrayList();
    public String deleteTime;
    public String endUpdateTime;
    public String goalId;
    public String goalType;
    public String id;
    public String isDelete;
    public String isEnd;
    public String isRecord;
    public String remarks;
    public String startTime;
    public String stopTime;
    public String take;
    public String userId;

    public static ArrayList<RecordBean> getRecordBeanArr(String jsonArr) {
        ArrayList<RecordBean> arrayList = new ArrayList();
        return (ArrayList) new Gson().fromJson(jsonArr, new TypeToken<ArrayList<RecordBean>>() {
        }.getType());
    }
}
