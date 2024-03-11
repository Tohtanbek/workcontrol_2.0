package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.Brigadier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrigadierRepository extends JpaRepository<Brigadier,Integer> {
    Optional<Brigadier> findByName(String name);
}
