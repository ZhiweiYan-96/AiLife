package com.record.view.chart.model;

public class Bar extends ChartEntry {
    private static final int DEFAULT_COLOR = -16777216;
    private int mColor = -16777216;

    public Bar(String label, float value) {
        super(label, value);
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }
}
