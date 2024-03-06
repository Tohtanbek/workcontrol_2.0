package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.Expense;
import com.tosDev.jpa.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income,Integer> {
}
