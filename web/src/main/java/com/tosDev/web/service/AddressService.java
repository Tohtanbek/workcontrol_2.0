package com.tosDev.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.dto.AddressDto;
import com.tosDev.web.dto.BrigadierSmallDto;
import com.tosDev.web.dto.WorkerDto;
import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.*;
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
                            .zone(dao.getZone())
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
            freshAddress.setZone(addressDto.getZone());
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
                addressDao.setZone(addressDto.getZone());

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

    public ResponseEntity<Void> updateBrigadiersOnAddress(
            Integer id, List<BrigadierSmallDto> brigadierDtos) {
        try {
            //Получаем дао адреса
            Address addressDao = addressRepository.findById(id).orElseThrow();
            //Получаем список айди актуальных бригадиров на адресе
            List<Integer> actualBrigadiersIds =
                    brigadierDtos.stream()
                            .map(BrigadierSmallDto::getId)
                            .toList();
            //Получаем старый список айди бригадиров на адресе
            List<Integer> oldBrigadierIds = addressDao.getBrigadierAddressList().stream()
                    .map(entity -> entity.getBrigadier().getId())
                    .toList();
            //Получаем список айди бригадиров, которых больше не должно быть на адресе
            List<Integer> notExistingIdsAnymore =
                    oldBrigadierIds.stream()
                            .filter(oldId -> !actualBrigadiersIds.contains(oldId))
                            .toList();

            //Удаляем сущности brigadierAddress, которые больше не актуальны
            Iterator<BrigadierAddress> iterator = addressDao.getBrigadierAddressList().iterator();
            while (iterator.hasNext()){
                BrigadierAddress brigadierAddress = iterator.next();
                if (notExistingIdsAnymore.contains(brigadierAddress.getBrigadier().getId())) {
                    iterator.remove();
                }
            }
            //save or update сущностей brigadierAddress. Сначала ищем, была ли такая пара уже
            //если была, то не сохраняем, если не было, то сохраняем новую
            for (BrigadierSmallDto brigadierSmallDto : brigadierDtos){
                Brigadier brigadierDao =
                        brigadierRepository.findById(brigadierSmallDto.getId()).orElseThrow();
                Optional<BrigadierAddress> existedBrigadierAddress =
                        brigadierAddressRepository.findByBrigadierIdAndAddressId(brigadierSmallDto.getId(),id);
                if (existedBrigadierAddress.isEmpty()) {
                    brigadierAddressRepository.save(BrigadierAddress
                            .builder()
                            .brigadier(brigadierDao)
                            .address(addressDao)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Не удалось сохранить изменения бригадиров на адресе с id {}",id);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Успешно сменили бригадиров на адресе с id {}",id);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateWorkersOnAddress(
            Integer id, List<WorkerDto> workerDtos) {
        try {
            //Получаем дао адреса
            Address addressDao = addressRepository.findById(id).orElseThrow();
            //Получаем список айди актуальных работников на адресе
            List<Integer> actualWorkerIds =
                    workerDtos.stream()
                            .map(WorkerDto::getId)
                            .toList();
            //Получаем старый список айди работников на адресе
            List<Integer> oldWorkerIds = addressDao.getWorkerAddressList().stream()
                    .map(entity -> entity.getWorker().getId())
                    .toList();
            //Получаем список айди работников, которых больше не должно быть на адресе
            List<Integer> notExistingIdsAnymore =
                    oldWorkerIds.stream()
                            .filter(oldId -> !actualWorkerIds.contains(oldId))
                            .toList();

            //Удаляем сущности workerAddress, которые больше не актуальны
            Iterator<WorkerAddress> iterator = addressDao.getWorkerAddressList().iterator();
            while (iterator.hasNext()){
                WorkerAddress workerAddress = iterator.next();
                if (notExistingIdsAnymore.contains(workerAddress.getWorker().getId())) {
                    iterator.remove();
                }
            }
            //save or update сущностей brigadierAddress. Сначала ищем, была ли такая пара уже
            //если была, то не сохраняем, если не было, то сохраняем новую
            for (WorkerDto workerDto : workerDtos){
                Worker workerDao =
                        workerRepository.findById(workerDto.getId()).orElseThrow();
                Optional<WorkerAddress> existedWorkerAddress =
                        workerAddressRepository.findByWorkerIdAndAddressId(workerDto.getId(),id);
                if (existedWorkerAddress.isEmpty()) {
                    workerAddressRepository.save(WorkerAddress
                            .builder()
                            .worker(workerDao)
                            .address(addressDao)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Не удалось сохранить изменения работников на адресе с id {}",id);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Успешно сменили работников на адресе с id {}",id);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<String> addressToJsonMap(){
        String addressMapStr;
        try {
            List<Address> addresses =
                    Optional.of(addressRepository.findAll()).orElse(Collections.emptyList());
            Map<Integer,String> addressMap = addresses
                    .stream()
                    .collect(Collectors.toMap(Address::getId,Address::getShortName));
            addressMapStr = objectMapper.writeValueAsString(addressMap);
        } catch (JsonProcessingException e) {
            log.error("Не удалось передать мапу бригадиров для фронтенда");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена мапа бригадиров из бд");
        return ResponseEntity.ok(addressMapStr);
    }
}
