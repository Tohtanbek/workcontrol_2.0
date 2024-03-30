package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Integer> {
}
