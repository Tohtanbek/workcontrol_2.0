package com.tosDev.tg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.tosDev.tg",
        "com.tosDev.web.jpa",
        "com.tosDev.amqp"
})
public class TgApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(TgApplicationRunner.class,args);
    }
}
