package com.sz.lyq.factoryDemo.simpleFactory;

/**
 * 生产玩具的工厂
 */
public class ToyFactory {

    /**
     *第一版工厂方法
     * 存在问题：
     * 1、每增加一种类型的玩具都需要修改工厂方法
     * 2、存在硬编码
     * 考虑：
     * 是否应该把所有种类的玩具都放在这个工厂方法里面来创建
     * 如果在不同城市生产的玩具外形是不一样的，那怎么办
     * @param toyName
     * @return
     */
    public static AnimalToy createToy(String toyName){
        AnimalToy animalToy = null;
        if("duck".equals(toyName)) {
            animalToy = new DuckToy();
        } else if("tiger".equals(toyName)) {
            animalToy = new TigerToy();
        }

        return animalToy;
    }

    /**
     * 第二版
     * 通过反射的方式解决第一版存在的问题
     * 异常的处理方式不能直接外抛让调用方处理，而是方法内部消化
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object createToyByReflect(Class<? extends AnimalToy> clazz) {

        Object obj = null;
        try {
            obj = Class.forName(clazz.getName()).newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
