package com.record.view.chart.view.animation.easing.expo;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class ExpoEaseOut implements BaseEasingMethod {
    public float next(float time) {
        return time == 1.0f ? 1.0f : 1.0f + (-((float) Math.pow(2.0d, (double) (-10.0f * time))));
    }
}
