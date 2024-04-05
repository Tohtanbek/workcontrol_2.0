package com.tosDev.web.spring.jpa.repository.client_orders;

import com.tosDev.web.enums.ServiceCategory;
import com.tosDev.web.spring.jpa.entity.client_orders.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Integer> {
    List<Service> findAllByCategory(ServiceCategory category);
    List<Service> findAllByPromoCode(String promoCode);
}
