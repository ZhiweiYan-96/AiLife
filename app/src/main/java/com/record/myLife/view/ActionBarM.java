package com.record.myLife.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.utils.AttrsUtils;

public class ActionBarM extends RelativeLayout {
    Context context;
    UserInterfaceComponent mComponent;

    class UserInterfaceComponent {
        ImageButton iv_left_first;
        ImageButton iv_right_first;
        ImageButton iv_right_second;
        RelativeLayout rl_left_first;
        RelativeLayout rl_right_first;
        RelativeLayout rl_right_second;
        TextView tv_left_first;
        TextView tv_left_second;
        TextView tv_right_first;
        TextView tv_right_second;

        UserInterfaceComponent() {
        }

        public void setTextLeftFirst(String str) {
            if (str != null && str.length() > 0) {
                ActionBarM.this.mComponent.tv_left_first.setText(str);
                ActionBarM.this.mComponent.iv_left_first.setVisibility(GONE);
                ActionBarM.this.mComponent.rl_left_first.setVisibility(VISIBLE);
            }
        }

        public void setTextLeftSecond(String str) {
            if (str != null && str.length() > 0) {
                ActionBarM.this.mComponent.tv_left_second.setText(str);
            }
        }

        public int setTextRightFirst(String str) {
            if (str == null || str.length() <= 0) {
                return 8;
            }
            ActionBarM.this.mComponent.tv_right_first.setText(str);
            ActionBarM.this.mComponent.iv_right_first.setVisibility(GONE);
            ActionBarM.this.mComponent.rl_right_first.setVisibility(VISIBLE);
            return 0;
        }

        public int setTextRightSecond(String str) {
            if (str == null || str.length() <= 0) {
                return 8;
            }
            ActionBarM.this.mComponent.tv_right_second.setText(str);
            ActionBarM.this.mComponent.iv_right_second.setVisibility(GONE);
            ActionBarM.this.mComponent.rl_right_second.setVisibility(VISIBLE);
            return 0;
        }

        public void setImageResourceLeftFirst(int res) {
            if (res != 0) {
                ActionBarM.this.mComponent.iv_left_first.setVisibility(VISIBLE);
                ActionBarM.this.mComponent.iv_left_first.setImageResource(res);
                ActionBarM.this.mComponent.tv_left_first.setVisibility(GONE);
                ActionBarM.this.mComponent.rl_left_first.setVisibility(VISIBLE);
            }
        }

        public int setImageResourceRightFirst(int res) {
            if (res == 0) {
                return 8;
            }
            ActionBarM.this.mComponent.iv_right_first.setVisibility(VISIBLE);
            ActionBarM.this.mComponent.iv_right_first.setImageResource(res);
            ActionBarM.this.mComponent.tv_right_first.setVisibility(GONE);
            ActionBarM.this.mComponent.rl_right_first.setVisibility(VISIBLE);
            return 0;
        }

        public int setImageResourceRightSecond(int res) {
            if (res == 0) {
                return 8;
            }
            ActionBarM.this.mComponent.iv_right_second.setVisibility(VISIBLE);
            ActionBarM.this.mComponent.iv_right_second.setImageResource(res);
            ActionBarM.this.mComponent.tv_right_second.setVisibility(GONE);
            ActionBarM.this.mComponent.rl_right_second.setVisibility(VISIBLE);
            return 0;
        }

        private void setVisibilityLeftFirst(int isVisible) {
            ActionBarM.this.mComponent.rl_left_first.setVisibility(isVisible);
        }

        private void setVisibilityRightFirst(int isVisible) {
            ActionBarM.this.mComponent.rl_right_first.setVisibility(isVisible);
        }

        private void setVisibilityRightSecond(int isVisible) {
            ActionBarM.this.mComponent.rl_right_second.setVisibility(isVisible);
        }
    }

    public ActionBarM(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ActionBarM(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        setAttrs(context, attrs);
    }

    public ActionBarM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        setAttrs(context, attrs);
    }

