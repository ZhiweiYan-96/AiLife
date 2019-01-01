package com.record.view.chart.view.animation.easing.linear;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class LinearEase implements BaseEasingMethod {
    public float next(float normalizedTime) {
        return normalizedTime;
    }
}
