package com.tosDev.controllers.table_pages;

import com.tosDev.jpa.repository.BrigadierRepository;
import com.tosDev.jpa.repository.WorkerRepository;
import com.tosDev.service.BrigadierService;
import com.tosDev.service.WorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
