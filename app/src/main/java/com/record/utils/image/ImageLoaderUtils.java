package com.record.utils.image;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.record.myLife.R;

public class ImageLoaderUtils {
    public static void setImage(Context context, String imageUrl, ImageView imageView, int loadingImg) {
        if (imageUrl != null) {
            try {
                if (imageUrl.trim().length() != 0) {
                    ImageLoader.getInstance().displayImage(imageUrl, imageView, new Builder().showImageOnLoading(loadingImg).showImageOnFail(loadingImg).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Config.RGB_565).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setImageFromLocal(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl != null) {
            try {
                if (imageUrl.trim().length() != 0) {
                    ImageLoader.getInstance().displayImage(imageUrl, imageView, new Builder().showImageOnLoading((int) R.drawable.ic_loading).showImageOnFail((int) R.drawable.ic_loading_fail).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Config.RGB_565).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
