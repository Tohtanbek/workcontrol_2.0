package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.tosDev.tg.db.AdminTgQueries;
import com.tosDev.tg.db.BrigadierTgQueries;
import com.tosDev.tg.db.TgQueries;
import com.tosDev.web.jpa.entity.Address;
import com.tosDev.web.jpa.entity.Admin;
import com.tosDev.web.jpa.entity.Brigadier;
import com.tosDev.web.jpa.entity.Shift;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.tosDev.tg.bot.enums.ShiftEndTypeEnum.PLANNED;
import static com.tosDev.tg.bot.enums.ShiftEndTypeEnum.UNPLANNED;

@Service
@Slf4j
public class BrigadierTgService extends BrigadierWorkerCommonTgMethods {

    private final String SUCCESSFUL_FINISH_OF_SHIFT = """
            Рабочий день успешно сохранен в системе.
            Для начала следующего выберите адрес снова.
            """;

    private final String GREETING = """
            Приветствую, пользователь! Вы успешно авторизованы.
            Ваша роль - менеджер.
            Для начала собственного рабочего дня нажмите на кнопку меню.
            """;
    private final String START_SHIFT_MSG = """
            Начать рабочий день с выбора объекта
            """;
    private final String START_SHIFT_CALLBACK = "START_SHIFT_MSG";

    private final String CHOOSE_ADDRESS_CALLBACK = "chosen_address_id_";
    private final String AGREE_TO_START_SHIFT_CALLBACK = "agree_to_start_shift_on_";
    private final String DISAGREE_TO_START_SHIFT_CALLBACK = "disagree_to_start_shift";
    private final String ALREADY_EXISTING_SHIFT_AT_WORK = """
            У вас не окончен другой рабочий день. Для начала нового, окончите старый.
            """;
    private final String SHIFT_WAS_BEGAN_MSG = """
            Вы успешно начали рабочий день, время и адрес сохранены в системе.
            """;
    private final String READY_TO_END_SHIFT_CALLBACK = "ready_to_end_shift";
    private final String ERROR_FINISH_OF_SHIFT = """
            Непредвиденная ошибка при сохранении смены. Пожалуйста обратитесь к менеджеру.
            """;
    private final String APPROVED_SHIFT_CALLBACK = "approved_shift_id_";
    private final String ALREADY_APPROVED_ERR_MSG = """
    Не удалось утвердить рабочий день, скорее всего
    он уже подтвержден другим бригадиром.
    """;

    private final String CHANGE_JOB_ON_SHIFT_CALLBACK = "change_job_of_shift_id_";

    private final TelegramBot bot;

    private final BrigadierTgQueries brigadierTgQueries;
    private final DateTimeFormatter tgDateTimeFormatter;
    private final AdminTgQueries adminTgQueries;
    private final TgQueries tgQueries;

    public BrigadierTgService(TelegramBot bot,
                              BrigadierTgQueries brigadierTgQueries,
                              TgQueries tgQueries,
                              DateTimeFormatter tgDateTimeFormatter, AdminTgQueries adminTgQueries) {
        super(bot, tgQueries);
        this.tgQueries = tgQueries;
        this.bot = bot;
        this.brigadierTgQueries = brigadierTgQueries;
        this.tgDateTimeFormatter = tgDateTimeFormatter;
        this.adminTgQueries = adminTgQueries;
    }

    public void startBrigadierLogic(Update update, Integer brigadierId) {
        editMenuForBrigadier(update);
        if (update.callbackQuery()!=null) {
            startBrigadierCallbackQueryLogic(update,brigadierId);
        }
        else if (update.message()!=null)
        {
            startBrigadierMessageLogic(update,brigadierId);
        }
    }

