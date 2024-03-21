package com.tosDev.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.dto.AssignEquipDto;
import com.tosDev.web.dto.EquipDto;
import com.tosDev.web.dto.EquipTypeDto;
import com.tosDev.web.enums.AssignmentStatus;
import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AssignmentEquipService {

    private final AssignmentEquipRepository assignEquipRepo;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    private final WorkerRepository workerRepository;
    private final ObjectMapper objectMapper;
    @Qualifier("basicDateTimeFormatter")
    private final DateTimeFormatter formatter;

    public ResponseEntity<String> mapAllAssignEquipToJson(){
        List<AssignmentEquip> assignmentEquipList =
                Optional.of(assignEquipRepo.findAll()).orElse(Collections.emptyList());
        String allAssignEquipStr;
        try {
            allAssignEquipStr = objectMapper.writeValueAsString(assignmentEquipList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы выданного оборужования в json произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица выдачт оборудования");
        return ResponseEntity.ok(allAssignEquipStr);
    }


    public ResponseEntity<Void> mapAndSaveFreshAssignEquip(AssignEquipDto assignEquipDto){
        AssignmentEquip freshAssignEquip;
        try {
            Worker worker = workerRepository.findByName(assignEquipDto.getWorker()).orElseThrow();
            Equipment equip =
                    equipmentRepository.findById(assignEquipDto.getEquipId()).orElseThrow();
            LocalDateTime startDateTime =
                    LocalDateTime.parse(assignEquipDto.getStartDateTime(),formatter);
            freshAssignEquip = AssignmentEquip
                    .builder()
                    .id(assignEquipDto.getId())
                    .worker(worker)
                    .equipment(equip)
                    .amount(assignEquipDto.getAmount())
                    .startDateTime(startDateTime)
                    .status(AssignmentStatus.AT_WORK)
                    .build();
            assignEquipRepo.save(freshAssignEquip);
        } catch (Exception e) {
            log.error("Ошибка при сохранении выдачи оборудования в бд{}",assignEquipDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Выдали новое оборудование {}", freshAssignEquip);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteAssignEquipRows(Long[] ids){
        try {
            Arrays.stream(ids)
                    .map(assignEquipRepo::findById)
                    .forEach(optional -> assignEquipRepo.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранной записи выдачи оборудования" +
                    " по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveAssignEquipUpdate(List<AssignEquipDto> assignEquipDtos){
        try {
            for (AssignEquipDto assignEquipDto : assignEquipDtos) {
                AssignmentStatus assignmentStatus =
                        Arrays.stream(AssignmentStatus.values())
                                .filter(status ->
                                        status.getDescription().equals(assignEquipDto.getStatus()))
                                .findFirst().orElseThrow();
                assignEquipRepo.save(AssignmentEquip
                        .builder()
                        .id(assignEquipDto.getId())
                        .status(assignmentStatus)
                        .build());
            }
        } catch (NoSuchElementException e) {
        log.error("При обновлении записей выдачи оборудования" +
                " по одному из id не было найдено записи в бд");
        e.printStackTrace();
    }
        log.info("Записи {} обновлены",assignEquipDtos);
        return ResponseEntity.ok().build();
    }
}
