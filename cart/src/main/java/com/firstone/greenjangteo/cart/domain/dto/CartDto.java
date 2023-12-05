package com.example.greenjangteo.cart.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long productId;
    private int quantity;
}
