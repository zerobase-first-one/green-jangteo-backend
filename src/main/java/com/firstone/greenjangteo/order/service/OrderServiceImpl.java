package com.firstone.greenjangteo.order.service;

import com.firstone.greenjangteo.cart.domain.dto.response.CartProductListResponseDto;
import com.firstone.greenjangteo.cart.service.CartService;
import com.firstone.greenjangteo.coupon.service.CouponService;
import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.repository.OrderRepository;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.service.ReserveService;
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
    private final CouponService couponService;
    private final ReserveService reserveService;
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
    public List<Order> getOrders(Long userId) {
        User user = userService.getUser(userId);

        if (user.getRoles().containSeller()) {
            return orderRepository.findBySellerId(userId);
        }

        return orderRepository.findByBuyerId(userId);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ORDER_ID_NOT_FOUND_EXCEPTION + orderId));
    }

    @Override
    public int useCoupon(Long orderId, Long couponId) {
        int couponAmount = couponService.updateUsedCoupon(orderId, couponId);
        Order order = getOrder(orderId);

        int totalOrderPriceAfterCouponUsed = order.updateCouponAmount(couponAmount);
        orderRepository.save(order);

        return totalOrderPriceAfterCouponUsed;
    }

    @Override
    public int cancelCoupon(Long orderId, Long couponId) {
        int couponAmount = couponService.rollBackUsedCoupon(orderId, couponId);
        Order order = getOrder(orderId);

        int totalOrderPriceAfterCouponUsed = order.updateCouponAmount(-couponAmount);
        orderRepository.save(order);

        return totalOrderPriceAfterCouponUsed;
    }

    @Override
    public int useReserve(Long orderId, UseReserveRequestDto useReserveRequestDto) {
        Order order = getOrder(orderId);
        int previouslyUsedReserveAmount = order.getUsedReserveAmount();
        if (previouslyUsedReserveAmount > 0) {
            AddReserveRequestDto addReserveRequestDto
                    = new AddReserveRequestDto(useReserveRequestDto.getUserId(), previouslyUsedReserveAmount);
            reserveService.rollBackUsedReserve(orderId, addReserveRequestDto);
        }

        reserveService.useReserve(orderId, useReserveRequestDto);

        int totalOrderPriceAfterReserveUsed = order.updateReserveAmount(useReserveRequestDto.getUsedReserve());
        orderRepository.save(order);

        return totalOrderPriceAfterReserveUsed;
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
