package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.AddressDto;
import com.tosDev.dto.EquipDto;
import com.tosDev.jpa.entity.*;
import com.tosDev.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;
    private final BrigadierRepository brigadierRepository;
    private final WorkerRepository workerRepository;
    private final WorkerAddressRepository workerAddressRepository;
    private final BrigadierAddressRepository brigadierAddressRepository;
    private final ObjectMapper objectMapper;
    public ResponseEntity<String> mapAllAddressToJson() {
        List<Address> addressList = Optional.of(addressRepository.findAll()).orElse(Collections.emptyList());
        List<AddressDto> dtoList = new ArrayList<>();
        for (Address dao : addressList){
            List<String> brigadierNames =
                    dao.getBrigadierAddressList()
                            .stream()
                            .map(entity -> entity.getBrigadier().getName())
                            .toList();
            List<String> workerNames =
                    dao.getWorkerAddressList()
                            .stream()
                            .map(entity -> entity.getWorker().getName())
                            .toList();
            dtoList.add(
                    AddressDto.builder()
                            .id(dao.getId())
                            .shortName(dao.getShortName())
                            .fullName(dao.getFullName())
                            .brigadiers(brigadierNames)
                            .workers(workerNames)
                            .build()
            );
        }
        String allAddressStr;
        try {
            allAddressStr = objectMapper.writeValueAsString(dtoList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы адресов в json произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица адресов");
        return ResponseEntity.ok(allAddressStr);
    }

    public ResponseEntity<Void> mapAndSaveFreshAddress(AddressDto addressDto){
        Address freshAddress = new Address();
        try {
            freshAddress.setShortName(addressDto.getShortName());
            freshAddress.setFullName(addressDto.getFullName());
            addressRepository.save(freshAddress);
            for (String brigadierName: addressDto.getBrigadiers()){
                Brigadier brigadier = brigadierRepository.findByName(brigadierName).orElseThrow();
                brigadierAddressRepository.save(BrigadierAddress
                        .builder()
                        .brigadier(brigadier)
                        .address(freshAddress)
                        .build());
            }
            for (String workerName: addressDto.getWorkers()){
                Worker worker = workerRepository.findByName(workerName).orElseThrow();
                workerAddressRepository.save(WorkerAddress
                        .builder()
                        .worker(worker)
                        .address(freshAddress)
                        .build());
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового адреса в бд{}",addressDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новый адрес {}", freshAddress);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveAddressUpdate(List<AddressDto> addressDtos){
        try {
            for (AddressDto addressDto : addressDtos) {
                Address addressDao = addressRepository.findById(addressDto.getId()).orElseThrow();
                addressDao.setFullName(addressDto.getFullName());
                addressDao.setShortName(addressDto.getShortName());

                addressRepository.save(addressDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного адреса по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",addressDtos);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteAddressRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(addressRepository::findById)
                    .forEach(optional -> addressRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного адреса по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }
}
