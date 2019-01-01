package com.record.conts;

import android.util.SparseArray;
import com.record.task.BaseTask;

public class RequestIDs {
    public static final int BASE_TASK = 1;
    public static SparseArray<String> classArr = new SparseArray<String>() {
        {
            put(1, BaseTask.class.getName());
        }
    };
}
