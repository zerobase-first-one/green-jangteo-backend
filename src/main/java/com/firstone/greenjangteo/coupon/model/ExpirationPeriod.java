package com.firstone.greenjangteo.coupon.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.firstone.greenjangteo.coupon.excpeption.message.BlankExceptionMessage.EXPIRATION_PERIOD_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.excpeption.message.InvalidExceptionMessage.INVALID_EXPIRATION_PERIOD_EXCEPTION;
import static com.firstone.greenjangteo.utility.RegularExpressionConstant.POSITIVE_INTEGER_PATTERN;

public class ExpirationPeriod {
    private final int expirationPeriod;

    private ExpirationPeriod(int expirationPeriod) {
        this.expirationPeriod = expirationPeriod;
    }

    public static ExpirationPeriod of(String expirationPeriod) {
        validate(expirationPeriod);
        return new ExpirationPeriod(Integer.parseInt(expirationPeriod));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpirationPeriod that = (ExpirationPeriod) o;
        return expirationPeriod == that.expirationPeriod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expirationPeriod);
    }

    public LocalDateTime computeExpirationTime(LocalDateTime now) {
        return now.plusDays(expirationPeriod);
    }

    private static void validate(String expirationPeriod) {
        checkExpirationPeriodIsNotBlank(expirationPeriod);
        checkExpirationPeriodPattern(expirationPeriod);
    }

    private static void checkExpirationPeriodIsNotBlank(String expirationPeriod) {
        if (expirationPeriod == null || expirationPeriod.isBlank()) {
            throw new IllegalArgumentException(EXPIRATION_PERIOD_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkExpirationPeriodPattern(String expirationPeriod) {
        if (!expirationPeriod.matches(POSITIVE_INTEGER_PATTERN)) {
            throw new IllegalArgumentException(INVALID_EXPIRATION_PERIOD_EXCEPTION + expirationPeriod);
        }
    }

    @Converter
    public static class ExpirationPeriodConverter implements AttributeConverter<ExpirationPeriod, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ExpirationPeriod expirationPeriod) {
            return expirationPeriod.expirationPeriod;
        }

        @Override
        public ExpirationPeriod convertToEntityAttribute(Integer expirationPeriod) {
            return expirationPeriod == null ? null : new ExpirationPeriod(expirationPeriod);
        }
    }
}
