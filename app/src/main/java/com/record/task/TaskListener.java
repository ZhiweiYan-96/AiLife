package com.record.task;

public interface TaskListener {
    void onTaskCompleted(Object obj);

    void onTaskFail(Object obj, Throwable th);

    void onTaskStart(Object obj);
}
