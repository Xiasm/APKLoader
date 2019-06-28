package com.xiashengming.plugin;

import android.app.ActivityThread;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;

import com.xiashengming.plugin.delegate.InstrumentationDelegate;
import com.xiashengming.plugin.delegate.ActivityThreadHandlerCallback;
import com.xiashengming.plugin.utils.RefInvoke;

import java.lang.reflect.Proxy;

public class PluginManager {
    private static PluginManager sInstance;
    private final Application mApplication;
    private final Context mContext;

    public static PluginManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PluginManager.class) {
                if (sInstance == null) {
                    sInstance = new PluginManager(context);
                }
            }
        }

        return sInstance;
    }

    private PluginManager(Context context) {
        if (context instanceof Application) {
            this.mApplication = (Application) context;
            this.mContext = mApplication.getBaseContext();
        } else {
            final Context app = context.getApplicationContext();
            this.mApplication = (Application) app;
            this.mContext = mApplication.getBaseContext();
        }
    }

    public void init() {
        hookInstrumentation();

        hookActivityThreadHandler();
    }

    private void hookInstrumentation() {
        //1.运行时拿到Instrumentation
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        Instrumentation instrumentation = activityThread.getInstrumentation();
        //2.设置代理
        InstrumentationDelegate delegate = new InstrumentationDelegate(instrumentation);
        RefInvoke.setFieldObject(activityThread, "mInstrumentation", delegate);
    }

    private void hookActivityThreadHandler() {

    }


}
