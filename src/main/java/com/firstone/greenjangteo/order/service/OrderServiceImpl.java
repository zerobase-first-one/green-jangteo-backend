package com.firstone.greenjangteo.order.service;

import com.firstone.greenjangteo.cart.domain.dto.response.CartProductListResponseDto;
import com.firstone.greenjangteo.cart.service.CartService;
import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.repository.OrderRepository;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final StoreService storeService;
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(OrderRequestDto orderRequestDto) {
        Store store = storeService.getStore(Long.parseLong(orderRequestDto.getSellerId()));
        User buyer = userService.getUser(Long.parseLong(orderRequestDto.getBuyerId()));
        Order order = Order.from(store, buyer, orderRequestDto, productService);
        order.getOrderProducts().addOrder(order);

        return orderRepository.save(order);
    }

    @Override
    public Order createOrderFromCart(CartOrderRequestDto cartOrderRequestDto) {
        Long buyerId = Long.parseLong(cartOrderRequestDto.getBuyerId());
        User buyer = userService.getUser(buyerId);

        List<CartProductListResponseDto> cartProductListResponseDtos = cartService.getCartList(buyerId);
        List<OrderProductRequestDto> orderProductRequestDtos = transferCartToOrderDto(cartProductListResponseDtos);

        Product product = productService.getProduct(Long.parseLong(orderProductRequestDtos.get(0).getProductId()));
        Store store = product.getStore();

        OrderRequestDto orderRequestDto
                = OrderRequestDto.of(
                String.valueOf(store.getSellerId()), cartOrderRequestDto.getBuyerId(),
                orderProductRequestDtos, cartOrderRequestDto.getShippingAddressDto()
        );

        Order order = Order.from(store, buyer, orderRequestDto, productService);
        order.getOrderProducts().addOrder(order);

        return orderRepository.save(order);
    }

    private List<OrderProductRequestDto> transferCartToOrderDto
            (List<CartProductListResponseDto> cartProductListResponseDtos) {
        List<OrderProductRequestDto> orderProductRequestDtos = new ArrayList<>();
        for (CartProductListResponseDto cartProductListResponseDto : cartProductListResponseDtos) {
            orderProductRequestDtos.add(OrderProductRequestDto.from(cartProductListResponseDto));
        }

        return orderProductRequestDtos;
    }
}
