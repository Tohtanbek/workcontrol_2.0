package com.tosDev.tg;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class TgSpringConfig {

    @Bean
    public TelegramBot telegramBot(@Value("${tg.token}") String botToken){
        return new TelegramBot(botToken);
    }
    @Bean
    DateTimeFormatter tgDateTimeFormatter(){
        return DateTimeFormatter.ofPattern("HH:mm E dd MMM yy");
    }
}
