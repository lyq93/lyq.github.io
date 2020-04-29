package com.sz.lyq.proxyDemo.demoClass;

/**
 * 被代理对象，实现接口
 * 动态代理基于接口实现的
 */
public class PersonHandler implements PersonOperation {

    @Override
    public void getName() {
        System.out.println("person name");
    }

    @Override
    public void getIdentify() {
        System.out.println("person identify");
    }
}
