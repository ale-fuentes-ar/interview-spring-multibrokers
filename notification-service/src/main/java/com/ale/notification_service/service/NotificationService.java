package com.ale.notification_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.ale.notification_service.model.Notification;
import com.ale.notification_service.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired private NotificationRepository repository;
    @Autowired private JmsTemplate jmsTemplate;
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired private SimpMessagingTemplate websocketTemplate;

    @Value("${app.messaging.topic.activemq}") private String activeMqTopic;
    @Value("${app.messaging.topic.rabbitmq}") private String rabbitMqExchange;
    @Value("${app.messaging.routingkey.rabbitmq}") private String rabbitMqRoutingKey;
    @Value("${app.messaging.topic.kafka}") private String kafkaTopic;

    public List<Notification> getAllNotifications(){
        return repository.findAll();
    }

    public Notification createNotification(String message){
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());

        Notification savedNotification = repository.save(notification);
        
        String eventMessage = String.format("%s: %s", "Nueva notificación creada: ", savedNotification.getMessage());
        jmsTemplate.convertAndSend(activeMqTopic, eventMessage);
        rabbitTemplate.convertAndSend(rabbitMqExchange, rabbitMqRoutingKey, eventMessage);
        kafkaTemplate.send(kafkaTopic, eventMessage);

        System.out.println("Nueva notificación enviada: WebSocket /topic/notifications");
        this.websocketTemplate.convertAndSend("/topic/notifications", savedNotification);
        
        return savedNotification;
    }
    
}
