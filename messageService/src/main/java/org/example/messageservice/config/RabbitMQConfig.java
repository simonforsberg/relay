package org.example.messageservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "message-published";

    @Bean
    public Queue messagePublishedQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}