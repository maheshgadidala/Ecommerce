package com.JavaEcommerce.Ecommerce.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaController {

    private com.JavaEcommerce.Ecommerce.service.KafkaProducer kafkaProducer;
    public KafkaController(com.JavaEcommerce.Ecommerce.service.KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }
    @GetMapping("/publish")
    public ResponseEntity<String> publishMessage(String message) {
     kafkaProducer.sendMessage("product-topic", message);
        return ResponseEntity.ok("Message sent to Kafka topic: " + message);
    }
}
