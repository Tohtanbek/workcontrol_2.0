package com.tosDev.spring.web.controllers.table_pages;

import com.tosDev.dto.tableDto.OrderDto;
import com.tosDev.spring.web.service.OrderEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tables/order")
public class OrderControllers {

    private final OrderEntityService orderEntityService;

    /**
     * Загружает страницу со сменами
     */
    @GetMapping("/main")
    String showOrderPage(){
        log.info("Загружена страница заказов");
        return "order/order_tab";
    }

    /**
     * Загружает из бд таблицу order и преобразует в json строку
     * @return json таблицы "Заказы"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllShiftRows(){
        return orderEntityService.mapAllOrdersToJson();
    }



    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id смен на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_order_rows")
    ResponseEntity<Void> deleteOrderRows(@RequestBody Long[] ids){
        return orderEntityService.deleteOrderRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param orderDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_order_rows")
    ResponseEntity<Void> update_order_rows(@RequestBody List<OrderDto> orderDtos){
        return orderEntityService.saveOrderUpdate(orderDtos);
    }
}
