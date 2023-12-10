package com.firstone.greenjangteo.cart.repository;

import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);

    List<CartProduct> findCartProductsByCartId(Long cartId);
}
