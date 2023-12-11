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
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public static AddCartProductResponseDto of(Long cartId, LocalDateTime createdAt) {
        return AddCartProductResponseDto.builder()
                .cartId(cartId)
                .createdAt(createdAt)
                .build();
    }
}