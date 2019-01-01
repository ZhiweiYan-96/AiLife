package com.record.utils;

import java.util.HashMap;

public class UploadUtils {
    public static HashMap<String, String> getPairByTable(String tableName) {
        if ("t_act".equals(tableName)) {
            return pairGoalTable();
        }
        return null;
    }

    public static HashMap<String, String> pairGoalTable() {
        HashMap<String, String> map = new HashMap();
        map.put("severId", "sGoalId");
        map.put("actName", "goalName");
        return map;
    }
}
