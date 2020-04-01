package com.sz.lyq.springcloudeurekaclient.controller;

import com.sz.lyq.springcloudeurekaclient.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试是否能正常获取配置服务器的配置信息
 */
@RestController
@EnableConfigurationProperties(Person.class)
public class PersonController {
    private final Person person;

    @Autowired
    public PersonController(Person person) {
        this.person = person;
    }

    @GetMapping("/person")
    public Person person() {
        return this.person;
    }
}
