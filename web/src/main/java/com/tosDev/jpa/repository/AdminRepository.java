package com.tosDev.jpa.repository;

import com.tosDev.jpa.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Optional<Admin> findByPhoneNumber(Long phoneNumber);

}
