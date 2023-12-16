package com.firstone.greenjangteo.order.service;

import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderRequestDto orderRequestDto);

    Order createOrderFromCart(CartOrderRequestDto cartOrderRequestDto);

    List<Order> getOrders(UserIdRequestDto userIdRequestDto);

    Order getOrder(Long orderId);

    void deleteOrder(Long orderId, UserIdRequestDto userIdRequestDto);
}
