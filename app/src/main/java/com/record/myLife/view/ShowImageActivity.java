package com.record.myLife.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DensityUtil;
import com.record.utils.Val;
import com.umeng.analytics.MobclickAgent;

public class ShowImageActivity extends BaseActivity {
    int bottomCentre = 1;
    int bottomRight = 2;
    Button btn_show_image_finish;
    CheckBox cb_no_show_check;
    Context context;
    ImageView iv_show_image_pic;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.btn_show_image_finish) {
                if (!(ShowImageActivity.this.cb_no_show_check.isChecked() || ShowImageActivity.this.preferenceName == null)) {
                    ShowImageActivity.this.context.getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(ShowImageActivity.this.preferenceName, 0).commit();
                }
                ShowImageActivity.this.finish();
            }
        }
    };
    String preferenceName;
    RelativeLayout rl_show_i_know_it;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        this.context = this;
        this.iv_show_image_pic = (ImageView) findViewById(R.id.iv_show_image_pic);
        this.btn_show_image_finish = (Button) findViewById(R.id.btn_show_image_finish);
        this.cb_no_show_check = (CheckBox) findViewById(R.id.cb_no_show_check);
        this.rl_show_i_know_it = (RelativeLayout) findViewById(R.id.rl_show_i_know_it);
        this.btn_show_image_finish.setOnClickListener(this.myClickListener);
        this.cb_no_show_check.setOnClickListener(this.myClickListener);
        SystemBarTintManager.setMIUIbar(this);
        this.cb_no_show_check.setChecked(true);
        Intent it = getIntent();
        int image = getIntent().getIntExtra("image", 0);
        if (image == 0) {
            finish();
            return;
        }
        this.preferenceName = it.getStringExtra("preferenceName");
        setImageFromLocal(this.context, "drawable://" + image, this.iv_show_image_pic);
        int knowPosition = it.getIntExtra("position", 0);
        if (knowPosition == this.bottomRight) {
            LayoutParams params = new LayoutParams(-2, -2);
            params.addRule(12);
            params.addRule(11);
            params.bottomMargin = DensityUtil.dip2px(this.context, 30.0f);
            params.rightMargin = DensityUtil.dip2px(this.context, 10.0f);
            this.rl_show_i_know_it.setLayoutParams(params);
            return;
        }
        if (knowPosition == this.bottomRight) {
        }
    }

    public static void setImageFromLocal(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl != null) {
            try {
                if (imageUrl.trim().length() != 0) {
                    ImageLoader.getInstance().displayImage(imageUrl, imageView, new Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Config.RGB_565).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap readBitMap(Context context, int resId) {
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeStream(context.getResources().openRawResource(resId), null, opt);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public static void startActivity(Context context, String configureName, int value, int image) {
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

    public static void startActivity(Context context, String configureName, int value, int image, int position) {
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
}
