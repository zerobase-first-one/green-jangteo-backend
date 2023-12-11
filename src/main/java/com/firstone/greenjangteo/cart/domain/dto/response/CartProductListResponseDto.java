package com.firstone.greenjangteo.cart.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductListResponseDto {
    @JsonProperty("productId")
    private Long productId;
    @JsonProperty("quantity")
    private int quantity;

    public static CartProductListResponseDto of(Long productId, int quantity) {
        return CartProductListResponseDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
