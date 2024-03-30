package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.tosDev.tg.db.ResponsibleTgQueries;
import com.tosDev.spring.jpa.entity.Brigadier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResponsibleTgService {

    private final String GREETING = """
            Приветствую, пользователь! Ваша роль в боте - супервайзер.
            Сюда будут приходить уведомления от привязанных к вам менеджеров.
            Доступные функции можно просмотреть на кнопке Menu
            """;

    private final String CHECK_BRIGS_COMMAND = "check_brigs";

    private final TelegramBot bot;
    private final ResponsibleTgQueries responsibleTgQueries;

    public void startResponsibleLogic(Update update, Integer responsibleId) {
        editMenuForResponsible(update);
        if (update.callbackQuery()!=null) {
            startResponsibleCallbackQueryLogic(update,responsibleId);
        }
        else if (update.message()!=null)
        {
            startResponsibleMessageLogic(update,responsibleId);
        }
    }

    private void startResponsibleCallbackQueryLogic(Update update, Integer responsibleId) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String callBackData = callbackQuery.data();
    }

    private void startResponsibleMessageLogic(Update update, Integer responsibleId) {
        Long chatId = update.message().chat().id();
        Message freshMsg = update.message();
        //Если перед этим супервайзер только авторизовался, то отправляем начало смены
        if (freshMsg.contact()!=null){
            sendGreeting(chatId);
        }
        else if (freshMsg.text().equals("/"+CHECK_BRIGS_COMMAND)) {
            log.info("Супервайзер {} запросил список бригадиров",responsibleId);
            sendBrigsList(chatId,responsibleId);
        }
    }


    //    Приватные методы ---------------------------------------------------------------

    private void sendBrigsList(Long chatId,Integer responsibleId){
        List<Brigadier> linkedBrigs = responsibleTgQueries.findLinkedBrigs(responsibleId);
        if (!linkedBrigs.isEmpty()){
            String msg = formatLinkedBrigs(linkedBrigs);
            bot.execute(new SendMessage(chatId,msg));
            log.info("Отправили супервайзеру {} список его бригадиров {}",
                    responsibleId,msg);
        }
        else {
            bot.execute(new SendMessage(chatId,"У вас пока нет привязанных бригадиров"));
            log.info("Отправили супервайзеру {}, что у него нет привязанных бригадиров",
                    responsibleId);
        }
    }
    private void sendGreeting(Long chatId){
        bot.execute(new SendMessage(chatId,GREETING));
        log.info("Поприветствовали супервайзера {} и отправили кнопку для начала смены",chatId);
    }

    private void editMenuForResponsible(Update update){
        Long chatId = update.message() == null?
                update.callbackQuery().from().id()
                :
                update.message().chat().id();
        BotCommand botCommand = new BotCommand(CHECK_BRIGS_COMMAND,
                "Проверить список моих менеджеров");

        bot.execute(new SetMyCommands(botCommand)
                .scope(new BotCommandsScopeChat(chatId)));
    }
    private String formatLinkedBrigs(List<Brigadier> brigs){
        String brigsStr = "";
        for (Brigadier brig : brigs){
            brigsStr = brigsStr.concat(brig.getName()+"\n");
        }
        return brigsStr;
    }
}
