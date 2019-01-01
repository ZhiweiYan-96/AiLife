package com.record.view.chart.view.animation.easing.circ;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class CircEaseOut implements BaseEasingMethod {
    public float next(float time) {
        time -= 1.0f;
        return ((float) Math.sqrt((double) (1.0f - (time * time)))) * 1.0f;
    }
}
