package com.sz.lyq.factoryDemo.simpleFactory;

public class DuckToy implements AnimalToy {

    @Override
    public void shape() {
        System.out.println("this is duck toy");
    }
}
