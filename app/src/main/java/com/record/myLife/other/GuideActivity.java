package com.record.myLife.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.Val;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    int[] IvResource = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3, R.drawable.guide_4, R.drawable.guide_5, R.drawable.guide_6};
    TextView about_tv_info4;
    TextView about_tv_info5;
    TextView about_tv_info6;
    Button btn_support_back;
    Context context;
    private ViewPager help_viewPager;

    public class MyPagerAdapter extends PagerAdapter {
        Button btn_start_use;
        private List<View> mListViews;
        OnClickListener myClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.btn_start_use) {
                    GuideActivity.this.finish();
                }
            }
        };

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View) this.mListViews.get(position), 0);
            if (position == this.mListViews.size() - 1) {
                this.btn_start_use = (Button) container.findViewById(R.id.btn_start_use);
                this.btn_start_use.setOnClickListener(this.myClickListener);
            }
            return this.mListViews.get(position);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        public int getCount() {
            return this.mListViews.size();
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) this.mListViews.get(position));
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        getSharedPreferences(Val.CONFIGURE_NAME, 0).edit().putInt(Val.CONFIGURE_IS_SHOW_GUIDE, 4).commit();
        ArrayList<View> list = new ArrayList();
        for (int readBitMap : this.IvResource) {
            LayoutParams params = new LayoutParams();
            params.width = -1;
            params.height = -1;
            ImageView iv = new ImageView(this.context);
            iv.setImageBitmap(readBitMap(this.context, readBitMap));
            iv.setLayoutParams(params);
            list.add(iv);
        }
        list.add((RelativeLayout) getLayoutInflater().inflate(R.layout.activity_guide_pager, null));
        MyPagerAdapter adapter = new MyPagerAdapter(list);
        this.help_viewPager = (ViewPager) findViewById(R.id.help_viewPager);
        this.help_viewPager.setAdapter(adapter);
        setResult(16);
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

    public void onBackPressed() {
    }
}
