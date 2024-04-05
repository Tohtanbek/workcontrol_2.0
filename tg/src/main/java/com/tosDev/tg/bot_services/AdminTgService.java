package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.web.spring.jpa.entity.main_tables.Admin;
import com.tosDev.web.spring.jpa.repository.main_tables.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTgService {

    private final String GREETING = """
            Приветствую, администратор! Вы успешно авторизованы.
            """;
    private final AdminRepository adminRepository;
    private final TelegramBot bot;

    @Transactional
    public Admin linkChatIdToExistingAdmin(Long adminPhoneNumber,Long chatId){
        Admin admin =
                adminRepository.findByPhoneNumber(adminPhoneNumber).orElseThrow();
        admin.setChatId(chatId);
        adminRepository.save(admin);
        log.info("Привязали новый чат {} пользователя {} в роли админа",
                chatId,adminPhoneNumber);
        return admin;
    }

    public void startAdminLogic(Update update) {
        Long chatId = update.message().chat().id();
        sendGreeting(chatId);
    }

    public void sendGreeting(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId,GREETING);
        bot.execute(sendMessage);
    }
}
