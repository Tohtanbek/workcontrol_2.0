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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tosDev.enums.ShiftEndTypeEnum.PLANNED;
import static com.tosDev.enums.ShiftEndTypeEnum.UNPLANNED;

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
    он уже подтвержден.
    """;
    private final String CHANGE_JOB_ON_SHIFT_CALLBACK = "change_job_of_shift_id_";
    private final String CHOSEN_JOB_ON_SHIFT_CALLBACK = "job_id_%s_shift_id_%s";
    private final String CHOSEN_JOB_ON_SHIFT_PREFIX = "job_id_";
    private final String NOT_FOUND_JOBS_ERR = """
    Для данного адреса не найдено ни одной связанной профессии.
    Утвердите смену без смены профессии.
    """;
    private final String START_SHIFT_COMMAND = "start_shift";
    private final String CHECK_WORKERS_COMMAND = "check_workers";
    private final String CHECK_ADDRESS_COMMAND = "check_address";
    private final String CHECK_WORKERS_WORKING_COMMAND = "check_workers_working";

    private final TelegramBot bot;

    private final BrigadierTgQueries brigadierTgQueries;
    private final DateTimeFormatter tgDateTimeFormatter;
    private final AdminTgQueries adminTgQueries;
    private final TgQueries tgQueries;

    public BrigadierTgService(TelegramBot bot,
                              BrigadierTgQueries brigadierTgQueries,
                              TgQueries tgQueries,
                              DateTimeFormatter tgDateTimeFormatter,
                              AdminTgQueries adminTgQueries) {
        super(bot, tgQueries,tgDateTimeFormatter,adminTgQueries);
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
            handleChangeOfJobRequest(update,brigadierId);
        }
        else if (callBackData.startsWith(CHOSEN_JOB_ON_SHIFT_PREFIX)){
            handleChangeOfJob(update,brigadierId);
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
        else if (freshMsg.text().equals("/"+START_SHIFT_COMMAND)){
            sendAddressList(update,brigadierId);
        }
        else if (freshMsg.text().equals("/"+CHECK_WORKERS_COMMAND)){
            checkWorkers(chatId,brigadierId);
        }
        else if (freshMsg.text().equals("/"+CHECK_WORKERS_WORKING_COMMAND)){
            checkWorkersWorking(chatId,brigadierId);
        }
        else if (freshMsg.text().equals("/"+CHECK_ADDRESS_COMMAND)){
            checkAddressList(chatId,brigadierId);
        }
    }
    //    Приватные методы ---------------------------------------------------------------

    private void checkAddressList(Long chatId,Integer brigadierId){
        List<Address> maybeAddressList =
                brigadierTgQueries.findAddressListOfBrig(brigadierId);
        if (!maybeAddressList.isEmpty()){
            String msg = formatListOfAddress(maybeAddressList);
            bot.execute(new SendMessage(chatId,msg));
            log.info("Отправили бригадиру {} список его адресов {}",
                    brigadierId,msg);
        }
        else {
            log.warn("Не найдены адреса бригадира {} по команде",
                    brigadierId);
            bot.execute(new SendMessage(chatId,"Адреса не найдены"));
        }
    }
    private void checkWorkersWorking(Long chatId,Integer brigadierId){
        List<Worker> maybeWorkers = brigadierTgQueries.findLinkedWorkersWorking(brigadierId);
        if (!maybeWorkers.isEmpty()){
            String msg = formatListOfWorkers(maybeWorkers);
            bot.execute(new SendMessage(chatId,msg));
            log.info("Отправили бригадиру {} список работников на смене {} по команде",
                    brigadierId,msg);
        }
        else {
            bot.execute(new SendMessage(chatId,"Работники не найдены"));
            log.warn("Не найдены работники на смене по запросу команды бригадира {}",
                    brigadierId);
        }
    }
    private void checkWorkers(Long chatId,Integer brigadierId){
        List<Worker> maybeWorkers = brigadierTgQueries.findLinkedWorkers(brigadierId);
        if (!maybeWorkers.isEmpty()){
            String msg = formatListOfWorkers(maybeWorkers);
            bot.execute(new SendMessage(chatId,msg));
            log.info("Отправили бригадиру {} список работников {} по команде",
                    brigadierId,msg);
        }
        else {
            bot.execute(new SendMessage(chatId,"Работники не найдены"));
            log.warn("Не найдены работники по запросу команды бригадира {}",
                    brigadierId);
        }
    }

    private void handleChangeOfJob(Update update, Integer brigadierId) {
        Long chatId = update.callbackQuery().from().id();
        String cbData = update.callbackQuery().data();
        String chosenJobId = "";
        String shiftId = "";
        Pattern jobPattern = Pattern.compile("job_id_(\\d+)");
        Matcher jobMatcher = jobPattern.matcher(cbData);
        if (jobMatcher.find()){
            chosenJobId = jobMatcher.group(1);
        }
        else {
            log.error("Регулярное выражение не нашло job_id");
        }
        Pattern shiftPattern = Pattern.compile("shift_id_(\\d+)");
        Matcher shiftMatcher = shiftPattern.matcher(cbData);
        if (shiftMatcher.find()){
            shiftId = shiftMatcher.group(1);
        }
        else {
            log.error("Регулярное выражение не нашло shift_id");
        }
        Optional<Shift> maybeShift =
                brigadierTgQueries
                        .saveChangeOfJob(Integer.parseInt(chosenJobId), Integer.parseInt(shiftId));
        maybeShift.ifPresentOrElse(updatedShift -> {
            log.info("Бригадир {} изменил job на {} в смене {}",
                    brigadierId,
                    updatedShift.getJob(),
                    updatedShift.getShortInfo());
            //Пересылаем смену бригадиру снова для подтверждения
            reSendEditedShift(chatId,updatedShift);
            log.info("Переслали бригадиру {} подтверждение обновленной им смены {}",
                    brigadierId,updatedShift.getShortInfo());
        },() -> bot.execute(new SendMessage(chatId,
                "Не удалось сменить профессию для рабочего дня")));
    }

    private void reSendEditedShift(Long chatId,Shift freshlyUpdatedShift){
        String msg = formatFinishedWorkerShift(freshlyUpdatedShift);
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton ikButtonApprove =
                new InlineKeyboardButton("Подтвердить отчет")
                        .callbackData(APPROVED_SHIFT_CALLBACK+freshlyUpdatedShift.getId());
        InlineKeyboardButton ikButtonChangeJob =
                new InlineKeyboardButton("Сменить должность")
                        .callbackData(CHANGE_JOB_ON_SHIFT_CALLBACK+freshlyUpdatedShift.getId());
        ikbMarkup.addRow(ikButtonApprove).addRow(ikButtonChangeJob);
        SendMessage sendMessage = new SendMessage(chatId,msg).replyMarkup(ikbMarkup);
        bot.execute(sendMessage);
    }

    private void handleChangeOfJobRequest(Update update, Integer brigadierId){
        Long chatId = update.callbackQuery().from().id();
        Integer shiftId = Integer.valueOf(update.callbackQuery().data()
                .substring(CHANGE_JOB_ON_SHIFT_CALLBACK.length()));
        List<Job> jobsOfAddress =
                brigadierTgQueries.loadJobsOfAddress(shiftId,brigadierId);
        if (!jobsOfAddress.isEmpty()){
            bot.execute(printJobsOfAddressToChange(chatId,shiftId,jobsOfAddress));
            log.info("Отправили бригадиру {} адреса на выбор {}",brigadierId,jobsOfAddress);
        }
        else {
            log.info("Для смены {} не удалось найти списка доступных профессий",shiftId);
            bot.execute(new SendMessage(chatId,NOT_FOUND_JOBS_ERR));
        }
    }

    //Вставляет в callback и id job, и id shift. Потом это разберет регулярное выражение
    private SendMessage printJobsOfAddressToChange(Long chatId,
                                                   Integer shiftId,
                                                   List<Job> jobsOfAddress){
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        for (Job job : jobsOfAddress){
            String cbData = String.format(CHOSEN_JOB_ON_SHIFT_CALLBACK,job.getId(),shiftId);
            ikbMarkup.addRow(new InlineKeyboardButton(job.getName())
                    .callbackData(cbData));
        }
        return new SendMessage(chatId,"Выберите из списка доступных на адресе:")
                .replyMarkup(ikbMarkup);
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
            sendOutLinkedSupervisorsShiftMsg(maybeShift.get(),approvedMsg);

            //Расчет зп
            Optional<String> optionalExpenseError =
                    brigadierTgQueries.countAndSaveExpense(maybeShift.get());
            optionalExpenseError.ifPresent(this::sendOutErrToAdmins);
            //Расчет счета
            Optional<String> optionalIncomeError =
                    brigadierTgQueries.countAndSaveIncome(maybeShift.get());
            optionalIncomeError.ifPresent(this::sendOutErrToAdmins);
        }
        else {
            //Скорее всего ее уже подтвердили
            bot.execute(new SendMessage(chatId,ALREADY_APPROVED_ERR_MSG));
        }
    }

    private void sendOutLinkedSupervisorsShiftMsg(Shift shift, String shiftMsg) {
        List<Responsible> linkedSupervisors = shift
                .getBrigadier()
                .getResponsibleBrigadierList()
                .stream().map(ResponsibleBrigadier::getResponsible)
                .filter(responsible -> responsible.getChatId()!=null)
                .toList();
        if (!linkedSupervisors.isEmpty()){
            for (Responsible responsible : linkedSupervisors){
                bot.execute(new SendMessage(responsible.getChatId(),shiftMsg));
            }
        }
        else {
            log.warn("У бригадира смены {} нет авторизованных в tg супервайзеров, рассылка" +
                    "супервайзерам не возможна",shift.getShortInfo());
        }
    }

    private void sendOutOtherBrigsThatShiftApproved(Shift approvedShift,Long chatId){
        List<Brigadier> allBrigs = tgQueries.findBrigsWithChatIdOnShiftAddress(approvedShift);
        if (!allBrigs.isEmpty()) {
            String msg = String.format("Рабочий день %s подтвержден другим бригадиром",
                    approvedShift.getShortInfo());
            List<Long> neededChatIds = allBrigs
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
        Long chatId = update.message() == null?
                update.callbackQuery().from().id()
                :
                update.message().chat().id();

        BotCommand startShiftC = new BotCommand(START_SHIFT_COMMAND,
                "Начать собственный рабочий день");
        BotCommand checkWorkersC = new BotCommand(CHECK_WORKERS_COMMAND,
                "Список работников на моих адресах");
        BotCommand checkWorkingC = new BotCommand(CHECK_WORKERS_WORKING_COMMAND,
                "Работники, начавшие рабочий день");
        BotCommand checkAddressC = new BotCommand(CHECK_ADDRESS_COMMAND,
                "Список моих адресов");

        bot.execute(new SetMyCommands(startShiftC,checkWorkersC,checkWorkingC,checkAddressC)
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
            String shiftMsg = formatFinishedBrigadierShift(shift.get());
            bot.execute(new SendMessage(chatId,shiftMsg));
            bot.execute(new SendMessage(chatId,SUCCESSFUL_FINISH_OF_SHIFT));
            sendOutBrigEndToAdmins(shift.get());
            sendOutLinkedSupervisorsShiftMsg(shift.get(),shiftMsg);
            log.info("Бригадир {} завершил смену",brigadierId);

            //Расчет зп
            Optional<String> optionalExpenseError =
                    brigadierTgQueries.countAndSaveBrigExpense(shift.get());
            optionalExpenseError.ifPresent(this::sendOutErrToAdmins);
            //Расчет счета
            Optional<String> optionalIncomeError =
                    brigadierTgQueries.countAndSaveBrigIncome(shift.get());
            optionalIncomeError.ifPresent(this::sendOutErrToAdmins);
        }
        else {
            bot.execute(new SendMessage(chatId,ERROR_FINISH_OF_SHIFT));
            handleStrangeMove(update);
        }
    }

    private void sendOutBrigStartToAdmins(Shift freshlyUpdatedShift){
        Optional<List<Admin>> maybeAdmins = adminTgQueries.findAuthorizedAdmins();
        if (maybeAdmins.isPresent()){
            for (Admin admin : maybeAdmins.get()){
                SendMessage sendMessage =
                        new SendMessage(admin.getChatId(),
                                formatStartedBrigadierShift(freshlyUpdatedShift));
                bot.execute(sendMessage);
                log.info("Отправлено сообщение о начале смены {} админу {}",
                        freshlyUpdatedShift.getShortInfo(),admin.getPhoneNumber());
            }
            log.info("Админам разосланы сообщения о начале смены {}",
                    freshlyUpdatedShift.getShortInfo());
        }
        else {
            log.warn("Для рассылки сообщения о начале смены " +
                    "нет ни одного авторизованного в тг админа");
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
        Optional<Shift> maybeShift =
                brigadierTgQueries.loadFreshBrigadierShift(addressId, brigadierId);
        deletePrevCallbackMessage(update);
        if (maybeShift.isEmpty()) {
            bot.execute(new SendMessage(chatId, ALREADY_EXISTING_SHIFT_AT_WORK));
            log.warn("У бригадира {} уже есть активная смена", brigadierId);
        } else {
            String shiftMsg = formatStartedBrigadierShift(maybeShift.get());
            bot.execute(new SendMessage(chatId, SHIFT_WAS_BEGAN_MSG));
            sendOutLinkedSupervisorsShiftMsg(maybeShift.get(),shiftMsg);
            sendOutBrigStartToAdmins(maybeShift.get());
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

    private String formatStartedBrigadierShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());

        return "ℹ Рабочий день начат "+"\n"+
                "✔" + shift.getShortInfo() +"\n"+
                "⏰ Начало в "+startDateTime+"\n";
    }

    private String formatFinishedBrigadierShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());
        String endDateTime = tgDateTimeFormatter.format(shift.getEndDateTime());

        return "ℹ Рабочий день окончен "+"\n"+
                "✔" + shift.getShortInfo() +"\n"+
                "⏰ Начало в "+startDateTime+"\n"+
                "⏰ Конец в "+endDateTime+"\n";
    }
    private String formatApprovedShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());
        String endDateTime = tgDateTimeFormatter.format(shift.getEndDateTime());

        return "✅ Рабочий день утвержден бригадиром "+shift.getBrigadier().getName()+"\n"+
                "✔" + shift.getShortInfo() +"\n"+
                "⏰ Начало в "+startDateTime+"\n"+
                "⏰ Конец в "+endDateTime+"\n"+
                "⏳ Отработано часов: "+shift.getTotalHours();
    }

    private String formatListOfWorkers(List<Worker> workerList){
        String msg = "Список ваших работников:\n";
        for (Worker worker : workerList){
            msg = msg.concat(worker.getJob().getName() + " " + worker.getName() + "\n");
        }
        return msg;
    }
    private String formatListOfAddress(List<Address> addressList){
        String msg = "Список ваших адресов:\n";
        for (Address address : addressList){
            msg = msg.concat(address.getShortName() + "\n");
        }
        return msg;
    }


}
