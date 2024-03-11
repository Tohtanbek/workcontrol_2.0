package com.tosDev.tg.bot_services;

import com.tosDev.web.jpa.entity.Brigadier;
import com.tosDev.web.jpa.entity.Worker;
import com.tosDev.web.jpa.repository.BrigadierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrigadierTgService {

    private final BrigadierRepository brigadierRepository;

    @Transactional
    public Brigadier linkChatIdToExistingBrigadier(Long brigadierPhoneNumber, Long chatId){
        Brigadier brigadier =
                brigadierRepository.findByPhoneNumber(brigadierPhoneNumber).orElseThrow();
        brigadier.setChatId(chatId);
        brigadierRepository.save(brigadier);
        log.info("Привязали новый чат {} пользователя {} в роли бригадира",
                chatId,brigadierPhoneNumber);
        return brigadier;
    }
}
