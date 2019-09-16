package com.xiashengming.plugin.delegate;

import android.content.pm.PackageInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IPackageManagerDelegate implements InvocationHandler {
    private Object mBase;

    public IPackageManagerDelegate(Object mBase) {
        this.mBase = mBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getPackageInfo")) {
            return new PackageInfo();
        }

        return method.invoke(mBase, args);
    }
}
