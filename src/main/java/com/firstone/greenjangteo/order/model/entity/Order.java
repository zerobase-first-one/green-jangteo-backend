package com.firstone.greenjangteo.order.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.OrderProducts;
import com.firstone.greenjangteo.order.model.OrderStatus;
import com.firstone.greenjangteo.order.model.TotalOrderPrice;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.model.embedment.Address;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

import static com.firstone.greenjangteo.order.model.OrderStatus.BEFORE_PAYMENT;

@Entity(name = "orders")
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Embedded
    private OrderProducts orderProducts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Convert(converter = TotalOrderPrice.TotalOrderPriceConverter.class)
    @Column(nullable = false)
    private TotalOrderPrice totalOrderPrice;

    @Embedded
    private Address shippingAddress;

    @Builder
    private Order(Long id, Store store, User buyer, OrderProducts orderProducts, OrderStatus orderStatus,
                  TotalOrderPrice totalOrderPrice, Address shippingAddress) {
        this.id = id;
        this.store = store;
        this.buyer = buyer;
        this.orderProducts = orderProducts;
        this.orderStatus = orderStatus;
        this.totalOrderPrice = totalOrderPrice;
        this.shippingAddress = shippingAddress;
    }

    public static Order from(Store store, User buyer,
                             OrderRequestDto orderRequestDto, ProductService productService) {
        OrderProducts orderProducts
                = OrderProducts.from(orderRequestDto.getOrderProductRequestDtos(), productService, store.getSellerId());

        Order order = Order.builder()
                .store(store)
                .buyer(buyer)
                .orderProducts(orderProducts)
                .orderStatus(BEFORE_PAYMENT)
                .totalOrderPrice(TotalOrderPrice.from(orderProducts))
                .shippingAddress(Address.from(orderRequestDto.getShippingAddressDto()))
                .build();

        orderProducts.addOrder(order);

        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(store, order.store)
                && Objects.equals(buyer, order.buyer) && orderStatus == order.orderStatus
                && Objects.equals(totalOrderPrice, order.totalOrderPrice)
                && Objects.equals(shippingAddress, order.shippingAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, store, buyer, orderStatus, totalOrderPrice, shippingAddress);
    }
}
