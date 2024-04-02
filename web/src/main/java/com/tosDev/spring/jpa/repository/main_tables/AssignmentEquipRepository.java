package com.tosDev.spring.jpa.repository.main_tables;

import com.tosDev.spring.jpa.entity.main_tables.AssignmentEquip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentEquipRepository extends JpaRepository<AssignmentEquip,Long> {

}
