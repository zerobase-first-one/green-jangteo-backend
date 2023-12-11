package com.firstone.greenjangteo.cart.form;

import com.firstone.greenjangteo.cart.domain.dto.request.CartProductRequestDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DeleteSelectCartProductForm {
    @ApiModelProperty(value = "userId", example = "1")
    private Long userId;

    @ApiModelProperty(value = "userId", example = "[{\"cartProductId\" : \"23\", \"quantity\" : \"2\"}, {\"cartProductId\" : \"25\", \"quantity\" : \"3\"}]")
    private List<CartProductRequestDto> cartProducts;
}
