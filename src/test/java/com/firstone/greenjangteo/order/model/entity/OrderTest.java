package com.firstone.greenjangteo.order.model.entity;

import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.repository.OrderRepository;
import com.firstone.greenjangteo.order.service.OrderService;
import com.firstone.greenjangteo.order.testutil.OrderTestObjectFactory;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.testutil.StoreTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY2;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("동일한 객체를 전송하면 동등한 Order 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.toString())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );
        userRepository.saveAll(List.of(seller, buyer));

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        List<OrderProductRequestDto> orderProductRequestDtos
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()),
                List.of(QUANTITY1, QUANTITY2)
        );

        OrderRequestDto orderRequestDto
                = OrderTestObjectFactory.createOrderRequestDto(
                seller.getId().toString(), buyer.getId().toString(), orderProductRequestDtos
        );

        // when
        Order order1 = Order.from(store, buyer, orderRequestDto, productService);
        Order order2 = Order.from(store, buyer, orderRequestDto, productService);

        // then
        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @DisplayName("다른 객체를 전송하면 동등하지 않은 Order 인스턴스를 생성한다.")
    @Test
    void fromDifferentPrice() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.toString())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );
        userRepository.saveAll(List.of(seller, buyer));

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        List<OrderProductRequestDto> orderProductRequestDtos1
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()),
                List.of(QUANTITY1, QUANTITY2)
        );

        List<OrderProductRequestDto> orderProductRequestDtos2
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()),
                List.of(QUANTITY2, QUANTITY1)
        );

        OrderRequestDto orderRequestDto1
                = OrderTestObjectFactory.createOrderRequestDto(
                seller.getId().toString(), buyer.getId().toString(), orderProductRequestDtos1
        );

        OrderRequestDto orderRequestDto2
                = OrderTestObjectFactory.createOrderRequestDto(
                seller.getId().toString(), buyer.getId().toString(), orderProductRequestDtos2
        );

        // when
        Order order1 = Order.from(store, buyer, orderRequestDto1, productService);
        Order order2 = Order.from(store, buyer, orderRequestDto2, productService);

        // then
        assertThat(order1).isNotEqualTo(order2);
        assertThat(order1.hashCode()).isNotEqualTo(order2.hashCode());
    }
}