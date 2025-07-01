package com.ale.notification_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.ale.notification_service.model.Notification;
import com.ale.notification_service.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock private JmsTemplate jmsTemplate;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @Mock private SimpMessagingTemplate websocketTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(notificationService, "activeMqTopic", "test.activemq.queue");
        ReflectionTestUtils.setField(notificationService, "rabbitMqExchange", "test.rabbitmq.exchange");
        ReflectionTestUtils.setField(notificationService, "rabbitMqRoutingKey", "test.rabbit.key");
        ReflectionTestUtils.setField(notificationService, "kafkaTopic", "test.kafka.topic");
    }

    @Test
    void createNotification_shouldSaveAndPublishToAllChannels(){
        // arrange
        String message = "Este es un mensaje de prueba";

        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setMessage(message);
        savedNotification.setTimestamp(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class)))
            .thenReturn(savedNotification);

        // act
        Notification result = notificationService.createNotification(message);

        // assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(message, result.getMessage());

        verify(notificationRepository).save(any(Notification.class));
        verify(jmsTemplate).convertAndSend(anyString(), anyString());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        verify(kafkaTemplate).send(anyString(), anyString());
        verify(websocketTemplate).convertAndSend(anyString(), any(Notification.class));
    }
}
