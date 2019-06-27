package com.xiashengming.plugin;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.xiashengming.plugin.delegate.ActivityManagerDelegate;
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
        hookActivityManager();

        hookActivityThreadHandler();
    }

    private void hookActivityManager() {
        try {
            //获取ActivityManagerNative中的gDefault单例
            Object gDefault = RefInvoke.getStaticFieldObject(
                    "android.app.ActivityManagerNative", "gDefault");

            //gDefault是一个android.util.Singleton 对象，内部有mInstance字段，
            // 即AMS返回多来的Binder转换为ActivityManagerProxy对象，继承自IActivityManager
            Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");

            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");

            //动态代理 IActivityManager
            Object proxymInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityManagerClass},
                    new ActivityManagerDelegate(mInstance));
            RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxymInstance);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new PluginException("hook IActivityManager 失败！", e.getException());
        }
    }

    private void hookActivityThreadHandler() {
        //获取到当前进程的 ActivityThread 对象
        Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        //获取到 ActivityThread 内部的 mH 对象
        Handler mH = (Handler) RefInvoke.getFieldObject("android.app.ActivityThread", currentActivityThread, "mH");

        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new ActivityThreadHandlerCallback(mH));
    }


}
