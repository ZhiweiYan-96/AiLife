package com.record.view.chart.model;

import android.graphics.drawable.Drawable;
import android.util.Log;
import com.record.view.chart.Tools;

public class LineSet extends ChartSet {
    private static final int DEFAULT_COLOR = -16777216;
    private static final float DOTS_RADIUS = 1.0f;
    private static final float DOTS_THICKNESS = 4.0f;
    private static final float LINE_THICKNESS = 4.0f;
    private static final String TAG = "com.db.chart.model.LineSet";
    private int mBegin = 0;
    private int mDotsColor = -16777216;
    private Drawable mDotsDrawable = null;
    private float mDotsRadius = Tools.fromDpToPx(4.0f);
    private int mDotsStrokeColor = -16777216;
    private float mDotsStrokeThickness = Tools.fromDpToPx(DOTS_RADIUS);
    private int mEnd = 0;
    private int mFillColor = -16777216;
    private int[] mGradientColors = null;
    private float[] mGradientPositions = null;
    private boolean mHasDots = false;
    private boolean mHasDotsStroke = false;
    private boolean mHasFill = false;
    private boolean mHasGradientFill = false;
    private boolean mIsDashed = false;
    private boolean mIsSmooth = false;
    private int mLineColor = -16777216;
    private float mLineThickness = Tools.fromDpToPx(4.0f);
    private int mPhase;

    public void addPoint(String label, float value) {
        addPoint(new Point(label, value));
    }

    public void addPoint(Point point) {
        addEntry(point);
    }

    public void addPoints(String[] labels, float[] values) {
        if (labels.length != values.length) {
            Log.e(TAG, "Arrays size doesn't match.", new IllegalArgumentException());
        }
        int nEntries = labels.length;
        for (int i = 0; i < nEntries; i++) {
            addPoint(labels[i], values[i]);
        }
    }

    public boolean hasDots() {
        return this.mHasDots;
    }

    public boolean hasDotsStroke() {
        return this.mHasDotsStroke;
    }

    public boolean isDashed() {
        return this.mIsDashed;
    }

    public boolean isSmooth() {
        return this.mIsSmooth;
    }

    public float getDotsStrokeThickness() {
        return this.mDotsStrokeThickness;
    }

    public float getLineThickness() {
        return this.mLineThickness;
    }

    public int getLineColor() {
        return this.mLineColor;
    }

    public int getDotsColor() {
        return this.mDotsColor;
    }

    public float getDotsRadius() {
        return this.mDotsRadius;
    }

    public int getDotsStrokeColor() {
        return this.mDotsStrokeColor;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public int[] getGradientColors() {
        return this.mGradientColors;
    }

    public float[] getGradientPositions() {
        return this.mGradientPositions;
    }

    public boolean hasFill() {
        return this.mHasFill;
    }

    public boolean hasGradientFill() {
        return this.mHasGradientFill;
    }

    public int getBegin() {
        return this.mBegin;
    }

    public int getEnd() {
        if (this.mEnd == 0) {
            return size();
        }
        return this.mEnd;
    }

    public Drawable getDotsDrawable() {
        return this.mDotsDrawable;
    }

    public int getPhase() {
        return this.mPhase;
    }

    public LineSet setDashed(boolean bool) {
        this.mIsDashed = bool;
        this.mPhase = 0;
        return this;
    }

    public void setPhase(int phase) {
        this.mPhase = phase;
    }

    public LineSet setSmooth(boolean bool) {
        this.mIsSmooth = bool;
        return this;
    }

    public LineSet setLineThickness(float thickness) {
        if (thickness <= 0.0f) {
            Log.e(TAG, "Line thickness <= 0.", new IllegalArgumentException());
        }
        this.mLineThickness = thickness;
        return this;
    }

    public LineSet setLineColor(int color) {
        this.mLineColor = color;
        return this;
    }

    public LineSet setDots(boolean bool) {
        this.mHasDots = bool;
        return this;
    }

    public LineSet setDotsColor(int color) {
        this.mDotsColor = color;
        return this;
    }

    public LineSet setDotsRadius(float radius) {
        this.mDotsRadius = radius;
        return this;
    }

    public LineSet setDotsStrokeThickness(float thickness) {
        if (thickness <= 0.0f) {
            Log.e(TAG, "Grid thickness <= 0.", new IllegalArgumentException());
        }
        this.mHasDotsStroke = true;
        this.mDotsStrokeThickness = thickness;
        return this;
    }

    public LineSet setDotsStrokeColor(int color) {
        this.mDotsStrokeColor = color;
        return this;
    }

    public LineSet setLineDashed(boolean bool) {
        this.mIsDashed = bool;
        return this;
    }

    public LineSet setLineSmooth(boolean bool) {
        this.mIsSmooth = bool;
        return this;
    }

    public LineSet setFill(int color) {
        this.mHasFill = true;
        this.mFillColor = color;
        return this;
    }

    public LineSet setGradientFill(int[] colors, float[] positions) {
        this.mHasGradientFill = true;
        this.mGradientColors = colors;
        this.mGradientPositions = positions;
        return this;
    }

    public LineSet setFill(boolean bool) {
        this.mHasFill = bool;
        return this;
    }

    public LineSet setDotsDrawable(Drawable drawable) {
        this.mDotsDrawable = drawable;
        return this;
    }

    public LineSet beginAt(int index) {
        if (index < 0) {
            Log.e(TAG, "Index can't be negative.", new IllegalArgumentException());
        }
        this.mBegin = index;
        return this;
    }

    public LineSet endAt(int index) {
        if (index > size()) {
            Log.e(TAG, "Index cannot be greater than the set's size.", new IllegalArgumentException());
        }
        this.mEnd = index;
        return this;
    }
}
