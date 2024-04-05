package com.tosDev.tg.db;

import com.tosDev.web.spring.jpa.entity.main_tables.Admin;
import com.tosDev.web.spring.jpa.repository.main_tables.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminTgQueries {

    private final AdminRepository adminRepository;

    public Optional<List<Admin>> findAuthorizedAdmins(){
        return adminRepository.findByChatIdIsNotNull();
    }
}
