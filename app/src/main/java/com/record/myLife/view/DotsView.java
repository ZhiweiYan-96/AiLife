package com.record.myLife.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.record.myLife.R;

public class DotsView extends LinearLayout {
    public static int SCALE = 24;
    Context context;
    int count = 2;
    int currentIndex = 0;
    LinearLayout ll;
    private Paint mPaint;

    public DotsView(Context context) {
        super(context);
        this.context = context;
    }

    public DotsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @SuppressLint({"NewApi"})
    public DotsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        this.ll = (LinearLayout) inflater.inflate(R.layout.tem_ll2, this);
        this.ll.removeAllViews();
        for (int i = 0; i < this.count; i++) {
            LinearLayout rl = (LinearLayout) inflater.inflate(R.layout.tem_dot, null);
            rl.getChildAt(0).setBackgroundResource(R.drawable.x_circle_ivory_1);
            this.ll.addView(rl);
        }
    }

    private void serCurrent() {
        int temCount = this.ll.getChildCount();
        for (int i = 0; i < temCount; i++) {
            ((LinearLayout) this.ll.getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.x_circle_ivory_1);
        }
        ((LinearLayout) this.ll.getChildAt(this.currentIndex)).getChildAt(0).setBackgroundResource(R.drawable.x_circle_blue_1);
    }

    public void setCurrentDot(int index) {
        this.currentIndex = index;
        serCurrent();
    }

    public void setCount(int count) {
        this.count = count;
        initView();
    }
}
