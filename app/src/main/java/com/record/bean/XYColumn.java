package com.record.bean;

public class XYColumn implements Comparable<Integer> {
    String value;
    int x;
    int y;

    public XYColumn(int x, int y, String value) {
        this.x = x;
        this.y = y;
        this.value = value;
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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int compareTo(Integer another) {
        return this.x >= another.intValue() ? 1 : -1;
    }

    public String toString() {
        return "x:" + this.x + ",y:" + this.y + ",value:" + this.value;
    }
}
