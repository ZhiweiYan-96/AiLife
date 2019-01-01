package com.record.bean;

import java.util.HashMap;

public class HttpParams {
    HashMap<String, String> params = new HashMap();

    public HashMap<String, String> put(String str, String value) {
        this.params.put(str, value);
        return this.params;
    }
}
