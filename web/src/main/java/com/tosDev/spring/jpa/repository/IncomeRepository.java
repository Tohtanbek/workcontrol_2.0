package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income,Integer> {
}
