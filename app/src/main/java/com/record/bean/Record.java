package com.record.bean;

public class Record {
    private int begin;
    private int color;
    private int end;
    private int goalId;
    private int itemsId;
    private int ranage;

    public Record(int begin, int end, int goalId, int color, int itemsId) {
        this.begin = begin;
        this.end = end;
        this.ranage = end - begin;
        this.goalId = goalId;
        this.color = color;
        this.itemsId = itemsId;
    }

    public Record(int begin, int end, int goalId, int color) {
        this.begin = begin;
        this.end = end;
        this.ranage = end - begin;
        this.goalId = goalId;
        this.color = color;
    }

    public int getBegin() {
        return this.begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getRanage() {
        return this.ranage;
    }

    public void setRanage(int ranage) {
        this.ranage = ranage;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getGoalId() {
        return this.goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getItemsId() {
        return this.itemsId;
    }

    public void setItemsId(int itemsId) {
        this.itemsId = itemsId;
    }

    public String toString() {
        return "(begin:" + this.begin + ",end:" + this.end + ",ranage:" + this.ranage + ",recordId:" + this.goalId + ",color:" + this.color + ",itemsId:" + this.itemsId + ")";
    }
}
