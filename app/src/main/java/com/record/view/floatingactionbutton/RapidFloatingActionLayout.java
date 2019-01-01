package com.record.view.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.record.myLife.R;
import com.record.view.floatingactionbutton.listener.OnRapidFloatingActionListener;

public class RapidFloatingActionLayout extends RelativeLayout implements OnClickListener {
    public static final long ANIMATION_DURATION = 150;
    private static final String TAG = RapidFloatingActionLayout.class.getSimpleName();
    private AnimatorSet animatorSet;
    private ObjectAnimator contentAnimator = new ObjectAnimator();
    private RapidFloatingActionContent contentView;
    private boolean disableContentDefaultAnimation = false;
    private ObjectAnimator fillFrameAnimator = new ObjectAnimator();
    private View fillFrameView;
    private float frameAlpha;
    private int frameColor;
    private boolean isContentAboveLayout = true;
    private boolean isExpanded = false;
    private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();
    private OnRapidFloatingActionListener onRapidFloatingActionListener;

    public RapidFloatingActionLayout(Context context) {
        super(context);
        initAfterConstructor();
    }

    public RapidFloatingActionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parserAttrs(context, attrs, 0, 0);
        initAfterConstructor();
    }

    public RapidFloatingActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parserAttrs(context, attrs, defStyleAttr, 0);
        initAfterConstructor();
    }

    @TargetApi(21)
    public RapidFloatingActionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parserAttrs(context, attrs, defStyleAttr, defStyleRes);
        initAfterConstructor();
    }

    private void parserAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        float f = 1.0f;
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RapidFloatingActionLayout, defStyleAttr, defStyleRes);
//        this.frameColor = a.getColor(0, getContext().getResources().getColor(R.color.rfab__color_frame));
//        this.frameAlpha = a.getFloat(1, Float.valueOf(getResources().getString(R.string.rfab_rfal__float_convert_color_alpha)).floatValue());
        if (this.frameAlpha <= 1.0f) {
            f = this.frameAlpha < 0.0f ? 0.0f : this.frameAlpha;
        }
        this.frameAlpha = f;
//        a.recycle();
    }

    private void initAfterConstructor() {
    }

    public void setOnRapidFloatingActionListener(OnRapidFloatingActionListener onRapidFloatingActionListener) {
        this.onRapidFloatingActionListener = onRapidFloatingActionListener;
    }

    public void setIsContentAboveLayout(boolean isContentAboveLayout) {
        this.isContentAboveLayout = isContentAboveLayout;
    }

    public boolean isContentAboveLayout() {
        return this.isContentAboveLayout;
    }

    public void setDisableContentDefaultAnimation(boolean disableContentDefaultAnimation) {
        this.disableContentDefaultAnimation = disableContentDefaultAnimation;
    }

    public RapidFloatingActionLayout setContentView(RapidFloatingActionContent contentView) {
        if (contentView == null) {
            throw new RuntimeException("contentView can not be null");
        }
        if (this.contentView != null) {
            removeView(this.contentView);
            Log.w(TAG, "contentView: [" + this.contentView + "] is already initialed");
        }
        this.contentView = contentView;
        this.fillFrameView = new View(getContext());
        this.fillFrameView.setLayoutParams(new LayoutParams(-1, -1));
        this.fillFrameView.setBackgroundColor(this.frameColor);
        this.fillFrameView.setVisibility(GONE);
        this.fillFrameView.setOnClickListener(this);
        addView(this.fillFrameView, Math.max(getChildCount() - 1, 0));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        int rfabId = this.onRapidFloatingActionListener.obtainRFAButton().getId();
        lp.addRule(2, rfabId);
        lp.addRule(7, rfabId);
        if (!(this.isContentAboveLayout || this.onRapidFloatingActionListener == null)) {
            lp.bottomMargin = -this.onRapidFloatingActionListener.obtainRFAButton().getRfabProperties().getRealSizePx(getContext());
        }
        this.contentView.setLayoutParams(lp);
        this.contentView.setVisibility(GONE);
        addView(this.contentView);
        return this;
    }

    public void onClick(View v) {
        if (this.fillFrameView == v) {
            collapseContent();
        }
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
        if (this.fillFrameView != null) {
            this.fillFrameView.setBackgroundColor(frameColor);
        }
    }

    public void setFrameAlpha(float frameAlpha) {
        this.frameAlpha = frameAlpha;
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }

    public void toggleContent() {
        if (this.isExpanded) {
            collapseContent();
        } else {
            expandContent();
        }
    }

    public void expandContent() {
        if (!this.isExpanded) {
            endAnimatorSet();
            this.isExpanded = true;
            this.fillFrameAnimator.setTarget(this.fillFrameView);
            this.fillFrameAnimator.setFloatValues(new float[]{0.0f, this.frameAlpha});
            this.fillFrameAnimator.setPropertyName("alpha");
            this.animatorSet = new AnimatorSet();
            if (this.disableContentDefaultAnimation) {
                this.animatorSet.playTogether(new Animator[]{this.fillFrameAnimator});
            } else {
                this.contentAnimator.setTarget(this.contentView);
                this.contentAnimator.setFloatValues(new float[]{0.0f, 1.0f});
                this.contentAnimator.setPropertyName("alpha");
                this.animatorSet.playTogether(new Animator[]{this.contentAnimator, this.fillFrameAnimator});
            }
            this.animatorSet.setDuration(150);
            this.animatorSet.setInterpolator(this.mAccelerateInterpolator);
            this.onRapidFloatingActionListener.onExpandAnimator(this.animatorSet);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    RapidFloatingActionLayout.this.contentView.setVisibility(VISIBLE);
                    RapidFloatingActionLayout.this.fillFrameView.setVisibility(VISIBLE);
                }

                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    RapidFloatingActionLayout.this.isExpanded = true;
                }
            });
            this.animatorSet.start();
        }
    }

    public void collapseContent() {
        if (this.isExpanded) {
            endAnimatorSet();
            this.isExpanded = false;
            this.fillFrameAnimator.setTarget(this.fillFrameView);
            this.fillFrameAnimator.setFloatValues(new float[]{this.frameAlpha, 0.0f});
            this.fillFrameAnimator.setPropertyName("alpha");
            this.animatorSet = new AnimatorSet();
            if (this.disableContentDefaultAnimation) {
                this.animatorSet.playTogether(new Animator[]{this.fillFrameAnimator});
            } else {
                this.contentAnimator.setTarget(this.contentView);
                this.contentAnimator.setFloatValues(new float[]{1.0f, 0.0f});
                this.contentAnimator.setPropertyName("alpha");
                this.animatorSet.playTogether(new Animator[]{this.contentAnimator, this.fillFrameAnimator});
            }
            this.animatorSet.setDuration(150);
            this.animatorSet.setInterpolator(this.mAccelerateInterpolator);
            this.onRapidFloatingActionListener.onCollapseAnimator(this.animatorSet);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    RapidFloatingActionLayout.this.fillFrameView.setVisibility(VISIBLE);
                    RapidFloatingActionLayout.this.contentView.setVisibility(VISIBLE);
                }

                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    RapidFloatingActionLayout.this.fillFrameView.setVisibility(GONE);
                    RapidFloatingActionLayout.this.contentView.setVisibility(GONE);
                    RapidFloatingActionLayout.this.isExpanded = false;
                }
            });
            this.animatorSet.start();
        }
    }

    private void endAnimatorSet() {
        if (this.animatorSet != null) {
            this.animatorSet.end();
        }
    }

    public RapidFloatingActionContent getContentView() {
        return this.contentView;
    }
}
