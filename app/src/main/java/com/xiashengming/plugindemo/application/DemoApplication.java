package com.xiashengming.plugindemo.application;

import android.app.Application;
import android.content.Context;

import com.xiashengming.plugin.PluginManager;

public class DemoApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance(this).init();
    }
}
