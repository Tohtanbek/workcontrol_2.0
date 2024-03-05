package com.tosDev.controllers.table_pages;

import com.tosDev.dto.BrigadierDtoWithSuper;
import com.tosDev.dto.BrigadierSmallDto;
import com.tosDev.dto.WorkerDto;
import com.tosDev.jpa.repository.BrigadierRepository;
import com.tosDev.service.BrigadierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tables/brigadier")
public class BrigadierControllers {

    private final BrigadierRepository brigadierRepository;
    private final BrigadierService brigadierService;

    /**
     * Получает запрос с фронтенда на список бригадиров
     * @return возвращает json мапы с ключом - id и value - именем бригадира.
     */
    @GetMapping("/load_brigadier_map")
    ResponseEntity<String> loadBrigadierMap(){
        return brigadierService.brigadiersToJsonMap();
    }

    /**
     * Получает запрос с фронтенда на список бригадиров без тех, которые уже есть в переданном ряду
     * @param id айди адреса, на котором надо менять бригадира
     * (Нужно для функционала перетаскивания рядов бригадиров)
     * @return возвращает укороченный список json бригадиров без тех, которые уже есть в переданном ряду
     */
    @GetMapping("/load_brigadier_small_table")
    ResponseEntity<String> loadBrigadierSmallTableWithoutChosenByAddressId(@RequestParam("id")Integer id){
        return brigadierService.mapBrigadiersToShortJsonWithoutChosen(id);
    }

    /**
     * Получает запрос с фронтенда на список бригадиров конкретного адреса
     * @return возвращает укороченный список json бригадиров конкретного адреса
     */
    @GetMapping("/load_brigadiers_by_address_id")
    ResponseEntity<String> loadBrigadiersByAddressId(@RequestParam("id") Integer id){
        return brigadierService.loadBrigadierDtosByAddressId(id);
    }


    /**
     * Загружает основную страницу бригадиров
     * @return html страница работников
     */
    @GetMapping("/main")
    String loadMainWorkerPage(){
        return "brigadier_tab";
    }

    /**
     * Загружает из бд таблицу worker и преобразует в json строку
     * @return json таблицы "работники"
     */
    @GetMapping("/main_table")
    @ResponseBody
    String getAllBrigadierRows(){
        return brigadierService.mapAllBrigadiersToJson();
    }

    /**
     * Загружает в бд нового бригадира
     * @param brigadierSmallDto Дто бригадира, созданного админом
     * @return 200 ok статус
     */
    @PostMapping("/add_brigadier_row")
    ResponseEntity<Void> addFreshBrigadierRow(@RequestBody BrigadierSmallDto brigadierSmallDto){
        return brigadierService.mapAndSaveFreshBrigadier(brigadierSmallDto);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id бригадиров на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_brigadier_rows")
    ResponseEntity<Void> deleteWorkerRows(@RequestBody Integer[] ids){
        return brigadierService.deleteBrigadierRows(ids);
    }

    /**
     * Получает запрос с фронтенда на список бригадиров без тех, которые уже есть в переданном ряду
     * @param id айди супервайзера, на котором надо менять бригадира
     * (Нужно для функционала перетаскивания рядов бригадиров)
     * @return возвращает укороченный список json бригадиров без тех, которые уже есть в переданном ряду
     */
    @GetMapping("/load_brigadier_for_supervisor")
    ResponseEntity<String> loadBrigadierSmallTableWithoutChosenBySupervisorId
    (@RequestParam("id")Integer id){
        return brigadierService.mapBrigadiersWithoutChosenForSuper(id);
    }

    /**
     * Получает запрос с фронтенда на список бригадиров конкретного адреса
     * @param id айди супервайзера
     * @return возвращает укороченный список json бригадиров конкретного адреса
     */
    @GetMapping("/load_brigadiers_by_supervisor_id")
    ResponseEntity<String> loadBrigadiersBySupervisorId(@RequestParam("id") Integer id){
        return brigadierService.loadBrigadierDtosBySuperId(id);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     *
     * @param brigadierDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_brigadier_rows")
    ResponseEntity<Void> updateBrigadierRows(@RequestBody List<BrigadierDtoWithSuper> brigadierDtos) {
        return brigadierService.saveBrigadierUpdate(brigadierDtos);
    }
}
