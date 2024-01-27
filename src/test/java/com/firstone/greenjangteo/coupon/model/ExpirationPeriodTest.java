package com.firstone.greenjangteo.coupon.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.firstone.greenjangteo.coupon.exception.message.BlankExceptionMessage.EXPIRATION_PERIOD_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.exception.message.InvalidExceptionMessage.INVALID_EXPIRATION_PERIOD_EXCEPTION;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.EXPIRATION_PERIOD1;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.EXPIRATION_PERIOD2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ExpirationPeriodTest {
    @DisplayName("동일한 유효기간을 전송하면 동등한 ExpirationPeriod 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        ExpirationPeriod expirationPeriod1 = ExpirationPeriod.of(EXPIRATION_PERIOD1);
        ExpirationPeriod expirationPeriod2 = ExpirationPeriod.of(EXPIRATION_PERIOD1);

        // then
        assertThat(expirationPeriod1).isEqualTo(expirationPeriod2);
        assertThat(expirationPeriod1.hashCode()).isEqualTo(expirationPeriod2.hashCode());
    }

    @DisplayName("다른 유효기간을 전송하면 동등하지 않은 ExpirationPeriod 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        ExpirationPeriod expirationPeriod1 = ExpirationPeriod.of(EXPIRATION_PERIOD1);
        ExpirationPeriod expirationPeriod2 = ExpirationPeriod.of(EXPIRATION_PERIOD2);

        // then
        assertThat(expirationPeriod1).isNotEqualTo(expirationPeriod2);
        assertThat(expirationPeriod1.hashCode()).isNotEqualTo(expirationPeriod2.hashCode());
    }

    @DisplayName("유효기간을 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void ofBlankValue(String expirationPeriod) {
        // when, then
        assertThatThrownBy(() -> ExpirationPeriod.of(expirationPeriod))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EXPIRATION_PERIOD_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 유효기간을 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"-1", "0", "abc", "ㄱㄴㄷ", "가나다"})
    void ofInvalidValue(String expirationPeriod) {
        // given, when, then
        assertThatThrownBy(() -> ExpirationPeriod.of(expirationPeriod))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_EXPIRATION_PERIOD_EXCEPTION + expirationPeriod);
    }
}