    private void setAttrs(Context context2, AttributeSet attrs) {
//        TypedArray array = this.context.obtainStyledAttributes(attrs, R.styleable.actionbarM);
//        String actionbarM_text_left_first = array.getString(0);
//        String actionbarM_text_left_second = array.getString(1);
//        String actionbarM_text_right_first = array.getString(2);
//        String actionbarM_text_right_second = array.getString(3);
//        this.mComponent.setTextLeftFirst(actionbarM_text_left_first);
//        this.mComponent.setTextLeftSecond(actionbarM_text_left_second);
//        int visibility_right_first = this.mComponent.setTextRightFirst(actionbarM_text_right_first);
//        int visibility_right_second = this.mComponent.setTextRightSecond(actionbarM_text_right_second);
//        int actionbarM_src_left_first = array.getResourceId(4, R.drawable.sel_arrow_gray2black_left);
//        int actionbarM_src_right_first = array.getResourceId(5, 0);
//        int actionbarM_src_right_second = array.getResourceId(6, 0);
//        this.mComponent.setImageResourceLeftFirst(actionbarM_src_left_first);
//        int visibility_right_first2 = this.mComponent.setImageResourceRightFirst(actionbarM_src_right_first);
//        int visibility_right_second2 = this.mComponent.setImageResourceRightSecond(actionbarM_src_right_second);
//        int actionbarM_visibility_left_first = array.getInt(7, 0);
//        if (visibility_right_first != 0) {
//            visibility_right_first = visibility_right_first2;
//        }
//        int actionbarM_visibility_right_first = array.getInt(9, visibility_right_first);
//        if (visibility_right_second != 0) {
//            visibility_right_second = visibility_right_second2;
//        }
//        int actionbarM_visibility_right_second = array.getInt(10, visibility_right_second);
//        this.mComponent.setVisibilityLeftFirst(actionbarM_visibility_left_first);
//        this.mComponent.setVisibilityRightFirst(actionbarM_visibility_right_first);
//        this.mComponent.setVisibilityRightSecond(actionbarM_visibility_right_second);
//        String text = AttrsUtils.getStringFromSystemAttrs(this.context, attrs, "text");
//        if (text != null && text.length() > 0) {
//            this.mComponent.tv_left_second.setText(text);
//        }
    }

    private void initView() {
        ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.component_actionbar, this);
        this.mComponent = new UserInterfaceComponent();
        setUserInterfaceComponent(this.mComponent);
        this.mComponent.rl_left_first.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((Activity) ActionBarM.this.context).onBackPressed();
            }
        });
    }

    private void setUserInterfaceComponent(UserInterfaceComponent uComponent) {
        uComponent.rl_left_first = (RelativeLayout) findViewById(R.id.rl_left_first);
        uComponent.iv_left_first = (ImageButton) findViewById(R.id.iv_left_first);
        uComponent.tv_left_first = (TextView) findViewById(R.id.tv_left_first);
        uComponent.tv_left_second = (TextView) findViewById(R.id.tv_left_second);
        uComponent.rl_right_second = (RelativeLayout) findViewById(R.id.rl_right_second);
        uComponent.iv_right_second = (ImageButton) findViewById(R.id.iv_right_second);
        uComponent.tv_right_second = (TextView) findViewById(R.id.tv_right_second);
        uComponent.rl_right_first = (RelativeLayout) findViewById(R.id.rl_right_first);
        uComponent.iv_right_first = (ImageButton) findViewById(R.id.ib_right_first);
        uComponent.tv_right_first = (TextView) findViewById(R.id.tv_right_first);
    }

    public void setOnClickListenerRightFirst(OnClickListener listener) {
        this.mComponent.rl_right_first.setOnClickListener(listener);
    }

    public void setOnClickListenerRightSecond(OnClickListener listener) {
        this.mComponent.rl_right_second.setOnClickListener(listener);
    }

    public void setOnClickListenerLeftFirst(OnClickListener listener) {
        this.mComponent.rl_left_first.setOnClickListener(listener);
    }

    public void setImageResourceRightFirst(int resId) {
        this.mComponent.iv_right_first.setImageResource(resId);
    }

    public void setImageResourceRightSecond(int resId) {
        this.mComponent.iv_right_second.setImageResource(resId);
    }

    public void setTextLeftSecond(String str) {
        this.mComponent.tv_left_second.setText(str);
    }

    public void setTextLeftSecond(int str) {
        this.mComponent.tv_left_second.setText(getResources().getString(str));
    }
}
