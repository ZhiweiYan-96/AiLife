package com.record.view.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import com.record.myLife.R;
import java.util.LinkedList;
import java.util.List;

public class WheelView extends View {
    private static final int ADDITIONAL_ITEMS_SPACE = 10;
    private static final int ADDITIONAL_ITEM_HEIGHT = 15;
    private static final int DEF_VISIBLE_ITEMS = 5;
    private static final int ITEMS_TEXT_COLOR = -16777216;
    private static final int ITEM_OFFSET = 4;
    private static final int LABEL_OFFSET = 8;
    private static final int MIN_DELTA_FOR_SCROLLING = 1;
    private static final int PADDING = 5;
    private static final int SCROLLING_DURATION = 400;
    private static final int[] SHADOWS_COLORS = new int[]{-1, ViewCompat.MEASURED_SIZE_MASK, ViewCompat.MEASURED_SIZE_MASK};
    private static final int TEXT_SIZE = 24;
    private static final int VALUE_TEXT_COLOR = -268435456;
    private final int MESSAGE_JUSTIFY = 1;
    private final int MESSAGE_SCROLL = 0;
    private WheelAdapter adapter = null;
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            WheelView.this.scroller.computeScrollOffset();
            int currY = WheelView.this.scroller.getCurrY();
            int delta = WheelView.this.lastScrollY - currY;
            WheelView.this.lastScrollY = currY;
            if (delta != 0) {
                WheelView.this.doScroll(delta);
            }
            if (Math.abs(currY - WheelView.this.scroller.getFinalY()) < 1) {
                currY = WheelView.this.scroller.getFinalY();
                WheelView.this.scroller.forceFinished(true);
            }
            if (!WheelView.this.scroller.isFinished()) {
                WheelView.this.animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == 0) {
                WheelView.this.justify();
            } else {
                WheelView.this.finishScrolling();
            }
        }
    };
    private GradientDrawable bottomShadow;
    private Drawable centerDrawable;
    private List<OnWheelChangedListener> changingListeners = new LinkedList();
    private int currentItem = 0;
    private GestureDetector gestureDetector;
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onDown(MotionEvent e) {
            if (!WheelView.this.isScrollingPerformed) {
                return false;
            }
            WheelView.this.scroller.forceFinished(true);
            WheelView.this.clearMessages();
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            WheelView.this.startScrolling();
            WheelView.this.doScroll((int) (-distanceY));
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int minY;
            WheelView.this.lastScrollY = (WheelView.this.currentItem * WheelView.this.getItemHeight()) + WheelView.this.scrollingOffset;
//            int maxY = WheelView.this.isCyclic ? ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED : WheelView.this.adapter.getItemsCount() * WheelView.this.getItemHeight();
//            if (WheelView.this.isCyclic) {
//                minY = -maxY;
//            } else {
//                minY = 0;
//            }
//            WheelView.this.scroller.fling(0, WheelView.this.lastScrollY, 0, ((int) (-velocityY)) / 2, 0, 0, minY, maxY);
//            WheelView.this.setNextMessage(0);
            return true;
        }
    };
    boolean isCyclic = false;
    private boolean isScrollingPerformed;
    private int itemHeight = 0;
    private StaticLayout itemsLayout;
    private TextPaint itemsPaint;
    private int itemsWidth = 0;
    private String label;
    private StaticLayout labelLayout;
    private int labelWidth = 0;
    private int lastScrollY;
    private Scroller scroller;
    private List<OnWheelScrollListener> scrollingListeners = new LinkedList();
    private int scrollingOffset;
    private GradientDrawable topShadow;
    private StaticLayout valueLayout;
    private TextPaint valuePaint;
    private int visibleItems = 5;

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public WheelView(Context context) {
        super(context);
        initData(context);
    }

    private void initData(Context context) {
        this.gestureDetector = new GestureDetector(context, this.gestureListener);
        this.gestureDetector.setIsLongpressEnabled(false);
        this.scroller = new Scroller(context);
    }

    public WheelAdapter getAdapter() {
        return this.adapter;
    }

    public void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        invalidateLayouts();
        invalidate();
    }

    public void setInterpolator(Interpolator interpolator) {
        this.scroller.forceFinished(true);
        this.scroller = new Scroller(getContext(), interpolator);
    }

    public int getVisibleItems() {
        return this.visibleItems;
    }

    public void setVisibleItems(int count) {
        this.visibleItems = count;
        invalidate();
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String newLabel) {
        if (this.label == null || !this.label.equals(newLabel)) {
            this.label = newLabel;
            this.labelLayout = null;
            invalidate();
        }
    }

    public void addChangingListener(OnWheelChangedListener listener) {
        this.changingListeners.add(listener);
    }

    public void removeChangingListener(OnWheelChangedListener listener) {
        this.changingListeners.remove(listener);
    }

    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : this.changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    public void addScrollingListener(OnWheelScrollListener listener) {
        this.scrollingListeners.add(listener);
    }

    public void removeScrollingListener(OnWheelScrollListener listener) {
        this.scrollingListeners.remove(listener);
    }

    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : this.scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : this.scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    public int getCurrentItem() {
        return this.currentItem;
    }

    public void setCurrentItem(int index, boolean animated) {
        if (this.adapter != null && this.adapter.getItemsCount() != 0) {
            if (index < 0 || index >= this.adapter.getItemsCount()) {
                if (this.isCyclic) {
                    while (index < 0) {
                        index += this.adapter.getItemsCount();
                    }
                    index %= this.adapter.getItemsCount();
                } else {
                    return;
                }
            }
            if (index == this.currentItem) {
                return;
            }
            if (animated) {
                scroll(index - this.currentItem, 400);
                return;
            }
            invalidateLayouts();
            int old = this.currentItem;
            this.currentItem = index;
            notifyChangingListeners(old, this.currentItem);
            invalidate();
        }
    }

    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    public boolean isCyclic() {
        return this.isCyclic;
    }

    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidate();
        invalidateLayouts();
    }

    private void invalidateLayouts() {
        this.itemsLayout = null;
        this.valueLayout = null;
        this.scrollingOffset = 0;
    }

    private void initResourcesIfNecessary() {
        if (this.itemsPaint == null) {
            this.itemsPaint = new TextPaint(33);
            this.itemsPaint.density = getResources().getDisplayMetrics().density;
            this.itemsPaint.setTextSize(this.itemsPaint.density * 24.0f);
        }
        if (this.valuePaint == null) {
            this.valuePaint = new TextPaint(37);
            this.valuePaint.density = getResources().getDisplayMetrics().density;
            this.valuePaint.setTextSize(this.itemsPaint.density * 24.0f);
            this.valuePaint.setShadowLayer(0.1f, 0.0f, 0.1f, -289357632);
        }
        if (this.centerDrawable == null) {
            this.centerDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val_v2);
        }
        if (this.topShadow == null) {
            this.topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }
        if (this.bottomShadow == null) {
            this.bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }
        setBackgroundResource(R.drawable.wheel_bg_v2);
    }

    private int getDesiredHeight(Layout layout) {
        if (layout == null) {
            return 0;
        }
        return Math.max(((getItemHeight() * this.visibleItems) - 8) - 15, getSuggestedMinimumHeight());
    }

    private String getTextItem(int index) {
        if (this.adapter == null || this.adapter.getItemsCount() == 0) {
            return null;
        }
        int count = this.adapter.getItemsCount();
        if ((index < 0 || index >= count) && !this.isCyclic) {
            return null;
        }
        while (index < 0) {
            index += count;
        }
        return this.adapter.getItem(index % count);
    }

    private String buildText(boolean useCurrentValue) {
        StringBuilder itemsText = new StringBuilder();
        int addItems = (this.visibleItems / 2) + 1;
        int i = this.currentItem - addItems;
        while (i <= this.currentItem + addItems) {
            if (useCurrentValue || i != this.currentItem) {
                String text = getTextItem(i);
                if (text != null) {
                    itemsText.append(text);
                }
            }
            if (i < this.currentItem + addItems) {
                itemsText.append("\n");
            }
            i++;
        }
        return itemsText.toString();
    }

    private int getMaxTextLength() {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }
        int adapterLength = adapter.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }
        String maxText = null;
        for (int i = Math.max(this.currentItem - (this.visibleItems / 2), 0); i < Math.min(this.currentItem + this.visibleItems, adapter.getItemsCount()); i++) {
            String text = adapter.getItem(i);
            if (text != null && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }
        if (maxText != null) {
            return maxText.length();
        }
        return 0;
    }

    private int getItemHeight() {
        if (this.itemHeight != 0) {
            return this.itemHeight;
        }
        if (this.itemsLayout == null || this.itemsLayout.getLineCount() <= 2) {
            return getHeight() / this.visibleItems;
        }
        this.itemHeight = this.itemsLayout.getLineTop(2) - this.itemsLayout.getLineTop(1);
        return this.itemHeight;
    }

    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();
        int width = widthSize;
        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
