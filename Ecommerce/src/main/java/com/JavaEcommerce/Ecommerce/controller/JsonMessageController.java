package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kafka/json")
public class JsonMessageController {

    private final com.JavaEcommerce.Ecommerce.service.KafkaProducer kafkaProducer;
    public JsonMessageController(com.JavaEcommerce.Ecommerce.service.KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @RequestMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody User user){
    kafkaProducer.sendMessage("user-topic", String.valueOf(user));
        return ResponseEntity.ok("Message sent to Kafka topic: " + user);

    }
}
