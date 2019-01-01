package com.record.bean;

public class Goal {
    String goalName;
    int id;

    public Goal(int id, String goalName) {
        this.id = id;
        this.goalName = goalName;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoalName() {
        return this.goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }
}
