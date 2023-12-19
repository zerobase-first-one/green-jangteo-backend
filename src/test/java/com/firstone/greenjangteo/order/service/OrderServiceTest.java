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
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
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

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.firstone.greenjangteo.order.excpeption.message.NotFoundExceptionMessage.ORDERED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.order.excpeption.message.NotFoundExceptionMessage.ORDER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.order.model.OrderStatus.BEFORE_PAYMENT;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY1;
import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.QUANTITY2;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
        );
        userRepository.saveAll(List.of(seller, buyer));

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        int totalOrderPrice = product1.getPrice() * Integer.parseInt(QUANTITY1)
                + product2.getPrice() * Integer.parseInt(QUANTITY2);

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
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
        );
        userRepository.saveAll(List.of(seller, buyer));

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Product product1 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(store, PRODUCT_NAME2, PRICE2, INVENTORY2);
        productRepository.saveAll(List.of(product1, product2));

        int totalOrderPrice = product1.getPrice() * Integer.parseInt(QUANTITY1)
                + product2.getPrice() * Integer.parseInt(QUANTITY2);

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

        // then
        assertThat(foundOrder.getId()).isEqualTo(createdOrder.getId());
        assertThat(foundOrder.getStore()).isEqualTo(store);
        assertThat(foundOrder.getBuyer()).isEqualTo(buyer);
        assertThat(foundOrder.getOrderStatus()).isEqualTo(BEFORE_PAYMENT);
        assertThat(foundOrder.getTotalOrderPrice()).isEqualTo(new TotalOrderPrice(totalOrderPrice));
        assertThat(foundOrder.getShippingAddress()).isEqualTo(
                Address.from(cartOrderRequestDto.getShippingAddressDto())
        );
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

    @DisplayName("판매자 ID를 전송해 주문 목록을 조회한다.")
    @Test
    void getOrdersFromSeller() {
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

        List<OrderProductRequestDto> orderProductRequestDtos
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString(), product2.getId().toString()),
                List.of(QUANTITY1, QUANTITY2)
        );

        OrderRequestDto orderRequestDto1
                = OrderTestObjectFactory.createOrderRequestDto(
                seller.getId().toString(), buyer1.getId().toString(), orderProductRequestDtos
        );

        OrderRequestDto orderRequestDto2
                = OrderTestObjectFactory.createOrderRequestDto(
                seller.getId().toString(), buyer2.getId().toString(), orderProductRequestDtos
        );

        orderService.createOrder(orderRequestDto1);
        orderService.createOrder(orderRequestDto2);

        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(seller.getId().toString());

        // when
        List<Order> orders = orderService.getOrders(userIdRequestDto);

        // then
        assertThat(orders).hasSize(2)
                .extracting("store", "buyer")
                .containsExactlyInAnyOrder(tuple(store, buyer1), tuple(store, buyer2));
    }

    @DisplayName("구매자 ID를 전송해 주문 목록을 조회한다.")
    @Test
    void getOrdersFromBuyer() {
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

        List<OrderProductRequestDto> orderProductRequestDtos1
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product1.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        List<OrderProductRequestDto> orderProductRequestDtos2
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(product2.getId().toString()), List.of(QUANTITY1, QUANTITY2)
        );

        OrderRequestDto orderRequestDto1
                = OrderTestObjectFactory.createOrderRequestDto(
                seller1.getId().toString(), buyer.getId().toString(), orderProductRequestDtos1
        );

        OrderRequestDto orderRequestDto2
                = OrderTestObjectFactory.createOrderRequestDto(
                seller2.getId().toString(), buyer.getId().toString(), orderProductRequestDtos2
        );

        orderService.createOrder(orderRequestDto1);
        orderService.createOrder(orderRequestDto2);

        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(buyer.getId().toString());

        // when
        List<Order> orders = orderService.getOrders(userIdRequestDto);

        // then
        assertThat(orders).hasSize(2)
                .extracting("store", "buyer")
                .containsExactlyInAnyOrder(tuple(store1, buyer), tuple(store2, buyer));
    }

    @DisplayName("주문 ID를 전송해 주문을 조회한다.")
    @Test
    void getOrder() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
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

        Order createdOrder = orderService.createOrder(orderRequestDto);

        // when
        Order foundOrder = orderService.getOrder(createdOrder.getId());

        // then
        assertThat(foundOrder.getId()).isEqualTo(createdOrder.getId());
        assertThat(foundOrder.getStore()).isEqualTo(store);
        assertThat(foundOrder.getBuyer()).isEqualTo(buyer);
        assertThat(foundOrder.getOrderStatus()).isEqualTo(createdOrder.getOrderStatus());
        assertThat(foundOrder.getTotalOrderPrice()).isEqualTo(createdOrder.getTotalOrderPrice());
        assertThat(foundOrder.getShippingAddress()).isEqualTo(createdOrder.getShippingAddress());
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

    @DisplayName("잘못된 주문 ID를 입력하면 EntityNotFoundException이 발생한다.")
    @Test
    void getOrderFromWrongOrderId() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
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

        Order createdOrder = orderService.createOrder(orderRequestDto);

        // when, then
        assertThatThrownBy(() -> orderService.getOrder(createdOrder.getId() + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ORDER_ID_NOT_FOUND_EXCEPTION + (createdOrder.getId() + 1));
    }

    @DisplayName("주문 ID와 구매자 ID를 전송해 주문을 삭제한다.")
    @Test
    void deleteOrder() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
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

        Order createdOrder = orderService.createOrder(orderRequestDto);
        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(buyer.getId().toString());

        // when
        orderService.deleteOrder(createdOrder.getId(), userIdRequestDto);
        Optional<Order> foundOrder = orderRepository.findById(createdOrder.getId());

        // then
        assertThat(foundOrder).isEmpty();
    }

    @DisplayName("잘못된 주문 ID를 통해 주문을 삭제하려 하면 EntityNotFoundException이 발생한다.")
    @Test
    void deleteOrderByWrongOrderId() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
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

        Order order = orderService.createOrder(orderRequestDto);
        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(buyer.getId().toString());

        // when, then
        assertThatThrownBy(() -> orderService.deleteOrder(order.getId() + 1, userIdRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ORDER_ID_NOT_FOUND_EXCEPTION + (order.getId() + 1)
                        + ORDERED_USER_ID_NOT_FOUND_EXCEPTION + buyer.getId());
    }

    @DisplayName("잘못된 구매자 ID를 통해 주문을 삭제하려 하면 EntityNotFoundException이 발생한다.")
    @Test
    void deleteOrderByWrongBuyerId() {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.name())
        );
        User buyer = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
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

        Order order = orderService.createOrder(orderRequestDto);
        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(String.valueOf(buyer.getId() + 1));

        // when, then
        assertThatThrownBy(() -> orderService.deleteOrder(order.getId(), userIdRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ORDER_ID_NOT_FOUND_EXCEPTION + (order.getId())
                        + ORDERED_USER_ID_NOT_FOUND_EXCEPTION + (buyer.getId() + 1));
    }
}
