package com.xiashengming.plugin.delegate;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ActivityManagerDelegate implements InvocationHandler {

    private Object mBase;

    public ActivityManagerDelegate(Object mBase) {
        this.mBase = mBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return method.invoke(mBase, args);
    }
}
