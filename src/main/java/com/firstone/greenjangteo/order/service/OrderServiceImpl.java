package com.firstone.greenjangteo.order.service;

import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.repository.OrderRepository;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final StoreService storeService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(OrderRequestDto orderRequestDto) {
        Store store = storeService.getStore(Long.parseLong(orderRequestDto.getSellerId()));
        User buyer = userService.getUser(Long.parseLong(orderRequestDto.getBuyerId()));
        Order order = Order.from(store, buyer, orderRequestDto, productService);
        order.getOrderProducts().addOrder(order);

        return orderRepository.save(order);
    }
}
