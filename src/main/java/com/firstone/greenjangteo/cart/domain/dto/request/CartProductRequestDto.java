package com.firstone.greenjangteo.cart.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductRequestDto {
    @ApiModelProperty(value = "cartProductId", example = "1")
    private Long cartProductId;
    @ApiModelProperty(value = "quantity", example = "2")
    private int quantity;
}
