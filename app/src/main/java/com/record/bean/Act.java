package com.record.bean;

public class Act {
    private static Act act;
    private String actName;
    private String color;
    private String deadline;
    private int expectSpend;
    private int hadSpend;
    private int hadWaste;
    private int id;
    private String image;
    private String intruction;
    private String level;
    private int position;
    private String startTime;
    private String timeOfEveryday;
    private int type;

    private Act() {
    }

    public static Act getInstance() {
        if (act == null) {
            act = new Act();
        }
        return act;
    }

    public void SetAct(int id, String name, String image, String color, int position) {
        this.id = id;
        this.actName = name;
        this.image = image;
        this.color = color;
        this.position = position;
    }

    public void SetAct(int id, String name, String image, String color, String intruction) {
        this.id = id;
        this.actName = name;
        this.image = image;
        this.color = color;
        this.intruction = intruction;
    }

    public void SetAct(int id, String name, String image, String color, String intruction, int type) {
        this.id = id;
        this.actName = name;
        this.image = image;
        this.color = color;
        this.intruction = intruction;
        this.type = type;
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

    public static void setAct(Act act) {
        act = act;
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

    public String getTimeOfEveryday() {
        return this.timeOfEveryday;
    }

    public void setTimeOfEveryday(String timeOfEveryday) {
        this.timeOfEveryday = timeOfEveryday;
    }

    public int getExpectSpend() {
        return this.expectSpend;
    }

    public void setExpectSpend(int expectSpend) {
        this.expectSpend = expectSpend;
    }

    public int getHadSpend() {
        return this.hadSpend;
    }

    public void setHadSpend(int hadSpend) {
        this.hadSpend = hadSpend;
    }

    public int getHadWaste() {
        return this.hadWaste;
    }

    public void setHadWaste(int hadWaste) {
        this.hadWaste = hadWaste;
    }

    public String getIntruction() {
        return this.intruction;
    }

    public void setIntruction(String intruction) {
        this.intruction = intruction;
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
}
