package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.BrigadierSmallDto;
import com.tosDev.jpa.entity.*;
import com.tosDev.jpa.repository.AddressRepository;
import com.tosDev.jpa.repository.BrigadierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BrigadierService {

    private final BrigadierRepository brigadierRepository;
    private final ObjectMapper objectMapper;
    private final AddressRepository addressRepository;

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
}
