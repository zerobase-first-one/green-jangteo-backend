package com.firstone.greenjangteo.order.model;

import com.firstone.greenjangteo.order.model.entity.OrderProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY2;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.PRICE1;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.PRICE2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TotalOrderPriceTest {
    @DisplayName("총 가격이 동일한 주문 상품 목록을 전송하면 동등한 TotalOrderPrice 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        OrderProduct orderProduct = mock(OrderProduct.class);
        OrderProducts orderProducts = new OrderProducts(List.of(orderProduct, orderProduct));

        OrderPrice orderPrice = OrderPrice.from(PRICE1, Quantity.of(QUANTITY1));

        when(orderProduct.getOrderPrice()).thenReturn(orderPrice);

        // when
        TotalOrderPrice totalOrderPrice1 = TotalOrderPrice.from(orderProducts);
        TotalOrderPrice totalOrderPrice2 = TotalOrderPrice.from(orderProducts);

        // then
        assertThat(totalOrderPrice1).isEqualTo(totalOrderPrice2);
        assertThat(totalOrderPrice1.hashCode()).isEqualTo(totalOrderPrice2.hashCode());
    }

    @DisplayName("총 가격이 다른 주문 상품 목록을 전송하면 동등하지 않은 TotalOrderPrice 인스턴스를 생성한다.")
    @Test
    void fromDifferentValue() {
        // given
        OrderProduct orderProduct = mock(OrderProduct.class);
        OrderProducts orderProducts = new OrderProducts(List.of(orderProduct, orderProduct));

        OrderPrice orderPrice1 = OrderPrice.from(PRICE1, Quantity.of(QUANTITY1));
        OrderPrice orderPrice2 = OrderPrice.from(PRICE2, Quantity.of(QUANTITY2));

        when(orderProduct.getOrderPrice()).thenReturn(orderPrice1);

        // when
        TotalOrderPrice totalOrderPrice1 = TotalOrderPrice.from(orderProducts);

        when(orderProduct.getOrderPrice()).thenReturn(orderPrice2);
        TotalOrderPrice totalOrderPrice2 = TotalOrderPrice.from(orderProducts);

        // then
        assertThat(totalOrderPrice1).isNotEqualTo(totalOrderPrice2);
        assertThat(totalOrderPrice1.hashCode()).isNotEqualTo(totalOrderPrice2.hashCode());
    }
}
