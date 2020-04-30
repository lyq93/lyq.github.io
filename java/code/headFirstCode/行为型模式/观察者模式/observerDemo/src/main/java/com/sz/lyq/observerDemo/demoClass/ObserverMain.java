package com.sz.lyq.observerDemo.demoClass;

public class ObserverMain {

    public static void main(String[] args) {
        SubjectImpl subject = new SubjectImpl();

        // 通过构造器默认把类注册成为观察者
        ObserverImpl observer = new ObserverImpl(subject);

        // 模拟数据发生改动
        subject.setData(1,1.6);
    }
}
