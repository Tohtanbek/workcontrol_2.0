package com.tosDev.web.spring.jpa.repository.main_tables;

import com.tosDev.web.spring.jpa.entity.main_tables.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker,Integer> {
    Optional<Worker> findByName(String name);

    Optional<Worker> findByPhoneNumber(Long workerPhoneNumber);


}
