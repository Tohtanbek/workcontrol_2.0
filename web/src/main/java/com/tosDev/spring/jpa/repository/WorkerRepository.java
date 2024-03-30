package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker,Integer> {
    Optional<Worker> findByName(String name);

    Optional<Worker> findByPhoneNumber(Long workerPhoneNumber);


}
