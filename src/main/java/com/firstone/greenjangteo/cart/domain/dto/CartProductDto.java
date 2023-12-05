package com.firstone.greenjangteo.cart.domain.dto;

import com.firstone.greenjangteo.cart.domain.model.Cart;
import com.firstone.greenjangteo.product.domain.model.Product;
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
