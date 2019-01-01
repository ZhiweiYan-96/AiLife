package com.record.view.chart.view.animation.easing.elastic;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class ElasticEaseOut implements BaseEasingMethod {
    public float next(float time) {
        if (time == 0.0f) {
            return 0.0f;
        }
        if (time == 1.0f) {
            return 1.0f;
        }
        return ((((float) Math.pow(2.0d, (double) (-10.0f * time))) * 1.0f) * ((float) Math.sin((double) ((((time * 1.0f) - (0.3f / 4.0f)) * 6.2831855f) / 0.3f)))) + 1.0f;
    }
}