//            this.itemsWidth = (int) (((float) maxLength) * FloatMath.ceil(Layout.getDesiredWidth("0", this.itemsPaint)));
        } else {
            this.itemsWidth = 0;
        }
        this.itemsWidth += 10;
        this.labelWidth = 0;
        if (this.label != null && this.label.length() > 0) {
//            this.labelWidth = (int) FloatMath.ceil(Layout.getDesiredWidth(this.label, this.valuePaint));
        }
        boolean recalculate = false;
        if (mode == 1073741824) {
            width = widthSize;
            recalculate = true;
        } else {
            width = (this.itemsWidth + this.labelWidth) + 10;
            if (this.labelWidth > 0) {
                width += 8;
            }
            width = Math.max(width, getSuggestedMinimumWidth());
            if (mode == Integer.MIN_VALUE && widthSize < width) {
                width = widthSize;
                recalculate = true;
            }
        }
        if (recalculate) {
            int pureWidth = (width - 8) - 10;
            if (pureWidth <= 0) {
                this.labelWidth = 0;
                this.itemsWidth = 0;
            }
            if (this.labelWidth > 0) {
                this.itemsWidth = (int) ((((double) this.itemsWidth) * ((double) pureWidth)) / ((double) (this.itemsWidth + this.labelWidth)));
                this.labelWidth = pureWidth - this.itemsWidth;
            } else {
                this.itemsWidth = pureWidth + 8;
            }
        }
        if (this.itemsWidth > 0) {
            createLayouts(this.itemsWidth, this.labelWidth);
        }
        return width;
    }

    private void createLayouts(int widthItems, int widthLabel) {
        String text = null;
        if (this.itemsLayout == null || this.itemsLayout.getWidth() > widthItems) {
            this.itemsLayout = new StaticLayout(buildText(this.isScrollingPerformed), this.itemsPaint, widthItems, widthLabel > 0 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_CENTER, 1.0f, 15.0f, false);
        } else {
            this.itemsLayout.increaseWidthTo(widthItems);
        }
        if (!this.isScrollingPerformed && (this.valueLayout == null || this.valueLayout.getWidth() > widthItems)) {
            if (getAdapter() != null) {
                text = getAdapter().getItem(this.currentItem);
            }
            this.valueLayout = new StaticLayout(text != null ? text : "", this.valuePaint, widthItems, widthLabel > 0 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_CENTER, 1.0f, 15.0f, false);
        } else if (this.isScrollingPerformed) {
            this.valueLayout = null;
        } else {
            this.valueLayout.increaseWidthTo(widthItems);
        }
        if (widthLabel <= 0) {
            return;
        }
        if (this.labelLayout == null || this.labelLayout.getWidth() > widthLabel) {
            this.labelLayout = new StaticLayout(this.label, this.valuePaint, widthLabel, Alignment.ALIGN_NORMAL, 1.0f, 15.0f, false);
        } else {
            this.labelLayout.increaseWidthTo(widthLabel);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = calculateLayoutWidth(widthSize, widthMode);
        if (heightMode == 1073741824) {
            height = heightSize;
        } else {
            height = getDesiredHeight(this.itemsLayout);
            if (heightMode == Integer.MIN_VALUE) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.itemsLayout == null) {
            if (this.itemsWidth == 0) {
                calculateLayoutWidth(getWidth(), 1073741824);
            } else {
                createLayouts(this.itemsWidth, this.labelWidth);
            }
        }
        if (this.itemsWidth > 0) {
            canvas.save();
            canvas.translate(5.0f, -4.0f);
            drawItems(canvas);
            drawValue(canvas);
            canvas.restore();
        }
        drawCenterRect(canvas);
        drawShadows(canvas);
    }

    private void drawShadows(Canvas canvas) {
        this.topShadow.setBounds(0, 0, getWidth(), getHeight() / this.visibleItems);
        this.topShadow.draw(canvas);
        this.bottomShadow.setBounds(0, getHeight() - (getHeight() / this.visibleItems), getWidth(), getHeight());
        this.bottomShadow.draw(canvas);
    }

    private void drawValue(Canvas canvas) {
        this.valuePaint.setColor(VALUE_TEXT_COLOR);
        this.valuePaint.drawableState = getDrawableState();
        Rect bounds = new Rect();
        this.itemsLayout.getLineBounds(this.visibleItems / 2, bounds);
        if (this.labelLayout != null) {
            canvas.save();
            canvas.translate((float) (this.itemsLayout.getWidth() + 8), (float) bounds.top);
            this.labelLayout.draw(canvas);
            canvas.restore();
        }
        if (this.valueLayout != null) {
            canvas.save();
            canvas.translate(0.0f, (float) (bounds.top + this.scrollingOffset));
            this.valueLayout.draw(canvas);
            canvas.restore();
        }
    }

    private void drawItems(Canvas canvas) {
        canvas.save();
        canvas.translate(0.0f, (float) ((-this.itemsLayout.getLineTop(1)) + this.scrollingOffset));
        this.itemsPaint.setColor(-16777216);
        this.itemsPaint.drawableState = getDrawableState();
        this.itemsLayout.draw(canvas);
        canvas.restore();
    }

    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2;
        int offset = getItemHeight() / 2;
        this.centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        this.centerDrawable.draw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(getAdapter() == null || this.gestureDetector.onTouchEvent(event) || event.getAction() != 1)) {
            justify();
        }
        return true;
    }

    private void doScroll(int delta) {
        this.scrollingOffset += delta;
        int count = this.scrollingOffset / getItemHeight();
        int pos = this.currentItem - count;
        if (this.isCyclic && this.adapter.getItemsCount() > 0) {
            while (pos < 0) {
                pos += this.adapter.getItemsCount();
            }
            pos %= this.adapter.getItemsCount();
        } else if (!this.isScrollingPerformed) {
            pos = Math.min(Math.max(pos, 0), this.adapter.getItemsCount() - 1);
        } else if (pos < 0) {
            count = this.currentItem;
            pos = 0;
        } else if (pos >= this.adapter.getItemsCount()) {
            count = (this.currentItem - this.adapter.getItemsCount()) + 1;
            pos = this.adapter.getItemsCount() - 1;
        }
        int offset = this.scrollingOffset;
        if (pos != this.currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }
        this.scrollingOffset = offset - (getItemHeight() * count);
        if (this.scrollingOffset > getHeight()) {
            this.scrollingOffset = (this.scrollingOffset % getHeight()) + getHeight();
        }
    }

    private void setNextMessage(int message) {
        clearMessages();
        this.animationHandler.sendEmptyMessage(message);
    }

    private void clearMessages() {
        this.animationHandler.removeMessages(0);
        this.animationHandler.removeMessages(1);
    }

    private void justify() {
        if (this.adapter != null) {
            this.lastScrollY = 0;
            int offset = this.scrollingOffset;
            int itemHeight = getItemHeight();
            boolean needToIncrease = offset > 0 ? this.currentItem < this.adapter.getItemsCount() : this.currentItem > 0;
            if ((this.isCyclic || needToIncrease) && Math.abs((float) offset) > ((float) itemHeight) / 2.0f) {
                offset = offset < 0 ? offset + (itemHeight + 1) : offset - (itemHeight + 1);
            }
            if (Math.abs(offset) > 1) {
                this.scroller.startScroll(0, 0, 0, offset, 400);
                setNextMessage(1);
                return;
            }
            finishScrolling();
        }
    }

    private void startScrolling() {
        if (!this.isScrollingPerformed) {
            this.isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
    }

    void finishScrolling() {
        if (this.isScrollingPerformed) {
            notifyScrollingListenersAboutEnd();
            this.isScrollingPerformed = false;
        }
        invalidateLayouts();
        invalidate();
    }

    public void scroll(int itemsToScroll, int time) {
        this.scroller.forceFinished(true);
        this.lastScrollY = this.scrollingOffset;
        int offset = itemsToScroll * getItemHeight();
        this.scroller.startScroll(0, this.lastScrollY, 0, offset - this.lastScrollY, time);
        setNextMessage(0);
        startScrolling();
    }
}
