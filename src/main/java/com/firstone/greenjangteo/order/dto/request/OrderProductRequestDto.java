package com.firstone.greenjangteo.order.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderProductRequestDto {
    private static final String PRODUCT_ID_VALUE = "상품 ID";
    private static final String QUANTITY_VALUE = "주문 수량";
    static final String QUANTITY_EXAMPLE = "5";

    @ApiModelProperty(value = PRODUCT_ID_VALUE, example = ID_EXAMPLE)
    private String productId;

    @ApiModelProperty(value = QUANTITY_VALUE, example = QUANTITY_EXAMPLE)
    private String quantity;
}
