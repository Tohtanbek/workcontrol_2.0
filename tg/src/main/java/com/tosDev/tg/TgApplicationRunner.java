package com.tosDev.tg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.tosDev.tg",
        "com.tosDev.amqp"
})
@EntityScan("com/tosDev/spring/jpa/entity")
@EnableJpaRepositories("com.tosDev.spring.jpa.repository")
public class TgApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(TgApplicationRunner.class,args);
    }
}
