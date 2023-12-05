package com.example.greenjangteo.cart.domain.dto;

import com.example.greenjangteo.cart.domain.model.Cart;
import com.example.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDto {
    private Cart cart; //장바구니 아이디

    private Product product;

    private int quantity;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
