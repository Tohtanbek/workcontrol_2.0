package com.tosDev.tg.bot_services;

import com.tosDev.web.jpa.entity.Brigadier;
import com.tosDev.web.jpa.entity.Responsible;
import com.tosDev.web.jpa.repository.ResponsibleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResponsibleTgService {

    private final ResponsibleRepository responsibleRepository;

    @Transactional
    public Responsible linkChatIdToExistingSupervisor(Long responsiblePhoneNumber, Long chatId){
        Responsible responsible =
                responsibleRepository.findByPhoneNumber(responsiblePhoneNumber).orElseThrow();
        responsible.setChatId(chatId);
        responsibleRepository.save(responsible);
        log.info("Привязали новый чат {} пользователя {} в роли супервайзера",
                chatId,responsiblePhoneNumber);
        return responsible;
    }
}
