package com.firstone.greenjangteo.cart.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartProductResponseDto {
    @JsonProperty("cartId")
    private Long cartId;
    @JsonProperty("cartProductId")
    private Long cartProductId;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public static AddCartProductResponseDto of(Long cartId, Long cartProductId, LocalDateTime createdAt) {
        return AddCartProductResponseDto.builder()
                .cartId(cartId)
                .cartProductId(cartProductId)
                .createdAt(createdAt)
                .build();
    }
}