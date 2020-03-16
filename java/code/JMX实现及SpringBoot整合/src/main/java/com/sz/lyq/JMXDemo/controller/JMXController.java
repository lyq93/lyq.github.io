package com.sz.lyq.JMXDemo.controller;

import com.sz.lyq.JMXDemo.jmx.SimpleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JMXController {
    @Autowired
    private SimpleBean simpleBean;

    public SimpleBean simpleBean(@RequestParam(required = false)Long id,
                                 @RequestParam(required = false)String name,
                                 @RequestParam(required = false)Integer value) {

        if(id != null) {
            simpleBean.setId(id);
        }
        if(name != null) {
            simpleBean.setName(name);
        }
        if(value != null) {
            simpleBean.setValue(value);
        }
        return simpleBean;
    }
}
