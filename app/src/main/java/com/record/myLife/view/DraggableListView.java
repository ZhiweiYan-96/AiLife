package com.record.myLife.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import com.record.myLife.R;

public class DraggableListView extends ListView {
    public static final int FLING = 0;
    public static final int SLIDE_LEFT = 2;
    public static final int SLIDE_RIGHT = 1;
    private int dragndropBackgroundColor;
    private int grabberId;
    private int mCoordOffset;
    private Bitmap mDragBitmap;
    private DragListener mDragListener;
    private int mDragPoint;
    private int mDragPos;
    private ImageView mDragView;
    private DropListener mDropListener;
    private int mFirstDragPos;
    private GestureDetector mGestureDetector;
    private int mHeight;
    private int mItemHeightExpanded;
    private int mItemHeightNormal;
    private int mLowerBound;
    private RemoveListener mRemoveListener;
    private int mRemoveMode;
    private Rect mTempRect;
    private final int mTouchSlop;
    private int mUpperBound;
    private WindowManager mWindowManager;
    private LayoutParams mWindowParams;

    public interface DragListener {
        void drag(int i, int i2);
    }

    public interface DropListener {
        void drop(int i, int i2);
    }

    public interface RemoveListener {
        void remove(int i);
    }

    public DraggableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRemoveMode = -1;
        this.mTempRect = new Rect();
        this.mItemHeightNormal = -1;
        this.mItemHeightExpanded = -1;
        this.grabberId = -1;
        this.dragndropBackgroundColor = 0;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        if (attrs != null) {
//            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TouchListView, 0, 0);
//            this.mItemHeightNormal = a.getDimensionPixelSize(0, 0);
//            this.mItemHeightExpanded = a.getDimensionPixelSize(1, this.mItemHeightNormal);
//            this.grabberId = a.getResourceId(2, -1);
//            this.dragndropBackgroundColor = a.getColor(3, 0);
//            this.mRemoveMode = a.getInt(4, -1);
//            a.recycle();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mRemoveListener != null && this.mGestureDetector == null && this.mRemoveMode == 0) {
            this.mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (DraggableListView.this.mDragView == null) {
                        return false;
                    }
                    if (velocityX <= 1000.0f) {
                        return true;
                    }
                    Rect r = DraggableListView.this.mTempRect;
                    DraggableListView.this.mDragView.getDrawingRect(r);
                    if (e2.getX() <= ((float) ((r.right * 2) / 3))) {
                        return true;
                    }
                    DraggableListView.this.stopDragging();
                    DraggableListView.this.mRemoveListener.remove(DraggableListView.this.mFirstDragPos);
                    DraggableListView.this.unExpandViews(true);
                    return true;
                }
            });
        }
        if (!(this.mDragListener == null && this.mDropListener == null)) {
            switch (ev.getAction()) {
                case 0:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    int itemnum = pointToPosition(x, y);
                    if (itemnum != -1) {
                        ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
                        this.mDragPoint = y - item.getTop();
                        this.mCoordOffset = ((int) ev.getRawY()) - y;
                        View dragger = item.findViewById(this.grabberId);
                        Rect r = this.mTempRect;
                        r.left = dragger.getLeft();
                        r.right = dragger.getRight();
                        r.top = dragger.getTop();
                        r.bottom = dragger.getBottom();
                        if (r.left >= x || x >= r.right) {
                            this.mDragView = null;
                            break;
                        }
                        item.setDrawingCacheEnabled(true);
                        startDragging(Bitmap.createBitmap(item.getDrawingCache()), y);
                        this.mDragPos = itemnum;
                        this.mFirstDragPos = this.mDragPos;
                        this.mHeight = getHeight();
                        int touchSlop = this.mTouchSlop;
                        this.mUpperBound = Math.min(y - touchSlop, this.mHeight / 3);
                        this.mLowerBound = Math.max(y + touchSlop, (this.mHeight * 2) / 3);
                        return false;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int myPointToPosition(int x, int y) {
        Rect frame = this.mTempRect;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return -1;
    }

    private int getItemForPosition(int y) {
        int adjustedy = (y - this.mDragPoint) - 32;
        int pos = myPointToPosition(0, adjustedy);
        if (pos >= 0) {
            if (pos <= this.mFirstDragPos) {
                return pos + 1;
            }
            return pos;
        } else if (adjustedy < 0) {
            return 0;
        } else {
            return pos;
        }
    }

    private void adjustScrollBounds(int y) {
        if (y >= this.mHeight / 3) {
            this.mUpperBound = this.mHeight / 3;
        }
        if (y <= (this.mHeight * 2) / 3) {
            this.mLowerBound = (this.mHeight * 2) / 3;
        }
    }

    private void unExpandViews(boolean deletion) {
        int i = 0;
        while (true) {
            View v = getChildAt(i);
            if (v == null) {
                if (deletion) {
                    int position = getFirstVisiblePosition();
                    int y = getChildAt(0).getTop();
                    setAdapter(getAdapter());
                    setSelectionFromTop(position, y);
                }
                layoutChildren();
                v = getChildAt(i);
                if (v == null) {
                    return;
                }
            }
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = this.mItemHeightNormal;
            v.setLayoutParams(params);
            v.setVisibility(VISIBLE);
            i++;
        }
    }

    private void doExpansion() {
        int childnum = this.mDragPos - getFirstVisiblePosition();
        if (this.mDragPos > this.mFirstDragPos) {
            childnum++;
        }
        View first = getChildAt(this.mFirstDragPos - getFirstVisiblePosition());
        int i = 0;
        while (true) {
            View vv = getChildAt(i);
            if (vv != null) {
                int height = this.mItemHeightNormal;
                int visibility = 0;
                if (vv.equals(first)) {
                    if (this.mDragPos == this.mFirstDragPos) {
                        visibility = 4;
                    } else {
                        height = 1;
                    }
                } else if (i == childnum && this.mDragPos < getCount() - 1) {
                    height = this.mItemHeightExpanded;
                }
                ViewGroup.LayoutParams params = vv.getLayoutParams();
                params.height = height;
                vv.setLayoutParams(params);
                vv.setVisibility(visibility);
                i++;
            } else {
                return;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mGestureDetector != null) {
            this.mGestureDetector.onTouchEvent(ev);
        }
        if ((this.mDragListener == null && this.mDropListener == null) || this.mDragView == null) {
            return super.onTouchEvent(ev);
        }
        int action = ev.getAction();
        switch (action) {
            case 0:
            case 2:
                int y = (int) ev.getY();
                dragView((int) ev.getX(), y);
                int itemnum = getItemForPosition(y);
                if (itemnum >= 0) {
                    if (action == 0 || itemnum != this.mDragPos) {
                        if (this.mDragListener != null) {
                            this.mDragListener.drag(this.mDragPos, itemnum);
                        }
                        this.mDragPos = itemnum;
                        doExpansion();
                    }
                    int speed = 0;
                    adjustScrollBounds(y);
                    if (y > this.mLowerBound) {
                        speed = y > (this.mHeight + this.mLowerBound) / 2 ? 16 : 4;
                    } else if (y < this.mUpperBound) {
                        speed = y < this.mUpperBound / 2 ? -16 : -4;
                    }
                    if (speed != 0) {
                        int ref = pointToPosition(0, this.mHeight / 2);
                        if (ref == -1) {
                            ref = pointToPosition(0, ((this.mHeight / 2) + getDividerHeight()) + 64);
                        }
                        View v = getChildAt(ref - getFirstVisiblePosition());
                        if (v != null) {
                            setSelectionFromTop(ref, v.getTop() - speed);
                            break;
                        }
                    }
                }
                break;
            case 1:
            case 3:
                this.mDragView.getDrawingRect(this.mTempRect);
                stopDragging();
                if (this.mDropListener != null && this.mDragPos >= 0 && this.mDragPos < getCount()) {
                    this.mDropListener.drop(this.mFirstDragPos, this.mDragPos);
                }
                unExpandViews(false);
                break;
        }
        return true;
    }

    private void startDragging(Bitmap bm, int y) {
        stopDragging();
//        this.mWindowParams = new LayoutParams();
//        this.mWindowParams.gravity = 48;
//        this.mWindowParams.x = 0;
//        this.mWindowParams.y = (y - this.mDragPoint) + this.mCoordOffset;
        this.mWindowParams.height = -2;
        this.mWindowParams.width = -2;
//        this.mWindowParams.flags = 408;
//        this.mWindowParams.format = -3;
//        this.mWindowParams.windowAnimations = 0;
        ImageView v = new ImageView(getContext());
        v.setBackgroundColor(this.dragndropBackgroundColor);
        v.setImageBitmap(bm);
        this.mDragBitmap = bm;
        this.mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        this.mWindowManager.addView(v, this.mWindowParams);
        this.mDragView = v;
    }

    private void dragView(int x, int y) {
        float alpha = 1.0f;
        int width = this.mDragView.getWidth();
        if (this.mRemoveMode == 1) {
            if (x > width / 2) {
                alpha = ((float) (width - x)) / ((float) (width / 2));
            }
//            this.mWindowParams.alpha = alpha;
        } else if (this.mRemoveMode == 2) {
            if (x < width / 2) {
                alpha = ((float) x) / ((float) (width / 2));
            }
//            this.mWindowParams.alpha = alpha;
        }
//        this.mWindowParams.y = (y - this.mDragPoint) + this.mCoordOffset;
        this.mWindowManager.updateViewLayout(this.mDragView, this.mWindowParams);
    }

    private void stopDragging() {
        if (this.mDragView != null) {
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this.mDragView);
            this.mDragView.setImageDrawable(null);
            this.mDragView = null;
        }
        if (this.mDragBitmap != null) {
            this.mDragBitmap.recycle();
            this.mDragBitmap = null;
        }
    }

    public void setDragListener(DragListener l) {
        this.mDragListener = l;
    }

    public void setDropListener(DropListener l) {
        this.mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        this.mRemoveListener = l;
    }
}
