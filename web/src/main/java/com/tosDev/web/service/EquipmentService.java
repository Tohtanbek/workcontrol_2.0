package com.tosDev.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.dto.equip.EquipTypeDto;
import com.tosDev.web.jpa.entity.Equipment;
import com.tosDev.web.jpa.entity.EquipmentType;
import com.tosDev.web.jpa.entity.Responsible;
import com.tosDev.web.jpa.repository.EquipmentTypeRepository;
import com.tosDev.web.jpa.repository.ResponsibleRepository;
import com.tosDev.web.dto.equip.EquipDto;
import com.tosDev.web.jpa.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final ObjectMapper objectMapper;
    @Qualifier("kebabFormatter")
    private final DateTimeFormatter kebabFormatter;
    private final DecimalFormat decimalFormat;

    public ResponseEntity<String> mapAllEquipmentToJson(){
        List<Equipment> equipmentList = equipmentRepository.findAll();
        try {
            List<EquipDto> equipDtoList = new ArrayList<>();
            for (Equipment equipDao : equipmentList){
                String typeStr =
                        Optional.ofNullable(equipDao.getType().getName()).orElse("");
                Optional<LocalDate> maybeDate = Optional.ofNullable(equipDao.getSupplyDate());
                String dateStr = maybeDate.map(kebabFormatter::format).orElse("");
                equipDtoList.add(EquipDto
                                .builder()
                                .id(equipDao.getId())
                                .naming(equipDao.getNaming())
                                .type(typeStr)
                                .amount(equipDao.getAmount())
                                .total(equipDao.getTotal())
                                .priceForEach(equipDao.getPrice4each())
                                .totalLeft(equipDao.getTotalLeft())
                                .amountLeft(equipDao.getAmountLeft())
                                .unit(equipDao.getUnit())
                                .givenAmount(equipDao.getGivenAmount())
                                .givenTotal(equipDao.getGivenTotal())
                                .link(equipDao.getLink())
                                .source(equipDao.getSource())
                                .supplyDate(dateStr)
                                .build());
            }
            String allEquipStr;
            allEquipStr = objectMapper.writeValueAsString(equipDtoList);
            log.info("Загружена таблица оборудования");
            return ResponseEntity.ok(allEquipStr);
        } catch (Exception e) {
            log.error("При загрузке json таблицы оборудования произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
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
            Set<EquipTypeDto> uniqueEquipTypes = new HashSet<>(equipTypeDtos);
            //Фильтруем пустые
            uniqueEquipTypes = uniqueEquipTypes.stream()
                    .filter(type -> Optional.ofNullable(type.getName()).isPresent())
                    .collect(Collectors.toSet());

            //Создаем из новых сущности для бд
            List<EquipmentType> rowEquipmentTypes =
                    uniqueEquipTypes.stream().map(dto -> EquipmentType.
                            builder()
                            .name(dto.getName())
                            .build()).toList();
            //Удаляем из старой бд те, которых нет в новой коллекции
            for (EquipmentType oldEquipmentType : equipmentTypeRepository.findAll()){
                if (!rowEquipmentTypes.contains(oldEquipmentType)){
                    equipmentTypeRepository.delete(oldEquipmentType);
                }
            }
            //Убираем из новой коллекции уже существующие объекты
            List<EquipmentType> equipmentTypesReady = new ArrayList<>();
            for (EquipmentType equipmentType : rowEquipmentTypes){
                if (!equipmentTypeRepository.findAll().contains(equipmentType)){
                    equipmentTypesReady.add(equipmentType);
                }
            }

            //Save or Update новой коллекции в старую бд
            equipmentTypeRepository.saveAll(equipmentTypesReady);

        }catch (Exception e){
            log.error("Не удалось загрузить в бд новый тип оборудования");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Загрузили новые типы оборудования оборудование {}",equipTypeDtos);
        return ResponseEntity.status(200).build();
    }

    public ResponseEntity<Void> mapAndSaveFreshEquip(EquipDto equipDto){
        Equipment freshEquip;
        try {
            EquipmentType chosenType =
                    equipmentTypeRepository.findByName(equipDto.getType()).orElseThrow();
            LocalDate localDate = equipDto.getSupplyDate().isBlank()?
                    null
                    :
                    LocalDate.parse(equipDto.getSupplyDate(), kebabFormatter);
            //Рассчитываем цену за единицу
            Float priceForEach = Float.parseFloat(
                    decimalFormat.format(equipDto.getTotal()/equipDto.getAmount()));

            freshEquip = Equipment
                    .builder()
                    .naming(equipDto.getNaming())
                    .type(chosenType)
                    .amount(equipDto.getAmount())
                    .amountLeft(equipDto.getAmount())
                    .givenAmount(0.0F)
                    .total(equipDto.getTotal())
                    .totalLeft(equipDto.getTotal())
                    .givenTotal(0.0F)
                    .price4each(priceForEach)
                    .unit(equipDto.getUnit())
                    .link(equipDto.getLink())
                    .source(equipDto.getSource())
                    .supplyDate(localDate)
                    .build();
            equipmentRepository.save(freshEquip);
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового оборудования в бд{}",equipDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новое оборудование {}", freshEquip);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteEquipRows(Long[] ids){
        try {
            Arrays.stream(ids)
                    .map(equipmentRepository::findById)
                    .forEach(optional -> equipmentRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного оборудования по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveEquipUpdate(List<EquipDto> equipDtos){
        try {
            for (EquipDto equipDto : equipDtos) {
                Equipment equipDao = equipmentRepository.findById(equipDto.getId()).orElseThrow();
                EquipmentType chosenType =
                        equipmentTypeRepository.findByName(equipDto.getType()).orElseThrow();
                Optional<String> optSupplyDate = Optional.ofNullable(equipDto.getSupplyDate());

                //При изменении количества нужно увеличить поля left и поля total
                Float updatedAmountLeft =
                        equipDao.getAmountLeft()+(equipDto.getAmount()-equipDao.getAmount());
                Float updatedTotal = Float.parseFloat(
                        decimalFormat.format(equipDto.getPriceForEach()*equipDto.getAmount()));
                Float updatedTotalLeft =
                        equipDao.getTotalLeft()+(updatedTotal - equipDao.getTotal());

                equipDao.setNaming(equipDto.getNaming());
                equipDao.setType(chosenType);
                equipDao.setAmount(equipDto.getAmount());
                equipDao.setAmountLeft(updatedAmountLeft);
                equipDao.setTotal(updatedTotal);
                equipDao.setTotalLeft(updatedTotalLeft);
                equipDao.setUnit(equipDto.getUnit());
                equipDao.setLink(equipDto.getLink());
                equipDao.setSource(equipDto.getSource());
                optSupplyDate.ifPresentOrElse(
                        dateStr -> equipDao.setSupplyDate(LocalDate.parse(dateStr,kebabFormatter)),
                        ()->equipDao.setSupplyDate(null));

                equipmentRepository.save(equipDao);

            }
        } catch (NoSuchElementException e) {
        log.error("При изменении выбранного оборудования по одному из id не было найдено записи в бд");
        e.printStackTrace();
    }
        log.info("Записи {} обновлены",equipDtos);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> mapSingleEquipById(Long equipId) {
        try {
            Equipment equipment = equipmentRepository.findById(equipId).orElseThrow();
            EquipDto equipDto = EquipDto
                    .builder()
                    .naming(equipment.getNaming())
                    .amountLeft(equipment.getAmountLeft())
                    .build();
            String equipDtoStr = objectMapper.writeValueAsString(equipDto);
            log.info("Загрузили единицу оборудования {} из бд",equipDtoStr);
            return ResponseEntity.ok(equipDtoStr);
        } catch (Exception e) {
            log.error("Ошибка при загрузке из бд единицы оборудования id: {}",equipId);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
