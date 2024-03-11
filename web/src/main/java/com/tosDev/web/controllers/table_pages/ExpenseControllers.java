package com.tosDev.web.controllers.table_pages;

import com.tosDev.web.dto.ExpenseDto;
import com.tosDev.web.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tables/expense")
public class ExpenseControllers {

    private final ExpenseService expenseService;

    /**
     * Загружает страницу со расходами
     */
    @GetMapping("/main")
    String showEquipPage(){
        log.info("Загружена страница смен");
        return "expense_tab";
    }

    /**
     * Загружает из бд таблицу expense и преобразует в json строку
     * @return json таблицы "расходы"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllExpenseRows(){
        return expenseService.mapAllExpenseToJson();
    }



    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id расходов на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_expense_rows")
    ResponseEntity<Void> deleteExpenseRows(@RequestBody Integer[] ids){
        return expenseService.deleteExpenseRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param expenseDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_expense_rows")
    ResponseEntity<Void> update_expense_rows(@RequestBody List<ExpenseDto> expenseDtos){
        return expenseService.saveExpenseUpdate(expenseDtos);
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице траты
     * @param expenseDto dto нового ряда трат
     * @return Response entity with http status
     */
    @PostMapping("/add_expense_row")
    ResponseEntity<Void> addAddressRow(@RequestBody ExpenseDto expenseDto){
        return expenseService.mapAndSaveFreshExpense(expenseDto);
    }
}
