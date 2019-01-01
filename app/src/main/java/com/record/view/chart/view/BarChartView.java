package com.record.view.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import com.record.view.chart.model.Bar;
import com.record.view.chart.model.BarSet;
import com.record.view.chart.model.ChartSet;
import com.record.view.chart.view.ChartView.Orientation;
import java.util.ArrayList;

public class BarChartView extends BaseBarChartView {
    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    public BarChartView(Context context) {
        super(context);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {
        int nSets = data.size();
        int nEntries = ((ChartSet) data.get(0)).size();
        int yZeroCoord = (int) getZeroPosition();
        for (int i = 0; i < nEntries; i++) {
            float offset = ((ChartSet) data.get(0)).getEntry(i).getX() - this.drawingOffset;
            for (int j = 0; j < nSets; j++) {
                BarSet barSet = (BarSet) data.get(j);
                Bar bar = (Bar) barSet.getEntry(i);
                if (barSet.isVisible() && bar.getValue() != 0.0f) {
                    this.style.barPaint.setColor(bar.getColor());
                    this.style.applyAlpha(this.style.barPaint, barSet.getAlpha());
                    if (this.style.hasBarBackground) {
                        drawBarBackground(canvas, offset, getInnerChartTop(), offset + this.barWidth, getInnerChartBottom());
                    }
                    if (bar.getValue() > 0.0f) {
                        drawBar(canvas, offset, bar.getY(), offset + this.barWidth, (float) yZeroCoord);
                    } else {
                        drawBar(canvas, offset, (float) yZeroCoord, offset + this.barWidth, bar.getY());
                    }
                    offset += this.barWidth;
                    if (j != nSets - 1) {
                        offset += this.style.setSpacing;
                    }
                }
            }
        }
    }

    protected void onPreDrawChart(ArrayList<ChartSet> data) {
        if (((ChartSet) data.get(0)).size() == 1) {
            this.style.barSpacing = 0.0f;
            calculateBarsWidth(data.size(), 0.0f, (getInnerChartRight() - getInnerChartLeft()) - (this.horController.borderSpacing * 2.0f));
        } else {
            calculateBarsWidth(data.size(), ((ChartSet) data.get(0)).getEntry(0).getX(), ((ChartSet) data.get(0)).getEntry(1).getX());
        }
        calculatePositionOffset(data.size());
    }

    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
        int i;
        int nSets = data.size();
        int nEntries = ((ChartSet) data.get(0)).size();
        int yZeroCoord = (int) getZeroPosition();
        ArrayList<ArrayList<Region>> result = new ArrayList(nSets);
        for (i = 0; i < nSets; i++) {
            result.add(new ArrayList(nEntries));
        }
        for (i = 0; i < nEntries; i++) {
            float offset = ((ChartSet) data.get(0)).getEntry(i).getX() - this.drawingOffset;
            for (int j = 0; j < nSets; j++) {
                Bar bar = (Bar) ((BarSet) data.get(j)).getEntry(i);
                if (bar.getValue() > 0.0f) {
                    offset += this.barWidth;
                    ((ArrayList) result.get(j)).add(new Region((int) offset, (int) bar.getY(), (int) offset, yZeroCoord));
                } else {
                    int i2 = (int) offset;
                    offset += this.barWidth;
                    ((ArrayList) result.get(j)).add(new Region(i2, yZeroCoord, (int) offset, (int) bar.getY()));
                }
                if (j != nSets - 1) {
                    offset += this.style.setSpacing;
                }
            }
        }
        return result;
    }
}
