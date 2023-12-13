package com.firstone.greenjangteo.order.service;

import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.entity.Order;

public interface OrderService {
    Order createOrder(OrderRequestDto orderRequestDto);
}
