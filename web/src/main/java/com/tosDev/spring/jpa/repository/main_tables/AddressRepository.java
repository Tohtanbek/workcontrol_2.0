package com.tosDev.spring.jpa.repository.main_tables;

import com.tosDev.spring.jpa.entity.main_tables.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AddressRepository extends JpaRepository<Address,Integer> {
    Optional<Address> findByShortName(String shortName);
}
