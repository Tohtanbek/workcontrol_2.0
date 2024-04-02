package com.tosDev.spring.jpa.repository.main_tables;

import com.tosDev.spring.jpa.entity.main_tables.Responsible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {

    Optional<Responsible> findByName(String name);

    Optional<Responsible> findByPhoneNumber(Long responsiblePhoneNumber);
}
