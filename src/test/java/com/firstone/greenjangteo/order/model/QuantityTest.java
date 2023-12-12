package com.firstone.greenjangteo.order.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.firstone.greenjangteo.order.excpeption.message.BlankExceptionMessage.ORDER_QUANTITY_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.order.excpeption.message.InvalidExceptionMessage.INVALID_ORDER_QUANTITY_EXCEPTION;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class QuantityTest {
    @DisplayName("동일한 주문 수량을 전송하면 동등한 Quantity 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        Quantity quantity1 = Quantity.of(QUANTITY1);
        Quantity quantity2 = Quantity.of(QUANTITY1);

        // then
        assertThat(quantity1).isEqualTo(quantity2);
        assertThat(quantity1.hashCode()).isEqualTo(quantity2.hashCode());
    }

    @DisplayName("다른 주문 수량을 전송하면 동등하지 않은 Quantity 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        Quantity quantity1 = Quantity.of(QUANTITY1);
        Quantity quantity2 = Quantity.of(QUANTITY2);

        // then
        assertThat(quantity1).isNotEqualTo(quantity2);
        assertThat(quantity1.hashCode()).isNotEqualTo(quantity2.hashCode());
    }

    @DisplayName("주문 수량을 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void ofBlankValue(String quantity) {

        // when, then
        assertThatThrownBy(() -> Quantity.of(quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ORDER_QUANTITY_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 주문 수량을 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"-1", "0", "abc", "ㄱㄴㄷ", "가나다"})
    void ofInvalidValue(String quantity) {
        // given, when, then
        assertThatThrownBy(() -> Quantity.of(quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_ORDER_QUANTITY_EXCEPTION + quantity);
    }
}