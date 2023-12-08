package com.firstone.greenjangteo.cart.domain.dto;

import com.firstone.greenjangteo.cart.domain.model.Cart;
import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDto {
    private Long cartProductId;
    private Long productId;
    private ProductDto product;
    private int cartProductQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CartProductDto filteredCartProductOf(CartProduct cartProduct) {
        return CartProductDto.builder()
                .productId(cartProduct.getProduct().getId())
                .cartProductQuantity(cartProduct.getQuantity())
                .build();
    }

    public static CartProductDto of(CartProduct cartProduct) {
        Product product = cartProduct.getProduct();
        ProductDto productDto = ProductDto.of(product);

        return CartProductDto.builder()
                .product(productDto)
                .cartProductQuantity(cartProduct.getQuantity())
                .createdAt(cartProduct.getCreatedAt())
                .modifiedAt(cartProduct.getModifiedAt())
                .build();
    }

    public static CartProduct cartProductCreatedOf(Cart cart, Product product, int quantity) {
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static CartProduct cartProductModifiedOf(CartProduct cartProduct, int quantity) {
        return CartProduct.builder()
                .id(cartProduct.getId())
                .cart(cartProduct.getCart())
                .product(cartProduct.getProduct())
                .quantity(quantity)
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static CartProduct cartProductModifiedOf(Cart cart, Product product, int quantity) {
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
