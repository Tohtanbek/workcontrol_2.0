package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker,Integer> {
    Optional<Worker> findByName(String name);
}
