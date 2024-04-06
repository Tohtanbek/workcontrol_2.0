package com.tosDev.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tosDev.amqp","com.tosDev.web"})
public class SpringMainModuleRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringMainModuleRunner.class,args);
    }
}
