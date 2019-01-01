package com.record.myLife.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.record.myLife.R;

public class LeftRightView extends RelativeLayout {
    public static final int TYPE_LEFT = 1;
    public static final int TYPE_RIGHT = 2;
    private Context mContext;
    UiComponent uiComponent;

    class UiComponent {
        public Button btn_left;
        public Button btn_right;
        public ImageView iv_window;

        UiComponent() {
        }

        private void setUiComponent(UiComponent uiComponent) {
            uiComponent.iv_window = (ImageView) LeftRightView.this.findViewById(R.id.iv_window);
            uiComponent.btn_left = (Button) LeftRightView.this.findViewById(R.id.btn_left);
            uiComponent.btn_right = (Button) LeftRightView.this.findViewById(R.id.btn_right);
        }
    }

    public LeftRightView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public LeftRightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public LeftRightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R.layout.temp_left, this);
        this.uiComponent = new UiComponent();
        this.uiComponent.setUiComponent(this.uiComponent);
    }

    public void setOnClickListenerIv(OnClickListener listener) {
        this.uiComponent.iv_window.setOnClickListener(listener);
    }

    public void setOnClickListenerLeft(OnClickListener listener) {
        this.uiComponent.btn_left.setOnClickListener(listener);
    }

    public void setOnClickListenerRight(OnClickListener listener) {
        this.uiComponent.btn_right.setOnClickListener(listener);
    }

    public void setType(int type) {
        if (type == 1) {
            this.uiComponent.iv_window.setImageResource(R.drawable.ic_window_left);
        } else {
            this.uiComponent.iv_window.setImageResource(R.drawable.ic_window_right);
        }
    }
}
