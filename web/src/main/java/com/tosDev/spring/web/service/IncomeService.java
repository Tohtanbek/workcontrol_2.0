package com.tosDev.spring.web.service;

import com.tosDev.spring.jpa.repository.main_tables.AddressRepository;
import com.tosDev.spring.jpa.repository.main_tables.IncomeRepository;
import com.tosDev.spring.jpa.repository.main_tables.WorkerRepository;
import com.tosDev.dto.tableDto.IncomeDto;
import com.tosDev.spring.jpa.entity.main_tables.Income;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class IncomeService {

    private final ObjectMapper objectMapper;
    private final IncomeRepository incomeRepository;
    private final AddressRepository addressRepository;
    private final WorkerRepository workerRepository;

    @Qualifier("basicDateTimeFormatter")
    private final DateTimeFormatter basicDateTimeFormatter;

    public ResponseEntity<String> mapAllIncomeToJson(){
        String allIncomeStr;
        try {
            List<Income> incomeList =
                    Optional.of(incomeRepository.findAll()).orElse(Collections.emptyList());
            List<IncomeDto> incomeDtos =
                    incomeList.stream()
                            .map(dao -> {
                                IncomeDto dto = IncomeDto.builder()
                                        .id(dao.getId())
                                        .shortInfo(dao.getShortInfo())
                                        .totalSum(dao.getTotalSum())
                                        .type(dao.getType())
                                        .status(dao.getStatus())
                                        .address(Optional.ofNullable(dao
                                                .getAddress()).isPresent() ? dao.getAddress().getShortName() : null)
                                        .worker(Optional.ofNullable(dao
                                                .getWorker()).isPresent() ? dao.getWorker().getName() : null)
                                        .shift(Optional.ofNullable(dao
                                                .getShift()).isPresent() ? dao.getShift().getShortInfo() : null)
                                        .zone(Optional.ofNullable(dao
                                                .getAddress()).isPresent() ? dao.getAddress().getZone() : null)
                                        .build();
                                Optional.ofNullable(dao.getDateTime())
                                        .ifPresent(dt -> dto.setDateTime(dt.format(basicDateTimeFormatter)));
                                return dto;
                            }).toList();
            allIncomeStr = objectMapper.writeValueAsString(incomeDtos);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы доходов в json произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица доходов");
        return ResponseEntity.ok(allIncomeStr);
    }

    public ResponseEntity<Void> deleteIncomeRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(incomeRepository::findById)
                    .forEach(optional -> incomeRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного дохода по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveIncomeUpdate(List<IncomeDto> incomeDtos){
        try {
            for (IncomeDto incomeDto : incomeDtos) {
                Income incomeDao = incomeRepository.findById(incomeDto.getId()).orElseThrow();

                incomeDao.setShortInfo(incomeDto.getShortInfo());
                incomeDao.setStatus(incomeDto.getStatus());
                incomeDao.setType(incomeDto.getType());

                incomeRepository.save(incomeDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного дохода по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",incomeDtos);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> mapAndSaveFreshIncome(IncomeDto incomeDto){
        Income freshIncome = new Income();
        try {
            freshIncome.setShortInfo(incomeDto.getShortInfo());
            freshIncome.setStatus(incomeDto.getStatus());
            freshIncome.setTotalSum(incomeDto.getTotalSum());
            freshIncome.setType(incomeDto.getType());
            freshIncome.setDateTime(LocalDateTime.parse(incomeDto.getDateTime()));
            if (!incomeDto.getAddress().equals("default")){
                freshIncome.setAddress(addressRepository.findByShortName(incomeDto.getAddress())
                        .orElseThrow());
            }
            if (!incomeDto.getWorker().equals("default")){
                freshIncome.setWorker(workerRepository.findByName(incomeDto.getWorker())
                        .orElseThrow());
            }
            incomeRepository.save(freshIncome);

        } catch (Exception e) {
            log.error("Ошибка при сохранении нового дохода в бд{}",incomeDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новый доход в бд {}", freshIncome);
        return ResponseEntity.ok().build();
    }
}

