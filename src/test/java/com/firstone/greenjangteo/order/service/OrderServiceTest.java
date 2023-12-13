package com.firstone.greenjangteo.order.service;

import com.firstone.greenjangteo.cart.domain.model.Cart;
import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import com.firstone.greenjangteo.cart.repository.CartProductRepository;
import com.firstone.greenjangteo.cart.repository.CartRepository;
import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.OrderPrice;
import com.firstone.greenjangteo.order.model.Quantity;
import com.firstone.greenjangteo.order.model.TotalOrderPrice;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.repository.OrderRepository;
import com.firstone.greenjangteo.order.testutil.OrderTestObjectFactory;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.testutil.StoreTestObjectFactory;
import com.firstone.greenjangteo.user.model.embedment.Address;
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

import static com.firstone.greenjangteo.order.model.OrderStatus.BEFORE_PAYMENT;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY2;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderServiceTest {
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
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("주문 요청 양식을 전송해 주문을 생성한다.")
    @Test
    void createOrder() {
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
        Order createdOrder = orderService.createOrder(orderRequestDto);
        Order foundOrder = orderRepository.findById(createdOrder.getId()).get();

        int totalOrderPrice = product1.getPrice() * Integer.parseInt(QUANTITY1)
                + product2.getPrice() * Integer.parseInt(QUANTITY2);

        // then
        assertThat(foundOrder.getId()).isEqualTo(createdOrder.getId());
        assertThat(foundOrder.getStore()).isEqualTo(store);
        assertThat(foundOrder.getBuyer()).isEqualTo(buyer);
        assertThat(foundOrder.getOrderStatus()).isEqualTo(BEFORE_PAYMENT);
        assertThat(foundOrder.getTotalOrderPrice()).isEqualTo(new TotalOrderPrice(totalOrderPrice));
        assertThat(foundOrder.getShippingAddress()).isEqualTo(Address.from(orderRequestDto.getShippingAddressDto()));
        assertThat(foundOrder.getCreatedAt()).isEqualTo(createdOrder.getCreatedAt());
        assertThat(foundOrder.getModifiedAt()).isEqualTo(createdOrder.getModifiedAt());
        assertThat(foundOrder.getOrderProducts().getOrderItems()).hasSize(2)
                .extracting("order", "product", "quantity", "orderPrice")
                .containsExactlyInAnyOrder(
                        tuple(
                                createdOrder, product1, Quantity.of(QUANTITY1),
                                OrderPrice.from(PRICE1, Quantity.of(QUANTITY1))
                        ),
                        tuple(
                                createdOrder, product2, Quantity.of(QUANTITY2),
                                OrderPrice.from(PRICE2, Quantity.of(QUANTITY2))
                        )
                );
    }

    @DisplayName("장바구니 ID를 통해 주문을 생성한다.")
    @Test
    void createOrderFromCart() {
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

        Cart cart = OrderTestObjectFactory.createCart(buyer);
        cartRepository.save(cart);

        CartProduct cartProduct1 = OrderTestObjectFactory.createCartProduct(cart, product1, QUANTITY1);
        CartProduct cartProduct2 = OrderTestObjectFactory.createCartProduct(cart, product2, QUANTITY2);
        cartProductRepository.saveAll(List.of(cartProduct1, cartProduct2));


        CartOrderRequestDto cartOrderRequestDto = OrderTestObjectFactory
                .createCartOrderRequestDto(buyer.getId().toString(), cart.getId().toString());

        // when
        Order createdOrder = orderService.createOrderFromCart(cartOrderRequestDto);
        Order foundOrder = orderRepository.findById(createdOrder.getId()).get();

        int totalOrderPrice = product1.getPrice() * Integer.parseInt(QUANTITY1)
                + product2.getPrice() * Integer.parseInt(QUANTITY2);

        // then
        assertThat(foundOrder.getId()).isEqualTo(createdOrder.getId());
        assertThat(foundOrder.getStore()).isEqualTo(store);
        assertThat(foundOrder.getBuyer()).isEqualTo(buyer);
        assertThat(foundOrder.getOrderStatus()).isEqualTo(BEFORE_PAYMENT);
        assertThat(foundOrder.getTotalOrderPrice()).isEqualTo(new TotalOrderPrice(totalOrderPrice));
        assertThat(foundOrder.getShippingAddress()).isEqualTo(Address.from(cartOrderRequestDto.getShippingAddressDto()));
        assertThat(foundOrder.getCreatedAt()).isEqualTo(createdOrder.getCreatedAt());
        assertThat(foundOrder.getModifiedAt()).isEqualTo(createdOrder.getModifiedAt());
        assertThat(foundOrder.getOrderProducts().getOrderItems()).hasSize(2)
                .extracting("order", "product", "quantity", "orderPrice")
                .containsExactlyInAnyOrder(
                        tuple(
                                createdOrder, product1, Quantity.of(QUANTITY1),
                                OrderPrice.from(PRICE1, Quantity.of(QUANTITY1))
                        ),
                        tuple(
                                createdOrder, product2, Quantity.of(QUANTITY2),
                                OrderPrice.from(PRICE2, Quantity.of(QUANTITY2))
                        )
                );
    }
}