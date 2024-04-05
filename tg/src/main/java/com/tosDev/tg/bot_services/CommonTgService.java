package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.tg.db.BrigadierTgQueries;
import com.tosDev.tg.db.ResponsibleTgQueries;
import com.tosDev.tg.db.WorkerTgQueries;
import com.tosDev.web.spring.jpa.entity.main_tables.Admin;
import com.tosDev.web.spring.jpa.entity.main_tables.Brigadier;
import com.tosDev.web.spring.jpa.entity.main_tables.Responsible;
import com.tosDev.web.spring.jpa.entity.main_tables.Worker;
import com.tosDev.tg.db.TgQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonTgService {

    private final TelegramBot bot;
    private final TgQueries tgQueries;
    private final WorkerTgQueries workerTgQueries;
    private final BrigadierTgQueries brigadierTgQueries;

    private final ResponsibleTgQueries responsibleTgQueries;
    private final AdminTgService adminTgService;
    private final WorkerTgService workerTgService;
    private final BrigadierTgService brigadierTgService;
    private final ResponsibleTgService responsibleTgService;

    private final String GREETING_MESSAGE = """
            Здравствуйте. Вам нужно авторизоваться, для этого нажмите на кнопку ниже.
            """;

    /**
     * Отправка кнопки для получения номера телефона неавторизованного пользователя
     */

    public void requestPhoneNumber(Update update){
        Long chatId = update.message().chat().id();
        KeyboardButton keyboardButton = new KeyboardButton("Проверить номер телефона");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup replyKeyboardMarkup =
                new ReplyKeyboardMarkup(new KeyboardButton[][]{{keyboardButton}});
        replyKeyboardMarkup.oneTimeKeyboard(true).resizeKeyboard(true);
        bot.execute(new SendMessage(chatId,GREETING_MESSAGE).replyMarkup(replyKeyboardMarkup));
        log.info("{} - Направили данному chatId запрос на авторизацию по номеру",chatId);
    }

    /**
     * Метод срабатывает, когда пользователь отправляет свой номер для авторизации
     * Метод вызывает поиск по базе данных полученного номера, в случае успеха привязывает id
     * и вызывает логику нового пользователя по его роли.
     */
    public void authorizeNewChatAndRunLogic(Update update){
        Long phoneNumber = Long.valueOf(update.message().contact().phoneNumber());
        Long chatId = update.message().chat().id();

        Optional<Object> optionalExistingUser = tgQueries.findByPhoneNumber(phoneNumber);
        if (optionalExistingUser.isPresent()) {
            String className = optionalExistingUser.get().getClass().getName();
            switch (className) {
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Admin") -> {
                    log.info("Пользователь найден в бд в роли админа");
                    Admin updatedAdmin = adminTgService.linkChatIdToExistingAdmin(phoneNumber,chatId);
                  adminTgService.startAdminLogic(update);
                }
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Worker") -> {
                    log.info("Пользователь найден в бд в роли работника");
                    Worker linkedWorker =
                            workerTgQueries.linkChatIdToExistingWorker(phoneNumber,chatId);
                  workerTgService.startWorkerLogic(update,linkedWorker.getId());
                }
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Brigadier") -> {
                    log.info("Пользователь найден в бд в роли бригадира");
                    Brigadier linkedBrigadier =
                            brigadierTgQueries.linkChatIdToExistingBrigadier(phoneNumber,chatId);
                  brigadierTgService.startBrigadierLogic(update,linkedBrigadier.getId());
                }
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Responsible") -> {
                    log.info("Пользователь найден в бд в роли супервайзера");
                    Responsible linkedResponsible =
                            responsibleTgQueries.linkChatIdToExistingSupervisor(phoneNumber,chatId);
                    responsibleTgService.startResponsibleLogic(update, linkedResponsible.getId());
                }
                default -> {
                    log.error("update от неправильного класса");
                    throw new RuntimeException();
                }
            }
        }
        else {
            log.warn("Попытка зайти от неавторизованного пользователя с номером {}",phoneNumber);
        }
    }

    /**
     * Вызывает проверку авторизации пользователя по chatId
     * Затем вызывает логику для роли авторизованного пользователя
     */
    public void checkAuthorityAndRunLogic(Update update) {
        Long chatId = update.message()==null?
                update.callbackQuery().from().id()
                :
                update.message().chat().id();

        Optional<Object> optionalAuthorizedUser = tgQueries.findByChatId(chatId);
        if (optionalAuthorizedUser.isPresent()) {
            Object someAuthorizedUser = optionalAuthorizedUser.get();
            String className = optionalAuthorizedUser.get().getClass().getName();
            switch (className) {
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Admin") -> {
                    log.info("update от админа");
//                    adminTgService.startAdminLogic((Admin)someAuthorizedUser);
                }
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Worker") -> {
                    log.info("update от работника");
                    Worker worker = (Worker) someAuthorizedUser;
                    workerTgService.startWorkerLogic(update,worker.getId());
                }
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Brigadier") -> {
                    log.info("update от бригадира");
                    Brigadier brigadier = (Brigadier) someAuthorizedUser;
                    brigadierTgService.startBrigadierLogic(update, brigadier.getId());
                }
                case ("com.tosDev.web.spring.jpa.entity.main_tables.Responsible") -> {
                    log.info("update от супервайзера");
                    Responsible responsible = (Responsible) someAuthorizedUser;
                    responsibleTgService.startResponsibleLogic(update,responsible.getId());
                }
                default -> {
                    log.error("update от неправильного класса");
                    throw new RuntimeException();
                }
            }
        }
        else {
            requestPhoneNumber(update);
        }
    }

}
