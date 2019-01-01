package com.record.myLife.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.record.myLife.R;

public class SampleView extends LinearLayout {
    Context context;
    int count = 4;
    int currentIndex = 2;
    int imageId = 0;
    LinearLayout ll;

    public SampleView(Context context) {
        super(context);
        this.context = context;
    }

    public SampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    @SuppressLint({"NewApi"})
    public SampleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (this.ll == null) {
            this.ll = (LinearLayout) inflater.inflate(R.layout.tem_ll_deliberate, this);
        }
    }
}
