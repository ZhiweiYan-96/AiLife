package com.record.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.IOException;
import java.io.InputStream;

public class Image {
    public Bitmap bitmap;

    public Image(Context context, int id) {
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), id);
    }

    public Image(Context context, String name, float scaleW, float scaleH) throws IOException {
        InputStream is = context.getAssets().open(name);
        this.bitmap = BitmapFactory.decodeStream(is);
        is.close();
        this.bitmap = setScaleSize(this.bitmap, scaleW, scaleH);
    }

    public int getWidth() {
        return this.bitmap.getWidth();
    }

    public int getHeight() {
        return this.bitmap.getHeight();
    }

    public Bitmap overlayColor(int color) {
        if (color == 0) {
            color = 1180787041;
        }
        int width = this.bitmap.getWidth();
        int height = this.bitmap.getHeight();
        int len = width * height;
        int[] imageARGB = new int[len];
        int[] newimage = new int[len];
        getPixel(imageARGB, 0, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (imageARGB[(i * width) + j] != 0) {
                    newimage[(i * width) + j] = color;
                } else {
                    newimage[(i * width) + j] = 0;
                }
            }
        }
        return CreateImage(newimage, width, height);
    }

    public int[] getPixel(int[] array, int x, int y) {
        this.bitmap.getPixels(array, 0, getWidth(), 0, 0, getWidth(), getHeight());
        return array;
    }

    public Bitmap CreateImage(int[] array, int w, int h) {
        return Bitmap.createBitmap(array, w, h, Config.ARGB_8888);
    }

    private Bitmap setScaleSize(Bitmap bitmap, float arg0, float arg1) {
        Matrix matrix = new Matrix();
        matrix.postScale(arg0, arg1);
        return Bitmap.createBitmap(bitmap, 0, 0, getWidth(), getHeight(), matrix, true);
    }

    public static void free(Image image) {
        try {
            if (image.bitmap == null) {
                image.bitmap = null;
            } else if (image.bitmap.isRecycled()) {
                image.bitmap = null;
            } else {
                image.bitmap.recycle();
                if (image.bitmap.isRecycled()) {
                    image.bitmap = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
