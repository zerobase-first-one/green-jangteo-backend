package com.firstone.greenjangteo.coupon.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.coupon.exception.message.BlankExceptionMessage.COUPON_AMOUNT_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.exception.message.InvalidExceptionMessage.INVALID_COUPON_AMOUNT_EXCEPTION;
import static com.firstone.greenjangteo.utility.RegularExpressionConstant.POSITIVE_INTEGER_PATTERN;

public class Amount {
    private final int amount;

    private Amount(int amount) {
        this.amount = amount;
    }

    public static Amount of(String amount) {
        validate(amount);
        return new Amount(Integer.parseInt(amount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount1 = (Amount) o;
        return amount == amount1.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    public int getValue() {
        return amount;
    }

    private static void validate(String amount) {
        checkAmountIsNotBlank(amount);
        checkAmountPattern(amount);
    }

    private static void checkAmountIsNotBlank(String amount) {
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException(COUPON_AMOUNT_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkAmountPattern(String amount) {
        if (!amount.matches(POSITIVE_INTEGER_PATTERN)) {
            throw new IllegalArgumentException(INVALID_COUPON_AMOUNT_EXCEPTION + amount);
        }
    }

    @Converter
    public static class AmountConverter implements AttributeConverter<Amount, Integer> {
        @Override
        public Integer convertToDatabaseColumn(Amount amount) {
            return amount.amount;
        }

        @Override
        public Amount convertToEntityAttribute(Integer amount) {
            return amount == null ? null : new Amount(amount);
        }
    }
}
