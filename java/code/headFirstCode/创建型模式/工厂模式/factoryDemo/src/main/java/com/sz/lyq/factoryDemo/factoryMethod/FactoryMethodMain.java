package com.sz.lyq.factoryDemo.factoryMethod;

public class FactoryMethodMain {

    public static void main(String[] args) {
        ToyFactory toyFactory = new DuckFactory();
        DuckBySHToy duckBySHToy = (DuckBySHToy)toyFactory.createToy("shanghai");
        duckBySHToy.shape();
    }
}
