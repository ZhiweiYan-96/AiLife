package com.record.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.record.myLife.R;
import java.lang.reflect.Method;

public class SystemBarTintManager {
    public static final int DEFAULT_TINT_COLOR = -1728053248;
    private static boolean sIsMiui = true;
    private static boolean sIsMiuiV6;
    private final SystemBarConfig mConfig;
    private boolean mNavBarAvailable;
    private boolean mNavBarTintEnabled;
    private View mNavBarTintView;
    private boolean mStatusBarAvailable;
    private boolean mStatusBarTintEnabled;
    private View mStatusBarTintView;

    public static class SystemBarConfig {
        private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
        private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
        private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
        private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
        private final int mActionBarHeight;
        private final boolean mHasNavigationBar;
        private final boolean mInPortrait;
        private final int mNavigationBarHeight;
        private final int mNavigationBarWidth;
        private final float mSmallestWidthDp;
        private final int mStatusBarHeight;
        private final boolean mTranslucentNavBar;
        private final boolean mTranslucentStatusBar;

        private SystemBarConfig(Activity activity, boolean translucentStatusBar, boolean traslucentNavBar) {
            boolean z = true;
            Resources res = activity.getResources();
            this.mInPortrait = res.getConfiguration().orientation == 1;
            this.mSmallestWidthDp = getSmallestWidthDp(activity);
            this.mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
            this.mActionBarHeight = getActionBarHeight(activity);
            this.mNavigationBarHeight = getNavigationBarHeight(activity);
            this.mNavigationBarWidth = getNavigationBarWidth(activity);
            if (this.mNavigationBarHeight <= 0) {
                z = false;
            }
            this.mHasNavigationBar = z;
            this.mTranslucentStatusBar = translucentStatusBar;
            this.mTranslucentNavBar = traslucentNavBar;
        }

        @TargetApi(14)
        private int getActionBarHeight(Context context) {
            if (VERSION.SDK_INT < 14) {
                return 0;
            }
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(16843499, tv, true);
            return context.getResources().getDimensionPixelSize(tv.resourceId);
        }

        @TargetApi(14)
        private int getNavigationBarHeight(Context context) {
            Resources res = context.getResources();
            if (VERSION.SDK_INT < 14 || ViewConfiguration.get(context).hasPermanentMenuKey()) {
                return 0;
            }
            String key;
            if (this.mInPortrait) {
                key = NAV_BAR_HEIGHT_RES_NAME;
            } else {
                key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
            }
            return getInternalDimensionSize(res, key);
        }

        @TargetApi(14)
        private int getNavigationBarWidth(Context context) {
            Resources res = context.getResources();
            if (VERSION.SDK_INT < 14 || ViewConfiguration.get(context).hasPermanentMenuKey()) {
                return 0;
            }
            return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
        }

        private int getInternalDimensionSize(Resources res, String key) {
            int resourceId = res.getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                return res.getDimensionPixelSize(resourceId);
            }
            return 0;
        }

        @SuppressLint({"NewApi"})
        private float getSmallestWidthDp(Activity activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            if (VERSION.SDK_INT >= 16) {
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            } else {
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }
            return Math.min(((float) metrics.widthPixels) / metrics.density, ((float) metrics.heightPixels) / metrics.density);
        }

        public boolean isNavigationAtBottom() {
            return this.mSmallestWidthDp >= 600.0f || this.mInPortrait;
        }

        public int getStatusBarHeight() {
            return this.mStatusBarHeight;
        }

        public int getActionBarHeight() {
            return this.mActionBarHeight;
        }

        public boolean hasNavigtionBar() {
            return this.mHasNavigationBar;
        }

        public int getNavigationBarHeight() {
            return this.mNavigationBarHeight;
        }

        public int getNavigationBarWidth() {
            return this.mNavigationBarWidth;
        }

        public int getPixelInsetTop(boolean withActionBar) {
            int i = 0;
            int i2 = this.mTranslucentStatusBar ? this.mStatusBarHeight : 0;
            if (withActionBar) {
                i = this.mActionBarHeight;
            }
            return i + i2;
        }

        public int getPixelInsetBottom() {
            if (this.mTranslucentNavBar && isNavigationAtBottom()) {
                return this.mNavigationBarHeight;
            }
            return 0;
        }

