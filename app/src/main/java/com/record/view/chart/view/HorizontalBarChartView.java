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

public class HorizontalBarChartView extends BaseBarChartView {
    public HorizontalBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(Orientation.HORIZONTAL);
        setMandatoryBorderSpacing();
    }

    public HorizontalBarChartView(Context context) {
        super(context);
        setOrientation(Orientation.HORIZONTAL);
        setMandatoryBorderSpacing();
    }

    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {
        int nSets = data.size();
        int yZeroCoord = (int) getZeroPosition();
        for (int i = ((ChartSet) data.get(0)).size() - 1; i >= 0; i--) {
            float offset = ((ChartSet) data.get(0)).getEntry(i).getY() - this.drawingOffset;
            for (int j = 0; j < nSets; j++) {
                BarSet barSet = (BarSet) data.get(j);
                Bar bar = (Bar) barSet.getEntry(i);
                if (barSet.isVisible() && bar.getValue() != 0.0f) {
                    this.style.barPaint.setColor(bar.getColor());
                    this.style.applyAlpha(this.style.barPaint, barSet.getAlpha());
                    if (this.style.hasBarBackground) {
                        drawBarBackground(canvas, getInnerChartLeft(), offset, getInnerChartRight(), offset + this.barWidth);
                    }
                    if (bar.getValue() > 0.0f) {
                        drawBar(canvas, (float) yZeroCoord, offset, bar.getX(), offset + this.barWidth);
                    } else {
                        drawBar(canvas, bar.getX(), offset, (float) yZeroCoord, offset + this.barWidth);
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
            calculateBarsWidth(data.size(), 0.0f, (getInnerChartBottom() - getInnerChartTop()) - (this.verController.borderSpacing * 2.0f));
        } else {
            calculateBarsWidth(data.size(), ((ChartSet) data.get(0)).getEntry(1).getY(), ((ChartSet) data.get(0)).getEntry(0).getY());
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
        for (i = nEntries - 1; i >= 0; i--) {
            float offset = ((ChartSet) data.get(0)).getEntry(i).getY() - this.drawingOffset;
            for (int j = 0; j < nSets; j++) {
                Bar bar = (Bar) ((BarSet) data.get(j)).getEntry(i);
                if (bar.getValue() > 0.0f) {
                    ((ArrayList) result.get(j)).add(new Region(yZeroCoord, (int) offset, (int) bar.getX(), (int) (this.barWidth + offset)));
                } else {
                    ((ArrayList) result.get(j)).add(new Region((int) bar.getX(), (int) offset, yZeroCoord, (int) (this.barWidth + offset)));
                }
                if (j != nSets - 1) {
                    offset += this.style.setSpacing;
                }
            }
        }
        return result;
    }
}
