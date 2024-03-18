package com.tosDev.web.controllers.table_pages;

import com.tosDev.web.dto.AddressDto;
import com.tosDev.web.dto.BrigadierSmallDto;
import com.tosDev.web.dto.JobDto;
import com.tosDev.web.dto.WorkerDto;
import com.tosDev.web.service.AddressService;
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
    private final AddressService addressService;

    /**
     * Загружает страницу с объектами
     */
    @GetMapping("/main")
    String showAddressPage(){
        log.info("Загружена страница адресов");
        return "/address/address_tab";
    }

    /**
     * Загружает страницу с профессиями на объектах
     */
    @GetMapping("/address_job_main")
    String showAddressJobPage(){
        log.info("Загружена страница профессий адресов");
        return "/address/address_job_tab";
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
    ResponseEntity<String> loadAddressMap(){
        return addressService.addressToJsonMap();
    }

    @GetMapping("/address_job_table")
    ResponseEntity<String> loadAddressJobs(){
        return addressService.mapAddressJobs();
    }

    /**
     * Принимает с фронтенда запрос на смену профессий на адресе
     * @param id - айди адреса
     * @param jobDtos новые актуальные профессии на адресе
     * @return http result code
     */
    @PutMapping("/change_jobs_on_address")
    ResponseEntity<Void> changeJobsOnAddress(
            @RequestParam("id") Integer id,
            @RequestBody List<JobDto> jobDtos) {
        return addressService.updateJobsOnAddress(id,jobDtos);
    }
}
