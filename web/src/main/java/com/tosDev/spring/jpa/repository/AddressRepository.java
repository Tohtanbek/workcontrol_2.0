package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AddressRepository extends JpaRepository<Address,Integer> {
    Optional<Address> findByShortName(String shortName);
}
