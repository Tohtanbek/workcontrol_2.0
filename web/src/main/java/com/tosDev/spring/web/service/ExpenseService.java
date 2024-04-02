package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.tableDto.ExpenseDto;
import com.tosDev.spring.jpa.entity.main_tables.Expense;
import com.tosDev.spring.jpa.repository.main_tables.AddressRepository;
import com.tosDev.spring.jpa.repository.main_tables.ExpenseRepository;
import com.tosDev.spring.jpa.repository.main_tables.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ExpenseService {

    private final ObjectMapper objectMapper;
    private final ExpenseRepository expenseRepository;
    private final AddressRepository addressRepository;
    private final WorkerRepository workerRepository;

    @Qualifier("basicDateTimeFormatter")
    private final DateTimeFormatter basicDateTimeFormatter;

    public ResponseEntity<String> mapAllExpenseToJson(){
        String allExpenseStr;
        try {
            List<Expense> expenseList =
                    Optional.of(expenseRepository.findAll()).orElse(Collections.emptyList());
            List<ExpenseDto> expenseDtos =
                    expenseList.stream()
                            .map(dao -> ExpenseDto.builder()
                                    .id(dao.getId())
                                    .shortInfo(dao.getShortInfo())
                                    .totalSum(dao.getTotalSum())
                                    .type(dao.getType())
                                    .status(dao.getStatus())
                                    .dateTime(dao.getDateTime().format(basicDateTimeFormatter))
                                    .address(Optional.ofNullable(dao
                                            .getAddress()).isPresent()?dao.getAddress().getShortName():null)
                                    .worker(Optional.ofNullable(dao
                                            .getWorker()).isPresent()?dao.getWorker().getName():null)
                                    .shift(Optional.ofNullable(dao
                                            .getShift()).isPresent()?dao.getShift().getShortInfo():null)
                                    .zone(Optional.ofNullable(dao
                                            .getAddress()).isPresent()?dao.getAddress().getZone():null)
                                    .build()).toList();
            allExpenseStr = objectMapper.writeValueAsString(expenseDtos);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы расходов в json произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица расходов");
        return ResponseEntity.ok(allExpenseStr);
    }

    public ResponseEntity<Void> deleteExpenseRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(expenseRepository::findById)
                    .forEach(optional -> expenseRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранной расхода по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveExpenseUpdate(List<ExpenseDto> expenseDtos){
        try {
            for (ExpenseDto expenseDto : expenseDtos) {
                Expense expenseDao = expenseRepository.findById(expenseDto.getId()).orElseThrow();

                expenseDao.setShortInfo(expenseDto.getShortInfo());
                expenseDao.setStatus(expenseDto.getStatus());
                expenseDao.setType(expenseDto.getType());

                expenseRepository.save(expenseDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного расхода по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",expenseDtos);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> mapAndSaveFreshExpense(ExpenseDto expenseDto){
        Expense freshExpense = new Expense();
        try {
            freshExpense.setShortInfo(expenseDto.getShortInfo());
            freshExpense.setStatus(expenseDto.getStatus());
            freshExpense.setTotalSum(expenseDto.getTotalSum());
            freshExpense.setType(expenseDto.getType());
            freshExpense.setDateTime(LocalDateTime.parse(expenseDto.getDateTime()));
            if (!expenseDto.getAddress().equals("default")){
                freshExpense.setAddress(addressRepository.findByShortName(expenseDto.getAddress())
                        .orElseThrow());
            }
            if (!expenseDto.getWorker().equals("default")){
                freshExpense.setWorker(workerRepository.findByName(expenseDto.getWorker())
                        .orElseThrow());
            }
            expenseRepository.save(freshExpense);

        } catch (Exception e) {
            log.error("Ошибка при сохранении новой траты в бд{}",expenseDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новую трату {}", freshExpense);
        return ResponseEntity.ok().build();
    }
}
