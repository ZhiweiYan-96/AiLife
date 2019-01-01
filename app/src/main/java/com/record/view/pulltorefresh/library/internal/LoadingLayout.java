package com.record.view.pulltorefresh.library.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.view.pullrefresh.view.ILoadingLayout;
import com.record.view.pullrefresh.view.PullToRefreshBase.Mode;
import com.record.view.pullrefresh.view.PullToRefreshBase.Orientation;

@SuppressLint({"ViewConstructor"})
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    static final String LOG_TAG = "PullToRefresh-LoadingLayout";
    protected final ImageView mHeaderImage;
    protected final ProgressBar mHeaderProgress;
    private final TextView mHeaderText;
    private RelativeLayout mInnerLayout;
    protected final Mode mMode;
    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;
    protected final Orientation mScrollDirection;
    private final TextView mSubHeaderText;
    private boolean mUseIntrinsicAnimation;

    protected abstract int getDefaultDrawableResId();

    protected abstract void onLoadingDrawableSet(Drawable drawable);

    protected abstract void onPullImpl(float f);

    protected abstract void pullToRefreshImpl();

    protected abstract void refreshingImpl();

    protected abstract void releaseToRefreshImpl();

    protected abstract void resetImpl();

    public LoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context);
        TypedValue styleID;
        ColorStateList colors;
        this.mMode = mode;
        this.mScrollDirection = scrollDirection;
        switch (scrollDirection) {
            case HORIZONTAL:
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this);
                break;
            default:
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);
                break;
        }
        this.mInnerLayout = (RelativeLayout) findViewById(R.id.fl_inner);
        this.mHeaderText = (TextView) this.mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        this.mHeaderProgress = (ProgressBar) this.mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
        this.mSubHeaderText = (TextView) this.mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
        this.mHeaderImage = (ImageView) this.mInnerLayout.findViewById(R.id.pull_to_refresh_image);
        LayoutParams lp = (LayoutParams) this.mInnerLayout.getLayoutParams();
        switch (mode) {
            case PULL_FROM_END:
                lp.gravity = scrollDirection == Orientation.VERTICAL ? 48 : 3;
                this.mPullLabel = context.getString(R.string.pull_to_refresh_from_bottom_pull_label);
                this.mRefreshingLabel = context.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
                this.mReleaseLabel = context.getString(R.string.pull_to_refresh_from_bottom_release_label);
                break;
            default:
                lp.gravity = scrollDirection == Orientation.VERTICAL ? 80 : 5;
                this.mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
                this.mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
                this.mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
                break;
        }
