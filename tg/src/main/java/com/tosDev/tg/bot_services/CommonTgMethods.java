package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.tg.db.TgQueries;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonTgMethods {

    private final TelegramBot bot;
    @Autowired
    public CommonTgMethods(TelegramBot bot) {
        this.bot = bot;
    }



    public void deletePrevCallbackMessage(Update update){
        Long chatId;
        Integer messageId;
        if (update.message()!=null) {
            chatId = update.message().chat().id();
            messageId = update.message().messageId();
        }
        else {
            chatId = update.callbackQuery().from().id();
            //todo: когда поправят maybeInaccessibleMessage, избавиться от deprecated
            messageId = update.callbackQuery().message().messageId();
        }
        try {
            bot.execute(new DeleteMessage(chatId,messageId));
        } catch (Exception e) {
            log.warn("Не удалось удалить предыдущее сообщение");
            e.printStackTrace();
        }
        log.info("Удалили предыдущее сообщение у chatId: {}",chatId);
    }
    public void handleStrangeMove(Update update) {
        Long chatId;
        if (update.message()!=null){
            chatId = update.message().chat().id();
        }
        else {
            chatId = update.callbackQuery().from().id();
        }
        bot.execute(new SendMessage(chatId,"Произошла ошибка, " +
                "напишите любое сообщение или нажмите /start для начала работы"));
        log.error("Ошибка в чате {}",chatId);
    }
}
