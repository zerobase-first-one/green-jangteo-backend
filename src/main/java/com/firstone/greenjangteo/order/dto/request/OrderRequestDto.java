package com.firstone.greenjangteo.order.dto.request;

import com.firstone.greenjangteo.user.dto.AddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class OrderRequestDto {
    private static final String SELLER_ID_VALUE = "판매자 ID";
    private static final String BUYER_ID_VALUE = "구매자 ID";
    private static final String ORDER_PRODUCTS_VALUE = "주문 상품 목록";
    private static final String SHIPPING_ADDRESS_VALUE = "배송 주소";

    @ApiModelProperty(value = SELLER_ID_VALUE, example = ID_EXAMPLE)
    private String sellerId;

    @ApiModelProperty(value = BUYER_ID_VALUE, example = ID_EXAMPLE)
    private String buyerId;

    @ApiModelProperty(ORDER_PRODUCTS_VALUE)
    private List<OrderProductRequestDto> orderProductRequestDtos;

    @ApiModelProperty(SHIPPING_ADDRESS_VALUE)
    private AddressDto shippingAddressDto;
}
