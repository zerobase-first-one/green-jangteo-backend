package com.firstone.greenjangteo.user.model;

import javax.persistence.AttributeConverter;
import java.util.Objects;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.FULL_NAME_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_FULL_NAME_EXCEPTION;

public class FullName {
    private final String fullName;

    private FullName(String fullName) {
        this.fullName = fullName;
    }

    public static FullName of(String fullName) {
        validate(fullName);

        return new FullName(fullName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullName fullName1 = (FullName) o;
        return Objects.equals(fullName, fullName1.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName);
    }

    private static void validate(String fullName) {
        checkFullNameIsNotEmpty(fullName);
        checkFullnamePattern(fullName);
    }

    private static void checkFullNameIsNotEmpty(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            throw new IllegalArgumentException(FULL_NAME_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkFullnamePattern(String fullName) {
        if (!fullName.matches("^[가-힣]{2,5}$")) {
            throw new IllegalArgumentException(INVALID_FULL_NAME_EXCEPTION);
        }
    }

    public static class FullNameConverter implements AttributeConverter<FullName, String> {
        @Override
        public String convertToDatabaseColumn(FullName fullName) {
            return fullName == null ? null : fullName.fullName;
        }

        @Override
        public FullName convertToEntityAttribute(String fullName) {
            return fullName == null ? null : new FullName(fullName);
        }
    }
}