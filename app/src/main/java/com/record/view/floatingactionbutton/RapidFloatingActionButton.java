package com.record.view.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.utils.DensityUtil;
import com.record.view.floatingactionbutton.constants.RFABSize;
import com.record.view.floatingactionbutton.listener.OnRapidFloatingActionListener;
import com.record.view.floatingactionbutton.listener.OnRapidFloatingButtonSeparateListener;
import com.record.view.floatingactionbutton.widget.CircleButtonDrawable;
import com.record.view.floatingactionbutton.widget.CircleButtonProperties;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.ABViewUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;

public class RapidFloatingActionButton extends FrameLayout implements OnClickListener {
    private static final int DEFAULT_BUTTON_DRAWABLE_RES_ID = 2130837906;
    public static final String IDENTIFICATION_CODE_NONE = "";
    private Drawable buttonDrawable;
    private int buttonDrawableSize;
    private String buttonText;
    private int buttonTextColor;
    private float buttonTextSize;
    private ImageView centerDrawableIv;
    TextView centerTv;
    private String identificationCode = "";
    private ObjectAnimator mDrawableAnimator;
    private OvershootInterpolator mOvershootInterpolator;
    private int normalColor;
    private OnRapidFloatingActionListener onRapidFloatingActionListener;
    private OnRapidFloatingButtonSeparateListener onRapidFloatingButtonSeparateListener;
    private int pressedColor;
    private CircleButtonProperties rfabProperties = new CircleButtonProperties();

    public String getIdentificationCode() {
        return this.identificationCode;
    }

    public void setIdentificationCode(@NonNull String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public ImageView getCenterDrawableIv() {
        return this.centerDrawableIv;
    }

    public void setOnRapidFloatingActionListener(OnRapidFloatingActionListener onRapidFloatingActionListener) {
        this.onRapidFloatingActionListener = onRapidFloatingActionListener;
    }

    public void setOnRapidFloatingButtonSeparateListener(OnRapidFloatingButtonSeparateListener onRapidFloatingButtonSeparateListener) {
        this.onRapidFloatingButtonSeparateListener = onRapidFloatingButtonSeparateListener;
    }

    public RapidFloatingActionButton(Context context) {
        super(context);
        initAfterConstructor();
    }

    public RapidFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parserAttrs(context, attrs, 0, 0);
        initAfterConstructor();
    }

