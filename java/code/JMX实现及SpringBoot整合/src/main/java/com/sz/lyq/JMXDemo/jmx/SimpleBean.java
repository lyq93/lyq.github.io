package com.sz.lyq.JMXDemo.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(
        objectName = "com.sz.lyq.JMXDemo.jmx:type=SimpleBean",
        description = "这是一个简单的bean，被spring托管"
)
public class SimpleBean {
    private Long id;
    private String name;
    private Integer value;

    @ManagedAttribute(description = "ID 属性")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @ManagedAttribute(description = "Name 属性")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @ManagedAttribute(description = "Value 属性")
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SimpleBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
