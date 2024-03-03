package com.tosDev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.jpa.entity.Brigadier;
import com.tosDev.jpa.entity.Worker;
import com.tosDev.jpa.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final ObjectMapper objectMapper;

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
}
