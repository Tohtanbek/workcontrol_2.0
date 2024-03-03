package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.WorkerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerAddressRepository extends JpaRepository<WorkerAddress,Integer> {
}
