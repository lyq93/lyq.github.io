package com.sz.lyq.configurationDemo.controller;

import com.sz.lyq.configurationDemo.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PersonController implements EnvironmentAware {
    //通过xml的方式动态获取对象的值
    @Autowired
    @Qualifier("person")
    private Person person;
    //通过注解的方式获取对象的值
    @Value("${person.id}")
    private Long id;

    @Value("${person.name}")
    private String name;

    private Integer age;

    @GetMapping("/getPerson")
    public Person getPerson() {
        return person;
    }

    @GetMapping("/getPersonMap")
    public Map<String,Object> getPersonMap() {
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("id",id);
        resultMap.put("age",age);
        resultMap.put("name",name);
        return resultMap;
    }

    //通过接口的方式动态获取对象的值
    @Override
    public void setEnvironment(Environment environment) {
        this.age = environment.getProperty("person.age",Integer.class);
    }
}
