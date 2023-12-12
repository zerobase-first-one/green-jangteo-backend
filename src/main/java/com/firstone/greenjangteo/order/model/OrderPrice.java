package com.firstone.greenjangteo.order.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

public class OrderPrice {
    private final int orderPrice;

    private OrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public static OrderPrice from(int price, Quantity quantity) {
        return new OrderPrice(price * quantity.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPrice that = (OrderPrice) o;
        return orderPrice == that.orderPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderPrice);
    }

    int getValue() {
        return orderPrice;
    }

    @Converter
    public static class OrderPriceConverter implements AttributeConverter<OrderPrice, Integer> {
        @Override
        public Integer convertToDatabaseColumn(OrderPrice orderPrice) {
            return orderPrice.orderPrice;
        }

        @Override
        public OrderPrice convertToEntityAttribute(Integer orderPrice) {
            return orderPrice == null ? null : new OrderPrice(orderPrice);
        }
    }
}
