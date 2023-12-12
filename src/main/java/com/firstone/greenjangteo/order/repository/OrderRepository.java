package com.firstone.greenjangteo.order.repository;

import com.firstone.greenjangteo.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
