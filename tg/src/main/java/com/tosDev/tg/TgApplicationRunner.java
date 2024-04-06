package com.tosDev.tg;

import com.tosDev.tg.bot.MainListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.tosDev.amqp",
        "com.tosDev.tg",
        "com.tosDev.web.enums"})
@EntityScan("com/tosDev/web/spring/jpa/entity/main_tables")
@EnableJpaRepositories("com.tosDev.web.spring.jpa.repository.main_tables")
public class TgApplicationRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(TgApplicationRunner.class,args);
        MainListener mainListener = context.getBean(MainListener.class);
        mainListener.activateListener();
    }
}
