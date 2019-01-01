package com.record.bean.dbbean;

public class Allocation {
    private static Allocation act;
    private String date = "";
    private double earnMoney = 0.0d;
    private int id = 0;
    private int invest = 0;
    private String morningVoice = "";
    private String remarks = "";
    private int routine = 0;
    private int sleep = 0;
    private String userId = "";
    private int waste = 0;

    public Allocation(String date, double earnMoney, int id, int invest, String morningVoice, String remarks, int routine, int sleep, String userId, int waste) {
        this.date = date;
        this.earnMoney = earnMoney;
        this.id = id;
        this.invest = invest;
        this.morningVoice = morningVoice;
        this.remarks = remarks;
        this.routine = routine;
        this.sleep = sleep;
        this.userId = userId;
        this.waste = waste;
    }

    public static Allocation getAct() {
        return act;
    }

    public static void setAct(Allocation act) {
        act = act;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getInvest() {
        return this.invest;
    }

    public void setInvest(int invest) {
        this.invest = invest;
    }

    public int getWaste() {
        return this.waste;
    }

    public void setWaste(int waste) {
        this.waste = waste;
    }

    public int getRoutine() {
        return this.routine;
    }

    public void setRoutine(int routine) {
        this.routine = routine;
    }

    public int getSleep() {
        return this.sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMorningVoice() {
        return this.morningVoice;
    }

    public void setMorningVoice(String morningVoice) {
        this.morningVoice = morningVoice;
    }

    public double getEarnMoney() {
        return this.earnMoney;
    }

    public void setEarnMoney(double earnMoney) {
        this.earnMoney = earnMoney;
    }
}
