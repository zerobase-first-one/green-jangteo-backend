package com.firstone.greenjangteo.user.domain.store.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.INVALID_STORE_NAME_EXCEPTION;
import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.STORE_NAME_NO_VALUE_EXCEPTION;

public class StoreName {
    private final String storeName;
    private static final String STORE_NAME_PATTERN = "^[a-zA-Z가-힣 ]{1,20}$";

    private StoreName(String storeName) {
        this.storeName = storeName;
    }

    public static StoreName of(String storeName) {
        validate(storeName);
        return new StoreName(storeName);
    }

    public String getValue() {
        return storeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreName storeName1 = (StoreName) o;
        return Objects.equals(storeName, storeName1.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName);
    }

    private static void validate(String storeName) {
        checkStoreNameIsNotBlank(storeName);
        checkStoreNamePattern(storeName);
    }

    private static void checkStoreNameIsNotBlank(String storeName) {
        if (storeName == null || storeName.isBlank()) {
            throw new IllegalArgumentException(STORE_NAME_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkStoreNamePattern(String storeName) {
        if (!storeName.matches(STORE_NAME_PATTERN)) {
            throw new IllegalArgumentException(INVALID_STORE_NAME_EXCEPTION);
        }
    }

    @Converter
    public static class StoreNameConverter implements AttributeConverter<StoreName, String> {
        @Override
        public String convertToDatabaseColumn(StoreName storeName) {
            return storeName == null ? null : storeName.storeName;
        }

        @Override
        public StoreName convertToEntityAttribute(String storeName) {
            return storeName == null ? null : new StoreName(storeName);
        }
    }
}
