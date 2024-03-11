package com.tosDev.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.dto.BrigadierSmallDto;
import com.tosDev.web.dto.ResponsibleDtoWithBrigs;
import com.tosDev.web.jpa.entity.Brigadier;
import com.tosDev.web.jpa.entity.Responsible;
import com.tosDev.web.jpa.entity.ResponsibleBrigadier;
import com.tosDev.web.jpa.repository.BrigadierRepository;
import com.tosDev.web.jpa.repository.ResponsibleBrigadierRepository;
import com.tosDev.web.jpa.repository.ResponsibleRepository;
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
    private final ResponsibleBrigadierRepository responsibleBrigadierRepository;
    private final BrigadierRepository brigadierRepository;

    public String mapAllResposnibleToJson(){
        String allResponsibleStr;
        try {
            List<Responsible> responsibleList =
                    Optional.of(responsibleRepository.findAll()).orElse(Collections.emptyList());
            List<ResponsibleDtoWithBrigs> responsibleDtos =
                    responsibleList.stream()
                            .map(dao -> ResponsibleDtoWithBrigs
                                    .builder()
                                    .id(dao.getId())
                                    .name(dao.getName())
                                    .phoneNumber(dao.getPhoneNumber())
                                    .brigadiers(
                                            dao.getResponsibleBrigadierList().stream()
                                                    .map(rb -> rb.getBrigadier().getName()).toList()
                                    )
                                    .build()).toList();
            allResponsibleStr = objectMapper.writeValueAsString(responsibleDtos);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы ответственных в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица ответственных");
        return allResponsibleStr;
    }

    public ResponseEntity<Void> mapAndSaveFreshResponsible(ResponsibleDtoWithBrigs responsibleDto){
        try {
            Responsible freshResponsible = responsibleRepository.save(Responsible
                    .builder()
                    .name(responsibleDto.getName())
                    .phoneNumber(responsibleDto.getPhoneNumber())
                    .build());
            for (String brigName : responsibleDto.getBrigadiers()){
                Brigadier brigadierDao = brigadierRepository.findByName(brigName).orElseThrow();
                responsibleBrigadierRepository.save(ResponsibleBrigadier
                        .builder()
                        .responsible(freshResponsible)
                        .brigadier(brigadierDao)
                        .build());
            }

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

    public ResponseEntity<Void> saveResponsibleUpdate(List<ResponsibleDtoWithBrigs> responsibleDtos){
        try {
            for (ResponsibleDtoWithBrigs responsibleDto : responsibleDtos) {
                Responsible responsibleDao =
                        responsibleRepository.findById(responsibleDto.getId()).orElseThrow();
                responsibleDao.setName(responsibleDto.getName());
                responsibleDao.setPhoneNumber(responsibleDto.getPhoneNumber());

                responsibleRepository.save(responsibleDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного супервайзера по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",responsibleDtos);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateBrigadiersOnSupervisor(
            Integer id, List<BrigadierSmallDto> brigadierDtos) {
        try {
            //Получаем дао адреса
            Responsible superDao = responsibleRepository.findById(id).orElseThrow();
            //Получаем список айди актуальных бригадиров на адресе
            List<Integer> actualBrigadiersIds =
                    brigadierDtos.stream()
                            .map(BrigadierSmallDto::getId)
                            .toList();
            //Получаем старый список айди бригадиров на адресе
            List<Integer> oldBrigadierIds = superDao.getResponsibleBrigadierList().stream()
                    .map(entity -> entity.getBrigadier().getId())
                    .toList();
            //Получаем список айди бригадиров, которых больше не должно быть на адресе
            List<Integer> notExistingIdsAnymore =
                    oldBrigadierIds.stream()
                            .filter(oldId -> !actualBrigadiersIds.contains(oldId))
                            .toList();

            //Удаляем сущности brigadierAddress, которые больше не актуальны
            Iterator<ResponsibleBrigadier> iterator = superDao.getResponsibleBrigadierList().iterator();
            while (iterator.hasNext()){
                ResponsibleBrigadier responsibleBrigadier = iterator.next();
                if (notExistingIdsAnymore.contains(responsibleBrigadier.getBrigadier().getId())) {
                    iterator.remove();
                }
            }
            //save or update сущностей brigadierAddress. Сначала ищем, была ли такая пара уже
            //если была, то не сохраняем, если не было, то сохраняем новую
            for (BrigadierSmallDto brigadierSmallDto : brigadierDtos){
                Brigadier brigadierDao =
                        brigadierRepository.findById(brigadierSmallDto.getId()).orElseThrow();
                Optional<ResponsibleBrigadier> existedResponsibleBrigadier =
                        responsibleBrigadierRepository.findByResponsibleIdAndBrigadierId(id,brigadierSmallDto.getId());
                if (existedResponsibleBrigadier.isEmpty()) {
                    responsibleBrigadierRepository.save(ResponsibleBrigadier
                            .builder()
                            .brigadier(brigadierDao)
                            .responsible(superDao)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Не удалось сохранить изменения бригадиров на супервайзоре с id {}",id);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Успешно сменили бригадиров на супервайзоре с id {}",id);
        return ResponseEntity.ok().build();
    }
}
