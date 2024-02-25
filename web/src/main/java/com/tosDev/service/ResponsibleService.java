package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.ResponsibleDto;
import com.tosDev.jpa.entity.Equipment;
import com.tosDev.jpa.entity.EquipmentType;
import com.tosDev.jpa.entity.Responsible;
import com.tosDev.jpa.repository.ResponsibleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResponsibleService {

    private final ObjectMapper objectMapper;
    private final ResponsibleRepository responsibleRepository;

    public String mapAllResposnibleToJson(){
        List<Responsible> responsibleList =
                Optional.of(responsibleRepository.findAll()).orElse(Collections.emptyList());
        String allResponsibleStr;
        try {
            allResponsibleStr = objectMapper.writeValueAsString(responsibleList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы ответственных в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица ответственных");
        return allResponsibleStr;
    }

    public ResponseEntity<Void> mapAndSaveFreshResponsible(ResponsibleDto responsibleDto){
        try {
            responsibleRepository.save(Responsible
                    .builder()
                    .name(responsibleDto.getName())
                    .phoneNumber(responsibleDto.getPhoneNumber())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового супервайзера в бд{}",responsibleDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили нового ответственного {}", responsibleDto);
        return ResponseEntity.ok().build();
    }

    public String[] mapAllResponsibleToArray(){
        List<Responsible> responsibleList = Optional.of(responsibleRepository.findAll())
                .orElse(Collections.emptyList());
        return responsibleList
                .stream().map(Responsible::getName).toArray(String[]::new);
    }
}
