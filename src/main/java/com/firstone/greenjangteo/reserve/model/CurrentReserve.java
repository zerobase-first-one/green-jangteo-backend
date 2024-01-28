package com.firstone.greenjangteo.reserve.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.reserve.exception.message.InvalidExceptionMessage.INVALID_UPDATING_RESERVE_EXCEPTION;

public class CurrentReserve {
    private final int currentReserve;

    public CurrentReserve(int currentReserve) {
        this.currentReserve = currentReserve;
    }

    public static CurrentReserve addReserve(CurrentReserve currentReserve, int addedReserve) {
        validateUpdatingReserve(addedReserve);
        int newCurrentReserve = currentReserve.getValue() + addedReserve;

        return new CurrentReserve(newCurrentReserve);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentReserve that = (CurrentReserve) o;
        return currentReserve == that.currentReserve;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentReserve);
    }

    public int getValue() {
        return currentReserve;
    }

    private static void validateUpdatingReserve(int updatingReserve) {
        if (updatingReserve < 0) {
            throw new IllegalArgumentException(INVALID_UPDATING_RESERVE_EXCEPTION + updatingReserve);
        }
    }

    @Converter
    public static class CurrentReserveConverter implements AttributeConverter<CurrentReserve, Integer> {
        @Override
        public Integer convertToDatabaseColumn(CurrentReserve currentReserve) {
            return currentReserve == null ? null : currentReserve.currentReserve;
        }

        @Override
        public CurrentReserve convertToEntityAttribute(Integer currentReserve) {
            return currentReserve == null ? null : new CurrentReserve(currentReserve);
        }
    }
}
