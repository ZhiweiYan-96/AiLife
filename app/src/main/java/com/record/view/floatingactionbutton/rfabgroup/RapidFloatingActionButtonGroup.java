package com.record.view.floatingactionbutton.rfabgroup;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.record.view.floatingactionbutton.RapidFloatingActionButton;
import com.record.view.floatingactionbutton.listener.OnRapidFloatingButtonGroupListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RapidFloatingActionButtonGroup extends FrameLayout implements OnClickListener {
    public static final int NO_SELECTED = -1;
    private static final long SWITCH_ANIMATION_DEFAULT_DURATION = 280;
    private static final String TAG = RapidFloatingActionButtonGroup.class.getSimpleName();
    private List<RapidFloatingActionButton> allRfabs = new ArrayList();
    private HashMap<String, RapidFloatingActionButton> allRfabsByIdentificationCode = new HashMap();
    private int currentSelectedIndex = -1;
    private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();
    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();
    private ScaleAnimation notSelectAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 1, 0.5f, 1, 0.5f);
    private OnRapidFloatingButtonGroupListener onRapidFloatingButtonGroupListener;
    private ScaleAnimation selectAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, 0.5f, 1, 0.5f);
    private ScaleAnimation selectCenterAnimation = new ScaleAnimation(1.0f, 1.0f, 0.3f, 1.0f, 1, 0.5f, 1, 0.5f);

    public RapidFloatingActionButtonGroup(Context context) {
        super(context);
        initAfterConstructor();
    }

    public RapidFloatingActionButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAfterConstructor();
    }

    public RapidFloatingActionButtonGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAfterConstructor();
    }

    @TargetApi(21)
    public RapidFloatingActionButtonGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAfterConstructor();
    }

    public void setOnRapidFloatingButtonGroupListener(OnRapidFloatingButtonGroupListener onRapidFloatingButtonGroupListener) {
        this.onRapidFloatingButtonGroupListener = onRapidFloatingButtonGroupListener;
    }

    private void initAfterConstructor() {
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            reset();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof RapidFloatingActionButton) {
                    addRFABToMemory((RapidFloatingActionButton) child);
                }
            }
            if (count > 0) {
                setSection(0, 0);
            }
            if (this.onRapidFloatingButtonGroupListener != null) {
                this.onRapidFloatingButtonGroupListener.onRFABGPrepared(this);
            }
        }
    }

    private void reset() {
        this.allRfabs.clear();
        this.allRfabsByIdentificationCode.clear();
    }

    public void addRFABs(RapidFloatingActionButton... rfabs) {
        for (RapidFloatingActionButton rfab : rfabs) {
            addRFABToMemory(rfab);
            addView(rfab);
        }
    }

    private void addRFABToMemory(@NonNull RapidFloatingActionButton rfab) {
        this.allRfabs.add(rfab);
        String identificationCode = rfab.getIdentificationCode();
        if ("".equals(identificationCode)) {
            throw new RuntimeException("RFAB[" + rfab + "]'s IDENTIFICATION CODE can not be IDENTIFICATION_CODE_NONE if you used RapidFloatingActionButtonGroup");
        } else if (this.allRfabsByIdentificationCode.containsKey(identificationCode)) {
            throw new RuntimeException("RFAB[" + rfab + "] Duplicate IDENTIFICATION CODE");
        } else {
            this.allRfabsByIdentificationCode.put(identificationCode, rfab);
        }
    }

    public RapidFloatingActionButton getRFABByIdentificationCode(String identificationCode) {
        return (RapidFloatingActionButton) this.allRfabsByIdentificationCode.get(identificationCode);
    }

    public void setSection(int expectIndex) {
        setSection(expectIndex, SWITCH_ANIMATION_DEFAULT_DURATION);
    }

    public void setSection(int expectIndex, long duration) {
        if (expectIndex >= 0 && expectIndex < this.allRfabs.size() && this.currentSelectedIndex != expectIndex) {
            executeSwitchWithAnimation(expectIndex, duration);
        }
    }

    public void onClick(View v) {
    }

    private void executeSwitchWithAnimation(final int expectIndex, long duration) {
        boolean shouldPlaySelectAnimation = false;
        boolean shouldPlayNotSelectAnimation = false;
        long perDuration = duration / 2;
        int size = this.allRfabs.size();
        for (int i = 0; i < size; i++) {
            final RapidFloatingActionButton rfab = (RapidFloatingActionButton) this.allRfabs.get(i);
            rfab.setVisibility(4);
            shouldPlaySelectAnimation = true;
            if (i == expectIndex) {
                this.selectAnimation.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        rfab.setVisibility(0);
                    }

                    public void onAnimationEnd(Animation animation) {
                        rfab.setVisibility(0);
                        rfab.clearAnimation();
                        RapidFloatingActionButtonGroup.this.currentSelectedIndex = expectIndex;
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                this.selectAnimation.setInterpolator(this.mDecelerateInterpolator);
                this.selectAnimation.setDuration(perDuration);
                rfab.setAnimation(this.selectAnimation);
                this.selectCenterAnimation.setInterpolator(this.mDecelerateInterpolator);
                this.selectCenterAnimation.setDuration(perDuration);
                final ImageView centerDrawableIv = rfab.getCenterDrawableIv();
                this.selectCenterAnimation.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        centerDrawableIv.clearAnimation();
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                centerDrawableIv.setAnimation(this.selectCenterAnimation);
            } else if (i == this.currentSelectedIndex) {
                rfab.setVisibility(0);
                shouldPlayNotSelectAnimation = true;
                this.notSelectAnimation.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        rfab.setVisibility(0);
                    }

                    public void onAnimationEnd(Animation animation) {
                        rfab.setVisibility(4);
                        rfab.clearAnimation();
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                this.notSelectAnimation.setInterpolator(this.mAccelerateInterpolator);
                this.notSelectAnimation.setDuration(perDuration);
                rfab.setAnimation(this.notSelectAnimation);
            }
        }
        if (!shouldPlaySelectAnimation) {
            return;
        }
        if (shouldPlayNotSelectAnimation) {
            this.selectAnimation.setStartOffset(perDuration);
            this.notSelectAnimation.start();
            this.selectAnimation.start();
            this.selectCenterAnimation.setStartOffset(2 * perDuration);
            this.selectCenterAnimation.start();
            return;
        }
        this.selectAnimation.start();
        this.selectCenterAnimation.setStartOffset(perDuration);
        this.selectCenterAnimation.start();
    }
}
