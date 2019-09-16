package com.xiashengming.plugin;

import android.app.Activity;
import android.content.res.Resources;

public class PluginBaseActivity extends Activity {

    @Override
    public Resources getResources() {

        if (getIntent() != null && PluginManager.getInstance().isPluginIntent(getIntent())) {
            return PluginManager.getInstance().getPlugin(getIntent()).mResources;
        }
        return super.getResources();
    }

}
