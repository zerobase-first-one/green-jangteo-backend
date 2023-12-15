package com.firstone.greenjangteo.order.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.order.excpeption.message.BlankExceptionMessage.ORDER_QUANTITY_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.order.excpeption.message.InvalidExceptionMessage.INVALID_ORDER_QUANTITY_EXCEPTION;
import static com.firstone.greenjangteo.utility.RegularExpressionConstant.POSITIVE_INTEGER_PATTERN;

public class Quantity {
    private final int quantity;

    private Quantity(int quantity) {
        this.quantity = quantity;
    }

    public static Quantity of(String quantity) {
        validate(quantity);
        return new Quantity(Integer.parseInt(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity1 = (Quantity) o;
        return quantity == quantity1.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }

    int getValue() {
        return quantity;
    }

    private static void validate(String quantity) {
        checkQuantityIsNotBlank(quantity);
        checkQuantityPattern(quantity);
    }

    private static void checkQuantityIsNotBlank(String quantity) {
        if (quantity == null || quantity.isBlank()) {
            throw new IllegalArgumentException(ORDER_QUANTITY_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkQuantityPattern(String quantity) {
        if (!quantity.matches(POSITIVE_INTEGER_PATTERN)) {
            throw new IllegalArgumentException(INVALID_ORDER_QUANTITY_EXCEPTION + quantity);
        }
    }

    @Converter
    public static class QuantityConverter implements AttributeConverter<Quantity, Integer> {
        @Override
        public Integer convertToDatabaseColumn(Quantity quantity) {
            return quantity.quantity;
        }

        @Override
        public Quantity convertToEntityAttribute(Integer quantity) {
            return quantity == null ? null : new Quantity(quantity);
        }
    }
}
