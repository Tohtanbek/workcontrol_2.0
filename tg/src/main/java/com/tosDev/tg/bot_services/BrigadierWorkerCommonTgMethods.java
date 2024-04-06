package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.amqp.PhotoShiftIdRecord;
import com.tosDev.amqp.RabbitMQMessageProducer;
import com.tosDev.tg.db.AdminTgQueries;
import com.tosDev.tg.db.TgQueries;
import com.tosDev.web.spring.jpa.entity.main_tables.Address;
import com.tosDev.web.spring.jpa.entity.main_tables.Admin;
import com.tosDev.web.spring.jpa.entity.main_tables.Shift;
import com.tosDev.web.enums.ShiftEndTypeEnum;
import com.tosDev.web.spring.jpa.entity.main_tables.Worker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class BrigadierWorkerCommonTgMethods extends CommonTgMethods {

    @Value("${spring.rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${spring.rabbitmq.routing-keys.internal-tg}")
    private String internalTgRoutingKey;

    private final String CHOOSE_ADDRESS_CALLBACK = "chosen_address_id_";
    private final String AGREE_TO_START_SHIFT_CALLBACK = "agree_to_start_shift_on_";
    private final String DISAGREE_TO_START_SHIFT_CALLBACK = "disagree_to_start_shift";
    private final String CHOOSE_ADDRESS_MSG = "Выберите адрес из списка доступных:";
    private final String READY_TO_END_SHIFT_CALLBACK = "ready_to_end_shift";
    private final String SEND_PHOTO_YES = "send_photo_yes";
    private final String SEND_PHOTO_NO = "send_photo_no";
    private final String CHOOSE_SHIFT_END_TYPE = """
            Выберите тип окончания рабочего дня. После нажатия на кнопку
            рабочий день будет окончен и сохранен в системе.
            """;
    private final String ASK_FOR_PHOTO = """
            Прикрепить фото?
            """;
    private final String BOT_READY_FOR_PHOTO = """
            Отправьте в чат до 20 фотографий одним сообщением
            """;

    private final TelegramBot bot;
    private final TgQueries tgQueries;
    private final DateTimeFormatter tgDateTimeFormatter;
    private final AdminTgQueries adminTgQueries;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @Autowired
    public BrigadierWorkerCommonTgMethods(TelegramBot bot,
                                          TgQueries tgQueries,
                                          DateTimeFormatter tgDateTimeFormatter,
                                          AdminTgQueries adminTgQueries,
                                          RabbitMQMessageProducer rabbitMQMessageProducer) {
        super(bot);
        this.bot = bot;
        this.tgQueries = tgQueries;
        this.tgDateTimeFormatter = tgDateTimeFormatter;
        this.adminTgQueries = adminTgQueries;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }

    String offerStart(Update update, String callBackData) {
        Long chatId = update.callbackQuery().from().id();
        String chosenAddressId = callBackData.substring(CHOOSE_ADDRESS_CALLBACK.length());
        String chosenAddressShortName = tgQueries.checkAddressNameById(chosenAddressId);
        String message = String.format("Начать рабочий день на %s?", chosenAddressShortName);
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton agreeIkb =
                new InlineKeyboardButton("Да")
                        .callbackData(AGREE_TO_START_SHIFT_CALLBACK + chosenAddressId);
        InlineKeyboardButton disagreeIkb =
                new InlineKeyboardButton("Нет")
                        .callbackData(DISAGREE_TO_START_SHIFT_CALLBACK);
        ikbMarkup.addRow(agreeIkb, disagreeIkb);

        bot.execute(new SendMessage(chatId, message).replyMarkup(ikbMarkup));
        return chosenAddressShortName;
    }

    void showAddressList(List<Address> availableAddressList, Long chatId) {
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        for (Address address : availableAddressList) {
            InlineKeyboardButton ikb =
                    new InlineKeyboardButton(address.getShortName())
                            .callbackData(CHOOSE_ADDRESS_CALLBACK + address.getId().toString());
            ikbMarkup.addRow(ikb);
        }
        bot.execute(
                new SendMessage(chatId, CHOOSE_ADDRESS_MSG)
                        .replyMarkup(ikbMarkup)
        );
    }

    void createButtonEndingShift(Update update) {
        Long chatId = update.callbackQuery().from().id();
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton ikbButton =
                new InlineKeyboardButton("Закончить рабочий день")
                        .callbackData(READY_TO_END_SHIFT_CALLBACK);
        ikbMarkup.addRow(ikbButton);
        bot.execute(new SendMessage(chatId, "Нажмите для окончания рабочего дня")
                .replyMarkup(ikbMarkup));
    }

    void createTypeChooserToEndShift(Update update){
        Long chatId;
        if (update.callbackQuery()!=null) {
            chatId = update.callbackQuery().from().id();
        }
        else {
            chatId = update.message().chat().id();
        }
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton ikbButtonPlanned =
                new InlineKeyboardButton("Плановое")
                        .callbackData(ShiftEndTypeEnum.PLANNED.getDescription());
        InlineKeyboardButton ikbButtonUnplanned =
                new InlineKeyboardButton("Неплановое")
                        .callbackData(ShiftEndTypeEnum.UNPLANNED.getDescription());

        ikbMarkup.addRow(ikbButtonPlanned);
        ikbMarkup.addRow(ikbButtonUnplanned);

        bot.execute(new SendMessage(chatId,CHOOSE_SHIFT_END_TYPE).replyMarkup(ikbMarkup));
    }

    void createOfferToUploadPhoto(Update update){
        Long chatId = update.callbackQuery().from().id();
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton ikButtonSendPhoto =
                new InlineKeyboardButton("Да")
                        .callbackData(SEND_PHOTO_YES);
        InlineKeyboardButton ikButtonNotSending =
                new InlineKeyboardButton("Нет")
                        .callbackData(SEND_PHOTO_NO);
        ikbMarkup.addRow(ikButtonSendPhoto);
        ikbMarkup.addRow(ikButtonNotSending);
        bot.execute(new SendMessage(chatId,ASK_FOR_PHOTO).replyMarkup(ikbMarkup));
    }

    boolean checkIfValidPhotoMsg(Message freshMsg, Integer id, Class<?> clazz){
        boolean hasPhotoInMsg = freshMsg.photo() !=null && freshMsg.photo().length>0;
        boolean entityReadyToSendPhoto = tgQueries.checkEntityReadyForPhoto(clazz,id);
        if (hasPhotoInMsg && entityReadyToSendPhoto){
            log.info("Сообщение с фото от {} {} корректно, можно его обработать",clazz.getName(),id);
            return true;
        }
        else {
            log.warn("Получено сообщение от {} {} с фотографиями, но чат не готов",
                    clazz.getName(),id);
            return false;
        }
    }

    void sendPhotoToQueue(Update update, Message photoMsg,Integer id, Class<?> clazz){
        //Получаем одну фотографию в разных размерах
        PhotoSize[] photoArr = photoMsg.photo();
        //Получаем самый крупный размер
        PhotoSize biggestSize =
                Arrays.stream(photoArr)
                        .max(Comparator.comparingLong(PhotoSize::fileSize)).orElseThrow();
        boolean isFirstPhotoInQueue = tgQueries.setShiftFirstPhotoReceived(clazz,id);
        //Если сохраняем самое первое фото этой смены, то отправляем кнопку работнику на завершение
        if (isFirstPhotoInQueue){
            createTypeChooserToEndShift(update);
        }
        Shift currentShift = tgQueries.findShiftByEntityId(id,clazz);
        PhotoShiftIdRecord record =
                new PhotoShiftIdRecord(biggestSize.fileId(),currentShift.getId(),
                        isFirstPhotoInQueue, bot.getToken());
        //Передаем фото в очередь rabbitmq
        rabbitMQMessageProducer.publish(record,internalExchange,internalTgRoutingKey);
    }

    void setReadyToReceivePhoto(Update update,Integer id, Class<?> clazz){
        tgQueries.setEntityReadyToSendPhoto(clazz,id);
        Long chatId = update.callbackQuery().from().id();
        bot.execute(new SendMessage(chatId,BOT_READY_FOR_PHOTO));
    }

    public String countTotalHours(LocalDateTime start, LocalDateTime finish){
        Duration duration = Duration.between(start,finish);
        float totalMinutes = (float) duration.toMinutes();
        DecimalFormat df = new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        float totalHours = totalMinutes/60;
        return df.format(totalHours);
    }

    public String formatFinishedWorkerShift(Shift shift){
        String startDateTime = tgDateTimeFormatter.format(shift.getStartDateTime());
        String endDateTime = tgDateTimeFormatter.format(shift.getEndDateTime());

        return "ℹ Рабочий день окончен "+"\n"+
                "✔" + shift.getShortInfo() +"\n"+
                "⏰ Начало в "+startDateTime+"\n"+
                "⏰ Конец в "+endDateTime;
    }

    public void sendOutErrToAdmins(String err){
        Optional<List<Admin>> authorizedAdmins = adminTgQueries.findAuthorizedAdmins();
        if (authorizedAdmins.isPresent()){
            for (Admin admin : authorizedAdmins.get()){
                bot.execute(new SendMessage(admin.getChatId(),err));
            }
        }
    }

}
