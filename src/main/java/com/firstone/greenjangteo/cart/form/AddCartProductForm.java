package com.firstone.greenjangteo.cart.form;

import com.firstone.greenjangteo.cart.domain.dto.ConvertToCartProductDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@Getter
public class AddCartProductForm {

    @ApiModelProperty(value = "userId", example = "1")
    private Long userId;

    @ApiModelProperty(value = "cartProduct")
    private List<ConvertToCartProductDto> cartProduct;
}