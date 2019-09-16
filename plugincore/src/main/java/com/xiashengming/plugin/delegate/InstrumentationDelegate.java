package com.xiashengming.plugin.delegate;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.xiashengming.plugin.PluginManager;
import com.xiashengming.plugin.internal.LoadedPlugin;
import com.xiashengming.plugin.utils.RefInvoke;
import com.xiashengming.plugin.utils.Reflector;

public class InstrumentationDelegate extends Instrumentation {
    protected Instrumentation mBase;

    public InstrumentationDelegate(Instrumentation mBase) {
        this.mBase = mBase;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        setResourcesForPluginActivity(activity);
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        setResourcesForPluginActivity(activity);
        super.callActivityOnCreate(activity, icicle, persistentState);
    }

    private void setResourcesForPluginActivity(Activity activity) {
        if (PluginManager.getInstance().isPluginIntent(activity.getIntent())) {
            Context base = activity.getBaseContext();
            try {
                LoadedPlugin plugin = PluginManager.getInstance().getPlugin(activity.getIntent());
                RefInvoke.setFieldObject(base, "mResources", plugin.mResources);
//                Reflector.with(base).field("mResources").set(plugin.getResources());
                Reflector reflector = Reflector.with(activity);
                reflector.field("mBase").set(plugin.createPluginContext(activity.getBaseContext()));
//                reflector.field("mApplication").set(plugin.getApplication());
//
//                // set screenOrientation
//                ActivityInfo activityInfo = plugin.getActivityInfo(PluginUtil.getComponent(intent));
//                if (activityInfo.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
//                    activity.setRequestedOrientation(activityInfo.screenOrientation);
//                }
//
//                // for native activity
//                ComponentName component = PluginUtil.getComponent(intent);
//                Intent wrapperIntent = new Intent(intent);
//                wrapperIntent.setClassName(component.getPackageName(), component.getClassName());
//                wrapperIntent.setExtrasClassLoader(activity.getClassLoader());
//                activity.setIntent(wrapperIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
