package com.firstone.greenjangteo.cart.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteCartProductRequestDto {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("cartProductId")
    private Long cartProductId;
}
