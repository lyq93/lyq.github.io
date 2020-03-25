package com.sz.lyq.validationDemo.domain;

import com.sz.lyq.validationDemo.validation.constraints.PersonNamePrefix;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Person {
    @PersonNamePrefix(prefix = "sz_")
    public String name;
    @Min(value = 0)
    @Max(value = 200, message = "{person.age.max.message}")
    public int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
