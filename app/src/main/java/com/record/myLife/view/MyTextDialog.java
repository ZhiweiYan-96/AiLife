package com.record.myLife.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.record.myLife.R;

public class MyTextDialog extends Dialog {
    Context context;
    int layoutRes;

    public static class Builder {
        private View contentView;
        private Context context;
        private String message;
        private OnClickListener negativeButtonClickListener;
        private String negativeButtonText;
        private OnClickListener positiveButtonClickListener;
        private String positiveButtonText;
        private String title;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) this.context.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) this.context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) this.context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) this.context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public MyTextDialog create() {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
            final MyTextDialog dialog = new MyTextDialog(this.context, R.style.customDialog);
            View layout = inflater.inflate(R.layout.dialog_my_text, null);
            dialog.addContentView(layout, new LayoutParams(-1, -2));
            if (this.title != null) {
                ((TextView) layout.findViewById(R.id.tv_dialog_text_title)).setText(this.title);
            } else {
                layout.findViewById(R.id.tv_dialog_text_title).setVisibility(8);
            }
            if (this.positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.bt_dialog_positive)).setText(this.positiveButtonText);
                if (this.positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.bt_dialog_positive)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Builder.this.positiveButtonClickListener.onClick(dialog, -1);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.bt_dialog_positive).setVisibility(8);
            }
            if (this.negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.bt_dialog_negative)).setText(this.negativeButtonText);
                if (this.negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.bt_dialog_negative)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Builder.this.negativeButtonClickListener.onClick(dialog, -2);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.bt_dialog_negative).setVisibility(8);
            }
            if (this.message != null) {
                ((TextView) layout.findViewById(R.id.tv_dialog_text_msg)).setText(this.message);
            } else if (this.contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.ll_dialog_view)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.ll_dialog_view)).addView(this.contentView, new LayoutParams(-1, -2));
            } else {
                layout.findViewById(R.id.ll_dialog_view).setVisibility(8);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }

    public MyTextDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MyTextDialog(Context context, int theme) {
        super(context, theme);
    }

    public MyTextDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
