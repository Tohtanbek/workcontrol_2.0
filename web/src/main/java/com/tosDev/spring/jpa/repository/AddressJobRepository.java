package com.tosDev.spring.jpa.repository;

import com.tosDev.spring.jpa.entity.AddressJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressJobRepository extends JpaRepository<AddressJob,Integer> {
    List<AddressJob> findAllByAddressId(Integer id);

    Optional<AddressJob> findByAddressIdAndJobId(Integer aId, Integer jId);
}
