package com.record.utils.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import com.record.myLife.R;
import com.record.myLife.add.AddRecordActivity;
import com.record.myLife.add.AddRecordDigitActivity;
import com.record.myLife.add.AddRecordWheelActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.utils.PreferUtils;
import com.record.utils.Val;

public class DialogUtils {

    public static class PopWindowM {
        public static int ADD_RECORD_DIGIT = 0;
        public static int ADD_RECORD_QUICK_ALLOCAT = 2;
        public static int ADD_RECORD_WHEEL = 1;
        Context context;

        public static void showChooseAddRecordTypeDialog(final Context context, final int current, final String addStartTime, final String addEndTime) {
            new Builder(context).setTitle((int) R.string.str_choose_add_record_type).setSingleChoiceItems(new String[]{context.getString(R.string.str_digit_keyboard), context.getString(R.string.str_idler_wheel), context.getString(R.string.str_quick_alloctioin)}, current, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == current) {
                        dialog.cancel();
                        return;
                    }
                    Intent it;
                    if (which == PopWindowM.ADD_RECORD_DIGIT) {
                        PreferUtils.getSP(context).edit().putInt(Val.CONFIGURE_ADD_RECORD_TYPE, 1).commit();
                        it = new Intent(context, AddRecordDigitActivity.class);
                        it.putExtra("startTime", addStartTime);
                        it.putExtra("stopTime", addEndTime);
                        context.startActivity(it);
                    } else if (which == PopWindowM.ADD_RECORD_WHEEL) {
                        PreferUtils.getSP(context).edit().putInt(Val.CONFIGURE_ADD_RECORD_TYPE, 2).commit();
                        it = new Intent(context, AddRecordWheelActivity.class);
                        it.putExtra("startTime", addStartTime);
                        it.putExtra("stopTime", addEndTime);
                        context.startActivity(it);
                    } else if (which == PopWindowM.ADD_RECORD_QUICK_ALLOCAT) {
                        PreferUtils.getSP(context).edit().putInt(Val.CONFIGURE_ADD_RECORD_TYPE, 3).commit();
                        context.startActivity(new Intent(context, AddRecordActivity.class));
                    }
                    dialog.cancel();
                    ((Activity) context).finish();
                }
            }).create().show();
        }
    }

    public static void showPrompt(Context context, String str) {
        new Builder(context).setTitle(context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setPositiveButton(context.getString(R.string.str_know_it_2), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    public static void showPromptForActivity(final Context context, String str, final Class cls) {
        new Builder(context).setTitle(context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setPositiveButton(context.getString(R.string.str_sign_in), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                context.startActivity(new Intent(context, cls));
            }
        }).setNegativeButton(context.getString(R.string.str_cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    public static void showPromptWithHandler(Context context, String str, OnClickListener onListener) {
        new Builder(context).setTitle(context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setPositiveButton(context.getString(R.string.str_sure), onListener).setNegativeButton(context.getString(R.string.str_cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    public static void showPromptWithHandler(Context context, String str, String positive, OnClickListener onListener) {
        new Builder(context).setTitle(context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setPositiveButton((CharSequence) positive, onListener).setNegativeButton(context.getString(R.string.str_cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    public static void showPromptWithPre(Context context, String str, String preName, int preValue) {
        SharedPreferences sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(preName, 0) < preValue) {
            new Builder(context).setTitle(context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setPositiveButton(context.getString(R.string.str_know_it_2), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
            sp.edit().putInt(preName, preValue).commit();
        }
    }

    public static void showPromptWithNoShow(final Context context, String str, final String preName) {
        if (context.getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(preName, 1) > 0) {
            new Builder(context).setTitle(context.getString(R.string.str_prompt)).setMessage((CharSequence) str).setPositiveButton(context.getString(R.string.str_no_show), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    context.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(preName, 0).commit();
                    dialog.cancel();
                }
            }).setNegativeButton(context.getString(R.string.str_sure), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
    }
}
