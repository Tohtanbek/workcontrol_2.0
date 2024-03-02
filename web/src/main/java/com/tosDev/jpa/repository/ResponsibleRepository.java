package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.Responsible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {

    Optional<Responsible> findByName(String name);
}