package com.tosDev.web.spring.web.controllers.table_pages;

import com.tosDev.web.dto.tableDto.JobDto;
import com.tosDev.web.spring.web.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/tables/job")
@RequiredArgsConstructor
public class JobControllers {

    private final JobService jobService;


    /**
     * Загружает из бд таблицу Job и преобразует в json строку
     * @return json таблицы "адреса"
     */
    @GetMapping("/main_table")
    ResponseEntity<String> getAllJobRows(){
        return jobService.mapAllAddressToJson();
    }

    /**
     * Принимает с фронтенда запрос на создание нового ряда в таблице адреса
     * @param jobDto dto нового оборудования
     * @return Response entity with http status
     */
    @PostMapping("/add_job_row")
    ResponseEntity<Void> addJobRow(@RequestBody JobDto jobDto){
        return jobService.mapAndSaveFreshJob(jobDto);
    }


    /**
     * Загружает список присвоенных адресу профессий по id объекта
     * @param id id объекта
     * @return json списка dto профессий адреса
     */
    @GetMapping("/load_jobs_by_address_id")
    ResponseEntity<String> loadJobsByAddressId(@RequestParam("id") Integer id){
        return jobService.loadJobDtosByAddressId(id);
    }

    /**
     * Загружает список доступных еще не присвоенных адресу профессий
     * @param id id адреса
     * @return json списка dto профессий, не присвоенных адресу
     */
    @GetMapping("/load_jobs_for_address")
    ResponseEntity<String> loadJobsWithoutChosenByAddressId
            (@RequestParam("id")Integer id){
        return jobService.mapJobsWithoutChosenForAddress(id);
    }

    /**
     * Получает запрос с фронтенда на список профессий
     *
     * @return возвращает json мапы с ключом - id и value - названием специальности.
     */
    @GetMapping("/load_job_map")
    ResponseEntity<String> loadJobMap() {
        return jobService.jobsToJsonMap();
    }

    /**
     *
     * @return Массив строк названий специальностей
     */
    @GetMapping("/job_names_array")
    @ResponseBody
    ResponseEntity<String[]> jobNamesArray(){
        return jobService.mapAllJobToNamesArray();
    }
}
