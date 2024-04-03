package com.tosDev.spring.jpa.repository.client_orders;

import com.tosDev.enums.ServiceCategory;
import com.tosDev.spring.jpa.entity.client_orders.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Integer> {
    List<Service> findAllByCategory(ServiceCategory category);
    List<Service> findAllByPromoCode(String promoCode);
}
