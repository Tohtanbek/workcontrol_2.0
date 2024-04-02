package com.tosDev.spring.jpa.repository.main_tables;

import com.tosDev.spring.jpa.entity.main_tables.Brigadier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrigadierRepository extends JpaRepository<Brigadier,Integer> {
    Optional<Brigadier> findByName(String name);

    Optional<Brigadier> findByPhoneNumber(Long brigadierPhoneNumber);
}
