package com.record.view.chart.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChartSet {
    private float mAlpha = 1.0f;
    private ArrayList<ChartEntry> mEntries = new ArrayList();
    private boolean mIsVisible = false;

    protected void addEntry(String label, float value) {
        this.mEntries.add(new ChartEntry(label, value));
    }

    protected void addEntry(ChartEntry e) {
        this.mEntries.add(e);
    }

    public float[][] updateValues(float[] newValues) {
        int nEntries = size();
        float[][] result = (float[][]) Array.newInstance(Float.TYPE, new int[]{nEntries, 2});
        for (int i = 0; i < nEntries; i++) {
            result[i][0] = ((ChartEntry) this.mEntries.get(i)).getX();
            result[i][1] = ((ChartEntry) this.mEntries.get(i)).getY();
            setValue(i, newValues[i]);
        }
        return result;
    }

    public ArrayList<ChartEntry> getEntries() {
        return this.mEntries;
    }

    public ChartEntry getEntry(int index) {
        return (ChartEntry) this.mEntries.get(index);
    }

    public float getValue(int index) {
        return ((ChartEntry) this.mEntries.get(index)).getValue();
    }

    public String getLabel(int index) {
        return ((ChartEntry) this.mEntries.get(index)).getLabel();
    }

    public float[][] getScreenPoints() {
        int nEntries = size();
        float[][] result = (float[][]) Array.newInstance(Float.TYPE, new int[]{nEntries, 2});
        for (int i = 0; i < nEntries; i++) {
            result[i][0] = ((ChartEntry) this.mEntries.get(i)).getX();
            result[i][1] = ((ChartEntry) this.mEntries.get(i)).getY();
        }
        return result;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    private void setValue(int index, float value) {
        ((ChartEntry) this.mEntries.get(index)).setValue(value);
    }

    public void setAlpha(float alpha) {
        if (alpha >= 1.0f) {
            alpha = 1.0f;
        }
        this.mAlpha = alpha;
    }

    public void setVisible(boolean visible) {
        this.mIsVisible = visible;
    }

    public String toString() {
        return this.mEntries.toString();
    }

    public int size() {
        return this.mEntries.size();
    }
}
