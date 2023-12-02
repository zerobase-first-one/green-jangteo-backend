package com.firstone.greenjangteo.user.model;

import javax.persistence.AttributeConverter;
import java.util.Objects;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.USERNAME_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_USERNAME_EXCEPTION;

public class Username {
    private final String username;

    private Username(String username) {
        this.username = username;
    }

    public static Username of(String username) {
        validate(username);

        return new Username(username);
    }

    String getValue() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username1 = (Username) o;
        return Objects.equals(username, username1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    private static void validate(String username) {
        checkUsernameIsNotBlank(username);
        checkUsernamePattern(username);
    }

    private static void checkUsernameIsNotBlank(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(USERNAME_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkUsernamePattern(String username) {
        if (!username.matches("^[a-z0-9]{4,16}$")) {
            throw new IllegalArgumentException(INVALID_USERNAME_EXCEPTION);
        }
    }

    public static class UsernameConverter implements AttributeConverter<Username, String> {
        @Override
        public String convertToDatabaseColumn(Username username) {
            return username == null ? null : username.username;
        }

        @Override
        public Username convertToEntityAttribute(String username) {
            return username == null ? null : new Username(username);
        }
    }
}
