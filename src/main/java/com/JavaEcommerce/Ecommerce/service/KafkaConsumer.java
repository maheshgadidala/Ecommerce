//package com.JavaEcommerce.Ecommerce.service;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.util.logging.Logger;
//
//@Service
//public class KafkaConsumer {
//
//    private static final Logger logger = Logger.getLogger(KafkaConsumer.class.getName());
//    @KafkaListener(topics = "product-topic", groupId = "EcommerceGroup")
//    public void consume(String message) {
//        logger.info(String.format("Consumed message: %s", message));
//
//
//    }
//}
