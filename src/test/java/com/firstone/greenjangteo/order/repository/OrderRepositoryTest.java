package com.firstone.greenjangteo.order.repository;

import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.testutil.OrderTestObjectFactory;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.repository.ProductRepository;
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

import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("판매자 ID를 통해 판매자의 주문 목록을 조회할 수 있다.")
    @Test
    void findBySellerId() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer1 = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
        );
        User buyer2 = UserTestObjectFactory.createUser(
                EMAIL3, USERNAME3, PASSWORD3, passwordEncoder, FULL_NAME3, PHONE3, List.of(ROLE_BUYER.name())
        );
        userRepository.saveAll(List.of(seller, buyer1, buyer2));

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        Order order1 = OrderTestObjectFactory.createOrder(store, buyer1, PRICE1);
        Order order2 = OrderTestObjectFactory.createOrder(store, buyer2, PRICE2);
        orderRepository.saveAll(List.of(order1, order2));

        // when
        List<Order> orders = orderRepository.findBySellerId(seller.getId());

        // then
        assertThat(orders).hasSize(2)
                .extracting(order -> order.getStore().getSellerId(), order -> order.getBuyer().getId())
                .containsExactlyInAnyOrder(
                        tuple(store.getSellerId(), buyer1.getId()),
                        tuple(store.getSellerId(), buyer2.getId())
                );
    }

    @DisplayName("구매자 ID를 통해 구매자의 주문 목록을 조회할 수 있다.")
    @Test
    void findByBuyerId() {
        // given
        User seller1 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User seller2 = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL3, USERNAME3, PASSWORD3, passwordEncoder, FULL_NAME3, PHONE3, List.of(ROLE_BUYER.name())
        );
        userRepository.saveAll(List.of(seller1, seller2, buyer));

        Store store1 = StoreTestObjectFactory.createStore(seller1.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);
        Store store2 = StoreTestObjectFactory.createStore(seller2.getId(), STORE_NAME2, DESCRIPTION2, IMAGE_URL2);

        Product product1 = StoreTestObjectFactory.createProduct(store1, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store2, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        Order order1 = OrderTestObjectFactory.createOrder(store1, buyer, PRICE1);
        Order order2 = OrderTestObjectFactory.createOrder(store2, buyer, PRICE2);
        orderRepository.saveAll(List.of(order1, order2));

        // when
        List<Order> orders = orderRepository.findByBuyerId(buyer.getId());

        // then
        assertThat(orders).hasSize(2)
                .extracting(order -> order.getStore().getSellerId(), order -> order.getBuyer().getId())
                .containsExactlyInAnyOrder(
                        tuple(store1.getSellerId(), buyer.getId()),
                        tuple(store2.getSellerId(), buyer.getId())
                );
    }
}