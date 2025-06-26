package com.ale.notification_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumers {

    @JmsListener(destination = "${app.messaging.topic.activemq}")
    public void receiveFromActiveMQ(String message){
        System.out.println(String.format("%s: %s", "[ACTIVEMQ CONSUMER] Mensaje recibido: ", message));
    }
    
    @RabbitListener(queues = "notification.queue")
    public void receiveFromRabbitMQ(String message){
        System.out.println(String.format("%s: %s", "[RABBITMQ CONSUMER] Mensaje recibido: ", message));
    }
    
    @KafkaListener(topics = "${app.messaging.topic.kafka}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveFromKafka(String message){
        System.out.println(String.format("%s: %s", "[KAFKA CONSUMER] Mensaje recibido: ", message));
    }





}
