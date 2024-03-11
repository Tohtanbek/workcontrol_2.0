package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Integer> {
}
