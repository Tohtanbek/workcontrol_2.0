package com.tosDev.spring.jpa.repository.client_orders;

import com.tosDev.spring.jpa.entity.client_orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
