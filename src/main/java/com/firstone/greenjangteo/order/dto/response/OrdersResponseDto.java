package com.firstone.greenjangteo.order.dto.response;

import com.firstone.greenjangteo.order.model.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OrdersResponseDto {
    private Long orderId;
    private String orderStatus;
    private int totalOrderPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrdersResponseDto from(Order order) {
        return OrdersResponseDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus().getDescription())
                .totalOrderPrice(order.getTotalOrderPrice().getValue())
                .createdAt(order.getCreatedAt())
                .modifiedAt(order.getModifiedAt())
                .build();
    }
}
