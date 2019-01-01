package com.record.view.chart.model;

import android.util.Log;
import java.util.Iterator;

public class BarSet extends ChartSet {
    private static final String TAG = "com.db.chart.model.BarSet";

    public void addBar(String label, float value) {
        addBar(new Bar(label, value));
    }

    public void addBar(Bar point) {
        addEntry(point);
    }

    public void addBars(String[] labels, float[] values) {
        if (labels.length != values.length) {
            Log.e(TAG, "Arrays size doesn't match.", new IllegalArgumentException());
        }
        int nEntries = labels.length;
        for (int i = 0; i < nEntries; i++) {
            addBar(labels[i], values[i]);
        }
    }

    public int getColor() {
        return ((Bar) getEntry(0)).getColor();
    }

    public BarSet setColor(int color) {
        Iterator it = getEntries().iterator();
        while (it.hasNext()) {
            ((Bar) ((ChartEntry) it.next())).setColor(color);
        }
        return this;
    }
}
