package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findByShortInfo(String shortInfo);
}
