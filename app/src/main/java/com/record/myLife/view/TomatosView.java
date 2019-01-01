package com.record.myLife.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.record.myLife.R;

public class TomatosView extends LinearLayout {
    Context context;
    int count = 4;
    int currentIndex = 2;
    int imageId = 0;
    LinearLayout ll;

    public TomatosView(Context context) {
        super(context);
        this.context = context;
    }

    public TomatosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    @SuppressLint({"NewApi"})
    public TomatosView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (this.ll == null) {
            this.ll = (LinearLayout) inflater.inflate(R.layout.tem_ll_vertical, this);
        }
        this.ll.removeAllViews();
        for (int i = 0; i < this.count; i++) {
            LinearLayout rl = (LinearLayout) inflater.inflate(R.layout.tem_tomato, null);
            rl.getChildAt(0).setBackgroundResource(R.drawable.x_circle_ivory_1);
            this.ll.addView(rl);
        }
    }

    private void serCurrent() {
        int i;
        int temCount = this.ll.getChildCount();
        for (i = 0; i < temCount; i++) {
            ((LinearLayout) this.ll.getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.x_circle_ivory_1);
        }
        for (i = 0; i <= this.currentIndex; i++) {
            if (this.imageId == 0) {
                this.imageId = R.drawable.ic_tomato_26;
            }
            ((LinearLayout) this.ll.getChildAt((this.count - 1) - i)).getChildAt(0).setBackgroundResource(this.imageId);
        }
    }

    public void setCurrentDot(int index) {
        this.currentIndex = index;
        serCurrent();
    }

    public void setCurrentIndex(int index, int resId) {
        this.imageId = resId;
        setCurrentDot(index);
    }

    public void setTomatoSize(int size) {
        int index = size % 4;
        int temIndex = index == 0 ? 3 : index - 1;
        setImageId((size - 1) / 4);
        setCurrentDot(temIndex);
    }

    private void setImageId(int groupSize) {
        if (groupSize <= 0) {
            this.imageId = R.drawable.ic_tomato_26;
        } else if (groupSize == 1) {
            this.imageId = R.drawable.ic_apple_cran;
        } else if (groupSize == 2) {
            this.imageId = R.drawable.ic_apple_cran_mature;
        } else if (groupSize == 3) {
            this.imageId = R.drawable.ic_apple_logo_red_green;
        } else if (groupSize == 4) {
            this.imageId = R.drawable.ic_apple_logo_red;
        } else {
            this.imageId = R.drawable.ic_apple_logo_color;
        }
    }

    public void setCount(int count) {
        this.count = count;
        initView();
    }
}
