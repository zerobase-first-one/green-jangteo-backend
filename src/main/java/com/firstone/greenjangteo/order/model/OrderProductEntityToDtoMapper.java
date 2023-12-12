package com.firstone.greenjangteo.order.model;

import com.firstone.greenjangteo.order.dto.response.OrderProductResponseDto;
import com.firstone.greenjangteo.order.dto.response.ProductToOrderResponseDto;
import com.firstone.greenjangteo.order.model.entity.OrderProduct;

public class OrderProductEntityToDtoMapper {
    public static OrderProductResponseDto toDto(OrderProduct orderProduct) {
        ProductToOrderResponseDto productToOrderResponseDto
                = ProductToOrderResponseDto.from(orderProduct.getProduct());
        Quantity quantity = orderProduct.getQuantity();
        OrderPrice orderPrice = orderProduct.getOrderPrice();

        return OrderProductResponseDto.builder()
                .orderProductId(orderProduct.getId())
                .productToOrderResponseDto(productToOrderResponseDto)
                .quantity(quantity.getValue())
                .orderPrice(orderPrice.getValue())
                .build();
    }
}
