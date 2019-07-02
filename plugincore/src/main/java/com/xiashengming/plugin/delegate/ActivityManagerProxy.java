package com.xiashengming.plugin.delegate;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ActivityManagerProxy implements InvocationHandler {
    private Object mBase;

    public ActivityManagerProxy(Object mBase) {
        this.mBase = mBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(mBase, args);
    }
}
