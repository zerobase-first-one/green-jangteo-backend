package com.firstone.greenjangteo.user.model.embedment;

import com.firstone.greenjangteo.user.dto.AddressDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.*;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Address {
    private String city;
    private String street;
    private String zipcode;
    private String detailedAddress;

    private static final String CITY_PATTERN = "^[가-힣]{1,5}$";
    private static final String STREET_PATTERN = "^[가-힣0-9 ]+$";
    private static final String ZIPCODE_PATTERN = "^\\d{5}$";
    private static final String DETAILED_ADDRESS_PATTERN = "^[가-힣0-9 ]+$";

    private Address(String city, String street, String zipcode, String detailedAddress) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.detailedAddress = detailedAddress;
    }

    public static Address from(AddressDto addressDto) {
        String city = addressDto.getCity();
        String street = addressDto.getStreet();
        String zipcode = addressDto.getZipcode();
        String detailedAddress = addressDto.getDetailedAddress();

        validate(city, street, zipcode, detailedAddress);

        return Address.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .detailedAddress(detailedAddress)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street)
                && Objects.equals(zipcode, address.zipcode)
                && Objects.equals(detailedAddress, address.detailedAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode, detailedAddress);
    }

    public AddressDto toDto() {
        return AddressDto.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .detailedAddress(detailedAddress)
                .build();
    }

    private static void validate(String city, String street, String zipcode, String detailedAddress) {
        checkAddressIsNotBlank(city, street, zipcode, detailedAddress);
        checkAddressPattern(city, street, zipcode, detailedAddress);
    }

    private static void checkAddressIsNotBlank(String city, String street, String zipcode, String detailedAddress) {
        checkCityIsNotBlank(city);
        checkStreetIsNotBlank(street);
        checkZipcodeIsNotBlank(zipcode);
        checkDetailedAddressIsNotBlank(detailedAddress);
    }


    private static void checkCityIsNotBlank(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException(CITY_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkStreetIsNotBlank(String street) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException(STREET_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkZipcodeIsNotBlank(String zipcode) {
        if (zipcode == null || zipcode.isBlank()) {
            throw new IllegalArgumentException(ZIPCODE_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkDetailedAddressIsNotBlank(String detailedAddress) {
        if (detailedAddress == null || detailedAddress.isBlank()) {
            throw new IllegalArgumentException(DETAILED_ADDRESS_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkAddressPattern(String city, String street, String zipcode, String detailedAddress) {
        checkCityPattern(city);
        checkStreetPattern(street);
        checkZipcodePattern(zipcode);
        checkDetailedAddressPattern(detailedAddress);
    }

    private static void checkCityPattern(String city) {
        if (!city.matches(CITY_PATTERN)) {
            throw new IllegalArgumentException(INVALID_CITY_EXCEPTION);
        }
    }

    private static void checkStreetPattern(String street) {
        if (!street.matches(STREET_PATTERN)) {
            throw new IllegalArgumentException(INVALID_STREET_EXCEPTION);
        }
    }

    private static void checkZipcodePattern(String zipcode) {
        if (!zipcode.matches(ZIPCODE_PATTERN)) {
            throw new IllegalArgumentException(INVALID_ZIPCODE_EXCEPTION);
        }
    }

    private static void checkDetailedAddressPattern(String detailedAddress) {
        if (!detailedAddress.matches(DETAILED_ADDRESS_PATTERN)) {
            throw new IllegalArgumentException(INVALID_DETAILED_ADDRESS_EXCEPTION);
        }
    }
}
