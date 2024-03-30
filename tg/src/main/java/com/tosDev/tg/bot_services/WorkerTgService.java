package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.tosDev.spring.jpa.entity.*;
import com.tosDev.tg.db.AdminTgQueries;
import com.tosDev.tg.db.BrigadierTgQueries;
import com.tosDev.tg.db.TgQueries;
import com.tosDev.tg.db.WorkerTgQueries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.tosDev.enums.ShiftEndTypeEnum.PLANNED;
import static com.tosDev.enums.ShiftEndTypeEnum.UNPLANNED;

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
    private final String SUCCESSFUL_FINISH_OF_SHIFT = """
            Рабочий день успешно сохранен в системе.
            Для начала следующего выберите адрес снова.
            """;
    private final String ERROR_FINISH_OF_SHIFT = """
            Непредвиденная ошибка при сохранении смены. Пожалуйста обратитесь к менеджеру.
            """;

    private final String APPROVED_SHIFT_CALLBACK = "approved_shift_id_";
    private final String CHANGE_JOB_ON_SHIFT_CALLBACK = "change_job_of_shift_id_";


    private final WorkerTgQueries workerTgQueries;
    private final BrigadierTgQueries brigadierTgQueries;
    private final AdminTgQueries adminTgQueries;
    private final TgQueries tgQueries;
    private final TelegramBot bot;
    private final DateTimeFormatter tgDateTimeFormatter;


    @Autowired
    public WorkerTgService(TelegramBot bot,
                           WorkerTgQueries workerTgQueries,
                           BrigadierTgQueries brigadierTgQueries, TgQueries tgQueries,
                           AdminTgQueries adminTgQueries,
                           DateTimeFormatter tgDateTimeFormatter) {
        super(bot,tgQueries,tgDateTimeFormatter,adminTgQueries);
        this.bot = bot;
        this.brigadierTgQueries = brigadierTgQueries;
        this.tgQueries = tgQueries;
        this.workerTgQueries = workerTgQueries;
        this.adminTgQueries = adminTgQueries;
        this.tgDateTimeFormatter = tgDateTimeFormatter;
    }


    public void startWorkerLogic(Update update, Integer workerId) {
        //Сначала актуализируем ему меню
        editMenuForWorker(update);
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


    private void editMenuForWorker(Update update){
        //Ставим личное меню работнику
        Long chatId = update.message() == null?
                update.callbackQuery().from().id()
                :
                update.message().chat().id();
        BotCommand botCommand = new BotCommand("start_shift","Начать рабочий день");

        bot.execute(new SetMyCommands(botCommand)
                .scope(new BotCommandsScopeChat(chatId)));
    }

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
            String shiftMsg = formatFinishedWorkerShift(shift.get());
            sendOutWorkerEndToBrigs(shift.get(),shiftMsg);
            sendOutWorkerShiftToSupervisors(shift.get(),shiftMsg);
            sendOutWorkerEndToAdmins(shift.get(),shiftMsg);

        }
        else {
            bot.execute(new SendMessage(chatId,ERROR_FINISH_OF_SHIFT));
            handleStrangeMove(update);
        }
    }

    private void sendOutWorkerEndToAdmins(Shift freshlyUpdatedShift, String msg){
        Optional<List<Admin>> maybeAdmins = adminTgQueries.findAuthorizedAdmins();
        if (maybeAdmins.isPresent()){
            for (Admin admin : maybeAdmins.get()){
                SendMessage sendMessage =
                        new SendMessage(admin.getChatId(), msg);
                bot.execute(sendMessage);
                log.info("Отправлено сообщение об окончании смены {} админу {}",
                        freshlyUpdatedShift.getShortInfo(),admin.getPhoneNumber());
            }
            log.info("Админам разосланы сообщения об окончании смены {}",
                    freshlyUpdatedShift.getShortInfo());
        }
        else {
            log.warn("Для рассылки сообщения об окончании смены " +
                    "нет ни одного авторизованного в тг админа");
        }
    }

    private void sendOutWorkerEndToBrigs(Shift freshlyUpdatedShift, String msg){
        List<Brigadier> maybeBrigs =
                tgQueries.findBrigsWithChatIdOnShiftAddress(freshlyUpdatedShift);
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton ikButtonApprove =
                new InlineKeyboardButton("Подтвердить отчет")
                        .callbackData(APPROVED_SHIFT_CALLBACK+freshlyUpdatedShift.getId());
        InlineKeyboardButton ikButtonChangeJob =
                new InlineKeyboardButton("Сменить должность")
                        .callbackData(CHANGE_JOB_ON_SHIFT_CALLBACK+freshlyUpdatedShift.getId());
        ikbMarkup.addRow(ikButtonApprove).addRow(ikButtonChangeJob);
        if (!maybeBrigs.isEmpty()){
            for (Brigadier brigadier : maybeBrigs){
                if (brigadier.getChatId()!=null) {
                    SendMessage sendMessage =
                            new SendMessage(brigadier.getChatId(), msg).replyMarkup(ikbMarkup);
                    bot.execute(sendMessage);
                    log.info("Отправили бригадиру {} запрос на подтверждение оконченной смены {}",
                            brigadier.getName(),freshlyUpdatedShift.getShortInfo());
                }
            }
            log.info("Разослали бригадирам запросы на подтверждение оконченной смены {}",
                    freshlyUpdatedShift.getShortInfo());
        }
        else {
            log.warn("Не найдено ни одного бригадира для отправки " +
                            "подтверждения оконченной смены {}",
                    freshlyUpdatedShift.getShortInfo());
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
        Optional<Shift> maybeShift = workerTgQueries.loadFreshWorkerShift(addressId,workerId);
        deletePrevCallbackMessage(update);
        if (maybeShift.isEmpty()){
            bot.execute(new SendMessage(chatId,ALREADY_EXISTING_SHIFT_AT_WORK));
            log.warn("У работника {} уже есть активная смена",workerId);
        } else {
            bot.execute(new SendMessage(chatId,SHIFT_WAS_BEGAN_MSG));
            String shiftMsg = formatStartedWorkerShift(maybeShift.get());
            sendOutWorkerStartToBrigs(maybeShift.get(),shiftMsg);
            sendOutWorkerShiftToSupervisors(maybeShift.get(),shiftMsg);
            sendOutWorkerStartToAdmins(maybeShift.get(),shiftMsg);
            sendButtonEndingShift(update,workerId);
        }
    }

    private void sendOutWorkerStartToAdmins(Shift freshlySavedShift,String message){
        Optional<List<Admin>> maybeAdmins = adminTgQueries.findAuthorizedAdmins();
        if (maybeAdmins.isPresent()){
            for (Admin admin : maybeAdmins.get()){
                SendMessage sendMessage =
                        new SendMessage(admin.getChatId(), message);
                bot.execute(sendMessage);
                log.info("Отправлено сообщение о начале смены {} админу {}",
                        freshlySavedShift.getShortInfo(),admin.getPhoneNumber());
            }
            log.info("Админам разосланы сообщения о начале смены {}",
                    freshlySavedShift.getShortInfo());
        }
        else {
            log.warn("Для рассылки сообщения о начале смены " +
                    "нет ни одного авторизованного в тг админа");
        }
    }

    private void sendOutWorkerStartToBrigs(Shift freshlySavedShift,String message) {
        List<Brigadier> maybeBrigs =
                tgQueries.findBrigsWithChatIdOnShiftAddress(freshlySavedShift);
        if (!maybeBrigs.isEmpty()){
            for (Brigadier brigadier : maybeBrigs){
                if (brigadier.getChatId()!=null){
                    SendMessage sendMessage =
                            new SendMessage(brigadier.getChatId(),message);
                    bot.execute(sendMessage);
                    log.info("Отправлено уведомление о начале смены {} бригадиру {}",
                            message,brigadier.getName());
                }
            }
            log.info("Разосланы уведомления бригадирам о начале смены {}",
                    freshlySavedShift.getShortInfo());
        }
        else {
            log.warn("У работника {} на адресе {} не найдено ни одного бригадира",
                    freshlySavedShift.getWorker().getName(),
                    freshlySavedShift.getAddress().getShortName());
        }
    }

    private void sendOutWorkerShiftToSupervisors(Shift shift, String shiftMsg){
       //Найти всех супервайзеров всех связанных со сменой бригадиров
       List<Responsible> linkedSupervisors =
               tgQueries.findAllAuthorizedResponsibleOfShift(shift);
       if (!linkedSupervisors.isEmpty()){
           for (Responsible responsible : linkedSupervisors){
               bot.execute(new SendMessage(responsible.getChatId(),shiftMsg));
           }
           log.info("Успешно разослали уведомление о смене {} связанным супервайзерам {}",
                   shift.getShortInfo(),linkedSupervisors);
       }
       else log.warn("У бригадиров адреса смены {} нет ни одного авторизованного супервайзера" +
                       "для отправки уведомлений о начале смены работника",
               shift.getShortInfo());
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

    private String formatStartedWorkerShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());

        return "ℹ Рабочий день начался "+"\n"+
                "✔" + shift.getShortInfo() +"\n"+
                "⏰ Начало в "+startDateTime+"\n";
    }


}
