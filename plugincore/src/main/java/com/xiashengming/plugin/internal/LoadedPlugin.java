package com.xiashengming.plugin.internal;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;

public class LoadedPlugin {

    protected ClassLoader mClassLoader;

    public LoadedPlugin(Context context, File apk) {
        mClassLoader = createClassLoader(context, apk, context.getClassLoader());

    }

    private ClassLoader createClassLoader(Context context, File apk, ClassLoader parent) {
        File dexFile = context.getDir("dex", Context.MODE_PRIVATE);
        String dexPath = dexFile.getAbsolutePath();
        return new DexClassLoader(apk.getAbsolutePath(), dexPath, null, parent);
    }

}
