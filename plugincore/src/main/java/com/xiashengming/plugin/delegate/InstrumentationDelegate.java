package com.xiashengming.plugin.delegate;


import android.app.Instrumentation;

public class InstrumentationDelegate extends Instrumentation {
    protected Instrumentation mBase;

    public InstrumentationDelegate(Instrumentation mBase) {
        this.mBase = mBase;
    }


}
