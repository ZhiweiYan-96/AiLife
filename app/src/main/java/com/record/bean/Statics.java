package com.record.bean;

public class Statics {
    String createTime;
    String deadline;
    double expectInvest;
    int goalId;
    String goalName;
    int goalType;
    double hadInvest;
    double sevenInvest;
    String startTime;
    int staticsType;
    double todayInvest;
    int userId;

    public Statics(int userId, int goalId, String goalName, int goalType, int staticsType, double expectInvest, double hadInvest, double todayInvest, double sevenInvest, String createTime, String startTime, String deadline) {
        this.userId = userId;
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalType = goalType;
        this.staticsType = staticsType;
        this.expectInvest = expectInvest;
        this.hadInvest = hadInvest;
        this.todayInvest = todayInvest;
        this.sevenInvest = sevenInvest;
        this.createTime = createTime;
        this.startTime = startTime;
        this.deadline = deadline;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGoalId() {
        return this.goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public String getGoalName() {
        return this.goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public int getGoalType() {
        return this.goalType;
    }

    public void setGoalType(int goalType) {
        this.goalType = goalType;
    }

    public int getStaticsType() {
        return this.staticsType;
    }

    public void setStaticsType(int staticsType) {
        this.staticsType = staticsType;
    }

    public double getExpectInvest() {
        return this.expectInvest;
    }

    public void setExpectInvest(double expectInvest) {
        this.expectInvest = expectInvest;
    }

    public double getHadInvest() {
        return this.hadInvest;
    }

    public void setHadInvest(double hadInvest) {
        this.hadInvest = hadInvest;
    }

    public double getTodayInvest() {
        return this.todayInvest;
    }

    public void setTodayInvest(double todayInvest) {
        this.todayInvest = todayInvest;
    }

    public double getSevenInvest() {
        return this.sevenInvest;
    }

    public void setSevenInvest(double sevenInvest) {
        this.sevenInvest = sevenInvest;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDeadline() {
        return this.deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
