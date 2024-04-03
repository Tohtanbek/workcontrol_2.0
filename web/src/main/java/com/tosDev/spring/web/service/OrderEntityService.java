package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.client_pages.CheckoutDto;
import com.tosDev.dto.client_pages.ClientDto;
import com.tosDev.dto.tableDto.OrderDto;
import com.tosDev.spring.jpa.entity.client_orders.Order;
import com.tosDev.spring.jpa.entity.client_orders.OrderService;
import com.tosDev.spring.jpa.entity.client_orders.Service;
import com.tosDev.spring.jpa.repository.client_orders.OrderRepository;
import com.tosDev.spring.jpa.repository.client_orders.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
@Slf4j
@Transactional
public class OrderEntityService {

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final ObjectMapper objectMapper;

    @Qualifier("basicDateTimeFormatter")
    private final DateTimeFormatter basicDateTimeFormatter;


    public ResponseEntity<String> mapAllOrdersToJson(){
        String allOrderStr;
        try {
            List<Order> orderList =
                    Optional.of(orderRepository.findAll()).orElse(Collections.emptyList());
            List<OrderDto> orderDtoList = new ArrayList<>();
            for (Order dao : orderList){
                OrderDto freshDto = OrderDto.builder()
                        .id(dao.getId())
                        .total(dao.getTotal())
                        .area(dao.getArea())
                        .dateTime(basicDateTimeFormatter.format(dao.getDateTime()))
                        .build();
                Optional.ofNullable(dao.getPhoneNumber())
                        .ifPresent(number -> freshDto.setPhoneNumber(number.toString()));
                Optional.ofNullable(dao.getEmail()).ifPresent(freshDto::setEmail);
                Optional.ofNullable(dao.getAddress()).ifPresent(freshDto::setAddress);
                orderDtoList.add(freshDto);
            }
            allOrderStr = objectMapper.writeValueAsString(orderDtoList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы заказов в json произошла ошибка",e);
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица заказов");
        return ResponseEntity.ok(allOrderStr);
    }

    public ResponseEntity<Void> deleteOrderRows(Long[] ids){
        try {
            Arrays.stream(ids)
                    .map(orderRepository::findById)
                    .forEach(optional -> orderRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного заказа по одному из id не было найдено записи в бд",e);
        }
        log.info("Записи удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveOrderUpdate(List<OrderDto> orderDtos){
        try {
            for (OrderDto orderDto : orderDtos) {
                Order orderDao = orderRepository.findById(orderDto.getId()).orElseThrow();

                orderDao.setAddress(orderDto.getAddress());
                orderDao.setEmail(orderDto.getEmail());
                orderDao.setPhoneNumber(Long.parseLong(orderDto.getPhoneNumber()));

                orderRepository.save(orderDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного заказа по одному из id не было найдено записи в бд",e);
        }
        log.info("Записи {} обновлены",orderDtos);
        return ResponseEntity.ok().build();
    }

    public Order mapAndSaveOrder(CheckoutDto checkoutDto,
                                ClientDto clientDto,
                                ZonedDateTime orderDateTime,
                                Float area){

        //Сначала по id из заказа получаем сущности Service
        Service mainService =
                serviceRepository.findById(checkoutDto.getMainServiceId()).orElseThrow();
        List<Service> extraServices =
                serviceRepository.findAllById(Arrays.asList(checkoutDto.getExtraServiceIds()));
        //Далее делим ZonedDateTime на utc и offset
        LocalDateTime utcOrderDateTime = orderDateTime.toLocalDateTime();
        Integer offset = orderDateTime.getOffset().getTotalSeconds();

        Order freshOrder = Order.builder()
                .subTotal(checkoutDto.getSubTotal())
                .total(checkoutDto.getTotal())
                .dateTime(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .orderDateTime(utcOrderDateTime)
                .orderOffset(offset)
                .clientName(clientDto.getUsername())
                .area(area)
                .build();
        //Если был номер, то вписываем, если не было, то ничего не делаем
        Optional.ofNullable(clientDto.getPhoneNumber()).ifPresent(str ->
                freshOrder.setPhoneNumber(Long.parseLong(str.substring(1))));
        //То же самое с почтой
        Optional.ofNullable(clientDto.getEmail()).ifPresent(freshOrder::setEmail);
        //И с промокодом
        Optional.ofNullable(checkoutDto.getPromoCode()).ifPresent(freshOrder::setPromoCode);

        //Создаем OrderService
        List<OrderService> freshOrderServices = new ArrayList<>();
        //Сначала одну основную услугу
        OrderService orderMainService = OrderService.builder()
                .service(mainService)
                .order(freshOrder)
                .build();
        freshOrderServices.add(orderMainService);
        //Потом все дополнительные
        extraServices.forEach(dao -> freshOrderServices.add(OrderService.builder()
                .order(freshOrder)
                .service(dao)
                .build()));

        freshOrder.setOrderServices(freshOrderServices);

        return orderRepository.save(freshOrder);
    }


}
