package com.firstone.greenjangteo.order.repository;

import com.firstone.greenjangteo.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM orders o WHERE o.store.sellerId = :sellerId")
    List<Order> findBySellerId(@Param("sellerId") long sellerId);

    @Query("SELECT o FROM orders o WHERE o.buyer.id = :buyerId")
    List<Order> findByBuyerId(@Param("buyerId") long buyerId);

    boolean existsByIdAndBuyerId(Long id, Long buyerId);
}
