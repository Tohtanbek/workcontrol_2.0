package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.tg.db.WorkerTgQueries;
import com.tosDev.web.jpa.entity.Address;
import com.tosDev.web.jpa.entity.Shift;
import com.tosDev.web.jpa.entity.Worker;
import com.tosDev.web.jpa.repository.AddressRepository;
import com.tosDev.web.jpa.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerTgService {
    private final String GREETING = """
            Приветствую, пользователь! Вы успешно авторизованы.
            Для начала рабочего дня нажмите на кнопку ниже.
            """;
    private final String START_SHIFT_MSG = """
            Начать рабочий день с выбора объекта
            """;
    private final String ALREADY_EXISTING_SHIFT_AT_WORK = """
            У вас не окончен другой рабочий день. Для начала нового, окончите старый.
            """;
    private final String START_SHIFT_CALLBACK = "START_SHIFT_MSG";

    private final String CHOOSE_ADDRESS_MSG = "Выберите адрес из списка доступных:";
    private final String CHOOSE_ADDRESS_CALLBACK = "chosen_address_id_";
    private final String AGREE_TO_START_SHIFT_CALLBACK = "agree_to_start_shift_on_";
    private final String DISAGREE_TO_START_SHIFT_CALLBACK = "disagree_to_start_shift";

    private final TelegramBot bot;
    private final WorkerTgQueries workerTgQueries;
    private final CommonTgService commonTgService;


    /**
     * Привязывает чат айди впервые авторизовавшегося работника к сущности в базе данных
     */

    public void startWorkerLogic(Update update, Integer workerId) {
        if (update.callbackQuery()!=null) {
            startWorkerCallBackQueryLogic(update,workerId);
        }
        else if (update.message()!=null)
        {
            startWorkerMessageLogic(update,workerId);
        }
    }

    //Если работник нажал на кнопку меню или отправил сообщение
    private void startWorkerMessageLogic(Update update, Integer workerId) {
        Long chatId = update.message().chat().id();
        //Если перед этим работник только авторизовался, то отправляем начало смены
        if (update.message().contact()!=null){
            sendGreeting(chatId);
        }
    }

    //Если работник нажал на inline кнопку
    private void startWorkerCallBackQueryLogic(Update update, Integer workerId) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String callBackData = callbackQuery.data();
        Long chatId = update.callbackQuery().from().id();
        if (callBackData.equals(START_SHIFT_CALLBACK)){
            sendAddressList(update,workerId);
        }
        else if (callBackData.startsWith(CHOOSE_ADDRESS_CALLBACK)){
            offerStartOfShift(update,callBackData,workerId);
        }
        else if (callBackData.startsWith(AGREE_TO_START_SHIFT_CALLBACK)){
            startWorkerShift(update,workerId,callBackData);
        }
        else if (callBackData.startsWith(DISAGREE_TO_START_SHIFT_CALLBACK)){
            sendAddressList(update,workerId);
        }


    }
    //    Приватные методы ---------------------------------------------------------------


    private void startWorkerShift(Update update, Integer workerId, String callBackData) {
        String addressId = callBackData.substring(AGREE_TO_START_SHIFT_CALLBACK.length());
        Long chatId = update.message().chat().id();
        log.info("Работник с id {} согласился начать смену на address_id {}",workerId,addressId);
        boolean savedSuccessfully = workerTgQueries.loadFreshWorkerShift(addressId,workerId);
        if (!savedSuccessfully){
            commonTgService.deletePrevMessage(update);
            bot.execute(new SendMessage(chatId,ALREADY_EXISTING_SHIFT_AT_WORK));
            log.warn("У работника {} уже есть активная смена ");
        }
}

    private void offerStartOfShift(Update update, String callBackData, Integer workerId) {
        //Удалить предыдущее сообщение
        Long chatId = update.callbackQuery().from().id();
        String chosenAddressId = callBackData.substring(CHOOSE_ADDRESS_CALLBACK.length());
        String chosenAddressShortName = workerTgQueries.checkAddressNameById(chosenAddressId);
        String message = String.format("Начать рабочий день на %s?",chosenAddressShortName);
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton agreeIkb =
                new InlineKeyboardButton("Да")
                        .callbackData(AGREE_TO_START_SHIFT_CALLBACK+chosenAddressId);
        InlineKeyboardButton disagreeIkb =
                new InlineKeyboardButton("Нет")
                        .callbackData(DISAGREE_TO_START_SHIFT_CALLBACK);
        ikbMarkup.addRow(agreeIkb,disagreeIkb);

        bot.execute(new SendMessage(chatId,message).replyMarkup(ikbMarkup));
        log.info("Работнику с worker_id {} предложено начать смену на {}",
                workerId,chosenAddressShortName);
        commonTgService.deletePrevMessage(update);
    }


    private void sendAddressList(Update update,Integer workerId) {
        Long chatId = update.callbackQuery().from().id();
        List<Address> availableAddressList =
                workerTgQueries.loadWorkerAvailableAddressList(workerId);
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        for (Address address : availableAddressList){
            InlineKeyboardButton ikb =
                    new InlineKeyboardButton(address.getShortName())
                            .callbackData(CHOOSE_ADDRESS_CALLBACK+address.getId().toString());
            ikbMarkup.addRow(ikb);
        }
        bot.execute(
                new SendMessage(chatId,"Выберите объект")
                        .replyMarkup(ikbMarkup)
                );
        log.info("Вывели работнику {} список объектов на выбор",workerId);
        commonTgService.deletePrevMessage(update);
    }


    private void sendGreeting(Long chatId){
        InlineKeyboardButton ikb =
                new InlineKeyboardButton(START_SHIFT_MSG).callbackData(START_SHIFT_CALLBACK);
        InlineKeyboardMarkup ikbMarkup =
                new InlineKeyboardMarkup().addRow(ikb);
        bot.execute(
                new SendMessage(chatId,GREETING).replyMarkup(ikbMarkup)
        );
        log.info("Поприветствовали работника {} и отправили кнопку для начала смены",chatId);
    }

}
