package com.firstone.greenjangteo.order.dto.request;

import com.firstone.greenjangteo.user.dto.AddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.firstone.greenjangteo.order.dto.DtoConstant.BUYER_ID_VALUE;
import static com.firstone.greenjangteo.order.dto.DtoConstant.SHIPPING_ADDRESS_VALUE;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CartOrderRequestDto {
    private static final String CART_ID_VALUE = "장바구니 ID";

    @ApiModelProperty(value = BUYER_ID_VALUE, example = ID_EXAMPLE)
    private String buyerId;

    @ApiModelProperty(value = CART_ID_VALUE, example = ID_EXAMPLE)
    private String cartId;

    @ApiModelProperty(SHIPPING_ADDRESS_VALUE)
    private AddressDto shippingAddressDto;
}
