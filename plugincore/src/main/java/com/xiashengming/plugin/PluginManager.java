package com.xiashengming.plugin;

import android.app.ActivityThread;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.xiashengming.plugin.delegate.ActivityManagerDelegate;
import com.xiashengming.plugin.delegate.ActivityThreadHandlerDelegate;
import com.xiashengming.plugin.delegate.IPackageManagerDelegate;
import com.xiashengming.plugin.delegate.InstrumentationDelegate;
import com.xiashengming.plugin.internal.Constants;
import com.xiashengming.plugin.internal.LoadedPlugin;
import com.xiashengming.plugin.utils.RefInvoke;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Application mApplication;
    private Context mContext;
    private List<LoadedPlugin> mPlugins = new ArrayList<>();

    public static PluginManager getInstance() {
        if (sInstance == null) {
            synchronized (PluginManager.class) {
                if (sInstance == null) {
                    sInstance = new PluginManager();
                }
            }
        }

        return sInstance;
    }

    private PluginManager() {}

    public Context getHostContext() {
        return mContext;
    }

    public void init(Context context) {
        if (context instanceof Application) {
            this.mApplication = (Application) context;
            this.mContext = mApplication.getBaseContext();
        } else {
            final Context app = context.getApplicationContext();
            this.mApplication = (Application) app;
            this.mContext = mApplication.getBaseContext();
        }

        hookActivityManagerNative();
        hookActivityThreadHandler();
        hookInstrumentation();
        hookPackageManager();
    }

    public boolean isPluginIntent(Intent intent) {
        if (intent == null) {
            return false;
        }
        Intent pluginIntent = intent.getParcelableExtra(Constants.PLUGIN_INTENT);
        return pluginIntent != null;
    }

    public LoadedPlugin getPlugin(Intent intent) {
        return mPlugins.get(0);
    }

    public void loadPlugin(File apk) {
        try {
            //根据apk地址新建一个插件
            LoadedPlugin plugin = new LoadedPlugin(mContext, apk);

            ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
            //获取到 ActivityThread 的 mPackages 对象
            Map mPackages = (Map) RefInvoke.getFieldObject(currentActivityThread, "mPackages");
            //将插件的LoadedApk对象缓存进 mPackages
            WeakReference<Object> weakReference = new WeakReference<>(plugin.mLoadedApk);
            mPackages.put(plugin.mPackage.applicationInfo.packageName, weakReference);

            //为插件的LoadedApk创建一个新的ClassLoader
            RefInvoke.setFieldObject(plugin.mLoadedApk, "mClassLoader", plugin.mClassLoader);

            mPlugins.add(plugin);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    new ActivityManagerDelegate(mInstance));

            //gDefault的mInstance字段修改为动态代理对象 proxymInstance
            RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxymInstance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void hookActivityThreadHandler() {
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        //给mH设置Callback
        Handler mH = (Handler) RefInvoke.getFieldObject(activityThread, "mH");
        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new ActivityThreadHandlerDelegate(mH));

    }

    private void hookInstrumentation() {
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        Instrumentation instrumentation = activityThread.getInstrumentation();

        InstrumentationDelegate delegate = new InstrumentationDelegate(instrumentation);
        RefInvoke.setFieldObject(activityThread, "mInstrumentation", delegate);
    }

    private void hookPackageManager() {
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        Object sPackageManager = RefInvoke.getFieldObject(activityThread, "sPackageManager");
        try {
            Class<?> ipackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Proxy.newProxyInstance(ipackageManagerInterface.getClassLoader(),
                    new Class[]{ipackageManagerInterface},
                    new IPackageManagerDelegate(sPackageManager));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
