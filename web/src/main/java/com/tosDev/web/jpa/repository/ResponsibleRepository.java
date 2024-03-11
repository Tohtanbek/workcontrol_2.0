package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.Responsible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {

    Optional<Responsible> findByName(String name);
}
