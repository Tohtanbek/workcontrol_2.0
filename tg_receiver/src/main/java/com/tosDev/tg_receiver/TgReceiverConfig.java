package com.tosDev.tg_receiver;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TgReceiverConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queue.tg}")
    private String tgQueue;

    @Value("${rabbitmq.routing-keys.internal-tg}")
    private String internalTgRoutingKey;

    //бин - exchange
    @Bean
    public TopicExchange internalTopicExchange(){
        return new TopicExchange(this.internalExchange);
    }

    //бин - queue
    @Bean
    public Queue tgQueue(){
        return new Queue(this.tgQueue);
    }

    //Теперь нужно bind exchange и queue
    @Bean
    public Binding internalToTgReceiverBinding(){
        return BindingBuilder
                .bind(tgQueue())
                .to(internalTopicExchange())
                .with(this.internalTgRoutingKey);
    }
}
