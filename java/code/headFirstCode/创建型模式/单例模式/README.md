[toc]

# 单例模式

## 饿汉式单例模式

### 定义

> 所谓饿汉式，即是在类加载的时候就实例化好对象，不管后续会不会用到。相当于是以空间换时间。

### 代码

```java
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
```

> 饿汉式单例模式是线程安全的

## 懒汉式单例模式

### 定义

> 所谓懒汉式，即是在需要用到对象的时候才去创建，相当于是以时间换空间。

### 代码

```java
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
```

> 第一个版本是最容易想到的版本，也是存在线程安全的版本。可能会有多个线程同时进入到条件判断里面。
>
> 第二个版本是在第一个版本上进行优化，对方法进行加锁，解决线程安全问题。但是，需要对整个方法加锁吗？造成线程安全问题的部分仅仅是new对象的时候，所以考虑只锁这部分。
>
> 第三个版本，也是双重校验版本。目的是把锁的范围缩小，先判断对象是否已经创建，如果没有创建那么进行加锁准备创建对象。然后再判断一下对象是否依然没创建，如果没就正式创建对象。