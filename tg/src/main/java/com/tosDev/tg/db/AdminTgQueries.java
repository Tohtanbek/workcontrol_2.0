package com.tosDev.tg.db;

import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.AddressRepository;
import com.tosDev.web.jpa.repository.AdminRepository;
import com.tosDev.web.jpa.repository.BrigadierRepository;
import com.tosDev.web.jpa.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.tosDev.tg.bot.enums.ShiftStatusEnum.AT_WORK;
import static com.tosDev.tg.bot.enums.ShiftStatusEnum.FINISHED;

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
