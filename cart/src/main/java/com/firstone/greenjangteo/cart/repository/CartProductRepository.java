package com.example.greenjangteo.cart.repository;

import com.example.greenjangteo.cart.domain.dto.CartDto;
import com.example.greenjangteo.cart.domain.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    CartProduct findCartProductByCartAndProduct(Long cartId, Long productId);

    List<CartDto> findCartProductsByCartId(Long cartId);

    void deleteByCartAndProduct(Long cartId, Long productId);
}
