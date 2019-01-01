package com.record.bean;

import com.record.utils.Val;

public class NoSubmitTomato {
    public int delaySec;
    public String endTime;
    public int lengthSec;
    public String startTime;
    public int totalMin;
    public int totalSec;
    public int type;
    public String typeAction;

    public NoSubmitTomato(int type, String startTime, String endTime, int lengthSec, int delaySec) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lengthSec = lengthSec;
        this.delaySec = delaySec;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getLengthSec() {
        return this.lengthSec;
    }

    public void setLengthSec(int lengthSec) {
        this.lengthSec = lengthSec;
    }

    public int getDelaySec() {
        return this.delaySec;
    }

    public void setDelaySec(int delaySec) {
        this.delaySec = delaySec;
    }

    public String getTypeAction() {
        if (1 == this.type) {
            return Val.INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT;
        }
        if (2 == this.type) {
            return Val.INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT;
        }
        return "";
    }

    public int getTotalSec() {
        return this.lengthSec + this.delaySec;
    }

    public int getTotalMin() {
        return (this.lengthSec + this.delaySec) / 60;
    }

    public String toString() {
        return "type:" + this.type + ",startTime:" + this.startTime + ",endTime:" + this.endTime + ",lengthSec;" + this.lengthSec + ",delaySec:" + this.delaySec;
    }
}
