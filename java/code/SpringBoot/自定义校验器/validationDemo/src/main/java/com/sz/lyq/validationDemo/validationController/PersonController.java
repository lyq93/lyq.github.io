package com.sz.lyq.validationDemo.validationController;

import com.sz.lyq.validationDemo.domain.Person;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PersonController {

    @PostMapping(value = "/person/save")
    public Person getPerson(@Valid @RequestBody Person person) {
        return person;
    }
}
