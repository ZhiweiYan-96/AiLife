package com.record.view.chart.view.animation.easing.cubic;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class CubicEaseOut implements BaseEasingMethod {
    public float next(float time) {
        return ((float) Math.pow((double) (time - 1.0f), 3.0d)) + 1.0f;
    }
}
