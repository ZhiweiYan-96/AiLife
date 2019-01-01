package com.record.view.floatingactionbutton.contentimpl.viewbase;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.record.view.floatingactionbutton.RapidFloatingActionContent;
import com.record.view.floatingactionbutton.util.ViewUtil;
import com.record.view.floatingactionbutton.widget.AnimationView;
import com.record.view.floatingactionbutton.widget.AnimationView.OnViewAnimationDrawableListener;

public abstract class RapidFloatingActionContentViewBase extends RapidFloatingActionContent implements OnViewAnimationDrawableListener {
    private static final String TAG = RapidFloatingActionContentViewBase.class.getSimpleName();
    private AnimationView mAnimationView;
    private View realContentView;

    @NonNull
    protected abstract View getContentView();

    public RapidFloatingActionContentViewBase(Context context) {
        super(context);
    }

    public RapidFloatingActionContentViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RapidFloatingActionContentViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RapidFloatingActionContentViewBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void initAfterRFABHelperBuild() {
        this.realContentView = getContentView();
        FrameLayout view = new FrameLayout(getContext());
        view.setLayoutParams(new LayoutParams(-1, -1));
        this.realContentView.setLayoutParams(new LayoutParams(-1, -1));
        view.addView(this.realContentView);
        this.mAnimationView = new AnimationView(getContext());
        ViewUtil.typeSoftWare(this.mAnimationView);
        this.mAnimationView.setLayoutParams(this.realContentView.getLayoutParams());
        this.mAnimationView.setDrawView(this.realContentView);
        this.mAnimationView.setOnViewAnimationDrawableListener(this);
        view.addView(this.mAnimationView);
        setRootView((View) view);
        setMeasureAllChildren(true);
        measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        this.mAnimationView.setLayoutParams(new LayoutParams(getMeasuredWidth(), getMeasuredHeight()));
        this.mAnimationView.setMinRadius(this.onRapidFloatingActionListener.obtainRFAButton().getRfabProperties().getRealSizePx(getContext()) / 2);
        this.mAnimationView.initialDraw();
    }

    protected void initialContentViews(View rootView) {
    }

    public void onAnimationDrawableOpenStart() {
        this.realContentView.setVisibility(8);
        this.mAnimationView.setVisibility(0);
    }

    public void onAnimationDrawableOpenEnd() {
        this.realContentView.setVisibility(0);
        this.mAnimationView.setVisibility(8);
    }

    public void onAnimationDrawableCloseStart() {
        this.realContentView.setVisibility(8);
        this.mAnimationView.setVisibility(0);
    }

    public void onAnimationDrawableCloseEnd() {
        this.realContentView.setVisibility(0);
        this.mAnimationView.setVisibility(8);
    }

    public void onExpandAnimator(AnimatorSet animatorSet) {
        if (this.mAnimationView != null) {
            animatorSet.playTogether(new Animator[]{this.mAnimationView.getOpenAnimator()});
        }
    }

    public void onCollapseAnimator(AnimatorSet animatorSet) {
        if (this.mAnimationView != null) {
            animatorSet.playTogether(new Animator[]{this.mAnimationView.getCloseAnimator()});
        }
    }
}
