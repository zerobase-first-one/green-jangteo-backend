package com.firstone.greenjangteo.order.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderPriceTest {
    @DisplayName("동일한 가격과 수량을 전송하면 동등한 OrderPrice 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given, when
        OrderPrice orderPrice1 = OrderPrice.from(PRICE1, Quantity.of(QUANTITY1));
        OrderPrice orderPrice2 = OrderPrice.from(PRICE1, Quantity.of(QUANTITY1));

        // then
        assertThat(orderPrice1).isEqualTo(orderPrice2);
        assertThat(orderPrice1.hashCode()).isEqualTo(orderPrice2.hashCode());
    }

    @DisplayName("다른 가격을 전송하면 동등하지 않은 OrderPrice 인스턴스를 생성한다.")
    @Test
    void fromDifferentPrice() {
        // given, when
        OrderPrice orderPrice1 = OrderPrice.from(PRICE1, Quantity.of(QUANTITY1));
        OrderPrice orderPrice2 = OrderPrice.from(PRICE2, Quantity.of(QUANTITY1));

        // then
        assertThat(orderPrice1).isNotEqualTo(orderPrice2);
        assertThat(orderPrice1.hashCode()).isNotEqualTo(orderPrice2.hashCode());
    }

    @DisplayName("다른 주문 수량을 전송하면 동등하지 않은 OrderPrice 인스턴스를 생성한다.")
    @Test
    void fromDifferentQuantity() {
        // given, when
        OrderPrice orderPrice1 = OrderPrice.from(PRICE1, Quantity.of(QUANTITY1));
        OrderPrice orderPrice2 = OrderPrice.from(PRICE1, Quantity.of(QUANTITY2));

        // then
        assertThat(orderPrice1).isNotEqualTo(orderPrice2);
        assertThat(orderPrice1.hashCode()).isNotEqualTo(orderPrice2.hashCode());
    }
}
