package com.record.view.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Region;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import com.record.myLife.R;
import com.record.view.chart.Tools;
import com.record.view.chart.model.ChartEntry;
import com.record.view.chart.model.ChartSet;
import com.record.view.chart.model.LineSet;
import com.record.view.chart.view.ChartView.Orientation;
import java.util.ArrayList;
import java.util.Iterator;

public class LineChartView extends ChartView {
    private static float sRegionRadius;
    private Style mStyle;

    class Style {
        private int mAlpha;
        private int mBlue;
        private Paint mDotsPaint;
        private Paint mDotsStrokePaint;
        private Paint mFillPaint;
        private int mGreen;
        private Paint mLinePaint;
        private int mRed;
//        private final int mShadowColor;
//        private final float mShadowDx;
//        private final float mShadowDy;
//        private final float mShadowRadius;

//        protected Style() {
//            this.mShadowRadius = 0.0f;
//            this.mShadowDx = 0.0f;
//            this.mShadowDy = 0.0f;
//            this.mShadowColor = 0;
//        }

        protected Style(TypedArray attrs) {
//            this.mShadowRadius = attrs.getDimension(11, 0.0f);
//            this.mShadowDx = attrs.getDimension(9, 0.0f);
//            this.mShadowDy = attrs.getDimension(10, 0.0f);
//            this.mShadowColor = attrs.getColor(8, 0);
        }

        private void init() {
//            this.mAlpha = Color.alpha(this.mShadowColor);
//            this.mRed = Color.red(this.mShadowColor);
//            this.mBlue = Color.blue(this.mShadowColor);
//            this.mGreen = Color.green(this.mShadowColor);
            this.mDotsPaint = new Paint();
            this.mDotsPaint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
            this.mDotsPaint.setAntiAlias(true);
            this.mDotsStrokePaint = new Paint();
            this.mDotsStrokePaint.setStyle(android.graphics.Paint.Style.STROKE);
            this.mDotsStrokePaint.setAntiAlias(true);
            this.mLinePaint = new Paint();
            this.mLinePaint.setStyle(android.graphics.Paint.Style.STROKE);
            this.mLinePaint.setAntiAlias(true);
            this.mFillPaint = new Paint();
            this.mFillPaint.setStyle(android.graphics.Paint.Style.FILL);
        }

        private void clean() {
            this.mLinePaint = null;
            this.mFillPaint = null;
            this.mDotsPaint = null;
        }
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(Orientation.VERTICAL);
//        this.mStyle = new Style(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
        sRegionRadius = getResources().getDimension(R.dimen.dot_region_radius);
    }

