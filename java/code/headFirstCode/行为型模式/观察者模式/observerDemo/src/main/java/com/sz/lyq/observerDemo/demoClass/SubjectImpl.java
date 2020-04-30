package com.sz.lyq.observerDemo.demoClass;

import java.util.ArrayList;
import java.util.List;

/**
 * 主题类
 */
public class SubjectImpl implements Subject {

    List<Observer> observers = null;
    // 模拟数据
    private Integer data_x;
    private Double data_y;

    public SubjectImpl() {
        observers = new ArrayList<>();
    }

    @Override
    public boolean registerObserver(Observer observer) {
        observers.add(observer);
        return true;
    }

    @Override
    public boolean removeObserver(Observer observer) {
        int i = observers.indexOf(observer);
        observers.remove(i);
        return true;
    }

    @Override
    public boolean notifyObserver() {
        // 通知所有观察者
        for(Observer observer : observers) {
            observer.update(data_x,data_y);
        }
        return true;
    }

    /**
     * 主题类数据变动方法
     */
    public void dataChange() {
        notifyObserver();
    }

    /**
     * 模拟数据发生改变
     * @param data_x
     * @param data_y
     */
    public void setData(Integer data_x,Double data_y) {
        this.data_x = data_x;
        this.data_y = data_y;
        dataChange();
    }
}
