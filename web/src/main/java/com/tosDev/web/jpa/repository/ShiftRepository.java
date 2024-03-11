package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.Shift;
import com.tosDev.web.jpa.entity.Worker;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findByShortInfo(String shortInfo);


    boolean existsByWorkerAndStatus(Worker worker,String status);
}
