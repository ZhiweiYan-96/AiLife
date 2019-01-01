package com.record.view.floatingactionbutton.widget;

import android.content.Context;
import com.record.view.floatingactionbutton.constants.RFABSize;
import com.wangjie.androidbucket.utils.ABTextUtil;
import java.io.Serializable;

public class CircleButtonProperties implements Serializable {
    private int shadowColor;
    private int shadowDx;
    private int shadowDy;
    private int shadowRadius;
    private RFABSize standardSize;

    public RFABSize getStandardSize() {
        return this.standardSize;
    }

    public CircleButtonProperties setStandardSize(RFABSize standardSize) {
        this.standardSize = standardSize;
        return this;
    }

    public int getRealSizePx(Context context) {
        return getStandardSizePx(context) + (getShadowOffsetHalf() * 2);
    }

    public int getShadowOffsetHalf() {
        return this.shadowRadius <= 0 ? 0 : Math.max(this.shadowDx, this.shadowDy) + this.shadowRadius;
    }

    public int getStandardSizePx(Context context) {
        return ABTextUtil.dip2px(context, (float) this.standardSize.getDpSize());
    }

    public int getShadowColor() {
        return this.shadowColor;
    }

    public CircleButtonProperties setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public float getShadowRadius() {
        return (float) this.shadowRadius;
    }

    public CircleButtonProperties setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    public int getShadowDx() {
        return this.shadowDx;
    }

    public CircleButtonProperties setShadowDx(int shadowDx) {
        this.shadowDx = shadowDx;
        return this;
    }

    public int getShadowDy() {
        return this.shadowDy;
    }

    public CircleButtonProperties setShadowDy(int shadowDy) {
        this.shadowDy = shadowDy;
        return this;
    }
}
