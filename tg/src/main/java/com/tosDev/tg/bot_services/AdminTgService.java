package com.tosDev.tg.bot_services;

import com.tosDev.web.jpa.entity.Admin;
import com.tosDev.web.jpa.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTgService {
    private final AdminRepository adminRepository;

    @Transactional
    public Admin linkChatIdToExistingAdmin(Long adminPhoneNumber,Long chatId){
        Admin admin =
                adminRepository.findByPhoneNumber(adminPhoneNumber).orElseThrow();
        admin.setChatId(chatId);
        adminRepository.save(admin);
        log.info("Привязали новый чат {} пользователя {} в роли админа",
                chatId,adminPhoneNumber);
        return admin;
    }
}
