package com.xiashengming.plugindemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xiashengming.plugin.PluginManager;
import com.xiashengming.plugin.utils.CopyApkUtils;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toPluginActivity(View view) {
        Intent intent = new Intent();
        ComponentName component = new ComponentName(
                "com.xiashengming.plugin1",
                "com.xiashengming.plugin1.MainActivity");
        intent.setComponent(component);
        startActivity(intent);
    }

    public void loadPlugin(View view) {
        File apk = CopyApkUtils.copyApk2InternelFiles(this, "plugin1.apk");
        PluginManager.getInstance().loadPlugin(apk);
    }
}
