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
import com.alibaba.fastjson.asm.Opcodes;
import com.record.bean.TimePieRecord;
import com.record.myLife.R;
import com.record.utils.DensityUtil;
import java.util.ArrayList;
import java.util.Iterator;

public class DayPie extends View {
    public static int SCALE = 24;
    private int bg_gray = getResources().getColor(R.color.gray);
    private int bg_red1 = getResources().getColor(R.color.bg_red1);
    private int bg_unknow_ivory = getResources().getColor(R.color.ivory);
    Context context;
    int insideCircleBound = 0;
    int insideCircleLeft = 0;
    int insideCircleTop = 0;
    private Paint mPaint;
    ArrayList<TimePieRecord> recordList;
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

    public DayPie(Context context) {
        super(context);
        this.context = context;
        if (this.mPaint == null) {
            getPaint();
        }
    }

    public DayPie(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (this.mPaint == null) {
            getPaint();
        }
    }

    public DayPie(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        if (this.mPaint == null) {
            getPaint();
        }
    }

    public void setRecord(TimePieRecord timePieRecord) {
        this.timePieRecord = timePieRecord;
        postInvalidate();
    }

    public void setRecords(ArrayList<TimePieRecord> recordList) {
        this.recordList = recordList;
        postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("TimePie", "TimePie--onDraw");
        RectF rect = new RectF(0.0f, 0.0f, (float) getHeight(), (float) getWidth());
        this.mPaint.setColor(this.bg_unknow_ivory);
        canvas.drawArc(rect, 0.0f, 360.0f, true, this.mPaint);
        if (this.recordList != null && this.recordList.size() > 0) {
            Iterator it = this.recordList.iterator();
            while (it.hasNext()) {
                canvas = drawArcM(rect, canvas, (TimePieRecord) it.next());
            }
        } else if (this.timePieRecord != null) {
            drawArcM(rect, canvas, this.timePieRecord);
        }
        drawCircleM(canvas);
        drawCircleInside(canvas);
        drawScale(canvas);
    }

    private void drawCircleInside(Canvas canvas) {
        this.insideCircleTop = (int) (((double) getHeight()) * 0.25d);
        this.insideCircleLeft = (int) (((double) getWidth()) * 0.25d);
        this.insideCircleBound = getHeight() - this.insideCircleTop;
        RectF rect = new RectF((float) this.insideCircleTop, (float) this.insideCircleLeft, (float) this.insideCircleBound, (float) this.insideCircleBound);
        this.mPaint.setColor(this.bg_gray);
        canvas.drawArc(rect, 0.0f, 360.0f, true, this.mPaint);
    }

    private void drawScale(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(-16777216);
        mPaint.setAlpha(60);
        mPaint.setStrokeJoin(Join.ROUND);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setAntiAlias(true);
        Paint mPaint2 = new Paint();
        mPaint2.setColor(-16777216);
        mPaint2.setAlpha(60);
        mPaint2.setStrokeJoin(Join.ROUND);
        mPaint2.setStrokeCap(Cap.ROUND);
        mPaint2.setStrokeWidth(2.0f);
        int textSize = DensityUtil.dip2px(this.context, 9.0f);
        int DP = DensityUtil.dip2px(this.context, 1.0f);
        mPaint2.setTextSize((float) textSize);
        mPaint2.setAntiAlias(true);
        int height = getHeight();
        int width = getWidth();
        XChartCalc calc = new XChartCalc();
        XChartCalc calc2 = new XChartCalc();
        int oneLenght = (int) ((((double) (this.insideCircleBound / 25)) * 1.0d) * 0.8d);
        int twoLenght = (int) ((((double) (this.insideCircleBound / 25)) * 1.5d) * 0.8d);
        int thirdLenght = (int) ((((double) (this.insideCircleBound / 25)) * 2.0d) * 0.8d);
        for (int i = 0; ((float) i) < 48.0f; i++) {
            int angle = (int) (((float) i) * (360.0f / 48.0f));
            calc.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((this.insideCircleBound / 3) - 1), (float) angle);
            if (angle % 30 == 0) {
                calc2.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((this.insideCircleBound / 3) - thirdLenght), (float) angle);
                if (angle == 0) {
                    canvas.drawText("6", (calc2.getPosX() - ((float) textSize)) + ((float) DP), calc2.getPosY() + ((float) ((textSize / 2) - DP)), mPaint2);
                } else if (angle == 90) {
                    canvas.drawText("12", calc2.getPosX() - ((float) ((textSize / 2) + DP)), calc2.getPosY() - ((float) (DP * 2)), mPaint2);
                } else if (angle == Opcodes.GETFIELD) {
                    canvas.drawText("18", calc2.getPosX() + ((float) (DP * 2)), calc2.getPosY() + ((float) ((textSize / 2) - DP)), mPaint2);
                } else if (angle == 270) {
                    canvas.drawText("0", calc2.getPosX() - ((float) ((textSize / 2) - (DP * 2))), calc2.getPosY() + ((float) (textSize + DP)), mPaint2);
                }
            } else if (angle % 15 == 0) {
                calc2.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((this.insideCircleBound / 3) - twoLenght), (float) angle);
            } else {
                calc2.CalcArcEndPointXY((float) (width / 2), (float) (height / 2), (float) ((this.insideCircleBound / 3) - oneLenght), (float) angle);
            }
            canvas.drawLine(calc2.getPosX(), calc2.getPosY(), calc.getPosX(), calc.getPosY(), mPaint);
        }
    }

    private void drawCircleM(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(-1);
        mPaint.setStrokeJoin(Join.MITER);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setAlpha(100);
        canvas.drawCircle((float) (getWidth() / 2), (float) (getHeight() / 2), (float) ((getHeight() / 2) - 1), mPaint);
    }

    private Canvas drawArcM(RectF rect, Canvas canvas, TimePieRecord timePieRecord) {
        if (timePieRecord != null) {
            this.mPaint.setColor(timePieRecord.getColor());
            canvas.drawArc(rect, (float) timePieRecord.getStartAngle(), (float) timePieRecord.getSweepAngle(), true, this.mPaint);
            int nextAngle = timePieRecord.getStartAngle() + timePieRecord.getSweepAngle();
            if (nextAngle > 360) {
                nextAngle -= 360;
            }
            this.mPaint.setColor(-1);
        }
        return canvas;
    }

    private void getPaint() {
        this.mPaint = new Paint();
        this.mPaint.setColor(-16777216);
        this.mPaint.setAlpha(100);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth(3.0f);
        this.mPaint.setAntiAlias(true);
    }
}
