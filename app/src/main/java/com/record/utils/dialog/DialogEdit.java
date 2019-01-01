package com.record.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import com.record.myLife.R;
import com.record.myLife.view.dialog.AlertDialogM.Builder;

public class DialogEdit {
    Dialog dialog;
    EditText et;

    public interface OnClickListener {
        void onClick(DialogInterface dialogInterface, int i, EditText editText);
    }

    @SuppressLint({"NewApi"})
    public DialogEdit(Context context, String title, String hint, int inputType, final OnClickListener listener) {
        this.et = new EditText(context);
        this.et.setLayoutParams(new LayoutParams(-1, -2));
        this.et.setBackground(null);
        this.et.setInputType(inputType);
        this.et.setHint(hint);
        this.dialog = new Builder(context).setTitle((CharSequence) title).setView(this.et).setPositiveButton(context.getString(R.string.str_sure), new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                listener.onClick(dialog, which, DialogEdit.this.et);
                dialog.cancel();
            }
        }).setNegativeButton(context.getString(R.string.str_cancel), new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create();
    }

    public void show() {
        this.dialog.show();
    }
}
