package com.record.myLife.view;

import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout.LayoutParams;

public class AnimationController {
    public final int Accelerate = 1;
    public final int AccelerateDecelerate = 3;
    public final int Anticipate = 6;
    public final int AnticipateOvershoot = 7;
    public final int Bounce = 4;
    public final int Decelerate = 2;
    public final int Default = -1;
    public final int Linear = 0;
    public final int Overshoot = 5;
    public final int rela1 = 1;
    public final int rela2 = 2;

    private class MyAnimationListener implements AnimationListener {
        private View view;

        public MyAnimationListener(View view) {
            this.view = view;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            this.view.setVisibility(8);
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private void setEffect(Animation animation, int interpolatorType, long durationMillis, long delayMillis) {
        switch (interpolatorType) {
            case 0:
                animation.setInterpolator(new LinearInterpolator());
                break;
            case 1:
                animation.setInterpolator(new AccelerateInterpolator());
                break;
            case 2:
                animation.setInterpolator(new DecelerateInterpolator());
                break;
            case 3:
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                break;
            case 4:
                animation.setInterpolator(new BounceInterpolator());
                break;
            case 5:
                animation.setInterpolator(new OvershootInterpolator());
                break;
            case 6:
                animation.setInterpolator(new AnticipateInterpolator());
                break;
            case 7:
                animation.setInterpolator(new AnticipateOvershootInterpolator());
                break;
        }
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
    }

    private void baseIn(View view, Animation animation, long durationMillis, long delayMillis) {
        setEffect(animation, -1, durationMillis, delayMillis);
        view.setVisibility(0);
        view.startAnimation(animation);
    }

    private void baseOut(View view, Animation animation, long durationMillis, long delayMillis) {
        setEffect(animation, -1, durationMillis, delayMillis);
        animation.setAnimationListener(new MyAnimationListener(view));
        view.startAnimation(animation);
    }

    public void show(View view) {
        view.setVisibility(0);
    }

    public void hide(View view) {
        view.setVisibility(8);
    }

    public void transparent(View view) {
        view.setVisibility(4);
    }

    public void fadeIn(View view, long durationMillis, long delayMillis) {
        baseIn(view, new AlphaAnimation(0.0f, 1.0f), durationMillis, delayMillis);
    }

    public void fadeOut(View view, long durationMillis, long delayMillis) {
        baseOut(view, new AlphaAnimation(1.0f, 0.0f), durationMillis, delayMillis);
    }

    public void slideIn(View view, long durationMillis, long delayMillis) {
        baseIn(view, new TranslateAnimation(2, 1.0f, 2, 0.0f, 2, 0.0f, 2, 0.0f), durationMillis, delayMillis);
    }

    public void slideOut(View view, long durationMillis, long delayMillis) {
        baseOut(view, new TranslateAnimation(2, 0.0f, 2, -1.0f, 2, 0.0f, 2, 0.0f), durationMillis, delayMillis);
    }

    public void slideUp(View view, long durationMillis, long delayMillis) {
        baseOut(view, new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 1.0f), durationMillis, delayMillis);
    }

    public void slideDown(View view, long durationMillis, long delayMillis) {
        baseOut(view, new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 1.0f, 2, 0.0f), durationMillis, delayMillis);
    }

    public void scaleIn(View view, long durationMillis, long delayMillis) {
        baseIn(view, new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 2, 0.5f, 2, 0.5f), durationMillis, delayMillis);
    }

    public void scaleIn2(View view, long durationMillis, long delayMillis) {
        baseIn(view, new ScaleAnimation(2.0f, 0.9f, 2.0f, 0.9f, 1, 0.5f, 1, 0.5f), durationMillis, delayMillis);
    }

    public void scaleOut(View view, long durationMillis, long delayMillis) {
        baseOut(view, new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 2, 0.5f, 2, 0.5f), durationMillis, delayMillis);
    }

    public void rotateIn(View view, long durationMillis, long delayMillis) {
        baseIn(view, new RotateAnimation(-90.0f, 0.0f, 1, 0.0f, 1, 1.0f), durationMillis, delayMillis);
    }

    public void rotateOut(View view, long durationMillis, long delayMillis) {
        baseOut(view, new RotateAnimation(0.0f, 90.0f, 1, 0.0f, 1, 1.0f), durationMillis, delayMillis);
    }

    public void scaleRotateIn(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation1 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, 0.5f, 1, 0.5f);
        RotateAnimation animation2 = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public void scaleRotateOut(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation1 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        RotateAnimation animation2 = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public void slideFadeIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(2, 1.0f, 2, 0.0f, 2, 0.0f, 2, 0.0f);
        AlphaAnimation animation2 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public void slideFadeOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(2, 0.0f, 2, -1.0f, 2, 0.0f, 2, 0.0f);
        AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public void slideFadeIn_up(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 1.0f, 2, 0.0f);
        AlphaAnimation animation2 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public void slideFadeOut_down(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 1.0f);
        AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public void slideLeft(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(2, 1.0f, 2, 0.0f, 2, 0.0f, 2, 0.0f);
        AlphaAnimation animation2 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public void moveView(final View view, final float p1, final float p2, int durationMillis, int delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(p1, p2, 0.0f, 0.0f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration((long) durationMillis);
        animation.setStartOffset((long) delayMillis);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft() + ((int) (p2 - p1));
                int top = view.getTop();
                int width = view.getWidth();
                int height = view.getHeight();
                view.clearAnimation();
                int r = (int) (p2 + ((float) width));
                int b = (int) (p2 + ((float) height));
                LayoutParams params = new LayoutParams(-2, -2);
                params.addRule(12);
                params.setMargins((int) p2, top, 0, 0);
                view.setLayoutParams(params);
                Log.i("override", "p1:" + p1 + "p2" + p2 + ",left" + left + ",top" + top + ",r" + r + ",b" + b + ",width:" + width + ",height" + height);
            }
        });
        view.clearAnimation();
        view.startAnimation(animation);
    }

    public void rotateView(View view, long durationMillis, long delayMillis) {
        RotateAnimation amAnimation = new RotateAnimation(0.0f, 720.0f, (float) (view.getHeight() / 2), (float) (view.getWidth() / 2));
        amAnimation.setDuration(durationMillis);
        amAnimation.setRepeatCount(20);
        amAnimation.setFillAfter(false);
        amAnimation.setInterpolator(new LinearInterpolator());
        amAnimation.setRepeatMode(-1);
        view.startAnimation(amAnimation);
    }
}
