package com.tosDev.spring.web.controllers.table_pages;

import com.tosDev.dto.tableDto.AddressDto;
import com.tosDev.dto.tableDto.OrderDto;
import com.tosDev.dto.tableDto.ServiceDto;
import com.tosDev.spring.web.service.ServiceEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tables/service")
public class ServiceControllers {

    private final ServiceEntityService serviceEntityService;

    /**
     * Загружает страницу со сменами
     */
    @GetMapping("/main")
    String showOrderPage(){
        log.info("Загружена страница услуг");
        return "order/service_tab";
    }

    /**
     * Загружает из бд таблицу service и преобразует в json строку
     * @return json таблицы "Услуги"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllShiftRows(){
        return serviceEntityService.mapAllServiceToJson();
    }



    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id смен на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_service_rows")
    ResponseEntity<Void> deleteServiceRows(@RequestBody Integer[] ids){
        return serviceEntityService.deleteServiceRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param serviceDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_service_rows")
    ResponseEntity<Void> update_service_rows(@RequestBody List<ServiceDto> serviceDtos){
        return serviceEntityService.saveServiceUpdate(serviceDtos);
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице адреса
     * @param serviceDto dto нового оборудования
     * @return Response entity with http status
     */
    @PostMapping("/add_service_row")
    ResponseEntity<Void> addServiceRow(@RequestBody ServiceDto serviceDto){
        return serviceEntityService.mapAndSaveFreshService(serviceDto);
    }
}
