package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.Responsible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibleRepository extends JpaRepository<Responsible,Integer> {

    Optional<Responsible> findByName(String name);

    Optional<Responsible> findByPhoneNumber(Long responsiblePhoneNumber);
}
