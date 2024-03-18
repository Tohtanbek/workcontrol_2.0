package com.tosDev.web.controllers.table_pages;

import com.tosDev.web.dto.ShiftDto;
import com.tosDev.web.service.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tables/shift")
public class ShiftControllers {

    private final ShiftService shiftService;

    /**
     * Загружает страницу со сменами
     */
    @GetMapping("/main")
    String showShiftPage(){
        log.info("Загружена страница смен");
        return "shift_tab";
    }

    /**
     * Загружает из бд таблицу shift и преобразует в json строку
     * @return json таблицы "оборудование"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllShiftRows(){
        return shiftService.mapAllShiftToJson();
    }



    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id смен на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_shift_rows")
    ResponseEntity<Void> deleteShiftRows(@RequestBody Integer[] ids){
        return shiftService.deleteShiftRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param shiftDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_shift_rows")
    ResponseEntity<Void> update_shift_rows(@RequestBody List<ShiftDto> shiftDtos){
        return shiftService.saveShiftUpdate(shiftDtos);
    }
}
