package com.tosDev.tg_receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TgMessageConsumer {

    //Добавляем сюда гугл сервис, который нужно вызвать, когда получаем message
    private final SomeService someService;

    @RabbitListener(queues = "${rabbitmq.queue.tg}")
    public void consumer(SomeTgRequest request){
        log.info("Получили {} из rabbitMQ очереди",request);
        someService.send(request);
    }
}
