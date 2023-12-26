package com.firstone.greenjangteo.cart.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductListResponseDto {
    @JsonProperty("productId")
    private Long productId;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("price")
    private int price;
    @JsonProperty("quantity")
    private int quantity;

    public static CartProductListResponseDto of(Product product, int quantity) {
        return CartProductListResponseDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .build();
    }
}
