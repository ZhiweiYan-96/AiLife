package com.record.view.floatingactionbutton;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.record.view.floatingactionbutton.listener.OnRapidFloatingActionListener;

public abstract class RapidFloatingActionContent extends FrameLayout {
    protected OnRapidFloatingActionListener onRapidFloatingActionListener;
    private View rootView;

    protected abstract void initialContentViews(View view);

    public RapidFloatingActionContent(Context context) {
        super(context);
        initInConstructor();
    }

    public RapidFloatingActionContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInConstructor();
    }

    public RapidFloatingActionContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInConstructor();
    }

    @TargetApi(21)
    public RapidFloatingActionContent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInConstructor();
    }

    protected void initInConstructor() {
    }

    protected void initAfterRFABHelperBuild() {
    }

    protected void setOnRapidFloatingActionListener(OnRapidFloatingActionListener onRapidFloatingActionListener) {
        this.onRapidFloatingActionListener = onRapidFloatingActionListener;
    }

    public RapidFloatingActionContent setRootView(View rootView) {
        if (rootView != null) {
            this.rootView = rootView;
            removeAllViews();
            LayoutParams lp = (LayoutParams) getLayoutParams();
            if (lp == null) {
                lp = new LayoutParams(-2, -2);
            }
            setLayoutParams(lp);
            addView(this.rootView);
            initialContentViews(this.rootView);
        }
        return this;
    }

    public RapidFloatingActionContent setRootView(int rootViewResId) {
        return setRootView(LayoutInflater.from(getContext()).inflate(rootViewResId, null));
    }

    public void onExpandAnimator(AnimatorSet animatorSet) {
    }

    public void onCollapseAnimator(AnimatorSet animatorSet) {
    }
}
