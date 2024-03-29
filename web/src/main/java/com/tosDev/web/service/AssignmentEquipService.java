package com.tosDev.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.dto.equip.AssignEquipDto;
import com.tosDev.web.enums.AssignmentStatus;
import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AssignmentEquipService {

    private final AssignmentEquipRepository assignEquipRepo;
    private final EquipmentRepository equipmentRepository;

    private final WorkerRepository workerRepository;
    private final ObjectMapper objectMapper;
    @Qualifier("basicDateTimeFormatter")
    private final DateTimeFormatter formatter;
    private final DecimalFormat decimalFormat;

    public ResponseEntity<String> mapAllAssignEquipToJson(){
        List<AssignmentEquip> assignmentEquipList =
                Optional.of(assignEquipRepo.findAll()).orElse(Collections.emptyList());
        List<AssignEquipDto> assignEquipDtoList = new ArrayList<>();
        for (AssignmentEquip assignmentEquip : assignmentEquipList){
            Optional<LocalDateTime> endDateTimeOpt =
                    Optional.ofNullable(assignmentEquip.getEndDateTime());
            String endDate = "";
            if (endDateTimeOpt.isPresent()){
                endDate = formatter.format(endDateTimeOpt.get());
            }
            assignEquipDtoList.add(
                    AssignEquipDto.builder()
                    .id(assignmentEquip.getId())
                    .equipId(assignmentEquip.getEquipment().getId())
                    .equipment(assignmentEquip.getEquipment().getNaming())
                    .naming(assignmentEquip.getNaming())
                    .status(assignmentEquip.getStatus().getDescription())
                    .total(assignmentEquip.getTotal())
                    .amount(assignmentEquip.getAmount())
                    .startDateTime(assignmentEquip.getStartDateTime().format(formatter))
                    .endDateTime(endDate)
                    .build());
        }
        String allAssignEquipStr;
        try {
            allAssignEquipStr = objectMapper.writeValueAsString(assignEquipDtoList);
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
            Worker worker = workerRepository.findById(assignEquipDto.getWorkerId()).orElseThrow();
            Equipment equip =
                    equipmentRepository.findById(assignEquipDto.getEquipId()).orElseThrow();
            Float total = equip.getPrice4each()*assignEquipDto.getAmount();
            total = Float.parseFloat(decimalFormat.format(total));
            LocalDateTime startDateTime =
                    LocalDateTime.ofInstant(Instant.now(),ZoneId.of("UTC"));
            freshAssignEquip = AssignmentEquip
                    .builder()
                    .naming(assignEquipDto.getNaming())
                    .worker(worker)
                    .equipment(equip)
                    .amount(assignEquipDto.getAmount())
                    .total(total)
                    .startDateTime(startDateTime)
                    .status(AssignmentStatus.AT_WORK)
                    .build();
            assignEquipRepo.save(freshAssignEquip);

            //Обновляем данные в equip об оставшемся оборудовании после выдачи
            equip.setAmountLeft(equip.getAmountLeft()-assignEquipDto.getAmount());
            equip.setTotalLeft(equip.getTotalLeft()-total);
            equip.setGivenAmount(equip.getGivenAmount()+assignEquipDto.getAmount());
            equip.setGivenTotal(equip.getGivenTotal()+total);
            equipmentRepository.save(equip);
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
