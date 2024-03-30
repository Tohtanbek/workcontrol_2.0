package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Optional<Admin> findByPhoneNumber(Long phoneNumber);

    Optional<List<Admin>> findByChatIdIsNotNull();

}
