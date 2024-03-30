package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.AssignmentEquip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentEquipRepository extends JpaRepository<AssignmentEquip,Long> {

}
