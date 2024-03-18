package com.tosDev.web.controllers.table_pages;

import com.tosDev.web.dto.IncomeDto;
import com.tosDev.web.service.IncomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tables/income")
public class IncomeControllers {

    private final IncomeService incomeService;

    /**
     * Загружает страницу со расходами
     */
    @GetMapping("/main")
    String showIncomePage(){
        log.info("Загружена страница смен");
        return "income_tab";
    }

    /**
     * Загружает из бд таблицу income и преобразует в json строку
     * @return json таблицы "расходы"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllIncomeRows(){
        return incomeService.mapAllIncomeToJson();
    }



    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id расходов на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_income_rows")
    ResponseEntity<Void> deleteIncomeRows(@RequestBody Integer[] ids){
        return incomeService.deleteIncomeRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param incomeDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_income_rows")
    ResponseEntity<Void> update_income_rows(@RequestBody List<IncomeDto> incomeDtos){
        return incomeService.saveIncomeUpdate(incomeDtos);
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице траты
     * @param incomeDto dto нового ряда трат
     * @return Response entity with http status
     */
    @PostMapping("/add_income_row")
    ResponseEntity<Void> addAddressRow(@RequestBody IncomeDto incomeDto){
        return incomeService.mapAndSaveFreshIncome(incomeDto);
    }
}
