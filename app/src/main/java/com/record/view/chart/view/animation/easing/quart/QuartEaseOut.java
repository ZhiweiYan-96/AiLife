package com.record.view.chart.view.animation.easing.quart;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class QuartEaseOut implements BaseEasingMethod {
    public float next(float time) {
        return ((float) Math.pow((double) (time - 1.0f), 4.0d)) + 1.0f;
    }
}
