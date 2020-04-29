package com.sz.lyq.proxyDemo.demoClass;

import java.lang.reflect.Proxy;

public class ProxyMain {
    public static void main(String[] args) {
        // 产生代理对象
        PersonOperation proxyInstance = (PersonOperation)Proxy.newProxyInstance(
                PersonHandler.class.getClassLoader(),
                PersonHandler.class.getInterfaces(), // 动态代理是基于接口的
                new MyInvocationHandler(new PersonHandler()));

        // 方法调用，进入到实现invocationHandler接口的invoke方法
        proxyInstance.getName();
    }

}
