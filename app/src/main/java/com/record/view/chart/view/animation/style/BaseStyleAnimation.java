package com.record.view.chart.view.animation.style;

import com.record.view.chart.model.ChartSet;
import com.record.view.chart.view.ChartView;

public abstract class BaseStyleAnimation {
    private static final long DELAY_BETWEEN_UPDATES = 100;
    private Runnable mAnimator = new Runnable() {
        public void run() {
            if (BaseStyleAnimation.this.mChartView.canIPleaseAskYouToDraw()) {
                BaseStyleAnimation.this.mChartView.postInvalidate();
                BaseStyleAnimation.this.getUpdate(BaseStyleAnimation.this.mSet);
            }
        }
    };
    private ChartView mChartView;
    private ChartSet mSet;

    public abstract void nextUpdate(ChartSet chartSet);

    public void play(ChartView lineChartView, ChartSet set) {
        this.mChartView = lineChartView;
        this.mSet = set;
        getUpdate(this.mSet);
    }

    private void getUpdate(ChartSet set) {
        nextUpdate(set);
        this.mChartView.postDelayed(this.mAnimator, DELAY_BETWEEN_UPDATES);
    }
}
