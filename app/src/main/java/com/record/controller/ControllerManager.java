package com.record.controller;

import android.os.Message;
import com.record.myLife.IActivity;
import org.json.JSONObject;

public class ControllerManager {
    NetController netController;

    public ControllerManager() {
        if (this.netController == null) {
            this.netController = new NetController();
        }
    }

    public void startTask(JSONObject object) {
        startTask(1, object);
    }

    public void startTask(int requestId, JSONObject object) {
        Message msg = this.netController.getInhandler().obtainMessage();
        msg.obj = object;
        msg.what = requestId;
        this.netController.getInhandler().sendMessage(msg);
    }

    public void addIActivity(IActivity iActivity) {
        this.netController.addIActivity(iActivity);
    }

    public void removeIActivity(IActivity iActivity) {
        this.netController.removeIActivity(iActivity);
    }
}
