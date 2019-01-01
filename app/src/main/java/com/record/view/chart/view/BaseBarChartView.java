package com.record.view.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import com.record.myLife.R;
import com.record.view.chart.model.ChartSet;
import java.util.ArrayList;

public abstract class BaseBarChartView extends ChartView {
    protected float barWidth;
    protected float drawingOffset;
    protected Style style;

    public class Style {
        private static final int DEFAULT_COLOR = -16777216;
        protected Paint barBackgroundPaint;
        protected Paint barPaint;
        protected float barSpacing;
        protected float cornerRadius;
        protected boolean hasBarBackground = false;
        private int mAlpha;
        private int mBarBackgroundColor = -16777216;
        private int mBlue;
        private int mGreen;
        private int mRed;
        private final int mShadowColor;
        private final float mShadowDx;
        private final float mShadowDy;
        private final float mShadowRadius;
        protected float setSpacing;

        protected Style() {
            this.barSpacing = BaseBarChartView.this.getResources().getDimension(R.dimen.bar_spacing);
            this.setSpacing = BaseBarChartView.this.getResources().getDimension(R.dimen.set_spacing);
            this.mShadowRadius = 0.0f;
            this.mShadowDx = 0.0f;
            this.mShadowDy = 0.0f;
            this.mShadowColor = -16777216;
        }

        protected Style(TypedArray attrs) {
            this.barSpacing = attrs.getDimension(0, BaseBarChartView.this.getResources().getDimension(R.dimen.bar_spacing));
            this.setSpacing = attrs.getDimension(0, BaseBarChartView.this.getResources().getDimension(R.dimen.set_spacing));
            this.mShadowRadius = attrs.getDimension(11, 0.0f);
            this.mShadowDx = attrs.getDimension(9, 0.0f);
            this.mShadowDy = attrs.getDimension(10, 0.0f);
            this.mShadowColor = attrs.getColor(8, 0);
        }

        private void init() {
            this.mAlpha = Color.alpha(this.mShadowColor);
            this.mRed = Color.red(this.mShadowColor);
            this.mBlue = Color.blue(this.mShadowColor);
            this.mGreen = Color.green(this.mShadowColor);
            this.barPaint = new Paint();
            this.barPaint.setStyle(android.graphics.Paint.Style.FILL);
            this.barPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
            this.barBackgroundPaint = new Paint();
            this.barBackgroundPaint.setColor(this.mBarBackgroundColor);
            this.barBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
        }

        private void clean() {
            this.barPaint = null;
            this.barBackgroundPaint = null;
        }

        protected void applyAlpha(Paint paint, float alpha) {
            paint.setAlpha((int) (alpha * 255.0f));
            paint.setShadowLayer(BaseBarChartView.this.style.mShadowRadius, BaseBarChartView.this.style.mShadowDx, BaseBarChartView.this.style.mShadowDy, Color.argb(((int) (alpha * 255.0f)) < BaseBarChartView.this.style.mAlpha ? (int) (alpha * 255.0f) : BaseBarChartView.this.style.mAlpha, BaseBarChartView.this.style.mRed, BaseBarChartView.this.style.mGreen, BaseBarChartView.this.style.mBlue));
        }
    }

    public BaseBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        this.style = new Style(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
    }

    public BaseBarChartView(Context context) {
        super(context);
        this.style = new Style();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.style.init();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.style.clean();
    }

    protected void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList) {
    }

    protected void drawBar(Canvas canvas, float left, float top, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) ((int) left), (float) ((int) top), (float) ((int) right), (float) ((int) bottom)), this.style.cornerRadius, this.style.cornerRadius, this.style.barPaint);
    }

    protected void drawBarBackground(Canvas canvas, float left, float top, float right, float bottom) {
        canvas.drawRoundRect(new RectF(left, top, right, bottom), this.style.cornerRadius, this.style.cornerRadius, this.style.barBackgroundPaint);
    }

    protected void calculateBarsWidth(int nSets, float x0, float x1) {
        this.barWidth = (((x1 - x0) - (this.style.barSpacing / 2.0f)) - (this.style.setSpacing * ((float) (nSets - 1)))) / ((float) nSets);
    }

    protected void calculatePositionOffset(int size) {
        if (size % 2 == 0) {
            this.drawingOffset = ((((float) size) * this.barWidth) / 2.0f) + (((float) (size - 1)) * (this.style.setSpacing / 2.0f));
        } else {
            this.drawingOffset = ((((float) size) * this.barWidth) / 2.0f) + (((float) ((size - 1) / 2)) * this.style.setSpacing);
        }
    }

    public void setBarSpacing(float spacing) {
        this.style.barSpacing = spacing;
    }

    public void setSetSpacing(float spacing) {
        this.style.setSpacing = spacing;
    }

    public void setBarBackground(boolean bool) {
        this.style.hasBarBackground = bool;
    }

    public void setBarBackgroundColor(int color) {
        this.style.mBarBackgroundColor = color;
    }

    public void setRoundCorners(float radius) {
        this.style.cornerRadius = radius;
    }
}
