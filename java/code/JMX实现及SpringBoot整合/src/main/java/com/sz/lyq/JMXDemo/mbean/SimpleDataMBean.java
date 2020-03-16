package com.sz.lyq.JMXDemo.mbean;

public interface SimpleDataMBean {
    /**
     * getter
     * @return
     */
    String getData();

    /**
     * setter
     */
    void setData(String data);

    /**
     * 数据展示
     * @return
     */
    String display();
}
