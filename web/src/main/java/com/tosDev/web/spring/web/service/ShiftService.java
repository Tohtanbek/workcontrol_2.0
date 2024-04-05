package com.tosDev.web.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.spring.jpa.repository.main_tables.JobRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.ShiftRepository;
import com.tosDev.web.dto.tableDto.ShiftDto;
import com.tosDev.web.spring.jpa.entity.main_tables.Job;
import com.tosDev.web.spring.jpa.entity.main_tables.Shift;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    @Qualifier("basicDateTimeFormatter")
    private final DateTimeFormatter basicDateTimeFormatter;


    public ResponseEntity<String> mapAllShiftToJson(){
        String allShiftStr;
        try {
            List<Shift> shiftList =
                    Optional.of(shiftRepository.findAll()).orElse(Collections.emptyList());
            List<ShiftDto> shiftDtos =
                    shiftList.stream()
                            .map(dao -> {
                                ShiftDto dto = ShiftDto.builder()
                                        .id(dao.getId())
                                        .shortInfo(dao.getShortInfo())
                                        .job(dao.getJob().getName())
                                        .address(dao.getAddress().getShortName())
                                        .status(dao.getStatus().getDescription())
                                        .zone(dao.getAddress().getZone())
                                        .startDateTime(dao.getStartDateTime().format(basicDateTimeFormatter))
                                        .totalHours(dao.getTotalHours())
                                        .worker(dao.getWorker().getName())
                                        .build();
                                Optional.ofNullable(dao.getBrigadier())
                                        .ifPresent(brig -> dto.setBrigadier(brig.getName()));
                                Optional.ofNullable(dao.getEndDateTime())
                                        .ifPresent(dt -> dto.setEndDateTime(dt.format(basicDateTimeFormatter)));
                                return dto;
                            }).toList();

            allShiftStr = objectMapper.writeValueAsString(shiftDtos);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы смен в json произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица смен");
        return ResponseEntity.ok(allShiftStr);
    }

    public ResponseEntity<Void> deleteShiftRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(shiftRepository::findById)
                    .forEach(optional -> shiftRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранной смены по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveShiftUpdate(List<ShiftDto> shiftDtos){
        try {
            for (ShiftDto shiftDto : shiftDtos) {
                Shift shiftDao = shiftRepository.findById(shiftDto.getId()).orElseThrow();

                shiftDao.setShortInfo(shiftDto.getShortInfo());
                shiftDao.setJob(checkAndReturnJob(shiftDto.getJob()));

                shiftRepository.save(shiftDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранной смены по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",shiftDtos);
        return ResponseEntity.ok().build();
    }

    private Job checkAndReturnJob(String jobName){
        Optional<Job> existingJob = jobRepository.findByName(jobName);
        //Если такой профессии еще не использовали, то сохраняем ее в бд и возвращаем
        if (existingJob.isEmpty()){
            return jobRepository.save(
                    Job.builder().name(jobName).build());
        }
        //Иначе берем уже существующую с таким же названием и возвращаем для новой сущности
        else {
            return existingJob.get();
        }
    }
}
