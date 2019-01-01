package com.record.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import com.record.myLife.view.ShowImageActivity;

public class ShowGuideImgUtils {
    public static void showImage(Context context, String configureName, int value, int image) {
        SharedPreferences sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(configureName, 0) < value) {
            Intent it2 = new Intent(context, ShowImageActivity.class);
            it2.putExtra("image", image);
            it2.putExtra("preferenceName", configureName);
            it2.putExtra("value", value);
            context.startActivity(it2);
            sp.edit().putInt(configureName, value).commit();
        }
    }

    public static void showImage_v2(Context context, String configureName, int value, int image, int position) {
        SharedPreferences sp = context.getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(configureName, 0) < value) {
            Intent it2 = new Intent(context, ShowImageActivity.class);
            it2.putExtra("image", image);
            it2.putExtra("position", position);
            it2.putExtra("preferenceName", configureName);
            it2.putExtra("value", value);
            context.startActivity(it2);
            sp.edit().putInt(configureName, value).commit();
        }
    }

    public static void isShowDot(Context context, View v, String configureName, int value) {
        if (context.getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).getInt(configureName, 0) < value) {
            v.setVisibility(0);
        } else {
            v.setVisibility(8);
        }
    }
}
