package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class JsonKafkaProducer {

    //logger
     private static final Logger logger = Logger.getLogger(JsonKafkaProducer.class.getName());
    // KafkaTemplate for sending User objects as JSON
    private final KafkaTemplate<String, User> kafkaTemplate;

    public JsonKafkaProducer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    // Method to send a User object to a specified topic
    public void sendMessage(User data) {
        logger.info("Producing message: " + data.toString());
        Message<User> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "productJson-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
