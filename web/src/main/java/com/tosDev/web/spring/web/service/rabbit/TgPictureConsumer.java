package com.tosDev.web.spring.web.service.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TgPictureConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.tg}")
    public void consumer(SomeTgRequest request){
        log.info("Получили {} из rabbitMQ очереди",request);
        googleService.send(request);
    }
}
