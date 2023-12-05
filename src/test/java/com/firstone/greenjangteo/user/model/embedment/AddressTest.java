package com.firstone.greenjangteo.user.model.embedment;

import com.firstone.greenjangteo.user.dto.AddressDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.ActiveProfiles;

import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
class AddressTest {
    @DisplayName("동일한 주소를 전송하면 동등한 Address 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        AddressDto addressDto = AddressDto.builder()
                .city(CITY1)
                .street(STREET1)
                .zipcode(ZIPCODE1)
                .detailedAddress(DETAILED_ADDRESS1)
                .build();

        // when
        Address address1 = Address.from(addressDto);
        Address address2 = Address.from(addressDto);

        // then
        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @DisplayName("다른 주소를 전송하면 동등하지 않은 Address 인스턴스를 생성한다.")
    @Test
    void fromDifferentValue() {
        // given
        AddressDto addressDto1 = AddressDto.builder()
                .city(CITY1)
                .street(STREET1)
                .zipcode(ZIPCODE1)
                .detailedAddress(DETAILED_ADDRESS1)
                .build();

        AddressDto addressDto2 = AddressDto.builder()
                .city(CITY2)
                .street(STREET1)
                .zipcode(ZIPCODE1)
                .detailedAddress(DETAILED_ADDRESS1)
                .build();

        // when
        Address address1 = Address.from(addressDto1);
        Address address2 = Address.from(addressDto2);

        // then
        assertThat(address1).isNotEqualTo(address2);
        assertThat(address1.hashCode()).isNotEqualTo(address2.hashCode());
    }

    @DisplayName("주소의 일부를 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({
            "null, 테헤란로, 12345, 서울특별시 강남구 테헤란로 2길",
            "서울, null, 12345, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란로, null, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란로, 12345, null",
    })
    void fromBlankValue(String city, String street, String zipcode, String detailedAddress) {
        // given
        AddressDto addressDto = AddressDto.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .detailedAddress(detailedAddress)
                .build();

        // when, then
        assertThatThrownBy(() -> Address.from(addressDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유효하지 않은 주소를 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({
            "서울특별시임, 테헤란로, 12345, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란 street, 12345, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란로, 123456, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란로, 12345, 서울특별시 강남구 테헤란로 2길!",
    })
    void fromInvalidValue(String city, String street, String zipcode, String detailedAddress) {
        // given
        AddressDto addressDto = AddressDto.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .detailedAddress(detailedAddress)
                .build();

        // when, then
        assertThatThrownBy(() -> Address.from(addressDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}