    public LineChartView(Context context) {
        super(context);
        setOrientation(Orientation.VERTICAL);
//        this.mStyle = new Style();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mStyle.init();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mStyle.clean();
    }

    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {
        Iterator it = data.iterator();
        while (it.hasNext()) {
            LineSet lineSet = (LineSet) ((ChartSet) it.next());
            if (lineSet.isVisible()) {
                this.mStyle.mLinePaint.setColor(lineSet.getLineColor());
                this.mStyle.mLinePaint.setStrokeWidth(lineSet.getLineThickness());
                applyAlpha(this.mStyle.mLinePaint, lineSet.getAlpha());
                if (lineSet.isDashed()) {
                    this.mStyle.mLinePaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 10.0f}, (float) lineSet.getPhase()));
                } else {
                    this.mStyle.mLinePaint.setPathEffect(null);
                }
                if (lineSet.isSmooth()) {
                    drawSmoothLine(canvas, lineSet);
                } else {
                    drawLine(canvas, lineSet);
                }
                if (lineSet.hasDots()) {
                    drawPoints(canvas, lineSet);
                }
            }
        }
    }

    private void drawPoints(Canvas canvas, LineSet set) {
        Bitmap dotsBitmap = null;
        float dotsBitmapWidthCenter = 0.0f;
        float dotsBitmapHeightCenter = 0.0f;
        if (set.getDotsDrawable() != null) {
            dotsBitmap = Tools.drawableToBitmap(set.getDotsDrawable());
            dotsBitmapWidthCenter = (float) (dotsBitmap.getWidth() / 2);
            dotsBitmapHeightCenter = (float) (dotsBitmap.getHeight() / 2);
        }
        this.mStyle.mDotsPaint.setColor(set.getDotsColor());
        applyAlpha(this.mStyle.mDotsPaint, set.getAlpha());
        this.mStyle.mDotsStrokePaint.setStrokeWidth(set.getDotsStrokeThickness());
        this.mStyle.mDotsStrokePaint.setColor(set.getDotsStrokeColor());
        applyAlpha(this.mStyle.mDotsStrokePaint, set.getAlpha());
        Path path = new Path();
        int begin = set.getBegin();
        int end = set.getEnd();
        for (int i = begin; i < end; i++) {
            path.addCircle(set.getEntry(i).getX(), set.getEntry(i).getY(), set.getDotsRadius(), Direction.CW);
            if (dotsBitmap != null) {
                canvas.drawBitmap(dotsBitmap, set.getEntry(i).getX() - dotsBitmapWidthCenter, set.getEntry(i).getY() - dotsBitmapHeightCenter, this.mStyle.mDotsPaint);
            }
        }
        canvas.drawPath(path, this.mStyle.mDotsPaint);
        if (set.hasDotsStroke()) {
            canvas.drawPath(path, this.mStyle.mDotsStrokePaint);
        }
    }

    public void drawLine(Canvas canvas, LineSet set) {
        float minY = getInnerChartBottom();
        Path path = new Path();
        Path bgPath = new Path();
        int begin = set.getBegin();
        int end = set.getEnd();
        for (int i = begin; i < end; i++) {
            float x = set.getEntry(i).getX();
            float y = set.getEntry(i).getY();
            if (y < minY) {
                minY = y;
            }
            if (i == begin) {
                path.moveTo(x, y);
                bgPath.moveTo(x, y);
            } else {
                path.lineTo(x, y);
                bgPath.lineTo(x, y);
            }
        }
        if (set.hasFill() || set.hasGradientFill()) {
            drawBackground(canvas, bgPath, set, minY);
        }
        canvas.drawPath(path, this.mStyle.mLinePaint);
    }

    private void drawSmoothLine(Canvas canvas, LineSet set) {
        float minY = getInnerChartBottom();
        Path path = new Path();
        path.moveTo(set.getEntry(set.getBegin()).getX(), set.getEntry(set.getBegin()).getY());
        Path bgPath = new Path();
        bgPath.moveTo(set.getEntry(set.getBegin()).getX(), set.getEntry(set.getBegin()).getY());
        int begin = set.getBegin();
        int end = set.getEnd();
        for (int i = begin; i < end - 1; i++) {
            float x = set.getEntry(i).getX();
            float y = set.getEntry(i).getY();
            if (y < minY) {
                minY = y;
            }
            float thisPointX = x;
            float thisPointY = y;
            float nextPointX = set.getEntry(i + 1).getX();
            float nextPointY = set.getEntry(i + 1).getY();
            float startdiffX = nextPointX - set.getEntry(si(set.size(), i - 1)).getX();
            float startdiffY = nextPointY - set.getEntry(si(set.size(), i - 1)).getY();
            float endDiffX = set.getEntry(si(set.size(), i + 2)).getX() - thisPointX;
            float firstControlX = thisPointX + (0.15f * startdiffX);
            float firstControlY = thisPointY + (0.15f * startdiffY);
            float secondControlX = nextPointX - (0.15f * endDiffX);
            float secondControlY = nextPointY - (0.15f * (set.getEntry(si(set.size(), i + 2)).getY() - thisPointY));
            path.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX, nextPointY);
            bgPath.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX, nextPointY);
        }
        if (set.hasFill() || set.hasGradientFill()) {
            drawBackground(canvas, bgPath, set, minY);
        }
        canvas.drawPath(path, this.mStyle.mLinePaint);
    }

    private void drawBackground(Canvas canvas, Path path, LineSet set, float minDisplayY) {
        float innerChartBottom = super.getInnerChartBottom();
        this.mStyle.mFillPaint.setAlpha((int) (set.getAlpha() * 255.0f));
        if (set.hasFill()) {
            this.mStyle.mFillPaint.setColor(set.getFillColor());
        }
        if (set.hasGradientFill()) {
            this.mStyle.mFillPaint.setShader(new LinearGradient(super.getInnerChartLeft(), minDisplayY, super.getInnerChartLeft(), innerChartBottom, set.getGradientColors(), set.getGradientPositions(), TileMode.MIRROR));
        }
        path.lineTo(set.getEntry(set.getEnd() - 1).getX(), innerChartBottom);
        path.lineTo(set.getEntry(set.getBegin()).getX(), innerChartBottom);
        path.close();
        canvas.drawPath(path, this.mStyle.mFillPaint);
    }

    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
        ArrayList<ArrayList<Region>> result = new ArrayList();
        Iterator it = data.iterator();
        while (it.hasNext()) {
            ChartSet set = (ChartSet) it.next();
            ArrayList<Region> regionSet = new ArrayList(set.size());
            Iterator it2 = set.getEntries().iterator();
            while (it2.hasNext()) {
                ChartEntry e = (ChartEntry) it2.next();
                float x = e.getX();
                float y = e.getY();
                regionSet.add(new Region((int) (x - sRegionRadius), (int) (y - sRegionRadius), (int) (sRegionRadius + x), (int) (sRegionRadius + y)));
            }
            result.add(regionSet);
        }
        return result;
    }

    private void applyAlpha(Paint paint, float alpha) {
        int i;
        paint.setAlpha((int) (alpha * 255.0f));
//        float access$600 = this.mStyle.mShadowRadius;
//        float access$700 = this.mStyle.mShadowDx;
//        float access$800 = this.mStyle.mShadowDy;
        if (((int) (alpha * 255.0f)) < this.mStyle.mAlpha) {
            i = (int) (alpha * 255.0f);
        } else {
            i = this.mStyle.mAlpha;
        }
//        paint.setShadowLayer(access$600, access$700, access$800, Color.argb(i, this.mStyle.mRed, this.mStyle.mGreen, this.mStyle.mBlue));
    }

    private static int si(int setSize, int i) {
        if (i > setSize - 1) {
            return setSize - 1;
        }
        if (i < 0) {
            return 0;
        }
        return i;
    }
}
