package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.tableDto.BrigadierDtoWithSuper;
import com.tosDev.dto.tableDto.BrigadierSmallDto;
import com.tosDev.spring.jpa.entity.main_tables.*;
import com.tosDev.spring.jpa.repository.main_tables.AddressRepository;
import com.tosDev.spring.jpa.repository.main_tables.BrigadierRepository;
import com.tosDev.spring.jpa.repository.main_tables.ResponsibleRepository;
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
public class BrigadierService {

    private final BrigadierRepository brigadierRepository;
    private final ObjectMapper objectMapper;
    private final AddressRepository addressRepository;
    private final ResponsibleRepository responsibleRepository;

    public ResponseEntity<String> brigadiersToJsonMap(){
        List<Brigadier> brigadiers =
                Optional.of(brigadierRepository.findAll()).orElse(Collections.emptyList());
        Map<Integer,String> brigadierMap = brigadiers
                .stream()
                .collect(Collectors.toMap(Brigadier::getId,Brigadier::getName));
        String brigadierMapStr;
        try {
            brigadierMapStr = objectMapper.writeValueAsString(brigadierMap);
        } catch (JsonProcessingException e) {
            log.error("Не удалось передать мапу бригадиров для фронтенда");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена мапа бригадиров из бд");
        return ResponseEntity.ok(brigadierMapStr);
    }

    /**
     *  Метод получает весь список бригадиров в бд, но отбрасывает те, которые уже есть
     *  у адреса с переданным айди
     * @param id - id адреса, который выбрал пользователь, чтобы менять бригадиров
     * @return json отфильтрованного списка бригадиров
     */
    public ResponseEntity<String> mapBrigadiersToShortJsonWithoutChosen(Integer id){
        Address chosenAddress = addressRepository.findById(id).orElseThrow();
        List<Integer> chosenAddressBrigadierIds = chosenAddress.getBrigadierAddressList()
                .stream()
                .map(entity -> entity.getBrigadier().getId())
                .toList();
        List<Brigadier> brigadierDaos =
                Optional.of(brigadierRepository.findAll()).orElse(Collections.emptyList());
        String dtoStr;
        try {
            List<BrigadierSmallDto> brigadierSmallDtoList =
                    brigadierDaos.stream()
                            .filter(brigadier -> !chosenAddressBrigadierIds.contains(brigadier.getId()) )
                            .map(dao -> BrigadierSmallDto.builder()
                                    .id(dao.getId())
                                    .name(dao.getName())
                                    .phoneNumber(dao.getPhoneNumber())
                                    .build())
                            .toList();
            dtoStr = objectMapper.writeValueAsString(brigadierSmallDtoList);
        } catch (Exception e) {
            log.error("Ошибка при загрузке таблицы бригадиров");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(dtoStr);
    }

    public ResponseEntity<String> loadBrigadierDtosByAddressId(Integer id){
        String jsonStr;
        try {
            List<BrigadierSmallDto> brigadierSmallDtoList = addressRepository.findById(id).orElseThrow()
                    .getBrigadierAddressList().stream()
                    .map(BrigadierAddress::getBrigadier)
                    .map(brigadierDao -> BrigadierSmallDto.builder()
                            .id(brigadierDao.getId())
                            .name(brigadierDao.getName())
                            .phoneNumber(brigadierDao.getPhoneNumber())
                            .build())
                    .toList();
            jsonStr = objectMapper.writeValueAsString(brigadierSmallDtoList);
        } catch (Exception e) {
            log.error("Не удалось загрузить json списка дто бригадиров по id адреса");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(jsonStr);
    }

    public String mapAllBrigadiersToJson(){
        String allBrigadiersStr;
        try {
            List<Brigadier> brigadierList =
                    Optional.of(brigadierRepository.findAll()).orElse(Collections.emptyList());
            List<BrigadierDtoWithSuper> brigadierSmallDtoList = brigadierList.stream()
                    .map(dao -> BrigadierDtoWithSuper.builder()
                            .id(dao.getId())
                            .name(dao.getName())
                            .phoneNumber(dao.getPhoneNumber())
                            .supervisors(
                                    dao.getResponsibleBrigadierList().stream()
                                            .map(rb -> rb.getResponsible().getName())
                                            .toList()
                            )
                            .isHourly(dao.isHourly())
                            .wageRate(dao.getWageRate())
                            .incomeRate(dao.getIncomeRate())
                            .build()).toList();
            allBrigadiersStr = objectMapper.writeValueAsString(brigadierSmallDtoList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы бригадиров в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица бригадиров");
        return allBrigadiersStr;
    }

    public ResponseEntity<Void> mapAndSaveFreshBrigadier(BrigadierSmallDto brigadierDto){
        try {
            brigadierRepository.save(Brigadier
                    .builder()
                    .name(brigadierDto.getName())
                    .phoneNumber(brigadierDto.getPhoneNumber())
                    .wageRate(brigadierDto.getWageRate())
                    .incomeRate(brigadierDto.getIncomeRate())
                    .isHourly(brigadierDto.isHourly())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового бригадира в бд{}",brigadierDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили нового бригадира {}", brigadierDto);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteBrigadierRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(brigadierRepository::findById)
                    .forEach(optional -> brigadierRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного бригадира по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи бригадиров удалены по айди: {}", ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> loadBrigadierDtosBySuperId(Integer id){
        String jsonStr;
        try {
            List<BrigadierSmallDto> brigadierSmallDtoList = responsibleRepository.findById(id).orElseThrow()
                    .getResponsibleBrigadierList().stream()
                    .map(ResponsibleBrigadier::getBrigadier)
                    .map(brigadierDao -> BrigadierSmallDto.builder()
                            .id(brigadierDao.getId())
                            .name(brigadierDao.getName())
                            .phoneNumber(brigadierDao.getPhoneNumber())
                            .build())
                    .toList();
            jsonStr = objectMapper.writeValueAsString(brigadierSmallDtoList);
        } catch (Exception e) {
            log.error("Не удалось загрузить json списка дто бригадиров по id супервайзера");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(jsonStr);
    }

    /**
     *  Метод получает весь список бригадиров в бд, но отбрасывает те, которые уже есть
     *  у супервайзера с переданным айди
     * @param id - id супервайзера, который выбрал пользователь, чтобы менять бригадиров
     * @return json отфильтрованного списка бригадиров
     */
    public ResponseEntity<String> mapBrigadiersWithoutChosenForSuper(Integer id){
        Responsible chosenResponsible = responsibleRepository.findById(id).orElseThrow();
        List<Integer> chosenSuperBrigadiers = chosenResponsible.getResponsibleBrigadierList()
                .stream()
                .map(entity -> entity.getBrigadier().getId())
                .toList();
        List<Brigadier> brigadierDaos =
                Optional.of(brigadierRepository.findAll()).orElse(Collections.emptyList());
        String dtoStr;
        try {
            List<BrigadierSmallDto> brigadierSmallDtoList =
                    brigadierDaos.stream()
                            .filter(brigadier -> !chosenSuperBrigadiers.contains(brigadier.getId()) )
                            .map(dao -> BrigadierSmallDto.builder()
                                    .id(dao.getId())
                                    .name(dao.getName())
                                    .phoneNumber(dao.getPhoneNumber())
                                    .build())
                            .toList();
            dtoStr = objectMapper.writeValueAsString(brigadierSmallDtoList);
        } catch (Exception e) {
            log.error("Ошибка при загрузке таблицы бригадиров");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(dtoStr);
    }

    public ResponseEntity<Void> saveBrigadierUpdate(List<BrigadierDtoWithSuper> brigadierDtos){
        try {
            for (BrigadierDtoWithSuper brigadierDtoWithSuper : brigadierDtos) {
                Brigadier brigadierDao =
                        brigadierRepository.findById(brigadierDtoWithSuper.getId()).orElseThrow();
                brigadierDao.setName(brigadierDtoWithSuper.getName());
                brigadierDao.setPhoneNumber(brigadierDtoWithSuper.getPhoneNumber());
                brigadierDao.setHourly(brigadierDtoWithSuper.isHourly());
                brigadierDao.setWageRate(brigadierDtoWithSuper.getWageRate());
                brigadierDao.setIncomeRate(brigadierDtoWithSuper.getIncomeRate());
                brigadierRepository.save(brigadierDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного бригадира по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",brigadierDtos);
        return ResponseEntity.ok().build();
    }
}
