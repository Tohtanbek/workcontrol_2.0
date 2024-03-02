package com.tosDev.controllers.table_pages;

import com.tosDev.dto.ResponsibleDto;
import com.tosDev.service.ResponsibleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping("/tables/supervisors")
public class ResponsibleControllers {

    private final ResponsibleService responsibleService;

    /**
     * Загружает основную страницу ответственных
     * @return html страница ответственных
     */
    @GetMapping("/main")
    String loadMainResponsiblePage(){
        return "responsible_tab";
    }

    /**
     * Загружает из бд таблицу responsible и преобразует в json строку
     * @return json таблицы "оборудование"
     */
    @GetMapping("/main_table")
    @ResponseBody
    String getAllResponsibleRows(){
        return responsibleService.mapAllResposnibleToJson();
    }

    /**
     *
     * @return Массив строк имен ответственных
     */
    @GetMapping("/responsible_names_array")
    @ResponseBody
    String[] responsibleNamesArray(){
        return responsibleService.mapAllResponsibleToArray();
    }

    /**
     * Загружает в бд нового ответственного
     * @param responsibleDto Дто ответственного, созданного админом
     * @return 200 ok статус
     */
    @PostMapping("/add_responsible_row")
    ResponseEntity<Void> addFreshResponsibleRow(@RequestBody ResponsibleDto responsibleDto){
        return responsibleService.mapAndSaveFreshResponsible(responsibleDto);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id ответственных на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_supervisor_rows")
    ResponseEntity<Void> deleteSupervisorRows(@RequestBody Integer[] ids){
        return responsibleService.deleteResponsibleRows(ids);
    }

    /**
     * Принимает запрос на список супервайзеров и связанных с ними бригадиров.
     * @return status ok и body с мапой бригадиров связанных с супервайзерами.
     */
    @GetMapping("/get_supervisor_brigadiers")
    ResponseEntity<String> getSupervisorBrigadierJsons(){
        return responsibleService.loadJsonSuperVisorBrigadiers();
    }

}
