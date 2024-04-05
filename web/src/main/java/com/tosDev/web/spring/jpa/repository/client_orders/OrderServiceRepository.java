package com.tosDev.web.spring.jpa.repository.client_orders;

import com.tosDev.web.spring.jpa.entity.client_orders.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderServiceRepository extends JpaRepository<OrderService,Long> {
    List<OrderService> findAllByOrderId(Long id);
}
