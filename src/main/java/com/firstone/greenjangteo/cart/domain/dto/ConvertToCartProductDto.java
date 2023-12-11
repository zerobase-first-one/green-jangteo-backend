package com.firstone.greenjangteo.cart.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvertToCartProductDto {
    @ApiModelProperty(value = "productId", example = "1")
    private Long productId;
    @ApiModelProperty(value = "quantity", example = "2")
    private int quantity;
}