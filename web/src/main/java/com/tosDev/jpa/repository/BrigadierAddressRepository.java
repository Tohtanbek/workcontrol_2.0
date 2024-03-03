package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.BrigadierAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrigadierAddressRepository extends JpaRepository<BrigadierAddress,Integer> {

}
