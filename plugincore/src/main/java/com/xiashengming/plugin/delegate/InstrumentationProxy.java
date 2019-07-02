package com.xiashengming.plugin.delegate;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class InstrumentationProxy extends Instrumentation {
    protected Instrumentation mBase;

    public InstrumentationProxy(Instrumentation mBase) {
        this.mBase = mBase;
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode) {
        injectIntent(intent);
        return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode);
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
        injectIntent(intent);
        return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment target, Intent intent, int requestCode, Bundle options) {
        injectIntent(intent);
        return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, String target, Intent intent, int requestCode, Bundle options) {
        injectIntent(intent);
        return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
    }

    private void injectIntent(Intent intent) {

    }
}
