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
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.firstone.greenjangteo.order.excpeption.message.NotFoundExceptionMessage.ORDERED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.order.excpeption.message.NotFoundExceptionMessage.ORDER_ID_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
@RequiredArgsConstructor
@Transactional(isolation = READ_COMMITTED, timeout = 10)
public class OrderServiceImpl implements OrderService {
    private final StoreService storeService;
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(isolation = SERIALIZABLE, timeout = 20)
    public Order createOrder(OrderRequestDto orderRequestDto) {
        Store store = storeService.getStore(Long.parseLong(orderRequestDto.getSellerId()));
        User buyer = userService.getUser(Long.parseLong(orderRequestDto.getBuyerId()));
        Order order = Order.from(store, buyer, orderRequestDto, productService);

        return orderRepository.save(order);
    }

    @Override
    @Transactional(isolation = SERIALIZABLE, timeout = 20)
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

        return orderRepository.save(order);
    }

    @Override
    @Transactional(isolation = SERIALIZABLE, readOnly = true, timeout = 20)
    public List<Order> getOrders(UserIdRequestDto userIdRequestDto) {
        User user = userService.getUser(Long.parseLong(userIdRequestDto.getUserId()));

        if (user.getRoles().containSeller()) {
            return orderRepository.findBySellerId(Long.parseLong(userIdRequestDto.getUserId()));
        }

        return orderRepository.findByBuyerId(Long.parseLong(userIdRequestDto.getUserId()));
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ORDER_ID_NOT_FOUND_EXCEPTION + orderId));
    }

    @Override
    public void deleteOrder(Long orderId, UserIdRequestDto userIdRequestDto) {
        Long buyerId = Long.parseLong(userIdRequestDto.getUserId());

        if (orderRepository.existsByIdAndBuyerId(orderId, buyerId)) {
            orderRepository.deleteById(orderId);
            return;
        }

        throw new EntityNotFoundException(
                ORDER_ID_NOT_FOUND_EXCEPTION + orderId + ORDERED_USER_ID_NOT_FOUND_EXCEPTION + buyerId
        );
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
