package com.tosDev.web.spring.jpa.repository.main_tables;

import com.tosDev.web.spring.jpa.entity.main_tables.AddressJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressJobRepository extends JpaRepository<AddressJob,Integer> {
    List<AddressJob> findAllByAddressId(Integer id);

    Optional<AddressJob> findByAddressIdAndJobId(Integer aId, Integer jId);
}
