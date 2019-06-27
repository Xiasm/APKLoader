package com.xiashengming.plugin.delegate;

import android.os.Handler;
import android.os.Message;

public class ActivityThreadHandlerCallback implements Handler.Callback {
    private Handler mBase;

    public ActivityThreadHandlerCallback(Handler mBase) {
        this.mBase = mBase;
    }

    @Override
    public boolean handleMessage(Message msg) {
        mBase.handleMessage(msg);
        return true;
    }
}
