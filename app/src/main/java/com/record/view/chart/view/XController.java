package com.record.view.chart.view;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import com.record.view.chart.view.AxisController.LabelPosition;

public class XController extends AxisController {
    private int mLabelVerCoord;
    private float mLastLabelWidth;

    public XController(ChartView chartView) {
        super(chartView);
    }

    public XController(ChartView chartView, TypedArray attrs) {
        super(chartView);
    }

    protected void init() {
        this.mLabelVerCoord = this.chartView.chartBottom;
        if (this.labelsPositioning == LabelPosition.INSIDE) {
            this.mLabelVerCoord -= this.distLabelToAxis;
        }
        defineLabels();
        this.mLastLabelWidth = this.chartView.style.labelPaint.measureText((String) this.labels.get(this.nLabels - 1));
        defineMandatoryBorderSpacing(this.chartView.getInnerChartLeft(), getInnerChartRight());
        defineLabelsPos(this.chartView.getInnerChartLeft(), getInnerChartRight());
    }

    public float getInnerChartRight() {
        float rightBorder = 0.0f;
        if (this.borderSpacing + this.mandatoryBorderSpacing < this.mLastLabelWidth / 2.0f) {
            rightBorder = (this.mLastLabelWidth / 2.0f) - (this.borderSpacing + this.mandatoryBorderSpacing);
        }
        return ((float) this.chartView.chartRight) - rightBorder;
    }

    protected float getAxisVerticalPosition() {
        if (this.labelsPositioning != LabelPosition.OUTSIDE) {
            return (float) this.chartView.chartBottom;
        }
        return (float) ((this.chartView.chartBottom - getLabelHeight()) - this.distLabelToAxis);
    }

    protected float parsePos(int index, double value) {
        if (!this.handleValues) {
            return ((Float) this.labelsPos.get(index)).floatValue();
        }
        return (float) (((((double) this.screenStep) * (value - ((double) this.minLabelValue))) / ((double) (((Integer) this.labelsValues.get(1)).intValue() - this.minLabelValue))) + ((double) this.chartView.getInnerChartLeft()));
    }

    protected void draw(Canvas canvas) {
        if (this.hasAxis) {
            canvas.drawLine(this.chartView.getInnerChartLeft(), getAxisVerticalPosition(), getInnerChartRight(), getAxisVerticalPosition(), this.chartView.style.chartPaint);
        }
        if (this.labelsPositioning != LabelPosition.NONE) {
            this.chartView.style.labelPaint.setTextAlign(Align.CENTER);
            for (int i = 0; i < this.nLabels; i++) {
                canvas.drawText((String) this.labels.get(i), ((Float) this.labelsPos.get(i)).floatValue(), (float) this.mLabelVerCoord, this.chartView.style.labelPaint);
            }
        }
    }
}
