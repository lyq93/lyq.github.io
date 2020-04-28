package com.sz.lyq.singleDemo.demoClass;

/**
 * 饿汉式单例模式，类加载的时候实例话对象
 */
public class HungerSingleClass {
    // 不能让这个类通过构造器创建对象
    private HungerSingleClass(){};

    /**
     * 类加载的时候就创建实例
     * 特点是不管该实例有没有用到，都占用了内存空间
     */
    private static HungerSingleClass hungerSingleClass = new HungerSingleClass();

    /**
     * 获取该类对象的方法
     * @return
     */
    public static HungerSingleClass getInstance() {
        return hungerSingleClass;
    }


}
