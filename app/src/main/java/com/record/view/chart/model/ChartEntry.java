package com.record.view.chart.model;

public class ChartEntry {
    private String mLabel;
    private float mValue;
    private float mX;
    private float mY;

    public ChartEntry(String label, float value) {
        this.mLabel = label;
        this.mValue = value;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public float getValue() {
        return this.mValue;
    }

    public float getX() {
        return this.mX;
    }

    public float getY() {
        return this.mY;
    }

    public void setValue(float value) {
        this.mValue = value;
    }

    public void setCoordinates(float x, float y) {
        this.mX = x;
        this.mY = y;
    }

    public String toString() {
        return "Label=" + this.mLabel + " \n" + "Value=" + this.mValue + "\n" + "X = " + this.mX + "\n" + "Y = " + this.mY;
    }
}
