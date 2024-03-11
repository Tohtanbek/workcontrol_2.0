package com.tosDev.tg.bot_services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.tosDev.amqp.RabbitMQMessageProducer;
import com.tosDev.tg.db.WorkerTgQueries;
import com.tosDev.web.jpa.entity.Admin;
import com.tosDev.web.jpa.entity.Worker;
import com.tosDev.web.jpa.repository.AdminRepository;
import com.tosDev.web.jpa.repository.BrigadierRepository;
import com.tosDev.web.jpa.repository.ResponsibleRepository;
import com.tosDev.web.jpa.repository.WorkerRepository;
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
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final TgQueries tgQueries;
    private final WorkerTgQueries workerTgQueries;
    private final AdminRepository adminRepository;
    private final WorkerRepository workerRepository;
    private final BrigadierRepository brigadierRepository;
    private final ResponsibleRepository responsibleRepository;
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
                case ("com.tosDev.web.jpa.entity.Admin") -> {
                    log.info("Пользователь найден в бд в роли админа");
                    Admin updatedAdmin = adminTgService.linkChatIdToExistingAdmin(phoneNumber,chatId);
                  adminTgService.startAdminLogic(update);
                }
                case ("com.tosDev.web.jpa.entity.Worker") -> {
                    log.info("Пользователь найден в бд в роли работника");
                    Worker linkedWorker =
                            workerTgQueries.linkChatIdToExistingWorker(phoneNumber,chatId);
                  workerTgService.startWorkerLogic(update,linkedWorker.getId());
                }
                case ("com.tosDev.web.jpa.entity.Brigadier") -> {
                    log.info("Пользователь найден в бд в роли бригадира");
                    brigadierTgService.linkChatIdToExistingBrigadier(phoneNumber,chatId);
//                  brigadierTgService.startBrigadierLogic(worker);
                }
                case ("com.tosDev.web.jpa.entity.Responsible") -> {
                    log.info("Пользователь найден в бд в роли супервайзера");
                    responsibleTgService.linkChatIdToExistingSupervisor(phoneNumber,chatId);
//                  responsibleTgService.startResponsibleLogic(worker);
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
                case ("com.tosDev.web.jpa.entity.Admin") -> {
                    log.info("update от админа");
//                    adminTgService.startAdminLogic((Admin)someAuthorizedUser);
                }
                case ("com.tosDev.web.jpa.entity.Worker") -> {
                    log.info("update от работника");
                    Worker worker = (Worker) someAuthorizedUser;
                    workerTgService.startWorkerLogic(update,worker.getId());
                }
                case ("com.tosDev.web.jpa.entity.Brigadier") -> {
                    log.info("update от бригадира");
//                    continueBrigadierLogic((Brigadier) someAuthorizedUser);
                }
                case ("com.tosDev.web.jpa.entity.Responsible") -> {
                    log.info("update от супервайзера");
//                    continueResponsibleLogic((Responsible) someAuthorizedUser);
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

    public void deletePrevMessage(Update update){
        Long chatId = update.callbackQuery().from().id();
        //todo: когда поправят maybeInaccessibleMessage, избавиться от deprecated
        Integer messageId = update.callbackQuery().message().messageId();
        try {
            bot.execute(new DeleteMessage(chatId,messageId));
        } catch (Exception e) {
            log.warn("Не удалось удалить предыдущее сообщение");
            e.printStackTrace();
        }
        log.info("Удалили предыдущее сообщение у chatId: {}",chatId);
    }
}