    //Если работник нажал на inline кнопку
    private void startBrigadierCallbackQueryLogic(Update update, Integer brigadierId) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String callBackData = callbackQuery.data();
        if (callBackData.equals(START_SHIFT_CALLBACK)) {
            sendAddressList(update, brigadierId);
        }
        else if (callBackData.startsWith(CHOOSE_ADDRESS_CALLBACK)){
            offerStartOfShift(update,callBackData,brigadierId);
        }
        else if (callBackData.startsWith(AGREE_TO_START_SHIFT_CALLBACK)){
            startBrigadierShift(update,brigadierId,callBackData);
        }
        else if (callBackData.startsWith(DISAGREE_TO_START_SHIFT_CALLBACK)){
            sendAddressList(update,brigadierId);
        }
        else if (callBackData.equals(READY_TO_END_SHIFT_CALLBACK)){
            sendTypeChooserToEndShift(update,brigadierId);
        }
        else if (callBackData.equals(PLANNED.getDescription())
                ||
                callBackData.equals(UNPLANNED.getDescription()))
        {
            handleFinishedShift(update,brigadierId);
        }
        else if (callBackData.startsWith(APPROVED_SHIFT_CALLBACK)){
            handleApprovedShift(update,brigadierId);
        }
        else if (callBackData.startsWith(CHANGE_JOB_ON_SHIFT_CALLBACK)){
            handleChangeOfJob(update,brigadierId){

            }
        }

    }

    //Если бригадир отправил сообщение или нажал на reply кнопку или кнопку меню
    private void startBrigadierMessageLogic(Update update, Integer brigadierId) {
        Long chatId = update.message().chat().id();
        Message freshMsg = update.message();
        //Если перед этим бригадир только авторизовался, то отправляем начало смены
        if (freshMsg.contact()!=null){
            sendGreeting(chatId);
        }
        else if (freshMsg.text().equals("/start_shift")){
            sendAddressList(update,brigadierId);
        }
    }
    //    Приватные методы ---------------------------------------------------------------

    private void handleChangeOfJob(Update update,Integer brigadierId){
        Long chatId = update.callbackQuery().from().id();
        Integer shiftId = Integer.valueOf(update.callbackQuery().data()
                .substring(CHANGE_JOB_ON_SHIFT_CALLBACK.length()));

    }

    private void handleApprovedShift(Update update, Integer brigadierId) {
        Long chatId = update.callbackQuery().from().id();
        Integer shiftId = Integer.valueOf(
                update.callbackQuery().data().substring(APPROVED_SHIFT_CALLBACK.length()));

        Optional<Shift> maybeShift =
                brigadierTgQueries.approveShift(brigadierId,shiftId);
        if (maybeShift.isPresent()) {
            String approvedMsg = formatApprovedShift(maybeShift.get());
            //Показываем этому же бригадиру
            bot.execute(new SendMessage(chatId,approvedMsg));
            sendOutShiftApprovedToAdmins(approvedMsg);
            sendOutOtherBrigsThatShiftApproved(maybeShift.get(),chatId);
            //todo:рассылка супервайзера
        }
        else {
            //Скорее всего ее уже подтвердили
            bot.execute(new SendMessage(chatId,ALREADY_APPROVED_ERR_MSG));
        }
    }

    private void sendOutOtherBrigsThatShiftApproved(Shift approvedShift,Long chatId){
        Optional<List<Brigadier>> allBrigs = tgQueries.findBrigsOnShiftAddress(approvedShift);
        if (allBrigs.isPresent()) {
            String msg = String.format("Рабочий день %s подтвержден другим бригадиром",
                    approvedShift.getShortInfo());
            List<Long> neededChatIds = allBrigs.get()
                    .stream()
                    .map(Brigadier::getChatId)
                    .filter(id -> !id.equals(chatId))
                    .toList();
            for (Long cId : neededChatIds){
                bot.execute(new SendMessage(cId,msg));
            }
            log.info("Разослали бригадирам уведомление о том, что бригадир {} подтвердил смену {}",
                    approvedShift.getBrigadier().getName(),approvedShift.getShortInfo());
        }
    }

    private void sendOutShiftApprovedToAdmins(String editedShiftMsg){
        Optional<List<Admin>> maybeAdmins = adminTgQueries.findAuthorizedAdmins();
        if (maybeAdmins.isPresent()){
            for (Admin admin : maybeAdmins.get()){
                SendMessage sendMessage =
                        new SendMessage(admin.getChatId(),
                                editedShiftMsg);
                bot.execute(sendMessage);
                log.info("Отправлено сообщение о подтвержденной смене {} админу {}",
                        editedShiftMsg,admin.getPhoneNumber());
            }
            log.info("Админам разосланы сообщения о подтвержденной сменк {}",
                    editedShiftMsg);
        }
        else {
            log.warn("Для рассылки сообщения о подтвержденной смене " +
                    "нет ни одного авторизованного в тг админа");
        }
    }

    private void editMenuForBrigadier(Update update){
        //Ставим личное меню работнику
        Long chatId = update.message() == null?
                update.callbackQuery().from().id()
                :
                update.message().chat().id();
        BotCommand botCommand = new BotCommand("start_shift",
                "Начать собственный рабочий день");

        bot.execute(new SetMyCommands(botCommand)
                .scope(new BotCommandsScopeChat(chatId)));
    }

    private void handleFinishedShift(Update update,Integer brigadierId){
        log.info("Бригадир с id {} выбрал тип смены и нажал на кнопку для окончания",brigadierId);
        Long chatId = update.callbackQuery().from().id();
        String callbackData = update.callbackQuery().data();
        //Если пустая, значит была ошибка
        Optional<Shift> shift =
                Optional.ofNullable(brigadierTgQueries.saveFinishedShift(brigadierId, callbackData));
        deletePrevCallbackMessage(update);
        if (shift.isPresent()){
            bot.execute(new SendMessage(chatId,formatFinishedBrigadierShift(shift.get())));
            bot.execute(new SendMessage(chatId,SUCCESSFUL_FINISH_OF_SHIFT));
            sendOutBrigEndToAdmins(shift.get());
            log.info("Бригадир {} завершил смену",brigadierId);
            //todo: Рассылка админам, супервайзерам.

        }
        else {
            bot.execute(new SendMessage(chatId,ERROR_FINISH_OF_SHIFT));
            handleStrangeMove(update);
        }
    }

    private void sendOutBrigEndToAdmins(Shift freshlyUpdatedShift){
        Optional<List<Admin>> maybeAdmins = adminTgQueries.findAuthorizedAdmins();
        if (maybeAdmins.isPresent()){
            for (Admin admin : maybeAdmins.get()){
                SendMessage sendMessage =
                        new SendMessage(admin.getChatId(),
                                formatFinishedBrigadierShift(freshlyUpdatedShift));
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
    private void sendTypeChooserToEndShift(Update update,Integer brigadierId){
        createTypeChooserToEndShift(update);
        log.info("Отправили бригадиру {} кнопку для " +
                "финального окончания смены с выбором план/неплан", brigadierId);
        deletePrevCallbackMessage(update);
    }

    private void startBrigadierShift(Update update, Integer brigadierId, String callBackData) {
        String addressId = callBackData.substring(AGREE_TO_START_SHIFT_CALLBACK.length());
        Long chatId = update.callbackQuery().from().id();
        log.info("Бригадир с id {} согласился начать смену на address_id {}", brigadierId, addressId);
        boolean savedSuccessfully =
                brigadierTgQueries.loadFreshBrigadierShift(addressId, brigadierId);
        deletePrevCallbackMessage(update);
        if (!savedSuccessfully) {
            bot.execute(new SendMessage(chatId, ALREADY_EXISTING_SHIFT_AT_WORK));
            log.warn("У бригадира {} уже есть активная смена", brigadierId);
        } else {
            bot.execute(new SendMessage(chatId, SHIFT_WAS_BEGAN_MSG));
            //todo: Рассылка админам, супервайзерам.
            sendButtonEndingShift(update, brigadierId);
        }
    }

    private void offerStartOfShift(Update update, String callBackData, Integer brigadierId) {
        String chosenAddressShortName = offerStart(update,callBackData);
        log.info("Бригадиру с brigadier_id {} предложено начать смену на {}",
                brigadierId,chosenAddressShortName);
        deletePrevCallbackMessage(update);
    }

    private void sendAddressList(Update update, Integer brigadierId) {
        Long chatId;
        if (update.message()!=null) {
            chatId = update.message().chat().id();
        }
        else {
            chatId = update.callbackQuery().from().id();
        }
        List<Address> availableAddressList =
                brigadierTgQueries.loadBrigadierAvailableAddressList(brigadierId);
        showAddressList(availableAddressList,chatId);
        log.info("Вывели бригадиру {} список объектов на выбор",brigadierId);
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
        log.info("Поприветствовали бригадира {} и отправили кнопку для начала смены",chatId);
    }
    private void sendButtonEndingShift(Update update, Integer brigadierId){
        createButtonEndingShift(update);
        log.info("Отправили бригадиру {} кнопку для окончания рабочего дня",brigadierId);
    }

    private String formatFinishedBrigadierShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());
        String endDateTime = tgDateTimeFormatter.format(shift.getEndDateTime());

        return "ℹ Рабочий день окончен "+"\n"+
                shift.getShortInfo() +"\n"+
                "Начало в "+startDateTime+"\n"+
                "Конец в "+endDateTime+"\n";
    }
    private String formatApprovedShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());
        String endDateTime = tgDateTimeFormatter.format(shift.getEndDateTime());

        return "ℹ Рабочий день утвержден бригадиром "+shift.getBrigadier().getName()+"\n"+
                shift.getShortInfo() +"\n"+
                "Начало в "+startDateTime+"\n"+
                "Конец в "+endDateTime+"\n"+
                "Отработано часов: "+shift.getTotalHours();
    }
}
