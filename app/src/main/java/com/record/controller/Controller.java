package com.record.controller;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.record.myLife.IActivity;
import com.record.task.AbsTask;
import com.record.task.TaskExecutor;
import java.util.HashSet;
import java.util.Iterator;

public abstract class Controller {
    private static HashSet<IActivity> iActivitySet = new HashSet();
    protected AbsTask absTask;
    protected Handler inHandler;
    protected HandlerThread inHandlerThread;
    protected Handler nofiHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Iterator it = Controller.iActivitySet.iterator();
            while (it.hasNext()) {
                IActivity iActivity = (IActivity) it.next();
                if (iActivity != null) {
                    iActivity.refresh(msg);
                }
            }
        }
    };
    protected Handler outHandler;
    protected HandlerThread outHandlerThread;
    protected TaskExecutor taskExecutor;

    public void addIActivity(IActivity iActivity) {
        iActivitySet.add(iActivity);
    }

    public void removeIActivity(IActivity iActivity) {
        iActivitySet.remove(iActivity);
    }

    public Handler getInhandler() {
        return this.inHandler;
    }

    public Handler getOutHandler() {
        return this.outHandler;
    }

    protected void start() {
        this.inHandlerThread = new HandlerThread("inHandlerThread");
        this.outHandlerThread = new HandlerThread("outHandlerThread");
        this.inHandlerThread.start();
        this.outHandlerThread.start();
        this.outHandler = new Handler(this.outHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Message msg2 = Controller.this.outHandler.obtainMessage();
                msg2.copyFrom(msg);
                Controller.this.nofiHandler.sendMessage(msg2);
            }
        };
        this.taskExecutor = new TaskExecutor();
    }

    protected void dispose() {
        this.inHandlerThread.getLooper().quit();
        this.outHandlerThread.getLooper().quit();
        iActivitySet.clear();
        this.taskExecutor.shutdown();
    }
}
