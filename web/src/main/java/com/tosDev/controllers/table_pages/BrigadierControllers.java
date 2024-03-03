package com.tosDev.controllers.table_pages;

import com.tosDev.jpa.repository.BrigadierRepository;
import com.tosDev.service.BrigadierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * Получает запрос с фронтенда на список бригадиров
     * @return возвращает укороченный список json бригадиров
     */
    @GetMapping("/load_brigadier_small_table")
    ResponseEntity<String> loadBrigadierSmallTable(){
        return brigadierService.mapBrigadiersToShortJson();
    }

    /**
     * Получает запрос с фронтенда на список бригадиров конкретного адреса
     * @return возвращает укороченный список json бригадиров конкретного адреса
     */
    @GetMapping("/load_brigadiers_by_address_id")
    ResponseEntity<String> loadBrigadiersByAddressId(@RequestParam("id") Integer id){
        return brigadierService.loadBrigadierDtosByAddressId(id);
    }
}
