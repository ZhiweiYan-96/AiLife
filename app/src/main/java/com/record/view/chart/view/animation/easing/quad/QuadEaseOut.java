package com.record.view.chart.view.animation.easing.quad;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class QuadEaseOut implements BaseEasingMethod {
    public float next(float time) {
        return (-time) * (time - 2.0f);
    }
}
