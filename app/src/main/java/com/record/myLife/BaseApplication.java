package com.record.myLife;

import android.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.record.controller.ControllerManager;
import com.record.utils.preference.Config;
import com.record.utils.preference.PreferenceConfig;

public class BaseApplication extends Application {
    private static BaseApplication instance;   //单例
    private static ControllerManager netControler;
    private static Config preferenceConfig;

    public static BaseApplication getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        initImageLoader();
    }

    private void initImageLoader() {
        try {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ControllerManager getControllerManager() {
        if (netControler == null) {
            netControler = new ControllerManager();
        }
        return netControler;
    }

    public Config getPreferenceConfig() {
        if (preferenceConfig == null) {
            preferenceConfig = PreferenceConfig.getPreferenceConfig(this);
            preferenceConfig.loadConfig();
        }
        return preferenceConfig;
    }
}