        public int getPixelInsetRight() {
            if (!this.mTranslucentNavBar || isNavigationAtBottom()) {
                return 0;
            }
            return this.mNavigationBarWidth;
        }
    }

    static {
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getStringMethod = sysClass.getDeclaredMethod("get", new Class[]{String.class});
            try {
                sIsMiuiV6 = "V6".equals((String) getStringMethod.invoke(sysClass, new Object[]{"ro.miui.ui.version.name"}));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String version = (String) getStringMethod.invoke(sysClass, new Object[]{"ro.miui.ui.version.name"});
            if (version != null && version.length() > 0) {
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @TargetApi(19)
    public SystemBarTintManager(Activity activity) {
        Window win = activity.getWindow();
        ViewGroup decorViewGroup = (ViewGroup) win.getDecorView();
        if (VERSION.SDK_INT >= 19) {
            TypedArray a = activity.obtainStyledAttributes(new int[]{16843759, 16843760});
            try {
                this.mStatusBarAvailable = a.getBoolean(0, false);
                this.mNavBarAvailable = a.getBoolean(1, false);
                LayoutParams winParams = win.getAttributes();
                if ((winParams.flags & 67108864) != 0) {
                    this.mStatusBarAvailable = true;
                }
                if ((winParams.flags & 134217728) != 0) {
                    this.mNavBarAvailable = true;
                }
            } finally {
                a.recycle();
            }
        }
        this.mConfig = new SystemBarConfig(activity, this.mStatusBarAvailable, this.mNavBarAvailable);
        if (!this.mConfig.hasNavigtionBar()) {
            this.mNavBarAvailable = false;
        }
        if (this.mStatusBarAvailable) {
            setupStatusBarView(activity, decorViewGroup);
        }
        if (this.mNavBarAvailable) {
            setupNavBarView(activity, decorViewGroup);
        }
    }

    public void setStatusBarTintEnabled(boolean enabled) {
        this.mStatusBarTintEnabled = enabled;
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setStatusBarDarkMode(boolean darkmode, Activity activity) {
        int i = 0;
        if (sIsMiui) {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
//                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
//                int darkModeFlag = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(layoutParams);
//                Method extraFlagField = clazz.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
//                Window window = activity.getWindow();
//                Object[] objArr = new Object[2];
//                if (darkmode) {
//                    i = darkModeFlag;
//                }
//                objArr[0] = Integer.valueOf(i);
//                objArr[1] = Integer.valueOf(darkModeFlag);
//                extraFlagField.invoke(window, objArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setNavigationBarTintEnabled(boolean enabled) {
        this.mNavBarTintEnabled = enabled;
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setTintColor(int color) {
        setStatusBarTintColor(color);
        setNavigationBarTintColor(color);
    }

    public void setTintResource(int res) {
        setStatusBarTintResource(res);
        setNavigationBarTintResource(res);
    }

    public void setTintDrawable(Drawable drawable) {
        setStatusBarTintDrawable(drawable);
        setNavigationBarTintDrawable(drawable);
    }

    public void setTintAlpha(float alpha) {
        setStatusBarAlpha(alpha);
        setNavigationBarAlpha(alpha);
    }

    public void setStatusBarTintColor(int color) {
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setBackgroundColor(color);
        }
    }

    public void setStatusBarTintResource(int res) {
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setBackgroundResource(res);
        }
    }

    public void setStatusBarTintDrawable(Drawable drawable) {
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setBackgroundDrawable(drawable);
        }
    }

    @TargetApi(11)
    public void setStatusBarAlpha(float alpha) {
        if (this.mStatusBarAvailable && VERSION.SDK_INT >= 11) {
            this.mStatusBarTintView.setAlpha(alpha);
        }
    }

    public void setNavigationBarTintColor(int color) {
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setBackgroundColor(color);
        }
    }

    public void setNavigationBarTintResource(int res) {
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setBackgroundResource(res);
        }
    }

    public void setNavigationBarTintDrawable(Drawable drawable) {
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setBackgroundDrawable(drawable);
        }
    }

    @TargetApi(11)
    public void setNavigationBarAlpha(float alpha) {
        if (this.mNavBarAvailable && VERSION.SDK_INT >= 11) {
            this.mNavBarTintView.setAlpha(alpha);
        }
    }

    public SystemBarConfig getConfig() {
        return this.mConfig;
    }

    public boolean isStatusBarTintEnabled() {
        return this.mStatusBarTintEnabled;
    }

    public boolean isNavBarTintEnabled() {
        return this.mNavBarTintEnabled;
    }

    private void setupStatusBarView(Context context, ViewGroup decorViewGroup) {
        this.mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, this.mConfig.getStatusBarHeight());
        params.gravity = 48;
        if (this.mNavBarAvailable && !this.mConfig.isNavigationAtBottom()) {
            params.rightMargin = this.mConfig.getNavigationBarWidth();
        }
        this.mStatusBarTintView.setLayoutParams(params);
        this.mStatusBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
        this.mStatusBarTintView.setVisibility(View.GONE);
        decorViewGroup.addView(this.mStatusBarTintView);
    }

    private void setupNavBarView(Context context, ViewGroup decorViewGroup) {
        FrameLayout.LayoutParams params;
        this.mNavBarTintView = new View(context);
        if (this.mConfig.isNavigationAtBottom()) {
            params = new FrameLayout.LayoutParams(-1, this.mConfig.getNavigationBarHeight());
            params.gravity = 80;
        } else {
            params = new FrameLayout.LayoutParams(this.mConfig.getNavigationBarWidth(), -1);
            params.gravity = 5;
        }
        this.mNavBarTintView.setLayoutParams(params);
        this.mNavBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
        this.mNavBarTintView.setVisibility(View.GONE);
        decorViewGroup.addView(this.mNavBarTintView);
    }

    public static void setMIUIbar(Activity activity) {
        try {
            if (VERSION.SDK_INT > 19) {
                setTranslucentStatus(activity.getWindow(), true);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.gray);
                tintManager.setStatusBarDarkMode(true, activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Window win, boolean on) {
        LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= 67108864;
        } else {
            winParams.flags &= -67108865;
        }
        win.setAttributes(winParams);
    }
}
