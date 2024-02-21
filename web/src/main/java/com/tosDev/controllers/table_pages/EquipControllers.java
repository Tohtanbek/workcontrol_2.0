package com.tosDev.controllers.table_pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tosDev.Dto.EquipTypeDto;
import com.tosDev.jpa.entity.Equipment;
import com.tosDev.jpa.entity.EquipmentType;
import com.tosDev.jpa.repository.EquipmentRepository;
import com.tosDev.jpa.repository.EquipmentTypeRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/equip")
@RequiredArgsConstructor
public class EquipControllers {

    private final ObjectMapper objectMapper;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    /**
     * Загружает страницу с оборудованием
     */
    @GetMapping("/main")
    String showEquipPage(){
        log.info("testt Загружена страница оборудования");
        System.out.println("Кириллица");
        return "equipment_tab";
    }

    /**
     * Загружает из бд таблицу equipments и преобразует в json строку
     * @return json таблицы "оборудование"
     */
    @GetMapping("/main_table")
    @ResponseBody
    String equipTableRows(){
        List<Equipment> equipments = equipmentRepository.findAll();
        String allEquipStr;
        try {
            allEquipStr = objectMapper.writeValueAsString(equipments);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы оборудования в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица оборудования");
        return allEquipStr;
    }
    @GetMapping("/equip_types")
    @ResponseBody
    String equipTypes(){
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

    /**
     *
     * @return Массив строк типов оборудования
     */
    @GetMapping("/equip_types_array")
    @ResponseBody
    String[] equipTypesArray(){
        List<EquipmentType> equipmentTypes = Optional.of(equipmentTypeRepository.findAll())
                .orElse(Collections.emptyList());
        return equipmentTypes
                .stream().map(EquipmentType::getName).toArray(String[]::new);
    }

    @PostMapping("/add_equip_type")
    @Transactional
    ResponseEntity<Void> addEquipType(@RequestBody List<EquipTypeDto> equipTypeDtos){
        try {
            //todo:сделать код безопаснее (без deleteAll)
            equipmentTypeRepository.deleteAll();
            for (EquipTypeDto equipTypeDto : equipTypeDtos){
                equipmentTypeRepository.save(
                        EquipmentType.builder()
                                .name(equipTypeDto.getName())
                                .build());
            }
        }catch (Exception e){
            log.error("Не удалось загрузить в бд новый тип оборудования");
            return ResponseEntity.status(500).build();
        }
        log.info("Загрузили новое оборудование {}",equipTypeDtos);
        return ResponseEntity.status(200).build();
    }

}
