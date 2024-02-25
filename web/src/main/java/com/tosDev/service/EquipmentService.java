package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.EquipDto;
import com.tosDev.dto.EquipTypeDto;
import com.tosDev.dto.ResponsibleDto;
import com.tosDev.jpa.entity.Equipment;
import com.tosDev.jpa.entity.EquipmentType;
import com.tosDev.jpa.entity.Responsible;
import com.tosDev.jpa.repository.EquipmentRepository;
import com.tosDev.jpa.repository.EquipmentTypeRepository;
import com.tosDev.jpa.repository.ResponsibleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final ResponsibleRepository responsibleRepository;
    private final ObjectMapper objectMapper;

    public String mapAllEquipmentToJson(){
        List<Equipment> equipmentList =
                Optional.of(equipmentRepository.findAll()).orElse(Collections.emptyList());
        String allEquipStr;
        try {
            allEquipStr = objectMapper.writeValueAsString(equipmentList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы оборудования в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица оборудования");
        return allEquipStr;
    }
    public String mapAllEquipTypesToJson(){
        List<EquipmentType> equipmentTypes =
                Optional.of(equipmentTypeRepository.findAll()).orElse(Collections.emptyList());
        String allEquipTypesStr;
        try {
            allEquipTypesStr = objectMapper.writeValueAsString(equipmentTypes);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы типов оборудования в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица типов оборудования");
        return allEquipTypesStr;
    }

    public String[] mapAllEquipTypesToArray(){
        List<EquipmentType> equipmentTypes = Optional.of(equipmentTypeRepository.findAll())
                .orElse(Collections.emptyList());
        return equipmentTypes
                .stream().map(EquipmentType::getName).toArray(String[]::new);
    }

    public ResponseEntity<Void> updateEquipTypes(List<EquipTypeDto> equipTypeDtos){
        try {
            //Фильтруем одинаковые записи в дто
            HashSet<EquipTypeDto> uniqueEquipTypes = new HashSet<>(equipTypeDtos);
            //Создаем коллекцию новых актуальных дао
            List<EquipmentType> uploadedEquipTypes =
                    uniqueEquipTypes.stream()
                            .map(dto -> EquipmentType.builder().name(dto.getName()).build())
                            .toList();
            equipmentTypeRepository.deleteAll();
            equipmentTypeRepository.saveAll(uploadedEquipTypes);
        }catch (Exception e){
            log.error("Не удалось загрузить в бд новый тип оборудования");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Загрузили новое оборудование {}",equipTypeDtos);
        return ResponseEntity.status(200).build();
    }

    public ResponseEntity<Void> mapAndSaveFreshEquip(EquipDto equipDto){
        try {
            //todo:обработать нормально optional
            EquipmentType chosenType = equipmentTypeRepository.findByName(equipDto.getType()).get();
            Responsible responsible = responsibleRepository.findByName(equipDto.getResponsible()).get();
            equipmentRepository.save(Equipment
                    .builder()
                    .naming(equipDto.getNaming())
                    .type(chosenType)
                    .responsible(responsible)
                    .amount(equipDto.getAmount())
                    .price4each(equipDto.getPriceForEach())
                    .unit(equipDto.getUnit())
                    .link(equipDto.getLink())
                    .source(equipDto.getSource())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового оборудования в бд{}",equipDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новое оборудование {}", equipDto);
        return ResponseEntity.ok().build();
    }
}
