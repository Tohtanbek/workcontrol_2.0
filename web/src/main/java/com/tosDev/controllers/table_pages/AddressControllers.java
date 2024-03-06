package com.tosDev.controllers.table_pages;

import com.tosDev.dto.AddressDto;
import com.tosDev.dto.BrigadierSmallDto;
import com.tosDev.dto.EquipDto;
import com.tosDev.dto.WorkerDto;
import com.tosDev.jpa.repository.AddressRepository;
import com.tosDev.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ResponseEntity<String> getAllAddressRows(){
        return addressService.mapAllAddressToJson();
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице адреса
     * @param addressDto dto нового оборудования
     * @return Response entity with http status
     */
    @PostMapping("/add_address_row")
    ResponseEntity<Void> addAddressRow(@RequestBody AddressDto addressDto){
        return addressService.mapAndSaveFreshAddress(addressDto);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param addressDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_address_rows")
    ResponseEntity<Void> updateAddressRows(@RequestBody List<AddressDto> addressDtos){
        return addressService.saveAddressUpdate(addressDtos);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id работников на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_address_rows")
    ResponseEntity<Void> deleteAddressRows(@RequestBody Integer[] ids){
        return addressService.deleteAddressRows(ids);
    }

    /**
     * Принимает с фронтенда запрос на смену бригадиров на адресе
     * @param id - айди адреса
     * @param brigadierDtos новые актуальные бригадиры на адресе
     * @return http result code
     */
    @PutMapping("/change_brigadiers_on_address")
    ResponseEntity<Void> changeBrigadiersOnAddress(
            @RequestParam("id") Integer id,
            @RequestBody List<BrigadierSmallDto> brigadierDtos) {
        return addressService.updateBrigadiersOnAddress(id,brigadierDtos);
    }

    /**
     * Принимает с фронтенда запрос на смену работников на адресе
     * @param id - айди адреса
     * @param workerDtos новые актуальные работники на адресе
     * @return http result code
     */
    @PutMapping("/change_workers_on_address")
    ResponseEntity<Void> changeWorkersOnAddress(
            @RequestParam("id") Integer id,
            @RequestBody List<WorkerDto> workerDtos) {
        return addressService.updateWorkersOnAddress(id,workerDtos);
    }

    /**
     * Получает запрос с фронтенда на список адресов
     * @return возвращает json мапы с ключом - id и value - коротким именем адреса.
     */
    @GetMapping("/load_address_map")
    ResponseEntity<String> loadBrigadierMap(){
        return addressService.addressToJsonMap();
    }

}
