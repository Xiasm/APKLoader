package com.xiashengming.plugin.delegate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.xiashengming.plugin.internal.Constants;
import com.xiashengming.plugin.utils.RefInvoke;

public class ActivityThreadHandlerDelegate implements Handler.Callback {
    private Handler mBase;

    public ActivityThreadHandlerDelegate(Handler mBase) {
        this.mBase = mBase;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                handleLaunchActivity(msg);
                break;
        }
        mBase.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        Object obj = msg.obj;
        //把代理Intent替换成真实的Intent
        Intent proxyIntent = (Intent) RefInvoke.getFieldObject(obj, "intent");
        Intent pluginIntent = proxyIntent.getParcelableExtra(Constants.PLUGIN_INTENT);
        if (pluginIntent != null) {
            proxyIntent.setComponent(pluginIntent.getComponent());
            ActivityInfo activityInfo = (ActivityInfo) RefInvoke.getFieldObject(obj, "activityInfo");
            activityInfo.applicationInfo.packageName =
                    pluginIntent.getPackage() == null ? pluginIntent.getComponent().getPackageName() : pluginIntent.getPackage();
        }

    }
}
