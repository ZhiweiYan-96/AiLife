package com.record.view.chart.view.animation.easing.sine;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class SineEaseOut implements BaseEasingMethod {
    public float next(float time) {
        return (float) Math.sin(((double) time) * 1.5707963267948966d);
    }
}
