package com.firstone.greenjangteo.order.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.excpeption.serious.InconsistentSellerIdException;
import com.firstone.greenjangteo.order.model.OrderPrice;
import com.firstone.greenjangteo.order.model.Quantity;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

import static com.firstone.greenjangteo.order.excpeption.message.InconsistentIdExceptionMessage.INCONSISTENT_SELLER_ID_EXCEPTION_REQUIRED_ID;
import static com.firstone.greenjangteo.order.excpeption.message.InconsistentIdExceptionMessage.INCONSISTENT_SELLER_ID_EXCEPTION_TRANSFERRED_ID;

@Entity(name = "order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_product")
public class OrderProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Convert(converter = Quantity.QuantityConverter.class)
    @Column(nullable = false)
    private Quantity quantity;

    @Convert(converter = OrderPrice.OrderPriceConverter.class)
    @Column(nullable = false)
    private OrderPrice orderPrice;

    private OrderProduct(Product product, Quantity quantity, OrderPrice orderPrice) {
        this.product = product;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
    }

    public static OrderProduct from
            (OrderProductRequestDto orderProductRequestDto, ProductService productService, Long sellerId) {
        Long productId = parseId(orderProductRequestDto.getProductId());
        Product product = productService.getProduct(productId);

        validateSellerIdConsistency(product.getStore().getSellerId(), sellerId);

        Quantity quantity = Quantity.of(orderProductRequestDto.getQuantity());
        OrderPrice orderPrice = OrderPrice.from(product.getPrice(), quantity);

        return new OrderProduct(product, quantity, orderPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProduct that = (OrderProduct) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order)
                && Objects.equals(product, that.product) && Objects.equals(quantity, that.quantity)
                && Objects.equals(orderPrice, that.orderPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, product, quantity, orderPrice);
    }

    public void addOrder(Order order) {
        this.order = order;
    }

    private static Long parseId(String productId) {
        InputFormatValidator.validateId(productId);
        return Long.parseLong(productId);
    }

    private static void validateSellerIdConsistency(Long productSellerId, Long sellerId) {
        if (!productSellerId.equals(sellerId)) {
            throw new InconsistentSellerIdException(
                    INCONSISTENT_SELLER_ID_EXCEPTION_REQUIRED_ID + productSellerId
                            + INCONSISTENT_SELLER_ID_EXCEPTION_TRANSFERRED_ID + sellerId);
        }
    }
}
