package com.firstone.greenjangteo.cart.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.firstone.greenjangteo.cart.domain.dto.ConvertToCartProductDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartProductRequestDto {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("cartProduct")
    private ConvertToCartProductDto cartProduct;
}