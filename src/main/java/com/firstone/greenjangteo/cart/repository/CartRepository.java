package com.firstone.greenjangteo.cart.repository;

import com.firstone.greenjangteo.cart.domain.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);
}
