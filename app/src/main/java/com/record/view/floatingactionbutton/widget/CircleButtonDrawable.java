package com.record.view.floatingactionbutton.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class CircleButtonDrawable extends Drawable {
    private RectF bounds = new RectF();
    private CircleButtonProperties circleButtonProperties;
    private Context context;
    private float halfLen;
    private Paint paint;
    private int realSizePx;

    public CircleButtonDrawable(Context context, CircleButtonProperties circleButtonProperties, int color) {
        this.context = context;
        this.circleButtonProperties = circleButtonProperties;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.paint.setDither(true);
        this.paint.setStyle(Style.FILL);
        this.paint.setColor(color);
        this.paint.setShadowLayer(circleButtonProperties.getShadowRadius(), (float) circleButtonProperties.getShadowDx(), (float) circleButtonProperties.getShadowDy(), circleButtonProperties.getShadowColor());
        this.realSizePx = this.circleButtonProperties.getRealSizePx(context);
        setBounds(0, 0, this.realSizePx, this.realSizePx);
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds.right - bounds.left > 0 && bounds.bottom - bounds.top > 0) {
            this.bounds.left = (float) bounds.left;
            this.bounds.right = (float) bounds.right;
            this.bounds.top = (float) bounds.top;
            this.bounds.bottom = (float) bounds.bottom;
            this.halfLen = Math.min((this.bounds.right - this.bounds.left) / 2.0f, (this.bounds.bottom - this.bounds.top) / 2.0f);
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.halfLen, this.halfLen, (float) (this.circleButtonProperties.getStandardSizePx(this.context) / 2), this.paint);
    }

    public Paint getPaint() {
        return this.paint;
    }

    public CircleButtonDrawable setColor(int color) {
        this.paint.setColor(color);
        return this;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }
}
