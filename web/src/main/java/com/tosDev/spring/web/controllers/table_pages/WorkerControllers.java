package com.tosDev.spring.web.controllers.table_pages;

import com.tosDev.dto.tableDto.WorkerDto;
import com.tosDev.spring.web.service.WorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tables/worker")
public class WorkerControllers {

    private final WorkerService workerService;

    /**
     * Получает запрос с фронтенда на список работников
     *
     * @return возвращает json мапы с ключом - id и value - именем работника.
     */
    @GetMapping("/load_worker_map")
    ResponseEntity<String> loadWorkerMap() {
        return workerService.workersToJsonMap();
    }

    /**
     * Получает запрос с фронтенда на список работников без тех, которые уже есть в переданном ряду
     * (Нужно для функционала перетаскивания рядов работников)
     *
     * @return возвращает укороченный список json работников без тех, которые уже есть в переданном ряду
     */
    @GetMapping("/load_workers_small_table")
    ResponseEntity<String> loadWorkerSmallTableWithoutChosen(@RequestParam("id") Integer id) {
        return workerService.mapWorkersToShortJsonWithoutChosen(id);
    }

    /**
     * Получает запрос с фронтенда на список работников конкретного адреса
     *
     * @return возвращает укороченный список json работников конкретного адреса
     */
    @GetMapping("/load_workers_by_address_id")
    ResponseEntity<String> loadWorkersByAddressId(@RequestParam("id") Integer id) {
        return workerService.loadWorkerDtosByAddressId(id);
    }

    /**
     * Загружает основную страницу работников
     *
     * @return html страница работников
     */
    @GetMapping("/main")
    String loadMainResponsiblePage() {
        return "worker_tab";
    }

    /**
     * Загружает из бд таблицу worker и преобразует в json строку
     *
     * @return json таблицы "работники"
     */
    @GetMapping("/main_table")
    @ResponseBody
    String getAllWorkerRows() {
        return workerService.mapAllWorkersToJson();
    }

    /**
     * Загружает в бд нового работника
     *
     * @param workerDto Дто работника, созданного админом
     * @return 200 ok статус
     */
    @PostMapping("/add_worker_row")
    ResponseEntity<Void> addFreshWorkerRow(@RequestBody WorkerDto workerDto) {
        return workerService.mapAndSaveFreshWorker(workerDto);
    }

    /**
     * Принимает с фронтенда запрос на удаление определенных рядов.
     *
     * @param ids массив id работников на удаление
     * @return Response entity with http status
     */
    @DeleteMapping("/delete_worker_rows")
    ResponseEntity<Void> deleteWorkerRows(@RequestBody Integer[] ids) {
        return workerService.deleteWorkerRows(ids);
    }

    /**
     * Принимает с фронтенда заявку на сохранение изменений в существующих записях
     *
     * @param workerDtos дто, в которых были совершены изменения
     * @return http status
     */
    @PutMapping("/update_worker_rows")
    ResponseEntity<Void> updateWorkerRows(@RequestBody List<WorkerDto> workerDtos) {
        return workerService.saveWorkerUpdate(workerDtos);
    }



}