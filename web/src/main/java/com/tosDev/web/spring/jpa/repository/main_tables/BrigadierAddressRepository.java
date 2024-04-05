package com.tosDev.web.spring.jpa.repository.main_tables;

import com.tosDev.web.spring.jpa.entity.main_tables.BrigadierAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrigadierAddressRepository extends JpaRepository<BrigadierAddress,Integer> {
    Optional<BrigadierAddress> findByBrigadierIdAndAddressId(Integer bId,Integer aId);
}
