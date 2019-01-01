package com.record.view.chart.view.animation.easing.bounce;

import com.record.view.chart.view.animation.easing.BaseEasingMethod;

public class BounceEaseOut implements BaseEasingMethod {
    public float next(float normalizedTime) {
        normalizedTime /= 1.0f;
        if (normalizedTime < 0.36363637f) {
            return (7.5625f * normalizedTime) * normalizedTime;
        }
        if (normalizedTime < 0.72727275f) {
            normalizedTime -= 0.54545456f;
            return ((7.5625f * normalizedTime) * normalizedTime) + 0.75f;
        } else if (((double) normalizedTime) < 0.9090909090909091d) {
            normalizedTime -= 0.8181818f;
            return ((7.5625f * normalizedTime) * normalizedTime) + 0.9375f;
        } else {
            normalizedTime -= 0.95454544f;
            return ((7.5625f * normalizedTime) * normalizedTime) + 0.984375f;
        }
    }
}
