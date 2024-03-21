package com.tosDev.web.jpa.repository;

import com.tosDev.web.jpa.entity.AssignmentEquip;
import com.tosDev.web.jpa.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentEquipRepository extends JpaRepository<AssignmentEquip,Long> {

}
