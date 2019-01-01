package com.record.view.floatingactionbutton.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.wangjie.androidbucket.utils.ABIOUtil;

public class AnimationView extends View {
    private static final int DURATION_DEFAULT = 300;
    private static final String TAG = AnimationView.class.getSimpleName();
    private ValueAnimator animator = new ValueAnimator();
    private AnimatorUpdateListener animatorUpdateListener = new AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            AnimationView.this.currentRadius = ((Integer) animation.getAnimatedValue()).intValue();
            AnimationView.this.invalidate();
        }
    };
    private AnimatorListenerAdapter closeAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if (AnimationView.this.onViewAnimationDrawableListener != null) {
                AnimationView.this.onViewAnimationDrawableListener.onAnimationDrawableCloseStart();
            }
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            AnimationView.this.clearAnimation();
            if (AnimationView.this.onViewAnimationDrawableListener != null) {
                AnimationView.this.onViewAnimationDrawableListener.onAnimationDrawableCloseEnd();
            }
        }
    };
    private DecelerateInterpolator closeInterpolator = new DecelerateInterpolator(1.8f);
    private int currentRadius;
    private View drawView;
    private int height;
    private PorterDuffXfermode mProPorterDuffXfermode = new PorterDuffXfermode(Mode.SRC_IN);
    private int minRadius = 0;
    private OnViewAnimationDrawableListener onViewAnimationDrawableListener;
    private AnimatorListenerAdapter openAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if (AnimationView.this.onViewAnimationDrawableListener != null) {
                AnimationView.this.onViewAnimationDrawableListener.onAnimationDrawableOpenStart();
            }
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            AnimationView.this.clearAnimation();
            if (AnimationView.this.onViewAnimationDrawableListener != null) {
                AnimationView.this.onViewAnimationDrawableListener.onAnimationDrawableOpenEnd();
            }
        }
    };
    private DecelerateInterpolator openInterpolator = new DecelerateInterpolator(0.6f);
    private Paint paint;
    private int radius;
    private Bitmap viewBitmap;
    private int width;

    public interface OnViewAnimationDrawableListener {
        void onAnimationDrawableCloseEnd();

        void onAnimationDrawableCloseStart();

        void onAnimationDrawableOpenEnd();

        void onAnimationDrawableOpenStart();
    }

    public void setOnViewAnimationDrawableListener(OnViewAnimationDrawableListener onViewAnimationDrawableListener) {
        this.onViewAnimationDrawableListener = onViewAnimationDrawableListener;
    }

    public AnimationView(Context context) {
        super(context);
        init();
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setBackgroundColor(0);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(-7829368);
        this.animator.addUpdateListener(this.animatorUpdateListener);
    }

    public void setDrawView(View drawView) {
        this.drawView = drawView;
    }

    public void setMinRadius(int minRadius) {
        this.minRadius = minRadius;
    }

    public void initialDraw() {
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();
        this.radius = (int) Math.sqrt((double) ((this.width * this.width) + (this.height * this.height)));
        this.currentRadius = this.radius;
        generateViewBitmap();
        invalidate();
    }

    private void generateViewBitmap() {
        if (this.viewBitmap == null) {
            Bitmap bm = convertViewToBitmap(this.drawView);
            if (bm != null) {
                this.viewBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight());
            }
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        generateViewBitmap();
        canvas.drawColor(0);
        this.paint.setXfermode(null);
        canvas.drawCircle((float) (this.width - this.minRadius), (float) (this.height - this.minRadius), (float) this.currentRadius, this.paint);
        this.paint.setXfermode(this.mProPorterDuffXfermode);
        if (this.viewBitmap != null) {
            canvas.drawBitmap(this.viewBitmap, 0.0f, 0.0f, this.paint);
        }
    }

    public void startOpenAnimation() {
        startOpenAnimation(300);
    }

    public void startOpenAnimation(long duration) {
        getOpenAnimator(duration).start();
    }

    public void startCloseAnimation() {
        startCloseAnimation(300);
    }

    public void startCloseAnimation(long duration) {
        getCloseAnimator(duration).start();
    }

    public ValueAnimator getOpenAnimator() {
        return getOpenAnimator(300);
    }

    public ValueAnimator getOpenAnimator(long duration) {
        this.animator.removeAllListeners();
        this.animator.setIntValues(new int[]{this.minRadius, this.radius});
        this.animator.setDuration(duration);
        this.animator.addListener(this.openAnimatorListenerAdapter);
        this.animator.setInterpolator(this.openInterpolator);
        return this.animator;
    }

    public ValueAnimator getCloseAnimator() {
        return getCloseAnimator(300);
    }

    public ValueAnimator getCloseAnimator(long duration) {
        this.animator.removeAllListeners();
        this.animator.setIntValues(new int[]{this.radius, this.minRadius});
        this.animator.setDuration(duration);
        this.animator.addListener(this.closeAnimatorListenerAdapter);
        this.animator.setInterpolator(this.closeInterpolator);
        return this.animator;
    }

    public void recycle() {
        ABIOUtil.recycleBitmap(this.viewBitmap);
    }

    public Bitmap convertViewToBitmap(View view) {
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    public static Bitmap convertViewToBitmapWithDraw(View view, int width, int height) {
        Bitmap viewBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        view.draw(new Canvas(viewBitmap));
        return viewBitmap;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycle();
    }
}
