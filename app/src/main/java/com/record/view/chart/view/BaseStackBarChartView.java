package com.record.view.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import com.record.view.chart.model.Bar;
import com.record.view.chart.model.BarSet;
import com.record.view.chart.model.ChartSet;
import java.util.ArrayList;

public abstract class BaseStackBarChartView extends BaseBarChartView {
    protected boolean mCalcMaxValue = true;

    public BaseStackBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseStackBarChartView(Context context) {
        super(context);
    }

    protected void calculateBarsWidth(int nSets, float x0, float x1) {
        this.barWidth = (x1 - x0) - this.style.barSpacing;
    }

    protected static int discoverBottomSet(int entryIndex, ArrayList<ChartSet> data) {
        int dataSize = data.size();
        int index = 0;
        while (index < dataSize && ((ChartSet) data.get(index)).getEntry(entryIndex).getValue() == 0.0f) {
            index++;
        }
        return index;
    }

    protected static int discoverTopSet(int entryIndex, ArrayList<ChartSet> data) {
        int index = data.size() - 1;
        while (index >= 0 && ((ChartSet) data.get(index)).getEntry(entryIndex).getValue() == 0.0f) {
            index--;
        }
        return index;
    }

    protected void calculateMaxStackBarValue() {
        int maxStackValue = 0;
        int dataSize = this.data.size();
        int setSize = ((ChartSet) this.data.get(0)).size();
        for (int i = 0; i < setSize; i++) {
            float stackValue = 0.0f;
            for (int j = 0; j < dataSize; j++) {
                stackValue += ((Bar) ((BarSet) this.data.get(j)).getEntry(i)).getValue();
            }
            if (maxStackValue < ((int) Math.ceil((double) stackValue))) {
                maxStackValue = (int) Math.ceil((double) stackValue);
            }
        }
        while (maxStackValue % getStep() != 0) {
            maxStackValue++;
        }
        super.setAxisBorderValues(0, maxStackValue, getStep());
    }

    public ChartView setAxisBorderValues(int minValue, int maxValue, int step) {
        this.mCalcMaxValue = false;
        return super.setAxisBorderValues(minValue, maxValue, step);
    }

    public void show() {
        if (this.mCalcMaxValue) {
            calculateMaxStackBarValue();
        }
        super.show();
    }
}
