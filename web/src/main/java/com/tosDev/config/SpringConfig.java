package com.tosDev.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.DateFormatter;
import java.time.format.DateTimeFormatter;

@Configuration
public class SpringConfig {
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        //Нужен для корректной работы с LocalDate в Entity
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    @Bean
    DateTimeFormatter kebabFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Bean
    DateTimeFormatter basicDateTimeFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
}
