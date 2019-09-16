package com.xiashengming.plugindemo.application;

import android.app.Application;
import android.content.Context;

import com.xiashengming.plugin.PluginManager;
import com.xiashengming.plugin.utils.CopyApkUtils;

import java.io.File;

public class DemoApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance().init(this);
    }
}
