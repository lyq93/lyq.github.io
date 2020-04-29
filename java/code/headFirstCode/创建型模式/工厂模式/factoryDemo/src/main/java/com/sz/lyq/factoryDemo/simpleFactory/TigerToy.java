package com.sz.lyq.factoryDemo.simpleFactory;

public class TigerToy implements AnimalToy {
    @Override
    public void shape() {
        System.out.println("this is tiger toy");
    }
}
