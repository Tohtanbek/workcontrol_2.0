package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.tableDto.OrderDto;
import com.tosDev.dto.tableDto.ShiftDto;
import com.tosDev.spring.jpa.entity.client_orders.Order;
import com.tosDev.spring.jpa.entity.main_tables.Job;
import com.tosDev.spring.jpa.entity.main_tables.Shift;
import com.tosDev.spring.jpa.repository.client_orders.OrderRepository;
import com.tosDev.spring.jpa.repository.main_tables.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class OrderEntityService {

    private final OrderRepository orderRepository;
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
                        .shortInfo(dao.getName())
                        .total(dao.getTotal())
                        .area(dao.getArea())
                        .dateTime(basicDateTimeFormatter.format(dao.getDateTime()))
                        .build();
                Optional.ofNullable(dao.getPhoneNumber()).ifPresent(freshDto::setPhoneNumber);
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

                orderDao.setName(orderDto.getShortInfo());
                orderDao.setAddress(orderDto.getAddress());
                orderDao.setEmail(orderDto.getEmail());
                orderDao.setPhoneNumber(orderDto.getPhoneNumber());

                orderRepository.save(orderDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного заказа по одному из id не было найдено записи в бд",e);
        }
        log.info("Записи {} обновлены",orderDtos);
        return ResponseEntity.ok().build();
    }


}
