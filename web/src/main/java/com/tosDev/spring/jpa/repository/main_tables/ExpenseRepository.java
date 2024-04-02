package com.tosDev.spring.jpa.repository.main_tables;

import com.tosDev.spring.jpa.entity.main_tables.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Integer> {
}
