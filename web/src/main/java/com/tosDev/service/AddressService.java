package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.AddressDto;
import com.tosDev.jpa.entity.Address;
import com.tosDev.jpa.entity.Equipment;
import com.tosDev.jpa.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;
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
}
