package com.tosDev.tg.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.tosDev.tg.bot_services.CommonTgMethods;
import com.tosDev.tg.bot_services.CommonTgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MainListener extends CommonTgMethods {

    private final TelegramBot bot;
    private final CommonTgService commonTgService;

    @Autowired
    public MainListener(TelegramBot bot, CommonTgService commonTgService) {
        super(bot);
        this.bot = bot;
        this.commonTgService = commonTgService;
    }


    public void activateListener(){
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update update : updates){
                    try {
                        //Если это сообщение с контактом
                        if (update.message()!= null && update.message().contact()!=null) {
                            commonTgService.authorizeNewChatAndRunLogic(update);
                        }
                        //Если это обычное сообщение или нажатие на кнопку
                        else if (update.message()!=null || update.callbackQuery()!=null){
                            commonTgService.checkAuthorityAndRunLogic(update);
                        }
                    } catch (Exception e) {
                        handleStrangeMove(update);
                        e.printStackTrace();
                    }
                }
                return CONFIRMED_UPDATES_ALL;
            }

        }, new ExceptionHandler() {
            @Override
            public void onException(TelegramException e) {
                log.error("Ошибка в telegramListener");
                if (e.response() != null) {
                    // got bad response from telegram
                    log.error("Телеграм вернул bad response: {}",e.response().errorCode());
                    log.error("Описание: {}",e.response().description());
                } else {
                    // probably network error
                    log.error("telegramListener вернул ошибку без response. Вероятно, ошибка сети");
                    e.printStackTrace();
                }
            }
        });
    }

}