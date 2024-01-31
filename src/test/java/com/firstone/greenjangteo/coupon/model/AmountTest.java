package com.firstone.greenjangteo.coupon.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.firstone.greenjangteo.coupon.exception.message.BlankExceptionMessage.COUPON_AMOUNT_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.exception.message.InvalidExceptionMessage.INVALID_COUPON_AMOUNT_EXCEPTION;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.AMOUNT1;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.AMOUNT2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AmountTest {
    @DisplayName("동일한 쿠폰 금액을 전송하면 동등한 Amount 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        Amount amount1 = Amount.of(AMOUNT1);
        Amount amount2 = Amount.of(AMOUNT1);

        // then
        assertThat(amount1).isEqualTo(amount2);
        assertThat(amount1.hashCode()).isEqualTo(amount2.hashCode());
    }

    @DisplayName("다른 쿠폰 금액을 전송하면 동등하지 않은 Amount 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        Amount amount1 = Amount.of(AMOUNT1);
        Amount amount2 = Amount.of(AMOUNT2);

        // then
        assertThat(amount1).isNotEqualTo(amount2);
        assertThat(amount1.hashCode()).isNotEqualTo(amount2.hashCode());
    }

    @DisplayName("쿠폰 금액을 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void ofBlankValue(String amount) {
        // when, then
        assertThatThrownBy(() -> Amount.of(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(COUPON_AMOUNT_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 쿠폰 금액을 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"-1", "0", "abc", "ㄱㄴㄷ", "가나다"})
    void ofInvalidValue(String amount) {
        // given, when, then
        assertThatThrownBy(() -> Amount.of(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_COUPON_AMOUNT_EXCEPTION + amount);
    }
}