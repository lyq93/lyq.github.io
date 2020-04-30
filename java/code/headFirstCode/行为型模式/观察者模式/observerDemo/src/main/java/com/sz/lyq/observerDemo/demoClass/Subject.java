package com.sz.lyq.observerDemo.demoClass;

/**
 * java API 的 Observable类
 */
public interface Subject {
    /**
     * 注册观察者
     * @param observer
     * @return
     */
    boolean registerObserver(Observer observer);

    /**
     * 移除观察者
     * @param observer
     * @return
     */
    boolean removeObserver(Observer observer);

    /**
     * 通知观察者
     * @return
     */
    boolean notifyObserver();
}
