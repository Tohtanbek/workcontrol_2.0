package com.tosDev.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMQMessageProducer {

    @Qualifier("amqpTemplate")
    private final AmqpTemplate amqpTemplate;

    public void publish (Object payload, String exchange, String routingKey){
        log.info("Отправляем в {}, используя {}, payload: {}",exchange,routingKey,payload);
        amqpTemplate.convertAndSend(exchange,routingKey,payload);
        log.info("Отправили в {}, используя {}, payload: {}",exchange,routingKey,payload);
    }
}
