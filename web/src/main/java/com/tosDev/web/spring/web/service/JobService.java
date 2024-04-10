package com.tosDev.web.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.web.dto.tableDto.AddressDto;
import com.tosDev.web.spring.jpa.entity.main_tables.Address;
import com.tosDev.web.spring.jpa.entity.main_tables.AddressJob;
import com.tosDev.web.spring.jpa.entity.main_tables.Job;
import com.tosDev.web.dto.tableDto.JobDto;
import com.tosDev.web.spring.jpa.entity.main_tables.Worker;
import com.tosDev.web.spring.jpa.repository.main_tables.AddressJobRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final AddressJobRepository addressJobRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<String> mapAllJobToJson() {
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
        log.info("Загружена таблица профессий");
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

    public ResponseEntity<String> jobsToJsonMap(){
        List<Job> jobs =
                Optional.of(jobRepository.findAll()).orElse(Collections.emptyList());
        Map<Integer,String> jsonMap = jobs
                .stream()
                .collect(Collectors.toMap(Job::getId,Job::getName));
        String jsonMapStr;
        try {
            jsonMapStr = objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            log.error("Не удалось передать мапу профессий для фронтенда");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена мапа профессий из бд");
        return ResponseEntity.ok(jsonMapStr);
    }

    public ResponseEntity<String[]> mapAllJobToNamesArray(){
        try{
            String[] namesArray = jobRepository.findAll().stream().map(Job::getName).toArray(String[]::new);
            return ResponseEntity.ok(namesArray);
        } catch (Exception e) {
            log.error("Ошибка при загрузке списка названий профессий",e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Void> saveJobUpdate(List<JobDto> jobDtos){
        try {
            for (JobDto jobDto : jobDtos) {
                Job jobDao = jobRepository.findById(jobDto.getId()).orElseThrow();
                jobDao.setName(jobDto.getName());
                jobDao.setWageRate(jobDto.getWageRate());
                jobDao.setIncomeRate(jobDto.getIncomeRate());
                jobDao.setHourly(jobDto.isHourly());

                jobRepository.save(jobDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного адреса по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",jobDtos);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<Void> deleteJobRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(jobRepository::findById)
                    .forEach(optional -> jobRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранной профессии по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи удалены по айди: {}", Arrays.toString(ids));
        return ResponseEntity.ok().build();
    }



}
