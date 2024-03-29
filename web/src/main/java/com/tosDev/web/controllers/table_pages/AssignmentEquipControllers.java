package com.tosDev.web.controllers.table_pages;

import com.tosDev.web.dto.equip.AssignEquipDto;
import com.tosDev.web.service.AssignmentEquipService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/tables/assignment_equip")
@RequiredArgsConstructor
public class AssignmentEquipControllers {

    private final AssignmentEquipService assignEquipService;

    /**
     * Загружает страницу с оборудованием
     */
    @GetMapping("/main")
    String showEquipPage(HttpServletRequest request,
                         @RequestParam(value = "row_id",required = false) Long equipRowId,
                         Model model)
    {
        if (request.getParameterMap().containsKey("assign_redirect")){
            model.addAttribute("assign_redirect","true");
            model.addAttribute("row_id",equipRowId);
            log.info("Перенаправление на выдачу оборудования");
            return "equipment/assignment_equipment_tab";
        }
        log.info("Загружена страница выданного оборудования");
        return "equipment/assignment_equipment_tab";
    }

    /**
     * Загружает из бд таблицу equipments и преобразует в json строку
     * @return json таблицы "оборудование"
     */
    @GetMapping("/main_table")
    @ResponseBody
    ResponseEntity<String> getAllAssignEquipRows(){
        return assignEquipService.mapAllAssignEquipToJson();
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице выдача оборудования
     * @param assignEquipDto dto новой выдачи оборудования
     * @return Response entity with http status
     */
    @PostMapping("/add_assignment_equip_row")
    ResponseEntity<Void> addAssignEquipRow(@RequestBody AssignEquipDto assignEquipDto){
        return assignEquipService.mapAndSaveFreshAssignEquip(assignEquipDto);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id оборудования на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_assignment_equip_rows")
    ResponseEntity<Void> deleteEquipRows(@RequestBody Long[] ids){
        return assignEquipService.deleteAssignEquipRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     * @param assignEquipDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_assignment_equip_rows")
    ResponseEntity<Void> updateAssignEquipRows(@RequestBody List<AssignEquipDto> assignEquipDtos){
        return assignEquipService.saveAssignEquipUpdate(assignEquipDtos);
    }


}
