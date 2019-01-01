package com.record.view.chart.view.animation.easing.quint;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class QuintEaseOut implements BaseEasingMethod {
    public float next(float time) {
        return ((float) Math.pow((double) (time - 1.0f), 5.0d)) + 1.0f;
    }
}
