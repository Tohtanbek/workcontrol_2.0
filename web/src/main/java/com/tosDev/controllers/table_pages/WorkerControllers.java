package com.tosDev.controllers.table_pages;

import com.tosDev.dto.ResponsibleDto;
import com.tosDev.dto.WorkerDto;
import com.tosDev.jpa.repository.WorkerRepository;
import com.tosDev.service.WorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tables/worker")
public class WorkerControllers {

    private final WorkerRepository workerRepository;
    private final WorkerService workerService;

    /**
     * Получает запрос с фронтенда на список бригадиров
     * @return возвращает json мапы с ключом - id и value - именем бригадира.
     */
    @GetMapping("/load_worker_map")
    ResponseEntity<String> loadWorkerMap(){
        return workerService.workersToJsonMap();
    }

    /**
     * Получает запрос с фронтенда на список работников без тех, которые уже есть в переданном ряду
     * (Нужно для функционала перетаскивания рядов работников)
     * @return возвращает укороченный список json работников без тех, которые уже есть в переданном ряду
     */
    @GetMapping("/load_workers_small_table")
    ResponseEntity<String> loadWorkerSmallTableWithoutChosen(@RequestParam("id")Integer id){
        return workerService.mapWorkersToShortJsonWithoutChosen(id);
    }

    /**
     * Получает запрос с фронтенда на список работников конкретного адреса
     * @return возвращает укороченный список json работников конкретного адреса
     */
    @GetMapping("/load_workers_by_address_id")
    ResponseEntity<String> loadWorkersByAddressId(@RequestParam("id") Integer id){
        return workerService.loadWorkerDtosByAddressId(id);
    }

    /**
     * Загружает основную страницу работников
     * @return html страница работников
     */
    @GetMapping("/main")
    String loadMainResponsiblePage(){
        return "worker_tab";
    }

    /**
     * Загружает из бд таблицу worker и преобразует в json строку
     * @return json таблицы "работники"
     */
    @GetMapping("/main_table")
    @ResponseBody
    String getAllResponsibleRows(){
        return workerService.mapAllWorkersToJson();
    }

    /**
     * Загружает в бд нового работника
     * @param workerDto Дто работника, созданного админом
     * @return 200 ok статус
     */
    @PostMapping("/add_worker_row")
    ResponseEntity<Void> addFreshWorkerRow(@RequestBody WorkerDto workerDto){
        return workerService.mapAndSaveFreshWorker(workerDto);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     * @param ids массив id работников на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_worker_rows")
    ResponseEntity<Void> deleteWorkerRows(@RequestBody Integer[] ids){
        return workerService.deleteWorkerRows(ids);
    }
}
