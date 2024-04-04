package com.tosDev.spring.jpa.repository.client_orders;

import com.tosDev.spring.jpa.entity.client_orders.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderServiceRepository extends JpaRepository<OrderService,Long> {
    List<OrderService> findAllByOrderId(Long id);
}
