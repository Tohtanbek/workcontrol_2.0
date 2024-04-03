package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.client_pages.*;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    /**
     * Метод для загрузки списка услуг клиенту
     * @param neededCategory - категория (Основные услуги\дополнительные)
     * @return Список услуг выбранной категории
     */
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

    /**
     * Загружает по id основную услугу и список дополнительных
     * @param extraServiceIdArr массив id выбранных доп услуг
     * @param mainServiceDto дто основной услуги с id и выбранной area
     * @return dto для выдачи клиенту страницы-корзины
     */
    public CartDto loadDtoForClientCart(Integer[] extraServiceIdArr,
                                        ChosenMainServiceDto mainServiceDto){

        DecimalFormat decimalFormat = new DecimalFormat("0.00",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        //Получаем extra услуги из бд и мапим в список дто
        List<Service> extraServices =
                serviceRepository.findAllById(List.of(extraServiceIdArr));
        List<ChosenExtraServiceDto> extraServiceDtoList =
                extraServices.stream().map(dao -> ChosenExtraServiceDto
                .builder()
                .id(dao.getId())
                .name(dao.getName())
                .price(decimalFormat.format(dao.getPrice()))
                .build()).toList();

        //Получаем основную услугу из бд
        Service mainService =
                serviceRepository.findById(mainServiceDto.getServiceId()).orElseThrow();

        return CartDto
                .builder()
                .mainServiceId(mainService.getId())
                .mainServiceName(mainService.getName())
                .mainServiceTotal(decimalFormat.format(mainServiceDto.getTotal()))
                .mainServiceArea(mainServiceDto.getArea())
                .extraServiceList(extraServiceDtoList)
                .build();
    }

    /**
     * Проверяет переданный промокод
     * @param promoCode введенный пользователем промокод
     * @return мапу с id сервиса и размером скидки
     */
    public Map<Integer,Integer> checkPromoCode(String promoCode){
        List<Service> servicesWithPromo = serviceRepository.findAllByPromoCode(promoCode);
        Map<Integer,Integer> resultMap = new HashMap<>();
        servicesWithPromo.forEach(dao -> resultMap.put(dao.getId(),dao.getPromoCodeDiscount()));
        return resultMap;
    }
}
