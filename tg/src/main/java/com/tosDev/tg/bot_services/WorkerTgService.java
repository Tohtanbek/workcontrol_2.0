package com.tosDev.tg.bot_services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScope;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.tosDev.tg.db.TgQueries;
import com.tosDev.tg.db.WorkerTgQueries;
import com.tosDev.web.jpa.entity.Address;
import com.tosDev.web.jpa.entity.Shift;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.tosDev.tg.bot.enums.ShiftEndTypeEnum.*;

@Service
@Slf4j
public class WorkerTgService extends BrigadierWorkerCommonTgMethods {
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
    private final String SHIFT_WAS_BEGAN_MSG = """
            Вы успешно начали рабочий день, время и адрес сохранены в системе.
            """;
    private final String START_SHIFT_CALLBACK = "START_SHIFT_MSG";

    private final String CHOOSE_ADDRESS_CALLBACK = "chosen_address_id_";
    private final String AGREE_TO_START_SHIFT_CALLBACK = "agree_to_start_shift_on_";
    private final String DISAGREE_TO_START_SHIFT_CALLBACK = "disagree_to_start_shift";
    private final String READY_TO_END_SHIFT_CALLBACK = "ready_to_end_shift";
    private final String CHOOSE_SHIFT_END_TYPE = """
            Выберите тип окончания рабочего дня. После нажатия на кнопку
            рабочий день будет окончен и сохранен в системе.
            """;
    private final String SUCCESSFUL_FINISH_OF_SHIFT = """
            Рабочий день успешно сохранен в системе.
            Для начала следующего выберите адрес снова.
            """;
    private final String ERROR_FINISH_OF_SHIFT = """
            Непредвиденная ошибка при сохранении смены. Пожалуйста обратитесь к менеджеру.
            """;


    private final WorkerTgQueries workerTgQueries;
    private final TelegramBot bot;
    private final DateTimeFormatter tgDateTimeFormatter;

    @Autowired
    public WorkerTgService(TelegramBot bot,
                           WorkerTgQueries workerTgQueries,
                           TgQueries tgQueries,
                           DateTimeFormatter tgDateTimeFormatter) {
        super(bot,tgQueries);
        this.bot = bot;
        this.workerTgQueries = workerTgQueries;
        this.tgDateTimeFormatter = tgDateTimeFormatter;
    }


    /**
     * Привязывает чат айди впервые авторизовавшегося работника к сущности в базе данных
     */

    public void startWorkerLogic(Update update, Integer workerId) {
        if (update.callbackQuery()!=null) {
            //Ставим личное меню работнику
            bot.execute(new SetMyCommands()
                    .scope(new BotCommandsScopeChat(update.callbackQuery().from().id())));
            startWorkerCallBackQueryLogic(update,workerId);
        }
        else if (update.message()!=null)
        {
            //Ставим личное меню работнику
            bot.execute(new SetMyCommands()
                    .scope(new BotCommandsScopeChat(update.message().chat().id())));
            startWorkerMessageLogic(update,workerId);
        }
    }

    //Если работник нажал на кнопку меню или отправил сообщение
    private void startWorkerMessageLogic(Update update, Integer workerId) {
        Long chatId = update.message().chat().id();
        Message freshMsg = update.message();
        //Если перед этим работник только авторизовался, то отправляем начало смены
        if (freshMsg.contact()!=null){
            sendGreeting(chatId);
        }
        else if (freshMsg.text().equals("/start_shift")){
            sendAddressList(update,workerId);
        }
    }

    //Если работник нажал на inline кнопку
    private void startWorkerCallBackQueryLogic(Update update, Integer workerId) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String callBackData = callbackQuery.data();
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
        else if (callBackData.equals(READY_TO_END_SHIFT_CALLBACK)){
            sendTypeChooserToEndShift(update,workerId);
        }
        else if (callBackData.equals(PLANNED.getDescription())
                ||
                callBackData.equals(UNPLANNED.getDescription()))
        {
            handleFinishedShift(update,workerId);
        }



    }
    //    Приватные методы ---------------------------------------------------------------

    private void handleFinishedShift(Update update,Integer workerId){
        Long chatId = update.callbackQuery().from().id();
        String callbackData = update.callbackQuery().data();
        log.info("Работник с id {} выбрал тип смены и нажал на кнопку для окончания",workerId);
        //Если пустая, значит была ошибка
        Optional<Shift> shift =
                Optional.ofNullable(workerTgQueries.saveFinishedShift(workerId, callbackData));
        deletePrevCallbackMessage(update);
        if (shift.isPresent()){
            bot.execute(new SendMessage(chatId,formatFinishedWorkerShift(shift.get())));
            bot.execute(new SendMessage(chatId,SUCCESSFUL_FINISH_OF_SHIFT));
            sendAddressList(update,workerId);
            log.info("Работник {} завершил смену, ему отправлен список адресов снова",workerId);
            //todo: Рассылка админам, супервайзерам.
            //todo: рассылка бригадирам с кнопкой утверждения
        }
        else {
            bot.execute(new SendMessage(chatId,ERROR_FINISH_OF_SHIFT));
            handleStrangeMove(update);
        }
    }

    private void sendTypeChooserToEndShift(Update update,Integer workerId){
        createTypeChooserToEndShift(update);
        log.info("Отправили работнику {} кнопку для " +
                        "финального окончания смены с выбором план/неплан", workerId);
        deletePrevCallbackMessage(update);
    }

    private void sendButtonEndingShift(Update update, Integer workerId){
        createButtonEndingShift(update);
        log.info("Отправили работнику {} кнопку для окончания рабочего дня",workerId);
    }

    private void startWorkerShift(Update update, Integer workerId, String callBackData) {
        String addressId = callBackData.substring(AGREE_TO_START_SHIFT_CALLBACK.length());
        Long chatId = update.callbackQuery().from().id();
        log.info("Работник с id {} согласился начать смену на address_id {}",workerId,addressId);
        boolean savedSuccessfully = workerTgQueries.loadFreshWorkerShift(addressId,workerId);
        deletePrevCallbackMessage(update);
        if (!savedSuccessfully){
            bot.execute(new SendMessage(chatId,ALREADY_EXISTING_SHIFT_AT_WORK));
            log.warn("У работника {} уже есть активная смена",workerId);
        } else {
            bot.execute(new SendMessage(chatId,SHIFT_WAS_BEGAN_MSG));
            //todo: Рассылка админам, бригадирам, супервайзерам.
            sendButtonEndingShift(update,workerId);
        }
}

    private void offerStartOfShift(Update update, String callBackData, Integer workerId) {
        String chosenAddressShortName = offerStart(update,callBackData);
        log.info("Работнику с worker_id {} предложено начать смену на {}",
                workerId,chosenAddressShortName);
        deletePrevCallbackMessage(update);
    }


    private void sendAddressList(Update update,Integer workerId) {
        Long chatId;
        if (update.message()!=null) {
            chatId = update.message().chat().id();
        }
        else {
            chatId = update.callbackQuery().from().id();
        }
        List<Address> availableAddressList =
                workerTgQueries.loadWorkerAvailableAddressList(workerId);
        showAddressList(availableAddressList,chatId);
        log.info("Вывели работнику {} список объектов на выбор",workerId);
        deletePrevCallbackMessage(update);
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

    private String formatFinishedWorkerShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());
        String endDateTime = tgDateTimeFormatter.format(shift.getEndDateTime());

        return "ℹ Рабочий день окончен "+"\n"+
                shift.getShortInfo() +"\n"+
                "Начало в "+startDateTime+"\n"+
                "Конец в "+endDateTime;
    }

}
