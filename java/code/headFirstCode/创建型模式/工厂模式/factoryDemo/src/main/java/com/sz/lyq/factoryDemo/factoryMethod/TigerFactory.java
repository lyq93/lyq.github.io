package com.sz.lyq.factoryDemo.factoryMethod;

/**
 * 生产老虎玩具的工厂
 */
public class TigerFactory extends ToyFactory {
    @Override
    public Object createToy(String cityName) {
        AnimalToy animalToy = null;
        if("shanghai".equals(cityName)) {
            animalToy = new TigerBySHToy();
        } else if("shenzhen".equals(cityName)) {
            animalToy = new TigerBySZToy();
        }

        return animalToy;
    }
}