    @TargetApi(11)
    public RapidFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parserAttrs(context, attrs, defStyleAttr, 0);
        initAfterConstructor();
    }

    @TargetApi(21)
    public RapidFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parserAttrs(context, attrs, defStyleAttr, defStyleRes);
        initAfterConstructor();
    }

    private void parserAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RapidFloatingActionButton, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar, defStyleAttr, defStyleRes);
        try {
            this.identificationCode = a.getString(11);
            if (this.identificationCode == null) {
                this.identificationCode = "";
            }
            this.buttonText = a.getString(2);
            this.buttonTextSize = (float) a.getDimensionPixelSize(3, DensityUtil.dip2px(context, 12.0f));
            this.buttonTextColor = a.getColor(4, 0);
            this.buttonDrawable = a.getDrawable(1);
            this.normalColor = a.getColor(5, getContext().getResources().getColor(R.color.rfab__color_background_normal));
            this.pressedColor = a.getColor(6, getContext().getResources().getColor(R.color.rfab__color_background_pressed));
            this.rfabProperties.setStandardSize(RFABSize.getRFABSizeByCode(a.getInt(0, RFABSize.NORMAL.getCode())));
            this.rfabProperties.setShadowColor(a.getInt(8, 0));
            this.rfabProperties.setShadowDx(a.getDimensionPixelSize(9, 0));
            this.rfabProperties.setShadowDy(a.getDimensionPixelSize(10, 0));
            this.rfabProperties.setShadowRadius(a.getDimensionPixelSize(7, 0));
        } finally {
            a.recycle();
        }
    }

    private void initAfterConstructor() {
        setOnClickListener(this);
        this.buttonDrawableSize = ABTextUtil.dip2px(getContext(), 24.0f);
        refreshRFABDisplay();
    }

    private void refreshRFABDisplay() {
        LayoutParams lp;
        CircleButtonDrawable normalDrawable = new CircleButtonDrawable(getContext(), this.rfabProperties, this.normalColor);
        ABViewUtil.setBackgroundDrawable(this, ABShape.selectorClickSimple(normalDrawable, new CircleButtonDrawable(getContext(), this.rfabProperties, this.pressedColor)));
        if (VERSION.SDK_INT > 11) {
            setLayerType(1, normalDrawable.getPaint());
        }
        if (this.buttonDrawable != null && this.centerDrawableIv == null) {
            removeAllViews();
            this.centerDrawableIv = new ImageView(getContext());
            addView(this.centerDrawableIv);
            lp = new LayoutParams(this.buttonDrawableSize, this.buttonDrawableSize);
            lp.gravity = 17;
            this.centerDrawableIv.setLayoutParams(lp);
            resetCenterImageView();
        }
        if (this.buttonText != null && this.buttonText.length() > 0) {
            this.centerTv = new TextView(getContext());
            this.centerTv.setText(this.buttonText);
            this.centerTv.setTextSize(0, this.buttonTextSize);
            this.centerTv.setGravity(17);
            if (this.buttonTextColor != 0) {
                this.centerTv.setTextColor(this.buttonTextColor);
            }
            lp = new LayoutParams(-1, -1);
            lp.gravity = 17;
            this.centerTv.setLayoutParams(lp);
            addView(this.centerTv);
        }
    }

    private void resetCenterImageView() {
        this.buttonDrawable.setBounds(0, 0, this.buttonDrawableSize, this.buttonDrawableSize);
        this.centerDrawableIv.setImageDrawable(this.buttonDrawable);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int realSize = this.rfabProperties.getRealSizePx(getContext());
        setMeasuredDimension(realSize, realSize);
    }

    public void onClick(View v) {
        if (this.onRapidFloatingActionListener != null) {
            this.onRapidFloatingActionListener.onRFABClick();
        }
        if (this.onRapidFloatingButtonSeparateListener != null) {
            this.onRapidFloatingButtonSeparateListener.onRFABClick();
        }
    }

    public CircleButtonProperties getRfabProperties() {
        return this.rfabProperties;
    }

    public void setButtonDrawable(Drawable buttonDrawable) {
        this.buttonDrawable = buttonDrawable;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    public void build() {
        refreshRFABDisplay();
    }

    public void onExpandAnimator(AnimatorSet animatorSet) {
        ensureDrawableAnimator();
        ensureDrawableInterpolator();
        this.mDrawableAnimator.cancel();
        this.mDrawableAnimator.setTarget(this.centerDrawableIv != null ? this.centerDrawableIv : this.buttonText);
        this.mDrawableAnimator.setFloatValues(new float[]{0.0f, -45.0f});
        this.mDrawableAnimator.setPropertyName("rotation");
        this.mDrawableAnimator.setInterpolator(this.mOvershootInterpolator);
        animatorSet.playTogether(new Animator[]{this.mDrawableAnimator});
    }

    public void onCollapseAnimator(AnimatorSet animatorSet) {
        ensureDrawableAnimator();
        ensureDrawableInterpolator();
        this.mDrawableAnimator.cancel();
        this.mDrawableAnimator.setTarget(this.centerDrawableIv != null ? this.centerDrawableIv : this.buttonText);
        this.mDrawableAnimator.setFloatValues(new float[]{-45.0f, 0.0f});
        this.mDrawableAnimator.setPropertyName("rotation");
        this.mDrawableAnimator.setInterpolator(this.mOvershootInterpolator);
        animatorSet.playTogether(new Animator[]{this.mDrawableAnimator});
    }

    private void ensureDrawableAnimator() {
        if (this.mDrawableAnimator == null) {
            this.mDrawableAnimator = new ObjectAnimator();
        }
    }

    private void ensureDrawableInterpolator() {
        if (this.mOvershootInterpolator == null) {
            this.mOvershootInterpolator = new OvershootInterpolator();
        }
    }
}
