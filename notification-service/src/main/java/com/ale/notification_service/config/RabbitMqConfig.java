package com.ale.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${app.messaging.topic.rabbitmq}") private String exchangeName;
    @Value("${app.messaging.routingkey.rabbitmq}") private String routingKey;

    private static final String QUEUE_NAME = "notification.queue";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
    
}
