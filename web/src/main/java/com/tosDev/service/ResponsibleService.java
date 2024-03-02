package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.ResponsibleDto;
import com.tosDev.jpa.entity.Equipment;
import com.tosDev.jpa.entity.EquipmentType;
import com.tosDev.jpa.entity.Responsible;
import com.tosDev.jpa.repository.ResponsibleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResponsibleService {

    private final ObjectMapper objectMapper;
    private final ResponsibleRepository responsibleRepository;

    public String mapAllResposnibleToJson(){
        List<Responsible> responsibleList =
                Optional.of(responsibleRepository.findAll()).orElse(Collections.emptyList());
        String allResponsibleStr;
        try {
            allResponsibleStr = objectMapper.writeValueAsString(responsibleList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы ответственных в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица ответственных");
        return allResponsibleStr;
    }

    public ResponseEntity<Void> mapAndSaveFreshResponsible(ResponsibleDto responsibleDto){
        try {
            responsibleRepository.save(Responsible
                    .builder()
                    .name(responsibleDto.getName())
                    .phoneNumber(responsibleDto.getPhoneNumber())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового супервайзера в бд{}",responsibleDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили нового ответственного {}", responsibleDto);
        return ResponseEntity.ok().build();
    }

    public String[] mapAllResponsibleToArray(){
        List<Responsible> responsibleList = Optional.of(responsibleRepository.findAll())
                .orElse(Collections.emptyList());
        return responsibleList
                .stream().map(Responsible::getName).toArray(String[]::new);
    }

    public ResponseEntity<Void> deleteResponsibleRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(responsibleRepository::findById)
                    .forEach(optional -> responsibleRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного ответственного по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи ответственных удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> loadJsonSuperVisorBrigadiers() {
        List<Responsible> list = responsibleRepository.findAll();
        //Получаем map с ключом - id ответственного, value - список его бригадиров
        Map<Integer, List<String>> map = list.stream()
                .collect(Collectors.toMap(Responsible::getId,
                        responsible -> responsible.getResponsibleBrigadierList().stream()
                                .map(responsibleBrigadier ->
                                        responsibleBrigadier.getBrigadier().getName()).toList()));
        String result;
        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Не удалось спарсить json бригадиров каждого супервайзера");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Отправили в фронтенд связанные с супервайзерами бригадиры");
        return ResponseEntity.ok().body(result);
    }
}
