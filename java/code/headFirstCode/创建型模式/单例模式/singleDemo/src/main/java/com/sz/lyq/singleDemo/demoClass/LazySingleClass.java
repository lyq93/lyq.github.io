package com.sz.lyq.singleDemo.demoClass;

/**
 * 懒汉式单例模式，在需要使用的时候才实例化对象
 */
public class LazySingleClass {

    private LazySingleClass(){};

    private static LazySingleClass lazySingleClass = null;

    /**
     * 第一个版本
     * 这是非线程安全的，当多个线程同时访问getInstance方法时
     * 会有线程安全的问题
     * @return
     */
    public static LazySingleClass getInstance() {
        if(lazySingleClass == null) {
            lazySingleClass = new LazySingleClass();
        }

        return lazySingleClass;
    }

    /**
     * 第二个版本
     * 对该方法进行加锁，但需要考虑的一个问题是
     * 锁的范围需要这么大吗？其实只需要针对创建对象这块进行加锁就可以了
     * 如果说对象已经创建了，就不应该还进到锁里
     * @return
     */
    public synchronized static LazySingleClass getInstanceSync() {
        if(lazySingleClass == null) {
            lazySingleClass = new LazySingleClass();
        }

        return lazySingleClass;
    }

    /**
     * 第三个版本
     * 只对创建对象部分进行加锁，缩小锁的范围
     * @return
     */
    public static LazySingleClass getInstanceSyncTwo() {
        // 对象如果创建了，就不会进入同步锁
        if(lazySingleClass == null) {
            // 如果对象没创建就锁创建对象部分，这样就达到了缩小锁的范围的目的
            synchronized (LazySingleClass.class) {
                // 在同步块内再次判断对象是否创建
                if(lazySingleClass == null) {
                    lazySingleClass = new LazySingleClass();
                }
            }
        }

        return lazySingleClass;
    }
}
