package com.firstone.greenjangteo.user.model;

import javax.persistence.AttributeConverter;
import java.util.Objects;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.PHONE_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_PHONE_EXCEPTION;

public class Phone {
    private final String phone;
    private static final String PHONE_PATTERN = "^010\\d{8}$";

    private Phone(String phone) {
        this.phone = phone;
    }

    public static Phone of(String phone) {
        validate(phone);

        return new Phone(phone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone1 = (Phone) o;
        return Objects.equals(phone, phone1.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }

    String getValue() {
        return phone;
    }

    private static void validate(String phone) {
        checkPhoneIsNotBlank(phone);
        checkPhonePattern(phone);
    }

    private static void checkPhoneIsNotBlank(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException(PHONE_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkPhonePattern(String phone) {
        if (!phone.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException(INVALID_PHONE_EXCEPTION);
        }
    }

    public static class PhoneConverter implements AttributeConverter<Phone, String> {
        @Override
        public String convertToDatabaseColumn(Phone phone) {
            return phone == null ? null : phone.phone;
        }

        @Override
        public Phone convertToEntityAttribute(String phone) {
            return phone == null ? null : new Phone(phone);
        }
    }
}
