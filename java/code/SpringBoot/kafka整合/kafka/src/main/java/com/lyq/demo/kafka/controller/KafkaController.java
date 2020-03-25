package com.lyq.demo.kafka.controller;

import com.lyq.demo.kafka.vo.User;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController(value = "/kafka")
public class KafkaController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @GetMapping(value = "/send/messge")
    public String sendMessage(@RequestParam(value = "message")String message) {
        kafkaTemplate.send("mytopic",message);
        return message;
    }

    @PostMapping(value = "/send/user")
    public Boolean kafkaProduce(@RequestBody User user) {
        kafkaTemplate.send("mytopic",user);
        return true;
    }

}
