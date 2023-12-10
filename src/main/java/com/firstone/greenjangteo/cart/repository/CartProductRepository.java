package com.firstone.greenjangteo.cart.repository;

import com.firstone.greenjangteo.cart.domain.dto.CartProductDto;
import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    CartProduct findCartProductByCartAndProduct(Long cartId, Long productId);

    List<CartProduct> findCartProductsByCartId(Long cartId);
}
