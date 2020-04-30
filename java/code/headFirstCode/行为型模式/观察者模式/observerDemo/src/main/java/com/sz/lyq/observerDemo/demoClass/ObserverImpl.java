package com.sz.lyq.observerDemo.demoClass;

/**
 * 观察者类
 */
public class ObserverImpl implements Observer,DisplayData {
    // 观察的数据
    private Integer data_x;
    private Double data_y;
    private Subject subject;

    public ObserverImpl(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    @Override
    public void update(Integer data_x, Double data_y) {
        this.data_x = data_x;
        this.data_y = data_y;
        display();
    }

    @Override
    public void display() {
        System.out.println("data_x:" + data_x + ",data_y:" + data_y);
    }
}
