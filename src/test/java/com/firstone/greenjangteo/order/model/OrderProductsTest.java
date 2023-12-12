package com.firstone.greenjangteo.order.model;

import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.testutil.OrderTestObjectFactory;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.testutil.StoreTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.order.excpeption.message.BlankExceptionMessage.ORDER_PRODUCTS_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.PRICE1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.*;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.PRICE2;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderProductsTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("객체를 전송해 OrderProducts 인스턴스를 생성한다.")
    @Test
    void from() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        List<OrderProductRequestDto> orderProductRequestDtos = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        // when
        OrderProducts orderProducts = OrderProducts.from(orderProductRequestDtos, productService, store.getSellerId());

        // then
        assertThat(orderProducts.getOrderItems()).hasSize(2)
                .extracting("product", "quantity", "orderPrice")
                .containsExactlyInAnyOrder(
                        tuple(product1, Quantity.of(QUANTITY1), OrderPrice.from(PRICE1, Quantity.of(QUANTITY1))),
                        tuple(product2, Quantity.of(QUANTITY2), OrderPrice.from(PRICE2, Quantity.of(QUANTITY2)))
                );
    }

    @DisplayName("동일한 객체를 전송하면 동등한 OrderProducts 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        List<OrderProductRequestDto> orderProductRequestDtos1 = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        List<OrderProductRequestDto> orderProductRequestDtos2 = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        // when
        OrderProducts orderProducts1 = OrderProducts.from(orderProductRequestDtos1, productService, store.getSellerId());
        OrderProducts orderProducts2 = OrderProducts.from(orderProductRequestDtos2, productService, store.getSellerId());

        // then
        assertThat(orderProducts1).isEqualTo(orderProducts2);
        assertThat(orderProducts1.hashCode()).isEqualTo(orderProducts2.hashCode());
    }

    @DisplayName("다른 객체를 전송하면 동등하지 않은 OrderProducts 인스턴스를 생성한다.")
    @Test
    void fromDifferentPrice() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        List<OrderProductRequestDto> orderProductRequestDtos1 = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        List<OrderProductRequestDto> orderProductRequestDtos2 = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()), List.of(QUANTITY2, QUANTITY1)
        );

        // when
        OrderProducts orderProducts1 = OrderProducts.from(orderProductRequestDtos1, productService, store.getSellerId());
        OrderProducts orderProducts2 = OrderProducts.from(orderProductRequestDtos2, productService, store.getSellerId());

        // then
        assertThat(orderProducts1).isNotEqualTo(orderProducts2);
        assertThat(orderProducts1.hashCode()).isNotEqualTo(orderProducts2.hashCode());
    }

    @DisplayName("상품 목록 객체를 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @Test
    void fromNullValue() {
        // given, when, then
        assertThatThrownBy(() -> OrderProducts.from(null, productService, Long.parseLong(SELLER_ID1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ORDER_PRODUCTS_NO_VALUE_EXCEPTION);
    }

    @DisplayName("총 주문 금액을 계산한다.")
    @Test
    void computeTotalOrderPrice() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        List<OrderProductRequestDto> orderProductRequestDtos = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        OrderProducts orderProducts = OrderProducts.from(orderProductRequestDtos, productService, store.getSellerId());

        int orderPrice1 = PRICE1 * Integer.parseInt(QUANTITY1);
        int orderPrice2 = PRICE2 * Integer.parseInt(QUANTITY2);

        // when
        int totalOrderPrice = orderProducts.computeTotalOrderPrice();

        // then
        assertThat(totalOrderPrice).isEqualTo(orderPrice1 + orderPrice2);
    }
}