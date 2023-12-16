package com.firstone.greenjangteo.order.model;

import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.response.OrderProductResponseDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.model.entity.OrderProduct;
import com.firstone.greenjangteo.product.service.ProductService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.firstone.greenjangteo.order.excpeption.message.BlankExceptionMessage.ORDER_PRODUCTS_NO_VALUE_EXCEPTION;
import static javax.persistence.CascadeType.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProducts {
    @OneToMany(mappedBy = "order", cascade = {PERSIST, MERGE, REMOVE}, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts;

    OrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public static OrderProducts from
            (List<OrderProductRequestDto> orderProductRequestDtos, ProductService productService, Long sellerId) {
        checkNullity(orderProductRequestDtos);

        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductRequestDto orderProductRequestDto : orderProductRequestDtos) {
            addOrderProduct(orderProductRequestDto, orderProducts, productService, sellerId);
        }

        return new OrderProducts(orderProducts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProducts that = (OrderProducts) o;
        return Objects.equals(orderProducts, that.orderProducts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderProducts);
    }

    public List<OrderProduct> getOrderItems() {
        return orderProducts;
    }

    public int computeTotalOrderPrice() {
        int totalOrderPrice = 0;

        for (OrderProduct orderProduct : orderProducts) {
            OrderPrice orderPrice = orderProduct.getOrderPrice();
            totalOrderPrice += orderPrice.getValue();
        }

        return totalOrderPrice;
    }

    public void addOrder(Order order) {
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.addOrder(order);
        }
    }

    private static void checkNullity(List<OrderProductRequestDto> orderProductRequestDtos) {
        if (orderProductRequestDtos == null || orderProductRequestDtos.isEmpty()) {
            throw new IllegalArgumentException(ORDER_PRODUCTS_NO_VALUE_EXCEPTION);
        }
    }

    private static void addOrderProduct(OrderProductRequestDto orderProductRequestDto, List<OrderProduct> orderProducts,
                                        ProductService productService, Long sellerId) {
        OrderProduct orderProduct = OrderProduct.from(orderProductRequestDto, productService, sellerId);
        orderProducts.add(orderProduct);
    }

    public List<OrderProductResponseDto> toDto() {
        List<OrderProductResponseDto> orderProductResponseDtos = new ArrayList<>();

        for (OrderProduct orderProduct : orderProducts) {
            OrderProductResponseDto orderProductResponseDto = OrderProductEntityToDtoMapper.toDto(orderProduct);
            orderProductResponseDtos.add(orderProductResponseDto);
        }

        return orderProductResponseDtos;
    }
}
