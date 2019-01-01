package com.record.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ColorPickerDialog extends Dialog {
    private final String TAG;
    Context context;
    private final boolean debug;
    private int mInitialColor;
    private OnColorChangedListener mListener;
    private String mytitle;

    private class ColorPickerView extends View {
        private float centerRadius;
        private boolean downInCircle = true;
        private boolean downInRect;
        private boolean highlightCenter;
        private boolean highlightCenterLittle;
        private Paint mCenterPaint;
        private final int[] mCircleColors;
        private int mHeight;
        private Paint mLinePaint;
        private Paint mPaint;
        private final int[] mRectColors;
        private Paint mRectPaint;
        private int mWidth;
        private float r;
        private float rectBottom;
        private float rectLeft;
        private float rectRight;
        private Shader rectShader;
        private float rectTop;

        public ColorPickerView(Context context, int height, int width) {
            super(context);
            this.mHeight = height - 36;
            this.mWidth = width;
            setMinimumHeight(height - 36);
            setMinimumWidth(width);
            this.mCircleColors = new int[]{SupportMenu.CATEGORY_MASK, -65281, -16776961, -16711681, -16711936, InputDeviceCompat.SOURCE_ANY, SupportMenu.CATEGORY_MASK};
            Shader s = new SweepGradient(0.0f, 0.0f, this.mCircleColors, null);
            this.mPaint = new Paint(1);
            this.mPaint.setShader(s);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setStrokeWidth(50.0f);
            this.r = (((float) (width / 2)) * 0.7f) - (this.mPaint.getStrokeWidth() * 0.5f);
            this.mCenterPaint = new Paint(1);
            this.mCenterPaint.setColor(ColorPickerDialog.this.mInitialColor);
            this.mCenterPaint.setStrokeWidth(5.0f);
            this.centerRadius = (this.r - (this.mPaint.getStrokeWidth() / 2.0f)) * 0.7f;
            this.mLinePaint = new Paint(1);
            this.mLinePaint.setColor(Color.parseColor("#72A1D1"));
            this.mLinePaint.setStrokeWidth(4.0f);
            this.mRectColors = new int[]{-16777216, this.mCenterPaint.getColor(), -1};
            this.mRectPaint = new Paint(1);
            this.mRectPaint.setStrokeWidth(5.0f);
            this.rectLeft = (-this.r) - (this.mPaint.getStrokeWidth() * 0.5f);
            this.rectTop = ((this.r + (this.mPaint.getStrokeWidth() * 0.5f)) + (this.mLinePaint.getStrokeMiter() * 0.5f)) + 15.0f;
            this.rectRight = this.r + (this.mPaint.getStrokeWidth() * 0.5f);
            this.rectBottom = this.rectTop + 50.0f;
        }

        protected void onDraw(Canvas canvas) {
            canvas.translate((float) (this.mWidth / 2), (float) ((this.mHeight / 2) - 50));
            canvas.drawCircle(0.0f, 0.0f, this.centerRadius, this.mCenterPaint);
            if (this.highlightCenter || this.highlightCenterLittle) {
                int c = this.mCenterPaint.getColor();
                this.mCenterPaint.setStyle(Style.STROKE);
                if (this.highlightCenter) {
                    this.mCenterPaint.setAlpha(255);
                } else if (this.highlightCenterLittle) {
                    this.mCenterPaint.setAlpha(144);
                }
                canvas.drawCircle(0.0f, 0.0f, this.centerRadius + this.mCenterPaint.getStrokeWidth(), this.mCenterPaint);
                this.mCenterPaint.setStyle(Style.FILL);
                this.mCenterPaint.setColor(c);
            }
            canvas.drawOval(new RectF(-this.r, -this.r, this.r, this.r), this.mPaint);
            if (this.downInCircle) {
                this.mRectColors[1] = this.mCenterPaint.getColor();
            }
            this.rectShader = new LinearGradient(this.rectLeft, 0.0f, this.rectRight, 0.0f, this.mRectColors, null, TileMode.MIRROR);
            this.mRectPaint.setShader(this.rectShader);
            canvas.drawRect(this.rectLeft, this.rectTop, this.rectRight, this.rectBottom, this.mRectPaint);
            float offset = this.mLinePaint.getStrokeWidth() / 2.0f;
            Canvas canvas2 = canvas;
            canvas2.drawLine(this.rectLeft - offset, this.rectTop - (offset * 2.0f), this.rectLeft - offset, (offset * 2.0f) + this.rectBottom, this.mLinePaint);
            canvas2 = canvas;
            canvas2.drawLine(this.rectLeft - (offset * 2.0f), this.rectTop - offset, (offset * 2.0f) + this.rectRight, this.rectTop - offset, this.mLinePaint);
            canvas2 = canvas;
            canvas2.drawLine(this.rectRight + offset, this.rectTop - (offset * 2.0f), this.rectRight + offset, (offset * 2.0f) + this.rectBottom, this.mLinePaint);
            canvas2 = canvas;
            canvas2.drawLine(this.rectLeft - (offset * 2.0f), this.rectBottom + offset, (offset * 2.0f) + this.rectRight, this.rectBottom + offset, this.mLinePaint);
            super.onDraw(canvas);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - ((float) (this.mWidth / 2));
            float y = (event.getY() - ((float) (this.mHeight / 2))) + 50.0f;
            boolean inCircle = inColorCircle(x, y, this.r + (this.mPaint.getStrokeWidth() / 2.0f), this.r - (this.mPaint.getStrokeWidth() / 2.0f));
            boolean inCenter = inCenter(x, y, this.centerRadius);
            boolean inRect = inRect(x, y);
            switch (event.getAction()) {
                case 0:
                    this.downInCircle = inCircle;
                    this.downInRect = inRect;
                    this.highlightCenter = inCenter;
                    break;
                case 1:
                    if (this.highlightCenter && inCenter && ColorPickerDialog.this.mListener != null) {
                        ColorPickerDialog.this.mListener.colorChanged(this.mCenterPaint.getColor());
                        ColorPickerDialog.this.dismiss();
                    }
                    if (this.downInCircle) {
                        this.downInCircle = false;
                    }
                    if (this.downInRect) {
                        this.downInRect = false;
                    }
                    if (this.highlightCenter) {
                        this.highlightCenter = false;
                    }
                    if (this.highlightCenterLittle) {
                        this.highlightCenterLittle = false;
                    }
                    invalidate();
                    break;
                case 2:
                    break;
            }
            if (this.downInCircle && inCircle) {
                float unit = (float) (((double) ((float) Math.atan2((double) y, (double) x))) / 6.283185307179586d);
                if (unit < 0.0f) {
                    unit += 1.0f;
                }
                this.mCenterPaint.setColor(interpCircleColor(this.mCircleColors, unit));
                Log.v("ColorPicker", "色环内, 坐标: " + x + "," + y);
            } else if (this.downInRect && inRect) {
                this.mCenterPaint.setColor(interpRectColor(this.mRectColors, x));
            }
            Log.v("ColorPicker", "[MOVE] 高亮: " + this.highlightCenter + "微亮: " + this.highlightCenterLittle + " 中心: " + inCenter);
            if ((this.highlightCenter && inCenter) || (this.highlightCenterLittle && inCenter)) {
                this.highlightCenter = true;
                this.highlightCenterLittle = false;
            } else if (this.highlightCenter || this.highlightCenterLittle) {
                this.highlightCenter = false;
                this.highlightCenterLittle = true;
            } else {
                this.highlightCenter = false;
                this.highlightCenterLittle = false;
            }
            invalidate();
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(this.mWidth, this.mHeight);
        }

        private boolean inColorCircle(float x, float y, float outRadius, float inRadius) {
            double inCircle = (3.141592653589793d * ((double) inRadius)) * ((double) inRadius);
            double fingerCircle = 3.141592653589793d * ((double) ((x * x) + (y * y)));
            if (fingerCircle >= (3.141592653589793d * ((double) outRadius)) * ((double) outRadius) || fingerCircle <= inCircle) {
                return false;
            }
            return true;
        }

        private boolean inCenter(float x, float y, float centerRadius) {
            if (3.141592653589793d * ((double) ((x * x) + (y * y))) < (((double) centerRadius) * 3.141592653589793d) * ((double) centerRadius)) {
                return true;
            }
            return false;
        }

        private boolean inRect(float x, float y) {
            if (x > this.rectRight || x < this.rectLeft || y > this.rectBottom || y < this.rectTop) {
                return false;
            }
            return true;
        }

        private int interpCircleColor(int[] colors, float unit) {
            if (unit <= 0.0f) {
                return colors[0];
            }
            if (unit >= 1.0f) {
                return colors[colors.length - 1];
            }
            float p = unit * ((float) (colors.length - 1));
            int i = (int) p;
            p -= (float) i;
            int c0 = colors[i];
            int c1 = colors[i + 1];
            return Color.argb(ave(Color.alpha(c0), Color.alpha(c1), p), ave(Color.red(c0), Color.red(c1), p), ave(Color.green(c0), Color.green(c1), p), ave(Color.blue(c0), Color.blue(c1), p));
        }

        private int interpRectColor(int[] colors, float x) {
            int c0;
            int c1;
            float p;
            if (x < 0.0f) {
                c0 = colors[0];
                c1 = colors[1];
                p = (this.rectRight + x) / this.rectRight;
            } else {
                c0 = colors[1];
                c1 = colors[2];
                p = x / this.rectRight;
            }
            return Color.argb(ave(Color.alpha(c0), Color.alpha(c1), p), ave(Color.red(c0), Color.red(c1), p), ave(Color.green(c0), Color.green(c1), p), ave(Color.blue(c0), Color.blue(c1), p));
        }

        private int ave(int s, int d, float p) {
            return Math.round(((float) (d - s)) * p) + s;
        }
    }

    public interface OnColorChangedListener {
        void colorChanged(int i);
    }

    public ColorPickerDialog(Context context, String title, OnColorChangedListener listener) {
        this(context, -1, title, listener);
    }

    public ColorPickerDialog(Context context, int initialColor, String title, OnColorChangedListener listener) {
        super(context);
        this.debug = true;
        this.TAG = "ColorPicker";
        this.context = context;
        this.mytitle = title;
        this.mListener = listener;
        this.mInitialColor = initialColor;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager manager = getWindow().getWindowManager();
        ColorPickerView myView = new ColorPickerView(this.context, (int) (((float) manager.getDefaultDisplay().getHeight()) * 0.5f), (int) (((float) manager.getDefaultDisplay().getWidth()) * 0.7f));
        myView.setBackgroundColor(-1);
        setContentView(myView);
        setTitle(this.mytitle);
    }

    public int getmInitialColor() {
        return this.mInitialColor;
    }

    public void setmInitialColor(int mInitialColor) {
        this.mInitialColor = mInitialColor;
    }

    public OnColorChangedListener getmListener() {
        return this.mListener;
    }

    public void setmListener(OnColorChangedListener mListener) {
        this.mListener = mListener;
    }
}
