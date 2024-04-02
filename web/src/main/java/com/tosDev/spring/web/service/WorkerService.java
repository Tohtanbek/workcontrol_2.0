package com.tosDev.spring.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.tableDto.WorkerDto;
import com.tosDev.spring.jpa.entity.main_tables.Address;
import com.tosDev.spring.jpa.entity.main_tables.Job;
import com.tosDev.spring.jpa.entity.main_tables.Worker;
import com.tosDev.spring.jpa.entity.main_tables.WorkerAddress;
import com.tosDev.spring.jpa.repository.main_tables.AddressRepository;
import com.tosDev.spring.jpa.repository.main_tables.JobRepository;
import com.tosDev.spring.jpa.repository.main_tables.WorkerAddressRepository;
import com.tosDev.spring.jpa.repository.main_tables.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;
    private final WorkerAddressRepository workerAddressRepository;
    private final JobRepository jobRepository;

    public ResponseEntity<String> workersToJsonMap(){
        List<Worker> workers =
                Optional.of(workerRepository.findAll()).orElse(Collections.emptyList());
        Map<Integer,String> workerMap = workers
                .stream()
                .collect(Collectors.toMap(Worker::getId,Worker::getName));
        String workerMapStr;
        try {
            workerMapStr = objectMapper.writeValueAsString(workerMap);
        } catch (JsonProcessingException e) {
            log.error("Не удалось передать мапу работников для фронтенда");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        log.info("Загружена мапа работников из бд");
        return ResponseEntity.ok(workerMapStr);
    }

    /**
     *  Метод получает весь список бригадиров в бд, но отбрасывает те, которые уже есть
     *  у адреса с переданным айди
     * @param id - id адреса, который выбрал пользователь, чтобы менять бригадиров
     * @return json отфильтрованного списка бригадиров
     */
    public ResponseEntity<String> mapWorkersToShortJsonWithoutChosen(Integer id){
        Address chosenAddress = addressRepository.findById(id).orElseThrow();
        List<Integer> chosenAddressWorkerIds = chosenAddress.getWorkerAddressList()
                .stream()
                .map(entity -> entity.getWorker().getId())
                .toList();
        List<Worker> workerDaos =
                Optional.of(workerRepository.findAll()).orElse(Collections.emptyList());
        String dtoStr;
        try {
            List<WorkerDto> workerDtoList =
                    workerDaos.stream()
                            .filter(worker -> !chosenAddressWorkerIds.contains(worker.getId()) )
                            .map(dao -> WorkerDto.builder()
                                    .id(dao.getId())
                                    .name(dao.getName())
                                    .phoneNumber(dao.getPhoneNumber())
                                    .job(dao.getJob().getName())
                                    .build())
                            .toList();
            dtoStr = objectMapper.writeValueAsString(workerDtoList);
        } catch (Exception e) {
            log.error("Ошибка при загрузке таблицы работников");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(dtoStr);
    }

    public ResponseEntity<String> loadWorkerDtosByAddressId(Integer id){
        String jsonStr;
        try {
            List<WorkerDto> workerDtoList = addressRepository.findById(id).orElseThrow()
                    .getWorkerAddressList().stream()
                    .map(WorkerAddress::getWorker)
                    .map(workerDao -> WorkerDto.builder()
                            .id(workerDao.getId())
                            .name(workerDao.getName())
                            .phoneNumber(workerDao.getPhoneNumber())
                            .job(workerDao.getJob().getName())
                            .build())
                    .toList();
            jsonStr = objectMapper.writeValueAsString(workerDtoList);
        } catch (Exception e) {
            log.error("Не удалось загрузить json списка дто работников по id адреса");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(jsonStr);
    }

    public String mapAllWorkersToJson(){
        List<Worker> workerList =
                Optional.of(workerRepository.findAll()).orElse(Collections.emptyList());
        List<WorkerDto> workerDtoList = workerList.stream()
                .map(dao -> WorkerDto.builder()
                        .id(dao.getId())
                        .name(dao.getName())
                        .phoneNumber(dao.getPhoneNumber())
                        .job(dao.getJob().getName())
                        .addresses(
                                dao.getWorkerAddressList().stream().
                                        map(wa -> wa.getAddress().getShortName()).toList()
                        )
                        .build()).toList();
        String allWorkerStr;
        try {
            allWorkerStr = objectMapper.writeValueAsString(workerDtoList);
        } catch (JsonProcessingException e) {
            log.error("При конвертации таблицы работников в json произошла ошибка");
            throw new RuntimeException(e);
        }
        log.info("Загружена таблица работников");
        return allWorkerStr;
    }

    public ResponseEntity<Void> mapAndSaveFreshWorker(WorkerDto workerDto){
        try {
            Worker freshWorker = workerRepository.save(Worker
                    .builder()
                    .name(workerDto.getName())
                    .phoneNumber(workerDto.getPhoneNumber())
                    .job(checkAndReturnJob(workerDto.getName()))
                    .build());

            for (String shortName : workerDto.getAddresses()){
                workerAddressRepository.save(WorkerAddress
                        .builder()
                        .worker(freshWorker)
                        .address(addressRepository.findByShortName(shortName).orElseThrow())
                        .build());
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового работника в бд{}",workerDto);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        log.info("Добавили нового работника {}", workerDto);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteWorkerRows(Integer[] ids){
        try {
            Arrays.stream(ids)
                    .map(workerRepository::findById)
                    .forEach(optional -> workerRepository.delete(optional.orElseThrow()));
        } catch (NoSuchElementException e) {
            log.error("При удалении выбранного работника по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи работников удалены по айди: {}",ids);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> saveWorkerUpdate(List<WorkerDto> workerDtos){
        try {
            for (WorkerDto workerDto : workerDtos) {
                Worker workerDao =
                        workerRepository.findById(workerDto.getId()).orElseThrow();
                workerDao.setName(workerDto.getName());
                workerDao.setPhoneNumber(workerDto.getPhoneNumber());
                workerDao.setJob(checkAndReturnJob(workerDto.getJob()));

                workerRepository.save(workerDao);
            }
        } catch (NoSuchElementException e) {
            log.error("При изменении выбранного работника по одному из id не было найдено записи в бд");
            e.printStackTrace();
        }
        log.info("Записи {} обновлены",workerDtos);
        return ResponseEntity.ok().build();
    }

    private Job checkAndReturnJob(String jobName){
        Optional<Job> existingJob = jobRepository.findByName(jobName);
        //Если такой профессии еще не использовали, то сохраняем ее в бд и возвращаем
        if (existingJob.isEmpty()){
            return jobRepository.save(
                    Job.builder().name(jobName).build());
        }
        //Иначе берем уже существующую с таким же названием и возвращаем для новой сущности
        else {
            return existingJob.get();
        }
    }
}
