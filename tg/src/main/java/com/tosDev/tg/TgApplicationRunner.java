package com.tosDev.tg;

import com.tosDev.tg.bot.MainListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.tosDev.tg",
        "com.tosDev.amqp"
})
@EntityScan("com/tosDev/spring/jpa/entity/main_tables")
@EnableJpaRepositories("com.tosDev.spring.jpa.repository.main_tables")
public class TgApplicationRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext x = SpringApplication.run(TgApplicationRunner.class,args);
        MainListener mainListener = (MainListener) x.getBean("mainListener");
        mainListener.activateListener();
    }
}
