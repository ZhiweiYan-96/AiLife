package com.record.view.chart.view;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import com.record.myLife.R;
import com.record.view.chart.model.ChartEntry;
import com.record.view.chart.model.ChartSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AxisController {
    private static final int DEFAULT_STEP = 1;
    protected float axisHorPosition;
    protected float borderSpacing;
    protected ChartView chartView;
    protected int distLabelToAxis;
    protected boolean handleValues;
    protected boolean hasAxis;
    protected DecimalFormat labelFormat;
    protected int labelHeight;
    protected ArrayList<String> labels;
    protected ArrayList<Float> labelsPos;
    protected LabelPosition labelsPositioning;
    protected ArrayList<Integer> labelsValues;
    protected float mandatoryBorderSpacing;
    protected int maxLabelValue;
    protected int minLabelValue;
    protected int nLabels;
    protected float screenStep;
    protected int step;
    protected float topSpacing;

    public enum LabelPosition {
        NONE,
        OUTSIDE,
        INSIDE
    }

    protected abstract void draw(Canvas canvas);

    public AxisController(ChartView view) {
        this.chartView = view;
        this.distLabelToAxis = (int) this.chartView.getResources().getDimension(R.dimen.axis_dist_from_label);
        this.mandatoryBorderSpacing = 0.0f;
        this.borderSpacing = 0.0f;
        this.topSpacing = 0.0f;
        this.step = 1;
        this.labelsPositioning = LabelPosition.OUTSIDE;
        this.labelFormat = new DecimalFormat();
        this.axisHorPosition = 0.0f;
        this.minLabelValue = 0;
        this.maxLabelValue = 0;
        this.labelHeight = -1;
        this.hasAxis = true;
        this.handleValues = false;
    }

    public AxisController(ChartView chartView, TypedArray attrs) {
        this(chartView);
    }

    protected void defineLabels() {
        this.labelsValues = calcLabels();
        if (this.handleValues) {
            this.labels = getLabelsFromValues();
        } else {
            this.labels = getLabelsFromData();
        }
        this.nLabels = this.labels.size();
    }

    protected void defineMandatoryBorderSpacing(float innerStart, float innerEnd) {
        if (this.mandatoryBorderSpacing == 1.0f) {
            this.mandatoryBorderSpacing = (((innerEnd - innerStart) - (this.borderSpacing * 2.0f)) / ((float) this.nLabels)) / 2.0f;
        }
    }

    protected void defineLabelsPos(float innerStart, float innerEnd) {
        this.labelsPos = new ArrayList(this.nLabels);
        this.screenStep = ((((innerEnd - innerStart) - this.topSpacing) - (this.borderSpacing * 2.0f)) - (this.mandatoryBorderSpacing * 2.0f)) / ((float) (this.nLabels - 1));
        float currPos = (this.borderSpacing + innerStart) + this.mandatoryBorderSpacing;
        for (int i = 0; i < this.nLabels; i++) {
            this.labelsPos.add(Float.valueOf(currPos));
            currPos += this.screenStep;
        }
    }

    private ArrayList<String> getLabelsFromValues() {
        int size = this.labelsValues.size();
        ArrayList<String> result = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            result.add(this.labelFormat.format(this.labelsValues.get(i)));
        }
        return result;
    }

    protected ArrayList<String> getLabelsFromData() {
        int size = ((ChartSet) this.chartView.data.get(0)).size();
        ArrayList<String> result = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            result.add(((ChartSet) this.chartView.data.get(0)).getLabel(i));
        }
        return result;
    }

    private float[] calcBorderValues() {
        float max = -2.14748365E9f;
        float min = 2.14748365E9f;
        Iterator it = this.chartView.data.iterator();
        while (it.hasNext()) {
            Iterator it2 = ((ChartSet) it.next()).getEntries().iterator();
            while (it2.hasNext()) {
                ChartEntry e = (ChartEntry) it2.next();
                if (e.getValue() >= max) {
                    max = e.getValue();
                }
                if (e.getValue() <= min) {
                    min = e.getValue();
                }
            }
        }
        return new float[]{min, max};
    }

    private ArrayList<Integer> calcLabels() {
        float[] borderValues = calcBorderValues();
        float minValue = borderValues[0];
        float maxValue = borderValues[1];
        if (this.minLabelValue == 0 && this.maxLabelValue == 0) {
            if (maxValue < 0.0f) {
                this.maxLabelValue = 0;
            } else {
                this.maxLabelValue = (int) Math.ceil((double) maxValue);
            }
            if (minValue > 0.0f) {
                this.minLabelValue = 0;
            } else {
                this.minLabelValue = (int) Math.floor((double) minValue);
            }
            while ((this.maxLabelValue - this.minLabelValue) % this.step != 0) {
                this.maxLabelValue++;
            }
        }
        ArrayList<Integer> result = new ArrayList();
        int pos = this.minLabelValue;
        while (pos <= this.maxLabelValue) {
            result.add(Integer.valueOf(pos));
            pos += this.step;
        }
        if (((Integer) result.get(result.size() - 1)).intValue() < this.maxLabelValue) {
            result.add(Integer.valueOf(this.maxLabelValue));
        }
        return result;
    }

    protected int getLabelHeight() {
        if (this.labelHeight == -1) {
            int result = 0;
            Iterator it = ((ChartSet) this.chartView.data.get(0)).getEntries().iterator();
            while (it.hasNext()) {
                result = this.chartView.style.getTextHeightBounds(((ChartEntry) it.next()).getLabel());
                if (result != 0) {
                    break;
                }
            }
            this.labelHeight = result;
        }
        return this.labelHeight;
    }
}
