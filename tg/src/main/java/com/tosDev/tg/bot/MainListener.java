package com.tosDev.tg.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.tosDev.tg.bot_services.CommonTgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MainListener {

    private final TelegramBot bot;
    private final CommonTgService commonTgService;


    public void activateListener(){
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update update : updates){
                    if (update.message().contact()!=null) {
                        commonTgService.authorizeNewChatAndRunLogic(update);
                    }
                    commonTgService.checkAuthorityAndRunLogic(update);
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