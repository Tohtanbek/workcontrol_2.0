package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.Responsible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {

    public Optional<Responsible> findByName(String name);
}
