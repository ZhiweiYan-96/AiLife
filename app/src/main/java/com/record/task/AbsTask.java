package com.record.task;

import com.record.controller.Controller;
import java.util.concurrent.Callable;
import org.json.JSONObject;

public abstract class AbsTask<V> implements Runnable, Callable<V> {
    protected Controller controller;
    protected JSONObject params;
    protected TaskCaller taskCaller;
    protected TaskListener taskListener;

    protected abstract void initCaller();

    protected abstract void initListener();

    public AbsTask() {
        initListener();
        initCaller();
    }

    public AbsTask(Controller controller) {
        this.controller = controller;
        initListener();
        initCaller();
    }

    public AbsTask(Controller controller, JSONObject params) {
        this.controller = controller;
        this.params = params;
        initListener();
        initCaller();
    }

    public void run() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public V call() throws Exception {
        try {
            if (this.taskListener != null) {
                this.taskListener.onTaskStart(this);
            }
            Object object = getExecutorResult();
            if (this.taskListener != null) {
                this.taskListener.onTaskCompleted(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.taskListener != null) {
                this.taskListener.onTaskFail(this, e);
            }
        }
        return null;
    }

    protected Object getExecutorResult() throws Exception {
        if (this.taskCaller != null) {
            return this.taskCaller.execute();
        }
        return null;
    }
}
