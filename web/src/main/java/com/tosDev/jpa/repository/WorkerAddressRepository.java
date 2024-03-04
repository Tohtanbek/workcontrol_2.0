package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.BrigadierAddress;
import com.tosDev.jpa.entity.WorkerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerAddressRepository extends JpaRepository<WorkerAddress,Integer> {
    Optional<WorkerAddress> findByWorkerIdAndAddressId(Integer wId, Integer aId);
}
