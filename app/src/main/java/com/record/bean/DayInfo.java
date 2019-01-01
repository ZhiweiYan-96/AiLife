package com.record.bean;

public class DayInfo implements Comparable<DayInfo> {
    String dayOfMonth;
    String dayOfWeek;
    int take;
    String time;
    int x;
    int y;

    public int getTake() {
        return this.take;
    }

    public void setTake(int take) {
        this.take = take;
    }

    public DayInfo(int x, int y, String time, int take, String dayOfMonth, String dayOfWeek) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.take = take;
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
    }

    public DayInfo(int x, int y, String time, String dayOfMonth, String dayOfWeek) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDayOfMonth() {
        return this.dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String toString() {
        return "x" + this.x + ",Y:" + this.y + ",take" + this.take + ",time" + this.time + ",号数：" + this.dayOfMonth + ",周数：" + this.dayOfWeek;
    }

    public int compareTo(DayInfo another) {
        return this.x > another.x ? 1 : -1;
    }
}
