package com.tosDev.web.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.DateFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    @Bean
    DecimalFormat decimalFormat(){
        return new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
