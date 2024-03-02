package com.tosDev.controllers.table_pages;

import com.tosDev.jpa.repository.AddressRepository;
import com.tosDev.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/tables/address")
@RequiredArgsConstructor
public class AddressControllers {
    private final AddressRepository addressRepository;
    private final AddressService addressService;

    /**
     * Загружает страницу с объектами
     */
    @GetMapping("/main")
    String showAddressPage(){
        log.info("Загружена страница адресов");
        return "address_tab";
    }

    /**
     * Загружает из бд таблицу address и преобразует в json строку
     * @return json таблицы "адреса"
     */
    @GetMapping("/main_table")
    ResponseEntity<String> getAllEquipRows(){
        return addressService.mapAllAddressToJson();
    }
}
