package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.tg.db.TgQueries;
import com.tosDev.web.jpa.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import static com.tosDev.tg.bot.enums.ShiftEndTypeEnum.PLANNED;
import static com.tosDev.tg.bot.enums.ShiftEndTypeEnum.UNPLANNED;

@Slf4j
@Component
public class BrigadierWorkerCommonTgMethods extends CommonTgMethods {

    private final String CHOOSE_ADDRESS_CALLBACK = "chosen_address_id_";
    private final String AGREE_TO_START_SHIFT_CALLBACK = "agree_to_start_shift_on_";
    private final String DISAGREE_TO_START_SHIFT_CALLBACK = "disagree_to_start_shift";
    private final String CHOOSE_ADDRESS_MSG = "Выберите адрес из списка доступных:";
    private final String READY_TO_END_SHIFT_CALLBACK = "ready_to_end_shift";
    private final String CHOOSE_SHIFT_END_TYPE = """
            Выберите тип окончания рабочего дня. После нажатия на кнопку
            рабочий день будет окончен и сохранен в системе.
            """;

    private final TelegramBot bot;
    private final TgQueries tgQueries;

    @Autowired
    public BrigadierWorkerCommonTgMethods(TelegramBot bot, TgQueries tgQueries) {
        super(bot);
        this.bot = bot;
        this.tgQueries = tgQueries;
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
        Long chatId = update.callbackQuery().from().id();
        InlineKeyboardMarkup ikbMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton ikbButtonPlanned =
                new InlineKeyboardButton("Плановое")
                        .callbackData(PLANNED.getDescription());
        InlineKeyboardButton ikbButtonUnplanned =
                new InlineKeyboardButton("Неплановое")
                        .callbackData(UNPLANNED.getDescription());

        ikbMarkup.addRow(ikbButtonPlanned);
        ikbMarkup.addRow(ikbButtonUnplanned);

        bot.execute(new SendMessage(chatId,CHOOSE_SHIFT_END_TYPE).replyMarkup(ikbMarkup));
    }

    public String countTotalHours(LocalDateTime start, LocalDateTime finish){
        Duration duration = Duration.between(start,finish);
        float totalMinutes = (float) duration.toMinutes();
        DecimalFormat df = new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        float totalHours = totalMinutes/60;
        return df.format(totalHours);
    }

}
