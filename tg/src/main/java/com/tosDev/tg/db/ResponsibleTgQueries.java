package com.tosDev.tg.db;

import com.pengrad.telegrambot.TelegramBot;
import com.tosDev.tg.bot_services.BrigadierWorkerCommonTgMethods;
import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.tosDev.tg.bot.enums.ShiftStatusEnum.*;

@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ResponsibleTgQueries {

    private final ResponsibleRepository responsibleRepository;
    public Responsible linkChatIdToExistingSupervisor(Long responsiblePhoneNumber, Long chatId){
        Responsible responsible =
                responsibleRepository.findByPhoneNumber(responsiblePhoneNumber).orElseThrow();
        responsible.setChatId(chatId);
        responsibleRepository.save(responsible);
        log.info("Привязали новый чат {} пользователя {} в роли супервайзера",
                chatId,responsiblePhoneNumber);
        return responsible;
    }

    public List<Brigadier> findLinkedBrigs(Integer responsibleId){
        try {
            Responsible responsible = responsibleRepository.findById(responsibleId).orElseThrow();
            return responsible.getResponsibleBrigadierList()
                    .stream().map(ResponsibleBrigadier::getBrigadier).toList();
        } catch (NoSuchElementException e) {
            log.error("Не найден ответственный с id {} при отправке сообщения от него",
                    responsibleId);
            return Collections.emptyList();
        }
    }

}