//        if (attrs.hasValue(1)) {
//            Drawable background = attrs.getDrawable(1);
//            if (background != null) {
//                ViewCompat.setBackground(this, background);
//            }
//        }
//        if (attrs.hasValue(10)) {
//            styleID = new TypedValue();
//            attrs.getValue(10, styleID);
//            setTextAppearance(styleID.data);
//        }
//        if (attrs.hasValue(11)) {
//            styleID = new TypedValue();
//            attrs.getValue(11, styleID);
//            setSubTextAppearance(styleID.data);
//        }
//        if (attrs.hasValue(2)) {
//            colors = attrs.getColorStateList(2);
//            if (colors != null) {
//                setTextColor(colors);
//            }
//        }
//        if (attrs.hasValue(3)) {
//            colors = attrs.getColorStateList(3);
//            if (colors != null) {
//                setSubTextColor(colors);
//            }
//        }
//        Drawable imageDrawable = null;
//        if (attrs.hasValue(6)) {
//            imageDrawable = attrs.getDrawable(6);
//        }
//        switch (mode) {
//            case PULL_FROM_END:
//                if (!attrs.hasValue(8)) {
//                    if (attrs.hasValue(18)) {
//                        Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
//                        imageDrawable = attrs.getDrawable(18);
//                        break;
//                    }
//                }
//                imageDrawable = attrs.getDrawable(8);
//                break;
//            default:
//                if (!attrs.hasValue(7)) {
//                    if (attrs.hasValue(17)) {
//                        Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
//                        imageDrawable = attrs.getDrawable(17);
//                        break;
//                    }
//                }
//                imageDrawable = attrs.getDrawable(7);
//                break;
//        }
//        if (imageDrawable == null) {
//            imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());
//        }
//        setLoadingDrawable(imageDrawable);
        reset();
    }

    public final void setHeight(int height) {
        getLayoutParams().height = height;
        requestLayout();
    }

    public final void setWidth(int width) {
        getLayoutParams().width = width;
        requestLayout();
    }

    public final int getContentSize() {
        switch (this.mScrollDirection) {
            case HORIZONTAL:
                return this.mInnerLayout.getWidth();
            default:
                return this.mInnerLayout.getHeight();
        }
    }

    public final void hideAllViews() {
        if (this.mHeaderText.getVisibility() == VISIBLE) {
            this.mHeaderText.setVisibility(INVISIBLE);
        }
        if (this.mHeaderProgress.getVisibility() == VISIBLE) {
            this.mHeaderProgress.setVisibility(INVISIBLE);
        }
        if (this.mHeaderImage.getVisibility() == VISIBLE) {
            this.mHeaderImage.setVisibility(INVISIBLE);
        }
        if (this.mSubHeaderText.getVisibility() == VISIBLE) {
            this.mSubHeaderText.setVisibility(INVISIBLE);
        }
    }

    public final void onPull(float scaleOfLayout) {
        if (!this.mUseIntrinsicAnimation) {
            onPullImpl(scaleOfLayout);
        }
    }

    public final void pullToRefresh() {
        if (this.mHeaderText != null) {
            this.mHeaderText.setText(this.mPullLabel);
        }
        pullToRefreshImpl();
    }

    public final void refreshing() {
        if (this.mHeaderText != null) {
            this.mHeaderText.setText(this.mRefreshingLabel);
        }
        if (this.mUseIntrinsicAnimation) {
            ((AnimationDrawable) this.mHeaderImage.getDrawable()).start();
        } else {
            refreshingImpl();
        }
        if (this.mSubHeaderText != null) {
            this.mSubHeaderText.setVisibility(GONE);
        }
    }

    public final void releaseToRefresh() {
        if (this.mHeaderText != null) {
            this.mHeaderText.setText(this.mReleaseLabel);
        }
        releaseToRefreshImpl();
    }

    public final void reset() {
        if (this.mHeaderText != null) {
            this.mHeaderText.setText(this.mPullLabel);
        }
        this.mHeaderImage.setVisibility(VISIBLE);
        if (this.mUseIntrinsicAnimation) {
            ((AnimationDrawable) this.mHeaderImage.getDrawable()).stop();
        } else {
            resetImpl();
        }
        if (this.mSubHeaderText == null) {
            return;
        }
        if (TextUtils.isEmpty(this.mSubHeaderText.getText())) {
            this.mSubHeaderText.setVisibility(GONE);
        } else {
            this.mSubHeaderText.setVisibility(VISIBLE);
        }
    }

    public void setLastUpdatedLabel(CharSequence label) {
        setSubHeaderText(label);
    }

    public final void setLoadingDrawable(Drawable imageDrawable) {
        this.mHeaderImage.setImageDrawable(imageDrawable);
        this.mUseIntrinsicAnimation = imageDrawable instanceof AnimationDrawable;
        onLoadingDrawableSet(imageDrawable);
    }

    public void setPullLabel(CharSequence pullLabel) {
        this.mPullLabel = pullLabel;
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
        this.mRefreshingLabel = refreshingLabel;
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
        this.mReleaseLabel = releaseLabel;
    }

    public void setTextTypeface(Typeface tf) {
        this.mHeaderText.setTypeface(tf);
    }

    public final void showInvisibleViews() {
        if (INVISIBLE == this.mHeaderText.getVisibility()) {
            this.mHeaderText.setVisibility(VISIBLE);
        }
        if (INVISIBLE == this.mHeaderProgress.getVisibility()) {
            this.mHeaderProgress.setVisibility(VISIBLE);
        }
        if (INVISIBLE == this.mHeaderImage.getVisibility()) {
            this.mHeaderImage.setVisibility(VISIBLE);
        }
        if (INVISIBLE == this.mSubHeaderText.getVisibility()) {
            this.mSubHeaderText.setVisibility(VISIBLE);
        }
    }

    private void setSubHeaderText(CharSequence label) {
        if (this.mSubHeaderText == null) {
            return;
        }
        if (TextUtils.isEmpty(label)) {
            this.mSubHeaderText.setVisibility(GONE);
            return;
        }
        this.mSubHeaderText.setText(label);
        if (GONE == this.mSubHeaderText.getVisibility()) {
            this.mSubHeaderText.setVisibility(VISIBLE);
        }
    }

    private void setSubTextAppearance(int value) {
        if (this.mSubHeaderText != null) {
            this.mSubHeaderText.setTextAppearance(getContext(), value);
        }
    }

    private void setSubTextColor(ColorStateList color) {
        if (this.mSubHeaderText != null) {
            this.mSubHeaderText.setTextColor(color);
        }
    }

    private void setTextAppearance(int value) {
        if (this.mHeaderText != null) {
            this.mHeaderText.setTextAppearance(getContext(), value);
        }
        if (this.mSubHeaderText != null) {
            this.mSubHeaderText.setTextAppearance(getContext(), value);
        }
    }

    private void setTextColor(ColorStateList color) {
        if (this.mHeaderText != null) {
            this.mHeaderText.setTextColor(color);
        }
        if (this.mSubHeaderText != null) {
            this.mSubHeaderText.setTextColor(color);
        }
    }
}
