package com.tosDev;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TgSpringConfig {

    @Bean
    public TelegramBot telegramBot(@Value("${tg.token}") String botToken){
        return new TelegramBot(botToken);
    }
}
