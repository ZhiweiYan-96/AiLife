package com.record.view.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import com.record.view.chart.model.Bar;
import com.record.view.chart.model.BarSet;
import com.record.view.chart.model.ChartSet;
import com.record.view.chart.view.ChartView.Orientation;
import java.util.ArrayList;

public class StackBarChartView extends BaseStackBarChartView {
    public StackBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    public StackBarChartView(Context context) {
        super(context);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {
        int dataSize = data.size();
        int setSize = ((ChartSet) data.get(0)).size();
        float innerChartBottom = getInnerChartBottom();
        for (int i = 0; i < setSize; i++) {
            if (this.style.hasBarBackground) {
                drawBarBackground(canvas, (float) ((int) (((ChartSet) data.get(0)).getEntry(i).getX() - (this.barWidth / 2.0f))), (float) ((int) getInnerChartTop()), (float) ((int) ((((ChartSet) data.get(0)).getEntry(i).getX() - (this.barWidth / 2.0f)) + this.barWidth)), (float) ((int) getInnerChartBottom()));
            }
            float verticalOffset = 0.0f;
            float nextBottomY = innerChartBottom;
            int bottomSetIndex = BaseStackBarChartView.discoverBottomSet(i, data);
            int topSetIndex = BaseStackBarChartView.discoverTopSet(i, data);
            for (int j = 0; j < dataSize; j++) {
                BarSet barSet = (BarSet) data.get(j);
                Bar bar = (Bar) barSet.getEntry(i);
                if (barSet.isVisible() && bar.getValue() > 0.0f) {
                    float x = bar.getX();
                    float y = bar.getY();
                    this.style.barPaint.setColor(bar.getColor());
                    this.style.applyAlpha(this.style.barPaint, barSet.getAlpha());
                    float dist = innerChartBottom - y;
                    Canvas canvas2;
                    if (j == bottomSetIndex) {
                        drawBar(canvas, (float) ((int) (x - (this.barWidth / 2.0f))), (float) ((int) (innerChartBottom - (dist + verticalOffset))), (float) ((int) ((this.barWidth / 2.0f) + x)), (float) ((int) nextBottomY));
                        if (!(bottomSetIndex == topSetIndex || this.style.cornerRadius == 0.0f)) {
                            canvas2 = canvas;
                            canvas2.drawRect(new Rect((int) (x - (this.barWidth / 2.0f)), (int) (innerChartBottom - (dist + verticalOffset)), (int) ((this.barWidth / 2.0f) + x), (int) ((innerChartBottom - (dist + verticalOffset)) + ((nextBottomY - (innerChartBottom - (dist + verticalOffset))) / 2.0f))), this.style.barPaint);
                        }
                    } else if (j == topSetIndex) {
                        drawBar(canvas, (float) ((int) (x - (this.barWidth / 2.0f))), (float) ((int) (innerChartBottom - (dist + verticalOffset))), (float) ((int) ((this.barWidth / 2.0f) + x)), (float) ((int) nextBottomY));
                        canvas2 = canvas;
                        canvas2.drawRect(new Rect((int) (x - (this.barWidth / 2.0f)), (int) (nextBottomY - ((nextBottomY - (innerChartBottom - (dist + verticalOffset))) / 2.0f)), (int) ((this.barWidth / 2.0f) + x), (int) nextBottomY), this.style.barPaint);
                    } else {
                        canvas.drawRect(new Rect((int) (x - (this.barWidth / 2.0f)), (int) (innerChartBottom - (dist + verticalOffset)), (int) ((this.barWidth / 2.0f) + x), (int) nextBottomY), this.style.barPaint);
                    }
                    nextBottomY = innerChartBottom - (dist + verticalOffset);
                    if (dist != 0.0f) {
                        verticalOffset += 2.0f + dist;
                    }
                }
            }
        }
    }

    public void onPreDrawChart(ArrayList<ChartSet> data) {
        if (((ChartSet) data.get(0)).size() == 1) {
            this.barWidth = (getInnerChartRight() - getInnerChartLeft()) - (this.horController.borderSpacing * 2.0f);
        } else {
            calculateBarsWidth(-1, ((ChartSet) data.get(0)).getEntry(0).getX(), ((ChartSet) data.get(0)).getEntry(1).getX());
        }
    }

    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
        int i;
        int dataSize = data.size();
        int setSize = ((ChartSet) data.get(0)).size();
        float innerChartBottom = getInnerChartBottom();
        ArrayList<ArrayList<Region>> result = new ArrayList(dataSize);
        for (i = 0; i < dataSize; i++) {
            result.add(new ArrayList(setSize));
        }
        for (i = 0; i < setSize; i++) {
            float verticalOffset = 0.0f;
            float nextBottomY = innerChartBottom;
            for (int j = 0; j < dataSize; j++) {
                Bar bar = (Bar) ((BarSet) data.get(j)).getEntry(i);
                float dist = innerChartBottom - bar.getY();
                ((ArrayList) result.get(j)).add(new Region((int) (bar.getX() - (this.barWidth / 2.0f)), (int) (innerChartBottom - (dist + verticalOffset)), (int) (bar.getX() + (this.barWidth / 2.0f)), (int) nextBottomY));
                nextBottomY = innerChartBottom - (dist + verticalOffset);
                verticalOffset += 2.0f + dist;
            }
        }
        return result;
    }
}
