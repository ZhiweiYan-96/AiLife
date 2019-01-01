package com.record.bean;

public class Record2 {
    private String begin;
    private int color;
    private String end;
    private int goalId;
    private int itemsId;

    public Record2(String begin, String end, int goalId, int color, int itemsId) {
        this.begin = begin;
        this.end = end;
        this.goalId = goalId;
        this.color = color;
        this.itemsId = itemsId;
    }

    public String getBegin() {
        return this.begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getGoalId() {
        return this.goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getItemsId() {
        return this.itemsId;
    }

    public void setItemsId(int itemsId) {
        this.itemsId = itemsId;
    }

    public String toString() {
        return "(begin:" + this.begin + ",end:" + this.end + ",ranage:" + ",recordId:" + this.goalId + ",color:" + this.color + ",itemsId:" + this.itemsId + ")";
    }
}
