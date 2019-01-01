package com.record.controller;

import android.os.Handler;
import android.os.Message;
import com.record.task.AbsTask;
import com.record.task.TaskFactory;
import org.json.JSONObject;
//import u.aly.InstantMsg;

public class NetController extends Controller {
    public NetController() {
        init();
    }

    private void init() {
        start();
        initInHandler();
    }

    private void initInHandler() {
        this.inHandler = new Handler(this.inHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                AbsTask<InstantMsg> task = TaskFactory.createTask(msg.what, NetController.this, (JSONObject) msg.obj);
//                if (task != null) {
//                    NetController.this.taskExecutor.excecute(task);
//                }
            }
        };
    }
}
