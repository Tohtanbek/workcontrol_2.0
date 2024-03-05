package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.ResponsibleBrigadier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsibleBrigadierRepository extends JpaRepository<ResponsibleBrigadier,Integer> {

    Optional<ResponsibleBrigadier> findByResponsibleIdAndBrigadierId(Integer rId, Integer bId);
}
