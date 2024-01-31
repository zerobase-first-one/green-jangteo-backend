package com.firstone.greenjangteo.order.model.entity;

import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.excpeption.serious.InconsistentSellerIdException;
import com.firstone.greenjangteo.order.model.OrderPrice;
import com.firstone.greenjangteo.order.model.Quantity;
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

import static com.firstone.greenjangteo.order.excpeption.message.InconsistentIdExceptionMessage.INCONSISTENT_SELLER_ID_EXCEPTION_REQUIRED_ID;
import static com.firstone.greenjangteo.order.excpeption.message.InconsistentIdExceptionMessage.INCONSISTENT_SELLER_ID_EXCEPTION_TRANSFERRED_ID;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.PRICE1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.*;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderProductTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("객체를 전송해 OrderProduct 인스턴스를 생성한다.")
    @Test
    void from() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        productRepository.save(product);

        OrderProductRequestDto orderProductRequestDto = new OrderProductRequestDto(product.getId().toString(), QUANTITY1);
        Quantity quantity = Quantity.of(QUANTITY1);

        // when
        OrderProduct orderProduct = OrderProduct.from(orderProductRequestDto, productService, store.getSellerId());

        // then
        assertThat(orderProduct.getProduct()).isEqualTo(product);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
        assertThat(orderProduct.getOrderPrice()).isEqualTo(OrderPrice.from(PRICE1, quantity));
    }

    @DisplayName("동일한 객체를 전송하면 동등한 OrderProduct 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        productRepository.save(product);

        OrderProductRequestDto orderProductRequestDto = new OrderProductRequestDto(product.getId().toString(), QUANTITY1);

        // when
        OrderProduct orderProduct1 = OrderProduct.from(orderProductRequestDto, productService, store.getSellerId());
        OrderProduct orderProduct2 = OrderProduct.from(orderProductRequestDto, productService, store.getSellerId());

        // then
        assertThat(orderProduct1).isEqualTo(orderProduct2);
        assertThat(orderProduct1.hashCode()).isEqualTo(orderProduct2.hashCode());
    }

    @DisplayName("다른 객체를 전송하면 동등하지 않은 OrderProduct 인스턴스를 생성한다.")
    @Test
    void fromDifferentPrice() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        productRepository.save(product);

        OrderProductRequestDto orderProductRequestDto1 = new OrderProductRequestDto(product.getId().toString(), QUANTITY1);
        OrderProductRequestDto orderProductRequestDto2 = new OrderProductRequestDto(product.getId().toString(), QUANTITY2);

        // when
        OrderProduct orderProduct1 = OrderProduct.from(orderProductRequestDto1, productService, store.getSellerId());
        OrderProduct orderProduct2 = OrderProduct.from(orderProductRequestDto2, productService, store.getSellerId());

        // then
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);
        assertThat(orderProduct1.hashCode()).isNotEqualTo(orderProduct2.hashCode());
    }

    @DisplayName("상품의 판매자 ID와 전송된 판매자 ID가 일치하지 않으면 InconsistentSellerIdException이 발생한다.")
    @Test
    void fromInconsistentSellerId() {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );

        Product product = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        productRepository.save(product);

        OrderProductRequestDto orderProductRequestDto = new OrderProductRequestDto(product.getId().toString(), QUANTITY1);
        // when, then
        assertThatThrownBy(() -> OrderProduct.from(orderProductRequestDto, productService, Long.parseLong(SELLER_ID2)))
                .isInstanceOf(InconsistentSellerIdException.class)
                .hasMessage(INCONSISTENT_SELLER_ID_EXCEPTION_REQUIRED_ID + store.getSellerId()
                        + INCONSISTENT_SELLER_ID_EXCEPTION_TRANSFERRED_ID + SELLER_ID2);
    }
}