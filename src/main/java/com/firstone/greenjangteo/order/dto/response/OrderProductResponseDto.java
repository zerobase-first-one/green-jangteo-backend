package com.firstone.greenjangteo.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class OrderProductResponseDto {
    private Long orderProductId;
    private ProductToOrderResponseDto productToOrderResponseDto;
    private int quantity;
    private int orderPrice;
}
