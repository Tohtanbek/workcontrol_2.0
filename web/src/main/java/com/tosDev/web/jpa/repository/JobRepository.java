package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Integer> {
    Optional<Job> findByName(String name);
}
