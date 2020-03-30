package com.sz.lyq.springcloudconfigclient.controller;

import com.sz.lyq.springcloudconfigclient.domain.Person;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableConfigurationProperties(Person.class)
public class PersonController {
    private final Person person;

    public PersonController(Person person) {
        this.person = person;
    }

    @GetMapping(value = "/person")
    public Person person() {
        return person;
    }
}
