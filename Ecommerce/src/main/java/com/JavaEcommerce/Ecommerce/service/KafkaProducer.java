package com.JavaEcommerce.Ecommerce.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class KafkaProducer {


   private static final Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessage(String topic, String message) {
        logger.info("Producing message: " + message);
        kafkaTemplate.send(topic, message);
        logger.info("Message sent to topic: " + topic);
    }

}
