package com.firstone.greenjangteo.order.testutil;

import com.firstone.greenjangteo.cart.domain.model.Cart;
import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.OrderProducts;
import com.firstone.greenjangteo.order.model.TotalOrderPrice;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.model.embedment.Address;
import com.firstone.greenjangteo.user.model.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.firstone.greenjangteo.order.model.OrderStatus.BEFORE_PAYMENT;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.mockito.Mockito.mock;

public class OrderTestObjectFactory {
    public static List<OrderProductRequestDto> createOrderProductDtos(List<String> productIds, List<String> quantities) {
        List<OrderProductRequestDto> orderProductRequestDtos = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            orderProductRequestDtos.add(createOrderProductDto(productIds.get(i), quantities.get(i)));
        }

        return orderProductRequestDtos;
    }

    public static OrderRequestDto createOrderRequestDto
            (String sellerId, String buyerId, List<OrderProductRequestDto> orderProductRequestDtos) {
        return OrderRequestDto.builder()
                .sellerId(sellerId)
                .buyerId(buyerId)
                .orderProductRequestDtos(orderProductRequestDtos)
                .shippingAddressDto(AddressDto.builder()
                        .city(CITY1)
                        .street(STREET1)
                        .zipcode(ZIPCODE1)
                        .detailedAddress(DETAILED_ADDRESS1)
                        .build())
                .build();
    }

    public static Order createOrder(Store store, User buyer, int totalOrderPrice) {
        return Order.builder()
                .store(store)
                .buyer(buyer)
                .orderStatus(BEFORE_PAYMENT)
                .totalOrderPrice(new TotalOrderPrice(totalOrderPrice))
                .shippingAddress(mock(Address.class))
                .build();
    }

    public static Order createOrder(Long id, Store store, User buyer, int totalOrderPrice) {
        return Order.builder()
                .id(id)
                .store(store)
                .buyer(buyer)
                .orderProducts(mock(OrderProducts.class))
                .orderStatus(BEFORE_PAYMENT)
                .totalOrderPrice(new TotalOrderPrice(totalOrderPrice))
                .shippingAddress(mock(Address.class))
                .build();
    }

    public static Cart createCart(User user) {
        return Cart.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static CartProduct createCartProduct(Cart cart, Product product1, String quantity1) {
        return CartProduct.builder()
                .cart(cart)
                .product(product1)
                .quantity(Integer.parseInt(quantity1))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static CartOrderRequestDto createCartOrderRequestDto(String buyerId, String cartId) {
        AddressDto shippingAddressDto = AddressDto.builder()
                .city(CITY1)
                .street(STREET1)
                .zipcode(ZIPCODE1)
                .detailedAddress(DETAILED_ADDRESS1)
                .build();

        return new CartOrderRequestDto(buyerId, cartId, shippingAddressDto);
    }

    private static OrderProductRequestDto createOrderProductDto(String productId, String quantity) {
        return new OrderProductRequestDto(productId, quantity);
    }
}
