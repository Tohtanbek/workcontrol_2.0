package com.tosDev.spring.jpa.repository.main_tables;

import com.tosDev.spring.jpa.entity.main_tables.ResponsibleBrigadier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsibleBrigadierRepository extends JpaRepository<ResponsibleBrigadier,Integer> {

    Optional<ResponsibleBrigadier> findByResponsibleIdAndBrigadierId(Integer rId, Integer bId);
}
