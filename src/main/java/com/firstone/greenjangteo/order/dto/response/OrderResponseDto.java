package com.firstone.greenjangteo.order.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.order.model.OrderProducts;
import com.firstone.greenjangteo.order.model.TotalOrderPrice;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.model.UserEntityToDtoMapper;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OrderResponseDto {
    private Long orderId;
    private BuyerResponseDto buyerResponseDto;
    private String storeName;
    private List<OrderProductResponseDto> orderProductResponseDtos;
    private int totalOrderPrice;
    private int amountToPay;
    private String orderStatus;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    public static OrderResponseDto from(Order order) {
        User buyer = order.getBuyer();
        Store store = order.getStore();
        OrderProducts orderProducts = order.getOrderProducts();
        TotalOrderPrice totalOrderPrice = order.getTotalOrderPrice();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .buyerResponseDto(UserEntityToDtoMapper.toOrder(buyer))
                .storeName(store.getStoreName().getValue())
                .orderProductResponseDtos(orderProducts.toDto())
                .totalOrderPrice(totalOrderPrice.getValue())
                .amountToPay(totalOrderPrice.getValue())
                .orderStatus(order.getOrderStatus().getDescription())
                .createdAt(order.getCreatedAt())
                .modifiedAt(order.getModifiedAt())
                .build();
    }
}
