package com.record.myLife.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.record.myLife.R;
import java.util.Date;

public class ElasticScrollView extends ScrollView {
    private static final int DONE = 3;
    private static final int LOADING = 4;
    private static final int PULL_To_REFRESH = 1;
    private static final int RATIO = 3;
    private static final int REFRESHING = 2;
    private static final int RELEASE_To_REFRESH = 0;
    private static final String TAG = "ElasticScrollView";
    private RotateAnimation animation;
    private ImageView arrowImageView;
    private boolean canReturn;
    private int headContentHeight;
    private int headContentWidth;
    private LinearLayout headView;
    private LinearLayout innerLayout;
    private boolean isBack;
    private boolean isRecored;
    private boolean isRefreshable;
    private TextView lastUpdatedTextView;
    private ProgressBar progressBar;
    private OnRefreshListener refreshListener;
    private RotateAnimation reverseAnimation;
    private int startY;
    private int state;
    private TextView tipsTextview;

    public interface OnRefreshListener {
        void onRefresh();
    }

    public ElasticScrollView(Context context) {
        super(context);
        init(context);
    }

    public ElasticScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        this.innerLayout = new LinearLayout(context);
        this.innerLayout.setLayoutParams(new LayoutParams(-1, -2));
        this.innerLayout.setOrientation(1);
        this.headView = (LinearLayout) inflater.inflate(R.layout.temp_listview_head, null);
        this.arrowImageView = (ImageView) this.headView.findViewById(R.id.head_arrowImageView);
        this.progressBar = (ProgressBar) this.headView.findViewById(R.id.head_progressBar);
        this.tipsTextview = (TextView) this.headView.findViewById(R.id.head_tipsTextView);
        this.lastUpdatedTextView = (TextView) this.headView.findViewById(R.id.head_lastUpdatedTextView);
        measureView(this.headView);
        this.headContentHeight = this.headView.getMeasuredHeight();
        this.headContentWidth = this.headView.getMeasuredWidth();
        this.headView.setPadding(0, this.headContentHeight * -1, 0, 0);
        this.headView.invalidate();
        Log.i("size", "width:" + this.headContentWidth + " height:" + this.headContentHeight);
        this.innerLayout.addView(this.headView);
        addView(this.innerLayout);
        this.animation = new RotateAnimation(0.0f, -180.0f, 1, 0.5f, 1, 0.5f);
        this.animation.setInterpolator(new LinearInterpolator());
        this.animation.setDuration(250);
        this.animation.setFillAfter(true);
        this.reverseAnimation = new RotateAnimation(-180.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        this.reverseAnimation.setInterpolator(new LinearInterpolator());
        this.reverseAnimation.setDuration(200);
        this.reverseAnimation.setFillAfter(true);
        this.state = 3;
        this.isRefreshable = false;
        this.canReturn = false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.isRefreshable) {
            switch (event.getAction()) {
                case 0:
                    if (getScrollY() == 0 && !this.isRecored) {
                        this.isRecored = true;
                        this.startY = (int) event.getY();
                        Log.i(TAG, "在down时候记录当前位置‘");
                        break;
                    }
                case 1:
                    if (!(this.state == 2 || this.state == 4)) {
                        if (this.state == 3) {
                        }
                        if (this.state == 1) {
                            this.state = 3;
                            changeHeaderViewByState();
                            Log.i(TAG, "由下拉刷新状态，到done状态");
                        }
                        if (this.state == 0) {
                            this.state = 2;
                            changeHeaderViewByState();
                            onRefresh();
                            Log.i(TAG, "由松开刷新状态，到done状态");
                        }
                    }
                    this.isRecored = false;
                    this.isBack = false;
                    break;
                case 2:
                    int tempY = (int) event.getY();
                    if (!this.isRecored && getScrollY() == 0) {
                        Log.i(TAG, "在move时候记录下位置");
                        this.isRecored = true;
                        this.startY = tempY;
                    }
                    if (!(this.state == 2 || !this.isRecored || this.state == 4)) {
                        if (this.state == 0) {
                            this.canReturn = true;
                            if ((tempY - this.startY) / 3 < this.headContentHeight && tempY - this.startY > 0) {
                                this.state = 1;
                                changeHeaderViewByState();
                                Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
                            } else if (tempY - this.startY <= 0) {
                                this.state = 3;
                                changeHeaderViewByState();
                                Log.i(TAG, "由松开刷新状态转变到done状态");
                            }
                        }
                        if (this.state == 1) {
                            this.canReturn = true;
                            if ((tempY - this.startY) / 3 >= this.headContentHeight) {
                                this.state = 0;
                                this.isBack = true;
                                changeHeaderViewByState();
                                Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
                            } else if (tempY - this.startY <= 0) {
                                this.state = 3;
                                changeHeaderViewByState();
                                Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
                            }
                        }
                        if (this.state == 3 && tempY - this.startY > 0) {
                            this.state = 1;
                            changeHeaderViewByState();
                        }
                        if (this.state == 1) {
                            this.headView.setPadding(0, (this.headContentHeight * -1) + ((tempY - this.startY) / 3), 0, 0);
                        }
                        if (this.state == 0) {
                            this.headView.setPadding(0, ((tempY - this.startY) / 3) - this.headContentHeight, 0, 0);
                        }
                        if (this.canReturn) {
                            this.canReturn = false;
                            return true;
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void changeHeaderViewByState() {
        switch (this.state) {
            case 0:
                this.arrowImageView.setVisibility(0);
                this.progressBar.setVisibility(8);
                this.tipsTextview.setVisibility(0);
                this.lastUpdatedTextView.setVisibility(8);
                this.arrowImageView.clearAnimation();
                this.arrowImageView.startAnimation(this.animation);
                this.tipsTextview.setText("松开刷新");
                Log.i(TAG, "当前状态，松开刷新");
                return;
            case 1:
                this.progressBar.setVisibility(8);
                this.tipsTextview.setVisibility(0);
                this.lastUpdatedTextView.setVisibility(8);
                this.arrowImageView.clearAnimation();
                this.arrowImageView.setVisibility(0);
                if (this.isBack) {
                    this.isBack = false;
                    this.arrowImageView.clearAnimation();
                    this.arrowImageView.startAnimation(this.reverseAnimation);
                    this.tipsTextview.setText("下拉刷新");
                } else {
                    this.tipsTextview.setText("下拉刷新");
                }
                Log.i(TAG, "当前状态，下拉刷新");
                return;
            case 2:
                this.headView.setPadding(0, 0, 0, 0);
                this.progressBar.setVisibility(0);
                this.arrowImageView.clearAnimation();
                this.arrowImageView.setVisibility(8);
                this.tipsTextview.setText("正在刷新...");
                this.lastUpdatedTextView.setVisibility(0);
                Log.i(TAG, "当前状态,正在刷新...");
                return;
            case 3:
                this.headView.setPadding(0, this.headContentHeight * -1, 0, 0);
                this.progressBar.setVisibility(8);
                this.arrowImageView.clearAnimation();
                this.arrowImageView.setImageResource(R.drawable.goicon);
                this.tipsTextview.setText("下拉刷新");
                this.lastUpdatedTextView.setVisibility(8);
                Log.i(TAG, "当前状态，done");
                return;
            default:
                return;
        }
    }

    private void measureView(View child) {
        int childHeightSpec;
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(-1, -2);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setonRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        this.isRefreshable = true;
    }

    public void onRefreshComplete() {
        this.state = 3;
        this.lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
        this.lastUpdatedTextView.setVisibility(8);
        changeHeaderViewByState();
        invalidate();
        scrollTo(0, 0);
    }

    private void onRefresh() {
        if (this.refreshListener != null) {
            this.refreshListener.onRefresh();
        }
    }

    public void addChild(View child) {
        this.innerLayout.addView(child);
    }

    public void addChild(View child, int position) {
        this.innerLayout.addView(child, position);
    }
}
