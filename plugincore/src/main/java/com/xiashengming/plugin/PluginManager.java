package com.xiashengming.plugin;

import android.app.ActivityThread;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;

import com.xiashengming.plugin.delegate.ActivityManagerProxy;
import com.xiashengming.plugin.delegate.ActivityThreadHandlerCallback;
import com.xiashengming.plugin.delegate.InstrumentationProxy;
import com.xiashengming.plugin.utils.RefInvoke;

import java.lang.reflect.Proxy;

/**
 * 加载插件步骤：
 * 一：将插件从server下载下来，拷贝到宿主目录下
 * 二：收集插件信息，为插件创建ClassLoader
 * 三：针对Activity，hook Instrumentation、AMS等实现启动未注册Activity
 * 四：解决Activity的lanuchMode
 *
 */
public class PluginManager {
    private static PluginManager sInstance;
    private final Application mApplication;
    private final Context mContext;
    private InstrumentationProxy mInstrumentation;


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
        hookActivityManagerNative();
        hookInstrumentation();
        hookActivityThreadHandler();
    }

    private void hookActivityManagerNative() {
        try {
            //获取ActivityManagerNative中的gDefault单例
            Object gDefault = RefInvoke.getStaticFieldObject(
                    "android.app.ActivityManagerNative", "gDefault");

            //gDefault是一个android.util.Singleton 对象，内部有mInstance字段，
            // 即AMS返回多来的Binder转换为ActivityManagerProxy对象，继承自IActivityManager
            Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");

            //动态代理找个对象
            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");

            Object proxymInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityManagerClass},
                    new ActivityManagerProxy(mInstance));

            //gDefault的mInstance字段修改为动态代理对象 proxymInstance
            RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxymInstance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void hookInstrumentation() {
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        Instrumentation instrumentation = activityThread.getInstrumentation();
        InstrumentationProxy proxy = new InstrumentationProxy(instrumentation);
        RefInvoke.setFieldObject(activityThread, "mInstrumentation", proxy);
        mInstrumentation = proxy;
    }

    private void hookActivityThreadHandler() {
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        //给mH设置Callback
        Handler mH = (Handler) RefInvoke.getFieldObject(activityThread, "mH");
        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new ActivityThreadHandlerCallback(mH));

    }


}
