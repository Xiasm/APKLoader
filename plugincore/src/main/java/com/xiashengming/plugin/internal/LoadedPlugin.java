package com.xiashengming.plugin.internal;

import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.os.Build;

import com.xiashengming.plugin.utils.RefInvoke;

import java.io.File;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class LoadedPlugin {
    public PackageParser.Package mPackage;
    public ClassLoader mClassLoader;
    public Resources mResources;
    public Object mLoadedApk;

    public LoadedPlugin(Context context, File apk) throws Exception {
        mPackage = createPackage(context, apk);

        mClassLoader = createClassLoader(context, apk, context.getClassLoader());

        mResources = createResources(context, apk);

        mLoadedApk = createLoadedApk(context, apk);
    }

    private Object createLoadedApk(Context context, File apk) {
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();

        //准备参数
        Object argusCompatibilityInfo = RefInvoke.getStaticFieldObject("android.content.res.CompatibilityInfo", "DEFAULT_COMPATIBILITY_INFO");
        ApplicationInfo argusApplicationInfo = mPackage.applicationInfo;

        Class[] params = new Class[]{ApplicationInfo.class, CompatibilityInfo.class};
        Object[] values = new Object[]{argusApplicationInfo, argusCompatibilityInfo};

        return RefInvoke.invokeInstanceMethod(currentActivityThread,
                "getPackageInfoNoCheck", params, values);
    }

    protected PackageParser.Package createPackage(Context context, File apk) {
        return PackageParseCompat.parsePackage(context, apk, PackageParser.PARSE_MUST_BE_APK);
    }

    protected Resources createResources(Context context, File apk) throws Exception {
        //插件使用独立的Resources，不与宿主有关系
        Resources hostResources = context.getResources();
        AssetManager assetManager = createAssetManager(context, apk);
        return new Resources(assetManager, hostResources.getDisplayMetrics(), hostResources.getConfiguration());

    }

    protected AssetManager createAssetManager(Context context, File apk) throws Exception {
        AssetManager am = AssetManager.class.newInstance();
        RefInvoke.invokeInstanceMethod(am, "addAssetPath", String.class, apk.getAbsolutePath());
        return am;
    }

    private ClassLoader createClassLoader(Context context, File apk, ClassLoader parent) {
        File dexFile = context.getDir("dex", Context.MODE_PRIVATE);
        String dexPath = dexFile.getAbsolutePath();
        return new DexClassLoader(apk.getAbsolutePath(), dexPath, null, parent);
    }

    public PluginContext createPluginContext(Context context) {
        if (context == null) {
            return new PluginContext(this);
        }

        return new PluginContext(this, context);
    }
}
