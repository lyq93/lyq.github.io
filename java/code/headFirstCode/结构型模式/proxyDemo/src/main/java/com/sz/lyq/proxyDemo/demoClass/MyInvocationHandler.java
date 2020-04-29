package com.sz.lyq.proxyDemo.demoClass;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 实现java API InvocationHandler
 * 代理对象的每一次想法调用都会到invoke方法里面来
 */
public class MyInvocationHandler implements InvocationHandler {
    // 被代理对象
    private PersonHandler personHandler;
    // 构造器注入
    public MyInvocationHandler(PersonHandler personHandler) {
        this.personHandler = personHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(personHandler,args);
    }
}
