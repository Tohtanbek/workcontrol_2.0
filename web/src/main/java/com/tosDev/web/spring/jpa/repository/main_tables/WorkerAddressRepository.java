package com.tosDev.web.spring.jpa.repository.main_tables;

import com.tosDev.web.spring.jpa.entity.main_tables.WorkerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerAddressRepository extends JpaRepository<WorkerAddress,Integer> {
    Optional<WorkerAddress> findByWorkerIdAndAddressId(Integer wId, Integer aId);
}
