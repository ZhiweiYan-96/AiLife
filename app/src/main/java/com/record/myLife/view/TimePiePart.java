package com.record.myLife.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.record.bean.TimePieRecord;

public class TimePiePart extends View {
    Paint mPaint;
    TimePieRecord timePieRecord;

    public class XChartCalc {
        private float posX = 0.0f;
        private float posY = 0.0f;

        public void CalcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle) {
            float arcAngle = (float) ((((double) cirAngle) * 3.141592653589793d) / 180.0d);
            if (cirAngle < 90.0f) {
                this.posX = (((float) Math.cos((double) arcAngle)) * radius) + cirX;
                this.posY = (((float) Math.sin((double) arcAngle)) * radius) + cirY;
            } else if (cirAngle == 90.0f) {
                this.posX = cirX;
                this.posY = cirY + radius;
            } else if (cirAngle > 90.0f && cirAngle < 180.0f) {
                arcAngle = (float) ((((double) (180.0f - cirAngle)) * 3.141592653589793d) / 180.0d);
                this.posX = cirX - (((float) Math.cos((double) arcAngle)) * radius);
                this.posY = (((float) Math.sin((double) arcAngle)) * radius) + cirY;
            } else if (cirAngle == 180.0f) {
                this.posX = cirX - radius;
                this.posY = cirY;
            } else if (cirAngle > 180.0f && cirAngle < 270.0f) {
                arcAngle = (float) ((((double) (cirAngle - 180.0f)) * 3.141592653589793d) / 180.0d);
                this.posX = cirX - (((float) Math.cos((double) arcAngle)) * radius);
                this.posY = cirY - (((float) Math.sin((double) arcAngle)) * radius);
            } else if (cirAngle == 270.0f) {
                this.posX = cirX;
                this.posY = cirY - radius;
            } else {
                arcAngle = (float) ((((double) (360.0f - cirAngle)) * 3.141592653589793d) / 180.0d);
                this.posX = (((float) Math.cos((double) arcAngle)) * radius) + cirX;
                this.posY = cirY - (((float) Math.sin((double) arcAngle)) * radius);
            }
        }

        public float getPosX() {
            return this.posX;
        }

        public float getPosY() {
            return this.posY;
        }
    }

    public TimePiePart(Context context) {
        super(context);
        if (this.mPaint == null) {
            init();
        }
    }

    public TimePiePart(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (this.mPaint == null) {
            init();
        }
    }

    public TimePiePart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (this.mPaint == null) {
            init();
        }
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setColor(-16777216);
        this.mPaint.setAlpha(100);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth(3.0f);
    }

    public void setRecord(TimePieRecord timePieRecord) {
        this.timePieRecord = timePieRecord;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("TimePie", "TimePie--onDraw");
        int height = getHeight();
        int width = getWidth();
        if (this.timePieRecord != null) {
            RectF rect = new RectF(0.0f, 0.0f, (float) height, (float) width);
            this.mPaint.setColor(this.timePieRecord.getColor());
            canvas.drawArc(rect, (float) this.timePieRecord.getStartAngle(), (float) this.timePieRecord.getSweepAngle(), true, this.mPaint);
            int nextAngle = this.timePieRecord.getStartAngle() + this.timePieRecord.getSweepAngle();
            if (nextAngle > 360) {
                nextAngle -= 360;
            }
            this.mPaint.setColor(-1);
            canvas.drawArc(rect, (float) nextAngle, (float) (360 - this.timePieRecord.getSweepAngle()), true, this.mPaint);
        }
        Paint mPaint = new Paint();
        mPaint.setColor(-16777216);
        mPaint.setStrokeJoin(Join.MITER);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setAlpha(100);
        canvas.drawCircle((float) (width / 2), (float) (height / 2), (float) ((height / 2) - 1), mPaint);
        XChartCalc calc = new XChartCalc();
        XChartCalc calc2 = new XChartCalc();
        int oneLenght = (width / 20) * 1;
        int twoLenght = (width / 20) * 2;
        for (int i = 0; i < 12; i++) {
            int angle = i * 30;
            calc.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((height / 2) - 1), (float) angle);
            if (angle % 30 == 0) {
                calc2.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((height / 2) - twoLenght), (float) angle);
            } else {
                calc2.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((height / 2) - oneLenght), (float) angle);
            }
            canvas.drawLine(calc2.getPosX(), calc2.getPosY(), calc.getPosX(), calc.getPosY(), mPaint);
        }
    }
}
