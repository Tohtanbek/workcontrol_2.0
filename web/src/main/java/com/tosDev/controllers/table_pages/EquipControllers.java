package com.tosDev.controllers.table_pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.EquipDto;
import com.tosDev.dto.EquipTypeDto;
import com.tosDev.jpa.repository.EquipmentRepository;
import com.tosDev.jpa.repository.EquipmentTypeRepository;
import com.tosDev.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@Controller
@RequestMapping("/tables/equip")
@RequiredArgsConstructor
public class EquipControllers {

    private final EquipmentService equipmentService;

    /**
     * Загружает страницу с оборудованием
     */
    @GetMapping("/main")
    String showEquipPage(){
        log.info("Загружена страница оборудования");
        return "equipment_tab";
    }

    /**
     * Загружает из бд таблицу equipments и преобразует в json строку
     * @return json таблицы "оборудование"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllEquipRows(){
        return equipmentService.mapAllEquipmentToJson();
    }

    /**
     * Загружает из бд таблицу equipment_type и преобразует в json строку
     * @return json таблицы типов оборудования
     */
    @GetMapping("/equip_types")
    @ResponseBody
    String equipTypes(){
        return equipmentService.mapAllEquipTypesToJson();
    }

    /**
     *
     * @return Массив строк типов оборудования
     */
    @GetMapping("/equip_types_array")
    @ResponseBody
    String[] equipTypesArray(){
        return equipmentService.mapAllEquipTypesToArray();
    }

    /**
     * Контроллер отвечает за обновление типов оборудования через таблицу.
     * @param equipTypeDtos - обновленные типы
     * @return статус Ok(200)
     */
    @PostMapping("/add_equip_type")
    ResponseEntity<Void> addEquipType(@RequestBody List<EquipTypeDto> equipTypeDtos){
        return equipmentService.updateEquipTypes(equipTypeDtos);
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице оборудование
     * @param equipDto dto нового оборудования
     * @return Response entity with http status
     */
    @PostMapping("/add_equip_row")
    ResponseEntity<Void> addEquipRow(@RequestBody EquipDto equipDto){
        return equipmentService.mapAndSaveFreshEquip(equipDto);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id оборудования на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_equip_rows")
    ResponseEntity<Void> deleteEquipRows(@RequestBody Long[] ids){
        return equipmentService.deleteEquipRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param equipDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_equip_rows")
    ResponseEntity<Void> update_equip_rows(@RequestBody List<EquipDto> equipDtos){
        return equipmentService.saveEquipUpdate(equipDtos);
    }


}
