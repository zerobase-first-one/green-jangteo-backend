package com.firstone.greenjangteo.cart.repository;

import com.firstone.greenjangteo.cart.domain.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
