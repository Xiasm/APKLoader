package com.xiashengming.plugin.delegate;


import android.content.ComponentName;
import android.content.Intent;

import com.xiashengming.plugin.PluginManager;
import com.xiashengming.plugin.internal.Constants;
import com.xiashengming.plugin.stubactivity.ProxyStandActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ActivityManagerDelegate implements InvocationHandler {
    private Object mBase;

    public ActivityManagerDelegate(Object mBase) {
        this.mBase = mBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            Intent pluginIntent;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            pluginIntent = (Intent) args[index];

            //替换Intent
            Intent proxyIntent = new Intent();
            String packageName = PluginManager.getInstance().getHostContext().getPackageName();
            String className = ProxyStandActivity.class.getName();
            proxyIntent.setComponent(new ComponentName(packageName, className));
            proxyIntent.putExtra(Constants.PLUGIN_INTENT, pluginIntent);
            args[index] = proxyIntent;
            return method.invoke(mBase, args);
        }

        return method.invoke(mBase, args);
    }

}
