package com.tosDev.tg_receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.tosDev.tg_receiver",
        "com.tosDev.amqp"
})
public class SpringTgReceiverRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringTgReceiverRunner.class,args);
    }


}
