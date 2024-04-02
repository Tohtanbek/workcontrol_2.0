package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.spring.jpa.entity.main_tables.AddressJob;
import com.tosDev.spring.jpa.entity.main_tables.Job;
import com.tosDev.dto.tableDto.JobDto;
import com.tosDev.spring.jpa.repository.main_tables.AddressJobRepository;
import com.tosDev.spring.jpa.repository.main_tables.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final AddressJobRepository addressJobRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<String> mapAllAddressToJson() {
        List<Job> jobList = Optional.of(jobRepository.findAll())
                .orElse(Collections.emptyList());
        List<JobDto> dtoList = new ArrayList<>();
        for (Job dao : jobList){
            dtoList.add(
                    JobDto.builder()
                            .id(dao.getId())
                            .name(dao.getName())
                            .wageRate(dao.getWageRate())
                            .incomeRate(dao.getIncomeRate())
                            .isHourly(dao.isHourly())
                            .build()
            );
        }
        String allJobsStr;
        try {
            allJobsStr = objectMapper.writeValueAsString(dtoList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы работников в json произошла ошибка");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена таблица работников");
        return ResponseEntity.ok(allJobsStr);
    }

    public ResponseEntity<Void> mapAndSaveFreshJob(JobDto jobDto){
        Job freshJob;
        try {
            freshJob = jobRepository.save(
                    Job.builder()
                    .name(jobDto.getName())
                    .wageRate(jobDto.getWageRate())
                    .incomeRate(jobDto.getIncomeRate())
                    .isHourly(jobDto.isHourly())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка при сохранении новой специальности в бд{}",jobDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили новую специальность {}", freshJob);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<String> loadJobDtosByAddressId(Integer id){
        String jsonStr;
        try {
            List<AddressJob> addressJobList = addressJobRepository.findAllByAddressId(id);
            List<Job> jobsOnAddress = addressJobList.stream().map(AddressJob::getJob).toList();
            List<JobDto> jobDtos = jobsOnAddress.stream().map(dao ->
                    JobDto.builder()
                            .id(dao.getId())
                            .name(dao.getName())
                            .wageRate(dao.getWageRate())
                            .incomeRate(dao.getIncomeRate())
                            .isHourly(dao.isHourly())
                            .build()
                    ).toList();
            jsonStr = objectMapper.writeValueAsString(jobDtos);
        } catch (Exception e) {
            log.error("Не удалось загрузить json списка дто профессий по id адреса");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загрузили список профессий на адресе в фронтенд");
        return ResponseEntity.ok(jsonStr);
    }

    /**
     *  Метод получает весь список профессий в бд, но отбрасывает те, которые уже есть у адреса
     *  с переданным айди
     * @param id - id адреса, который выбрал пользователь, чтобы менять доступные профессии
     * @return json отфильтрованного списка профессий
     */
    public ResponseEntity<String> mapJobsWithoutChosenForAddress(Integer id){
        List<Integer> chosenAddressJobs = addressJobRepository
                .findAllByAddressId(id).stream().map(AddressJob::getJob).map(Job::getId).toList();
        List<Job> jobDaos =
                Optional.of(jobRepository.findAll()).orElse(Collections.emptyList());
        String dtoStr;
        try {
            List<JobDto> jobDtos =
                    jobDaos.stream()
                            .filter(job -> !chosenAddressJobs.contains(job.getId()))
                            .map(dao -> JobDto.builder()
                                    .id(dao.getId())
                                    .name(dao.getName())
                                    .wageRate(dao.getWageRate())
                                    .incomeRate(dao.getIncomeRate())
                                    .isHourly(dao.isHourly())
                                    .build())
                            .toList();
            dtoStr = objectMapper.writeValueAsString(jobDtos);
        } catch (Exception e) {
            log.error("Ошибка при загрузке таблицы профессий для изменения на адресе");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(dtoStr);
    }

}
