package com.record.view.chart.view;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import com.record.view.chart.view.AxisController.LabelPosition;
import java.util.Collections;
import java.util.Iterator;

public class YController extends AxisController {
    public YController(ChartView chartView) {
        super(chartView);
    }

    public YController(ChartView chartView, TypedArray attrs) {
        super(chartView, attrs);
    }

    protected void init() {
        if (this.labelsPositioning == LabelPosition.INSIDE) {
            this.distLabelToAxis *= -1;
        }
        defineLabels();
        defineMandatoryBorderSpacing(this.chartView.getInnerChartTop(), getInnerChartBottom());
        defineAxisHorizontalPosition();
        defineLabelsPos(this.chartView.getInnerChartTop(), getInnerChartBottom());
    }

    protected void defineAxisHorizontalPosition() {
        if (this.labelsPositioning == LabelPosition.OUTSIDE) {
            float maxLabelLength = 0.0f;
            Iterator it = this.labels.iterator();
            while (it.hasNext()) {
                float aux = this.chartView.style.labelPaint.measureText((String) it.next());
                if (aux > maxLabelLength) {
                    maxLabelLength = aux;
                }
            }
            this.axisHorPosition = (((float) this.chartView.chartLeft) + maxLabelLength) + ((float) this.distLabelToAxis);
            return;
        }
        this.axisHorPosition = (float) this.chartView.chartLeft;
    }

    protected void defineLabelsPos(float innerStart, float innerEnd) {
        super.defineLabelsPos(innerStart, innerEnd);
        Collections.reverse(this.labelsPos);
    }

    protected float parsePos(int index, double value) {
        if (!this.handleValues) {
            return ((Float) this.labelsPos.get(index)).floatValue();
        }
        return (float) (((double) this.chartView.horController.getAxisVerticalPosition()) - ((((double) this.screenStep) * (value - ((double) this.minLabelValue))) / ((double) (((Integer) this.labelsValues.get(1)).intValue() - this.minLabelValue))));
    }

    public float getInnerChartLeft() {
        if (this.hasAxis) {
            return this.axisHorPosition + (this.chartView.style.axisThickness / 2.0f);
        }
        return this.axisHorPosition;
    }

    public float getInnerChartBottom() {
        return this.chartView.horController.getAxisVerticalPosition() - (this.chartView.style.axisThickness / 2.0f);
    }

    protected void draw(Canvas canvas) {
        if (this.hasAxis) {
            canvas.drawLine(this.axisHorPosition, (float) this.chartView.chartTop, this.axisHorPosition, (this.chartView.style.axisThickness / 2.0f) + this.chartView.horController.getAxisVerticalPosition(), this.chartView.style.chartPaint);
        }
        if (this.labelsPositioning != LabelPosition.NONE) {
            this.chartView.style.labelPaint.setTextAlign(this.labelsPositioning == LabelPosition.OUTSIDE ? Align.RIGHT : Align.LEFT);
            for (int i = 0; i < this.nLabels; i++) {
                canvas.drawText((String) this.labels.get(i), (this.axisHorPosition - (this.chartView.style.axisThickness / 2.0f)) - ((float) this.distLabelToAxis), ((Float) this.labelsPos.get(i)).floatValue() + ((float) (getLabelHeight() / 2)), this.chartView.style.labelPaint);
            }
        }
    }
}
