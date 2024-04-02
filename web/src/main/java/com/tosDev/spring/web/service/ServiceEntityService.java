package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.client_pages.ShortServiceDto;
import com.tosDev.dto.tableDto.AddressDto;
import com.tosDev.dto.tableDto.ServiceDto;
import com.tosDev.enums.ServiceCategory;
import com.tosDev.spring.jpa.entity.client_orders.Service;
import com.tosDev.spring.jpa.entity.main_tables.*;
import com.tosDev.spring.jpa.repository.client_orders.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
@Slf4j
@Transactional
public class ServiceEntityService {

    private final ServiceRepository serviceRepository;

    private final ObjectMapper objectMapper;


    public ResponseEntity<String> mapAllServiceToJson(){
        String allServiceStr;
        try {
            List<Service> serviceList =
                    Optional.of(serviceRepository.findAll()).orElse(Collections.emptyList());
            List<ServiceDto> serviceDtoList = new ArrayList<>();
            for (Service dao : serviceList){
                ServiceDto freshDto = ServiceDto.builder()
                        .id(dao.getId())
                        .name(dao.getName())
                        .category(dao.getCategory().name())
                        .price(dao.getPrice())
                        .minimalPrice(dao.getMinimalPrice())
                        .build();
                Optional.ofNullable(dao.getPromoCode()).ifPresent(freshDto::setPromoCode);
                Optional.ofNullable(dao.getPromoCodeDiscount()).ifPresent(freshDto::setPromoCodeDiscount);
                serviceDtoList.add(freshDto);
            }
            allServiceStr = objectMapper.writeValueAsString(serviceDtoList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы услуг в json произошла ошибка",e);
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица услуг");
        return ResponseEntity.ok(allServiceStr);
    }

    public ResponseEntity<Void> deleteServiceRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(serviceRepository::findById)
                    .forEach(optional -> serviceRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранной услуги по одному из id не было найдено записи в бд",e);
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveServiceUpdate(List<ServiceDto> serviceDtos){
        try {
            for (ServiceDto serviceDto : serviceDtos) {
                Service serviceDao = serviceRepository.findById(serviceDto.getId()).orElseThrow();

                serviceDao.setName(serviceDto.getName());
                serviceDao.setCategory(ServiceCategory.valueOf(serviceDto.getCategory()));
                serviceDao.setPrice(serviceDto.getPrice());
                serviceDao.setMinimalPrice(serviceDto.getMinimalPrice());
                serviceDao.setPromoCodeDiscount(serviceDto.getPromoCodeDiscount());
                if (!serviceDto.getPromoCode().isBlank()){
                    serviceDao.setPromoCode(serviceDto.getPromoCode());
                }

                serviceRepository.save(serviceDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранной услуги по одному из id не было найдено записи в бд",e);
        }
        log.info("Записи {} обновлены",serviceDtos);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> mapAndSaveFreshService(ServiceDto serviceDto){
        Service freshService = new Service();
        try {
            freshService.setName(serviceDto.getName());
            freshService.setCategory(ServiceCategory.valueOf(serviceDto.getCategory()));
            freshService.setPrice(serviceDto.getPrice());
            freshService.setMinimalPrice(
                    Optional.ofNullable(serviceDto.getMinimalPrice()).orElse(0F)
            );
            if (!serviceDto.getPromoCode().isBlank()){
                freshService.setPromoCode(serviceDto.getPromoCode());
            }
            Optional.ofNullable(serviceDto.getPromoCodeDiscount())
                    .ifPresent(freshService::setPromoCodeDiscount);
            serviceRepository.save(freshService);
        } catch (Exception e) {
            log.error("Ошибка при сохранении новой услуги в бд{}",serviceDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новую услугу {}", freshService);
        return ResponseEntity.ok().build();
    }

    public List<ShortServiceDto> loadAndMapToShorServices(ServiceCategory neededCategory){
        List<ShortServiceDto> serviceDtoList = new ArrayList<>();
        try {
            List<Service> services = serviceRepository.findAllByCategory(neededCategory);
            for (Service dao : services){
                serviceDtoList.add(ShortServiceDto.builder()
                        .id(dao.getId())
                        .name(dao.getName())
                        .price(dao.getPrice())
                        .minimalPrice(dao.getMinimalPrice())
                        .build());
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки списка услуг",e);
        }
        log.info("Успешно загрузили список shortServiceDto");
        return serviceDtoList;
    }
}
