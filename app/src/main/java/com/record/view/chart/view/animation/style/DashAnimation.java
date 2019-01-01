package com.record.view.chart.view.animation.style;

import com.record.view.chart.model.ChartSet;
import com.record.view.chart.model.LineSet;

public class DashAnimation extends BaseStyleAnimation {
    public void nextUpdate(ChartSet set) {
        LineSet line = (LineSet) set;
        line.setPhase(line.getPhase() - 4);
    }
}
