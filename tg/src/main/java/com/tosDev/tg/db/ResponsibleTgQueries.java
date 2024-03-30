package com.tosDev.tg.db;

import com.tosDev.spring.jpa.entity.Brigadier;
import com.tosDev.spring.jpa.entity.Responsible;
import com.tosDev.spring.jpa.entity.ResponsibleBrigadier;
import com.tosDev.spring.jpa.repository.ResponsibleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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
