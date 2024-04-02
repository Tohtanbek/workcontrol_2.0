package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.equip.AssignEquipDto;
import com.tosDev.enums.AssignmentStatus;
import com.tosDev.spring.jpa.entity.main_tables.*;
import com.tosDev.spring.jpa.repository.main_tables.*;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AssignmentEquipService {

    private final AssignmentEquipRepository assignEquipRepo;
    private final EquipmentRepository equipmentRepository;
    private final ShiftRepository shiftRepository;
    private final ExpenseRepository expenseRepository;

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
                    .worker(assignmentEquip.getWorker().getName())
                    .workerId(assignmentEquip.getWorker().getId())
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
        log.info("Загружена таблица выдачи оборудования");
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
                AssignmentEquip dao = assignEquipRepo.findById(assignEquipDto.getId()).orElseThrow();
                AssignmentStatus updatedStatus =
                        Arrays.stream(AssignmentStatus.values())
                                .filter(status ->
                                        status.getDescription().equals(assignEquipDto.getStatus()))
                                .findFirst().orElseThrow();
                LocalDateTime endDateTime =
                        LocalDateTime.ofInstant(Instant.now(),ZoneId.of("UTC"));
                //Запуск логики расчета по оборудованию, если переключили статус на "отработано"
                try {
                    dao =
                            countEquipmentAssignmentResult(dao,assignEquipDto,updatedStatus,endDateTime);
                } catch (Exception e) {
                    log.error("Ошибка при выставлении счетов за траты оборудования",e);
                    return ResponseEntity.internalServerError().build();
                }

                dao.setNaming(assignEquipDto.getNaming());
                dao.setStatus(updatedStatus);
                assignEquipRepo.save(dao);
            }
        } catch (NoSuchElementException e) {
        log.error("При обновлении записей выдачи оборудования" +
                " по одному из id не было найдено записи в бд",e);
        return ResponseEntity.internalServerError().build();
    }
        log.info("Записи {} обновлены",assignEquipDtos);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод рассчитывает траты оборудования на каждом объекте из списка смен
     * @param dao dao выдачи оборудования
     * @param dto dto возможно отработанного оборудования
     * @param updatedStatus enum статуса из dto
     * @param endDateTime Рассчитанная дата получения dto по гринвичу
     * @return entity выданного оборудования на сохранение обновления
     */
    private AssignmentEquip countEquipmentAssignmentResult(AssignmentEquip dao,
                                                AssignEquipDto dto,
                                                AssignmentStatus updatedStatus,
                                                LocalDateTime endDateTime){
        //Запуск логики расчета по оборудованию, если переключили статус на "отработано"
        if (dao.getStatus()!=updatedStatus){
            if (updatedStatus.equals(AssignmentStatus.READY)){
                LocalDateTime startDateTime =
                        LocalDateTime.parse(dto.getStartDateTime(),formatter);
                //Ищем смены между датой выдачи и отработкой у этого работника
                List<Shift> shiftsOfRange =
                        shiftRepository.findAllByWorkerIdAndDataRange(
                                dto.getWorkerId(),
                                startDateTime,
                                endDateTime);

                //Рассчитываем долю каждого объекта в отработке оборудования
                //ключ - адрес, значение - кол-во часов работы на адресе работника за промежуток вр.
                Map<Address,Float> addressTotalHoursMap = new HashMap<>();
                for (Shift shift : shiftsOfRange){
                    Address shiftAddress = shift.getAddress();
                    //Если уже записывали часы работы на этом адресе, то добавляем к ним еще
                    if (addressTotalHoursMap.containsKey(shiftAddress)){
                        Float totalHoursOfShiftsOnAddress = addressTotalHoursMap.get(shiftAddress);
                        totalHoursOfShiftsOnAddress+=shift.getTotalHours();
                        addressTotalHoursMap.put(shiftAddress,totalHoursOfShiftsOnAddress);
                    }
                    //Иначе добавляем новый адрес в мапу и записываем первые часы работы
                    else {
                        addressTotalHoursMap.put(shiftAddress,shift.getTotalHours());
                    }
                }
                //Рассчитываем % каждого адреса в отработке оборудования за промежуток времени
                Map<Address,Float> addressPercentMap = new HashMap<>();
                Float totalExpense = dto.getTotal();
                for (Map.Entry<Address,Float> entry : addressTotalHoursMap.entrySet()){
                    Float addressShare = (entry.getValue()*100)/totalExpense;
                    addressPercentMap.put(entry.getKey(),addressShare);
                }
                //Рассчитываем сколько денег было потрачено на каждом адресе
                Map<Address,Float> addressMoneyShareMap = new HashMap<>();
                for (Map.Entry<Address,Float> entry : addressPercentMap.entrySet()){
                    Float moneyShare = (totalExpense*entry.getValue())/100;
                    addressMoneyShareMap.put(entry.getKey(),moneyShare);
                }
                //Формируем ExpenseDao на каждый адрес в мапе
                List<Expense> expenseList = new ArrayList<>();
                for (Map.Entry<Address,Float> entry : addressMoneyShareMap.entrySet()){
                    String description = String.format("""
                            Трата оборудования на объекте %s
                            с %s по %s работником %s
                            """,entry.getKey().getShortName(),
                            startDateTime.format(formatter),
                            endDateTime.format(formatter),
                            dto.getWorker());
                    Expense expense = Expense
                            .builder()
                            .shortInfo(description)
                            .address(entry.getKey())
                            .totalSum(entry.getValue())
                            .type("оборудование")
                            .status("Выставлен")
                            .dateTime(endDateTime)
                            .worker(dao.getWorker())
                            .build();
                    expenseList.add(expense);
                }
                //Сохраняем траты в таблицу expense
                expenseRepository.saveAll(expenseList);

                //Ставим dao конечную дату, чтобы вернуть с ней на сохранение изменений
                dao.setEndDateTime(endDateTime);
                return dao;
            }
        }
        //Если это не обновление отработки оборудования, то просто вернем пустую entity
        return dao;
    }
}

