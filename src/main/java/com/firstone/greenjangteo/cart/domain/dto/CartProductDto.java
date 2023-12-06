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
        ProductDto productDto = ProductDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .inventory(product.getInventory())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();

        return CartProductDto.builder()
                .product(productDto)
                .cartProductQuantity(cartProduct.getQuantity())
                .createdAt(cartProduct.getCreatedAt())
                .modifiedAt(cartProduct.getModifiedAt())
                .build();
    }
}
