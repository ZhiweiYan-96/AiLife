package com.record.bean;

public class Act2 {
    private String actName;
    private String color;
    private String deadline;
    private int expectSpend;
    private int expectSpend2;
    private int hadSpend;
    private int id;
    private String image;
    private String intruction;
    private int isHided;
    private int isSubGoal;
    private String level;
    private int position;
    private String startTime;
    private int timeOfEveryday;
    private int type;

    public int getIsHided() {
        return this.isHided;
    }

    public void setIsHided(int isHided) {
        this.isHided = isHided;
    }

    public int getIsSubGoal() {
        return this.isSubGoal;
    }

    public void setIsSubGoal(int isSubGoal) {
        this.isSubGoal = isSubGoal;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Act2(int id, String image, String color, String actName, String intruction, int expectSpend, int hadSpend, int position, int type, int isSubGoal, int isHided) {
        this.id = id;
        this.image = image;
        this.color = color;
        this.actName = actName;
        this.intruction = intruction;
        this.expectSpend = expectSpend;
        this.hadSpend = hadSpend;
        this.position = position;
        this.type = type;
        this.isSubGoal = isSubGoal;
        this.isHided = isHided;
    }

    public Act2(int id, String image, String color, String actName, String intruction, int expectSpend, int hadSpend, int position, int type, int isSubGoal, int isHided, int expectSpend2, int timeOfEveryday) {
        this.id = id;
        this.image = image;
        this.color = color;
        this.actName = actName;
        this.intruction = intruction;
        this.expectSpend = expectSpend;
        this.hadSpend = hadSpend;
        this.position = position;
        this.type = type;
        this.isSubGoal = isSubGoal;
        this.isHided = isHided;
        this.expectSpend2 = expectSpend2;
        this.timeOfEveryday = timeOfEveryday;
    }

    public Act2(int id, String image, String color, String actName, String intruction, String startTime, String deadline, String level, int timeOfEveryday, int expectSpend, int hadSpend, int position, int type) {
        this.id = id;
        this.image = image;
        this.color = color;
        this.actName = actName;
        this.intruction = intruction;
        this.startTime = startTime;
        this.deadline = deadline;
        this.level = level;
        this.timeOfEveryday = timeOfEveryday;
        this.expectSpend = expectSpend;
        this.hadSpend = hadSpend;
        this.position = position;
        this.type = type;
    }

    public Act2(int id, String image, String color, String actName, String intruction, String startTime, String deadline, String level, int timeOfEveryday, int expectSpend, int hadSpend, int position) {
        this.id = id;
        this.image = image;
        this.color = color;
        this.actName = actName;
        this.intruction = intruction;
        this.startTime = startTime;
        this.deadline = deadline;
        this.level = level;
        this.timeOfEveryday = timeOfEveryday;
        this.expectSpend = expectSpend;
        this.hadSpend = hadSpend;
        this.position = position;
    }

    public Act2(int id, String image, String color, String actName, String intruction, int position) {
        this.id = id;
        this.image = image;
        this.color = color;
        this.actName = actName;
        this.intruction = intruction;
        this.position = position;
    }

    public int getHadSpend() {
        return this.hadSpend;
    }

    public void setHadSpend(int hadSpend) {
        this.hadSpend = hadSpend;
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

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getTimeOfEveryday() {
        return this.timeOfEveryday;
    }

    public void setTimeOfEveryday(int timeOfEveryday) {
        this.timeOfEveryday = timeOfEveryday;
    }

    public int getExpectSpend() {
        return this.expectSpend;
    }

    public void setExpectSpend(int expectSpend) {
        this.expectSpend = expectSpend;
    }

    public int getExpectSpend2() {
        return this.expectSpend2;
    }

    public void setExpectSpend2(int expectSpend) {
        this.expectSpend2 = expectSpend;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getActName() {
        return this.actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getIntruction() {
        return this.intruction;
    }

    public void setIntruction(String intruction) {
        this.intruction = intruction;
    }

    public String toString() {
        return "id:" + this.id + ",名:" + this.actName + ",expectSpend:" + this.expectSpend + ",hadSpend" + this.hadSpend + ",isSubGoal" + this.isSubGoal + ",image:" + this.image + ",color" + this.color;
    }
}
