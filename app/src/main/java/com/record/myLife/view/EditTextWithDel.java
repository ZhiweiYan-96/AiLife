package com.record.myLife.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import com.record.myLife.R;

public class EditTextWithDel extends EditText {
    private static final String TAG = "EditTextWithDel";
    private Drawable imgAble;
    private Drawable imgInable;
    private Context mContext;

    public EditTextWithDel(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        this.imgInable = this.mContext.getResources().getDrawable(R.drawable.ic_alph);
        this.imgAble = this.mContext.getResources().getDrawable(R.drawable.ic_close_2);
        addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                EditTextWithDel.this.setDrawable();
            }
        });
        setDrawable();
    }

    private void setDrawable() {
        if (length() < 1) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, this.imgInable, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, this.imgAble, null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.imgAble != null && event.getAction() == 1) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;
            if (rect.contains(eventX, eventY)) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }
}
