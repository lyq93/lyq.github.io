package com.sz.lyq.factoryDemo.factoryMethod;

/**
 * 生产鸭子玩具的工厂
 */
public class DuckFactory extends ToyFactory {
    @Override
    public AnimalToy createToy(String cityName) {
        AnimalToy animalToy = null;
        if("shanghai".equals(cityName)) {
            animalToy = new DuckBySHToy();
        } else if("shenzhen".equals(cityName)) {
            animalToy = new DuckBySZToy();
        }

        return animalToy;
    }
}
