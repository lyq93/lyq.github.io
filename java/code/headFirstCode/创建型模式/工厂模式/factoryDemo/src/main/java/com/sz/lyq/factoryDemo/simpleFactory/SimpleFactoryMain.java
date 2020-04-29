package com.sz.lyq.factoryDemo.simpleFactory;

public class SimpleFactoryMain {

    public static void main(String[] args) {
        // 第一版工厂测试
        DuckToy duck = (DuckToy)ToyFactory.createToy("duck");
        duck.shape();

        // 第二版工厂测试
        DuckToy duckToy = (DuckToy)ToyFactory.createToyByReflect(com.sz.lyq.factoryDemo.simpleFactory.DuckToy.class);
        duckToy.shape();
    }
}
