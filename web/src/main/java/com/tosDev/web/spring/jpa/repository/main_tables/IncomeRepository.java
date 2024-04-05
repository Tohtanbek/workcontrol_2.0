package com.tosDev.web.spring.jpa.repository.main_tables;

import com.tosDev.web.spring.jpa.entity.main_tables.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income,Integer> {
}
