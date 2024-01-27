package com.firstone.greenjangteo.order.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.order.excpeption.message.InvalidExceptionMessage.INVALID_ORDER_PRICE_EXCEPTION;

public class TotalOrderPrice {
    private final int totalOrderPrice;

    public TotalOrderPrice(int totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    public static TotalOrderPrice from(OrderProducts orderProducts) {
        int totalOrderPrice = orderProducts.computeTotalOrderPrice();

        return new TotalOrderPrice(totalOrderPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalOrderPrice that = (TotalOrderPrice) o;
        return totalOrderPrice == that.totalOrderPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalOrderPrice);
    }

    public int getValue() {
        return totalOrderPrice;
    }

    public int computeOrderPriceAfterUpdate(int usedCouponAmount, int usedReserveAmount) {
        int orderPriceAfterCouponUsed = totalOrderPrice - (usedCouponAmount + usedReserveAmount);
        validateOrderPriceAfterUpdate(orderPriceAfterCouponUsed);

        return orderPriceAfterCouponUsed;
    }

    private static void validateOrderPriceAfterUpdate(int totalOrderPriceAfterUpdate) {
        if (totalOrderPriceAfterUpdate < 0) {
            throw new IllegalArgumentException(INVALID_ORDER_PRICE_EXCEPTION + totalOrderPriceAfterUpdate);
        }
    }

    @Converter
    public static class TotalOrderPriceConverter implements AttributeConverter<TotalOrderPrice, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TotalOrderPrice totalOrderPrice) {
            return totalOrderPrice.totalOrderPrice;
        }

        @Override
        public TotalOrderPrice convertToEntityAttribute(Integer totalOrderPrice) {
            return totalOrderPrice == null ? null : new TotalOrderPrice(totalOrderPrice);
        }
    }
}
