package com.sz.lyq.starterDemo.controller;

import com.sz.lyq.starterDemo.domian.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    private final Person person;

    @Autowired
    public PersonController(Person person) {
        this.person = person;
    }

    @GetMapping("/getPerson")
    public Person getPerson() {
        return this.person;
    }

}
