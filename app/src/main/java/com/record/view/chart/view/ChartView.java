package com.record.view.chart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.record.myLife.R;
import com.record.view.chart.listener.OnEntryClickListener;
import com.record.view.chart.model.ChartSet;
import com.record.view.chart.view.AxisController.LabelPosition;
import com.record.view.chart.view.animation.Animation;
import com.record.view.chart.view.animation.style.BaseStyleAnimation;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ChartView extends RelativeLayout {
    private static final String TAG = "com.db.chart.view.ChartView";
    protected int chartBottom;
    protected int chartLeft;
    protected int chartRight;
    protected int chartTop;
    protected ArrayList<ChartSet> data;
    private OnPreDrawListener drawListener;
    protected XController horController;
    private Animation mAnim;
    private OnClickListener mChartListener;
    private OnEntryClickListener mEntryListener;
    private int mIndexClicked;
    private boolean mIsDrawing;
    private boolean mReadyToDraw;
    private ArrayList<ArrayList<Region>> mRegions;
    private int mSetClicked;
    private float mThresholdValue;
    private ArrayList<Pair<Integer, float[]>> mToUpdateValues;
    protected Orientation orientation;
    protected Style style;
    protected YController verController;

    public enum GridType {
        FULL,
        VERTICAL,
        HORIZONTAL
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    class Style {
        private static final int DEFAULT_COLOR = -16777216;
        protected int axisColor;
        protected float axisThickness;
        protected Paint chartPaint;
        protected float fontSize;
        protected Paint gridPaint;
        protected boolean hasHorizontalGrid;
        protected boolean hasVerticalGrid;
        protected int labelColor;
        protected Paint labelPaint;
        private Paint thresholdPaint;
        protected Typeface typeface;

        protected Style() {
            this.hasHorizontalGrid = false;
            this.hasVerticalGrid = false;
            this.axisColor = -16777216;
            this.axisThickness = ChartView.this.getResources().getDimension(R.dimen.grid_thickness);
            this.labelColor = -16777216;
            this.fontSize = ChartView.this.getResources().getDimension(R.dimen.font_size);
        }

        protected Style(TypedArray attrs) {
            this.hasHorizontalGrid = false;
            this.hasVerticalGrid = false;
            this.axisColor = attrs.getColor(1, -16777216);
            this.axisThickness = attrs.getDimension(0, ChartView.this.getResources().getDimension(R.dimen.axis_thickness));
            this.labelColor = attrs.getColor(5, -16777216);
            this.fontSize = attrs.getDimension(6, ChartView.this.getResources().getDimension(R.dimen.font_size));
            String typefaceName = attrs.getString(7);
            if (typefaceName != null) {
                this.typeface = Typeface.createFromAsset(ChartView.this.getResources().getAssets(), typefaceName);
            }
        }

        private void init() {
            this.chartPaint = new Paint();
            this.chartPaint.setColor(this.axisColor);
            this.chartPaint.setStyle(android.graphics.Paint.Style.STROKE);
            this.chartPaint.setStrokeWidth(this.axisThickness);
            this.chartPaint.setAntiAlias(true);
            this.labelPaint = new Paint();
            this.labelPaint.setColor(this.labelColor);
            this.labelPaint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
            this.labelPaint.setAntiAlias(true);
            this.labelPaint.setTextSize(this.fontSize);
            this.labelPaint.setTypeface(this.typeface);
        }

        public void clean() {
            this.chartPaint = null;
            this.labelPaint = null;
            this.gridPaint = null;
            this.thresholdPaint = null;
        }

        protected int getTextHeightBounds(String character) {
            if (character == "") {
                return 0;
            }
            Rect bounds = new Rect();
            ChartView.this.style.labelPaint.getTextBounds(character, 0, 1, bounds);
            return bounds.height();
        }
    }

    protected abstract void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList);

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.drawListener = new OnPreDrawListener() {
            @SuppressLint({"NewApi"})
            public boolean onPreDraw() {
                ChartView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                ChartView.this.chartTop = ChartView.this.getPaddingTop() + (ChartView.this.verController.getLabelHeight() / 2);
                ChartView.this.chartBottom = ChartView.this.getMeasuredHeight() - ChartView.this.getPaddingBottom();
                ChartView.this.chartLeft = ChartView.this.getPaddingLeft();
                ChartView.this.chartRight = ChartView.this.getMeasuredWidth() - ChartView.this.getPaddingRight();
                ChartView.this.verController.init();
                ChartView.this.mThresholdValue = ChartView.this.verController.parsePos(0, (double) ChartView.this.mThresholdValue);
                ChartView.this.horController.init();
                ChartView.this.digestData();
                ChartView.this.onPreDrawChart(ChartView.this.data);
                if (ChartView.this.mEntryListener != null) {
                    ChartView.this.mRegions = ChartView.this.defineRegions(ChartView.this.data);
                }
                if (ChartView.this.mAnim != null) {
                    ChartView.this.data = ChartView.this.mAnim.prepareEnterAnimation(ChartView.this);
                }
                if (VERSION.SDK_INT >= 11) {
                    ChartView.this.setLayerType(1, null);
                }
                return ChartView.this.mReadyToDraw = true;
            }
        };
//        this.horController = new XController(this, context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
//        this.verController = new YController(this, context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
//        this.style = new Style(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
        init();
    }

    public ChartView(Context context) {
        super(context);
        this.drawListener = null /* anonymous class already generated */;
        this.horController = new XController(this);
        this.verController = new YController(this);
        this.style = new Style();
        init();
    }

    private void init() {
        this.mReadyToDraw = false;
        this.mSetClicked = -1;
        this.mIndexClicked = -1;
        this.mThresholdValue = 0.0f;
        this.mIsDrawing = false;
        this.data = new ArrayList();
        this.mRegions = new ArrayList();
        this.mToUpdateValues = new ArrayList();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setWillNotDraw(false);
        this.style.init();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.style.clean();
    }

    private void digestData() {
        int nEntries = ((ChartSet) this.data.get(0)).size();
        Iterator it = this.data.iterator();
        while (it.hasNext()) {
            ChartSet set = (ChartSet) it.next();
            for (int i = 0; i < nEntries; i++) {
                set.getEntry(i).setCoordinates(this.horController.parsePos(i, (double) set.getValue(i)), this.verController.parsePos(i, (double) set.getValue(i)));
            }
        }
    }

    protected void onPreDrawChart(ArrayList<ChartSet> arrayList) {
    }

    protected ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> arrayList) {
        return this.mRegions;
    }

    public void addData(ChartSet set) {
        if (!(this.data.isEmpty() || set.size() == ((ChartSet) this.data.get(0)).size())) {
            Log.e(TAG, "The number of labels between sets doesn't match.", new IllegalArgumentException());
        }
        this.data.add(set);
    }

    public void addData(ArrayList<ChartSet> data) {
        this.data = data;
    }

    private void display() {
        getViewTreeObserver().addOnPreDrawListener(this.drawListener);
        postInvalidate();
    }

    public void show() {
        Iterator it = this.data.iterator();
        while (it.hasNext()) {
            ((ChartSet) it.next()).setVisible(true);
        }
        display();
    }

    public void show(int setIndex) {
        ((ChartSet) this.data.get(setIndex)).setVisible(true);
        display();
    }

    public void show(Animation anim) {
        this.mAnim = anim;
        show();
    }

    public void dismiss() {
        this.data.clear();
        invalidate();
    }

    public void dismiss(int setIndex) {
        ((ChartSet) this.data.get(setIndex)).setVisible(false);
        invalidate();
    }

    public void dismiss(Animation anim) {
        this.mAnim = anim;
        final Runnable endAction = this.mAnim.getEndAction();
        this.mAnim.setEndAction(new Runnable() {
            public void run() {
                if (endAction != null) {
                    endAction.run();
                }
                ChartView.this.dismiss();
            }
        });
        this.data = this.mAnim.prepareExitAnimation(this);
        invalidate();
    }

    public void reset() {
        this.data.clear();
        this.mRegions.clear();
        this.mToUpdateValues.clear();
        this.verController.minLabelValue = 0;
        this.verController.maxLabelValue = 0;
        if (this.horController.mandatoryBorderSpacing != 0.0f) {
            this.horController.mandatoryBorderSpacing = 1.0f;
        }
        this.style.thresholdPaint = null;
        this.style.gridPaint = null;
        this.style.hasHorizontalGrid = false;
        this.style.hasVerticalGrid = false;
    }

    public ChartView updateValues(int setIndex, float[] values) {
        if (values.length != ((ChartSet) this.data.get(setIndex)).size()) {
            Log.e(TAG, "New values size doesn't match current dataset size.", new IllegalArgumentException());
        }
        ((ChartSet) this.data.get(setIndex)).updateValues(values);
        return this;
    }

    public void notifyDataUpdate() {
        ArrayList<float[][]> oldCoords = new ArrayList(this.data.size());
        ArrayList<float[][]> newCoords = new ArrayList(this.data.size());
        Iterator it = this.data.iterator();
        while (it.hasNext()) {
            oldCoords.add(((ChartSet) it.next()).getScreenPoints());
        }
        digestData();
        it = this.data.iterator();
        while (it.hasNext()) {
            newCoords.add(((ChartSet) it.next()).getScreenPoints());
        }
        this.mRegions = defineRegions(this.data);
        if (this.mAnim != null) {
            this.data = this.mAnim.prepareAnimation(this, oldCoords, newCoords);
        }
        this.mToUpdateValues.clear();
        invalidate();
    }

    public void showTooltip(View tooltip, boolean bool) {
        if (bool) {
            LayoutParams layoutParams = (LayoutParams) tooltip.getLayoutParams();
            if (layoutParams.leftMargin < this.chartLeft - getPaddingLeft()) {
                layoutParams.leftMargin = this.chartLeft - getPaddingLeft();
            }
            if (layoutParams.topMargin < this.chartTop - getPaddingTop()) {
                layoutParams.topMargin = this.chartTop - getPaddingTop();
            }
            if (layoutParams.leftMargin + layoutParams.width > this.chartRight - getPaddingRight()) {
                layoutParams.leftMargin -= layoutParams.width - ((this.chartRight - getPaddingRight()) - layoutParams.leftMargin);
            }
            if (((float) (layoutParams.topMargin + layoutParams.height)) > getInnerChartBottom() - ((float) getPaddingBottom())) {
                layoutParams.topMargin = (int) (((float) layoutParams.topMargin) - (((float) layoutParams.height) - ((getInnerChartBottom() - ((float) getPaddingBottom())) - ((float) layoutParams.topMargin))));
            }
            tooltip.setLayoutParams(layoutParams);
        }
        addView(tooltip);
    }

    public void showTooltip(View tooltip) {
        showTooltip(tooltip, true);
    }

    public void dismissTooltip(View tooltip) {
        removeView(tooltip);
    }

    public void dismissAllTooltips() {
        removeAllViews();
    }

    public void animateSet(int index, BaseStyleAnimation anim) {
        anim.play(this, (ChartSet) this.data.get(index));
    }

    public boolean canIPleaseAskYouToDraw() {
        return !this.mIsDrawing;
    }

    protected void onDraw(Canvas canvas) {
        this.mIsDrawing = true;
        super.onDraw(canvas);
        if (this.mReadyToDraw) {
            if (this.style.hasVerticalGrid) {
                drawVerticalGrid(canvas);
            }
            if (this.style.hasHorizontalGrid) {
                drawHorizontalGrid(canvas);
            }
            this.verController.draw(canvas);
            if (!this.data.isEmpty()) {
                onDrawChart(canvas, this.data);
            }
            this.horController.draw(canvas);
            if (this.style.thresholdPaint != null) {
                drawThresholdLine(canvas);
            }
        }
        this.mIsDrawing = false;
    }

    private void drawThresholdLine(Canvas canvas) {
        canvas.drawLine(getInnerChartLeft(), this.mThresholdValue, getInnerChartRight(), this.mThresholdValue, this.style.thresholdPaint);
    }

    private void drawVerticalGrid(Canvas canvas) {
        Iterator it = this.horController.labelsPos.iterator();
        while (it.hasNext()) {
            Float pos = (Float) it.next();
            canvas.drawLine(pos.floatValue(), getInnerChartBottom(), pos.floatValue(), getInnerChartTop(), this.style.gridPaint);
        }
        if (this.horController.borderSpacing != 0.0f || this.horController.mandatoryBorderSpacing != 0.0f) {
            if (this.verController.labelsPositioning == LabelPosition.NONE) {
                canvas.drawLine(getInnerChartLeft(), getInnerChartBottom(), getInnerChartLeft(), getInnerChartTop(), this.style.gridPaint);
            }
            canvas.drawLine(getInnerChartRight(), getInnerChartBottom(), getInnerChartRight(), getInnerChartTop(), this.style.gridPaint);
        }
    }

    private void drawHorizontalGrid(Canvas canvas) {
        Iterator it = this.verController.labelsPos.iterator();
        while (it.hasNext()) {
            Float pos = (Float) it.next();
            canvas.drawLine(getInnerChartLeft(), pos.floatValue(), getInnerChartRight(), pos.floatValue(), this.style.gridPaint);
        }
        if (!this.horController.hasAxis) {
            canvas.drawLine(getInnerChartLeft(), getInnerChartBottom(), getInnerChartRight(), getInnerChartBottom(), this.style.gridPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mAnim == null || !this.mAnim.isPlaying()) {
            if (event.getAction() == 0 && this.mEntryListener != null && this.mRegions != null) {
                int nSets = this.mRegions.size();
                int nEntries = ((ArrayList) this.mRegions.get(0)).size();
                for (int i = 0; i < nSets; i++) {
                    for (int j = 0; j < nEntries; j++) {
                        if (((Region) ((ArrayList) this.mRegions.get(i)).get(j)).contains((int) event.getX(), (int) event.getY())) {
                            this.mSetClicked = i;
                            this.mIndexClicked = j;
                        }
                    }
                }
            } else if (event.getAction() == 1) {
                if (this.mEntryListener != null && this.mSetClicked != -1 && this.mIndexClicked != -1) {
                    if (((Region) ((ArrayList) this.mRegions.get(this.mSetClicked)).get(this.mIndexClicked)).contains((int) event.getX(), (int) event.getY())) {
                        this.mEntryListener.onClick(this.mSetClicked, this.mIndexClicked, new Rect(((Region) ((ArrayList) this.mRegions.get(this.mSetClicked)).get(this.mIndexClicked)).getBounds().left - getPaddingLeft(), ((Region) ((ArrayList) this.mRegions.get(this.mSetClicked)).get(this.mIndexClicked)).getBounds().top - getPaddingTop(), ((Region) ((ArrayList) this.mRegions.get(this.mSetClicked)).get(this.mIndexClicked)).getBounds().right - getPaddingLeft(), ((Region) ((ArrayList) this.mRegions.get(this.mSetClicked)).get(this.mIndexClicked)).getBounds().bottom - getPaddingTop()));
                    }
                    this.mSetClicked = -1;
                    this.mIndexClicked = -1;
                } else if (this.mChartListener != null) {
                    this.mChartListener.onClick(this);
                }
            }
        }
        return true;
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public float getInnerChartBottom() {
        return this.verController.getInnerChartBottom();
    }

    public float getInnerChartLeft() {
        return this.verController.getInnerChartLeft();
    }

    public float getInnerChartRight() {
        return this.horController.getInnerChartRight();
    }

    public float getInnerChartTop() {
        return (float) this.chartTop;
    }

    public float getZeroPosition() {
        if (this.orientation == Orientation.VERTICAL) {
            return this.verController.parsePos(0, 0.0d);
        }
        return this.horController.parsePos(0, 0.0d);
    }

    protected int getStep() {
        if (this.orientation == Orientation.VERTICAL) {
            return this.verController.step;
        }
        return this.horController.step;
    }

    public ArrayList<ChartSet> getData() {
        return this.data;
    }

    protected void setOrientation(Orientation orien) {
        this.orientation = orien;
        if (this.orientation == Orientation.VERTICAL) {
            this.verController.handleValues = true;
        } else {
            this.horController.handleValues = true;
        }
    }

    public ChartView setYLabels(LabelPosition position) {
        this.verController.labelsPositioning = position;
        return this;
    }

    public ChartView setXLabels(LabelPosition position) {
        this.horController.labelsPositioning = position;
        return this;
    }

    public ChartView setXAxis(boolean bool) {
        this.horController.hasAxis = bool;
        return this;
    }

    public ChartView setYAxis(boolean bool) {
        this.verController.hasAxis = bool;
        return this;
    }

    public ChartView setAxisBorderValues(int minValue, int maxValue, int step) {
        if ((maxValue - minValue) % step != 0) {
            Log.e(TAG, "Step value must be a divisor of distance between minValue and maxValue", new IllegalArgumentException());
        }
        if (this.orientation == Orientation.VERTICAL) {
            this.verController.maxLabelValue = maxValue;
            this.verController.minLabelValue = minValue;
            this.verController.step = step;
        } else {
            this.horController.maxLabelValue = maxValue;
            this.horController.minLabelValue = minValue;
            this.horController.step = step;
        }
        return this;
    }

    public ChartView setStep(int step) {
        if (step <= 0) {
            Log.e(TAG, "Step can't be lower or equal to 0", new IllegalArgumentException());
        }
        if (this.orientation == Orientation.VERTICAL) {
            this.verController.step = step;
        } else {
            this.horController.step = step;
        }
        return this;
    }

    public void setOnEntryClickListener(OnEntryClickListener listener) {
        this.mEntryListener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mChartListener = listener;
    }

    public ChartView setLabelColor(int color) {
        this.style.labelColor = color;
        return this;
    }

    public ChartView setFontSize(int size) {
        this.style.fontSize = (float) size;
        return this;
    }

    public ChartView setTypeface(Typeface typeface) {
        this.style.typeface = typeface;
        return this;
    }

    public ChartView setBorderSpacing(float spacing) {
        if (this.orientation == Orientation.VERTICAL) {
            this.horController.borderSpacing = spacing;
        } else {
            this.verController.borderSpacing = spacing;
        }
        return this;
    }

    public ChartView setTopSpacing(float spacing) {
        if (this.orientation == Orientation.VERTICAL) {
            this.verController.topSpacing = spacing;
        } else {
            this.horController.borderSpacing = spacing;
        }
        return this;
    }

    public ChartView setGrid(GridType type, Paint paint) {
        if (type.compareTo(GridType.FULL) == 0) {
            this.style.hasVerticalGrid = true;
            this.style.hasHorizontalGrid = true;
        } else if (type.compareTo(GridType.VERTICAL) == 0) {
            this.style.hasVerticalGrid = true;
        } else {
            this.style.hasHorizontalGrid = true;
        }
        this.style.gridPaint = paint;
        return this;
    }

    public ChartView setThresholdLine(float value, Paint paint) {
        this.mThresholdValue = value;
        this.style.thresholdPaint = paint;
        return this;
    }

    protected ChartView setMandatoryBorderSpacing() {
        if (this.orientation == Orientation.VERTICAL) {
            this.horController.mandatoryBorderSpacing = 1.0f;
        } else {
            this.verController.mandatoryBorderSpacing = 1.0f;
        }
        return this;
    }

    public ChartView setLabelsFormat(DecimalFormat format) {
        this.verController.labelFormat = format;
        return this;
    }
}
