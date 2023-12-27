package com.firstone.greenjangteo.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderProductRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.service.OrderService;
import com.firstone.greenjangteo.order.testutil.OrderTestObjectFactory;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.testutil.StoreTestObjectFactory;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.*;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.PRICE2;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("올바른 주문 요청 양식을 전송하면 주문을 생성할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void requestOrder() throws Exception {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.toString())
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2, passwordEncoder,
                FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Order order = OrderTestObjectFactory.createOrder(Long.parseLong(ORDER_ID), store, buyer, PRICE2);

        List<OrderProductRequestDto> orderProductRequestDtos
                = OrderTestObjectFactory.createOrderProductDtos(
                List.of(PRODUCT_ID1, PRODUCT_ID2),
                List.of(QUANTITY1, QUANTITY2)
        );

        OrderRequestDto orderRequestDto
                = OrderTestObjectFactory.createOrderRequestDto(
                SELLER_ID1, BUYER_ID, orderProductRequestDtos
        );

        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(order);

        // when, then
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(orderRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("장바구니 ID를 전송해 주문을 생성할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void requestOrderFromCart() throws Exception {
        // given
        User seller = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.toString())
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2, passwordEncoder,
                FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Store store = StoreTestObjectFactory.createStore(seller.getId(), STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        Order order = OrderTestObjectFactory.createOrder(Long.parseLong(ORDER_ID), store, buyer, PRICE2);

        CartOrderRequestDto cartOrderRequestDto
                = OrderTestObjectFactory.createCartOrderRequestDto(BUYER_ID, CART_ID);

        when(orderService.createOrderFromCart(any(CartOrderRequestDto.class))).thenReturn(order);

        // when, then
        mockMvc.perform(post("/orders/cart-order")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(cartOrderRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("판매자 ID를 입력해 판매자의 주문 목록을 조회할 수 있다.")
    @Test
    @WithMockUser(username = SELLER_ID1, roles = {"SELLER"})
    void getOrdersToSeller() throws Exception {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2,
                passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Order order1 = OrderTestObjectFactory.createOrder(1L, store, buyer, PRICE2);
        Order order2 = OrderTestObjectFactory.createOrder(2L, store, buyer, PRICE2);

        List<Order> orders = List.of(order1, order2);

        when(orderService.getOrders(store.getSellerId())).thenReturn(orders);

        // when, then
        mockMvc.perform(get("/orders")
                        .with(csrf())
                        .queryParam("userId", SELLER_ID1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("구매자 ID를 입력해 구매자의 주문 목록을 조회할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void getOrdersToBuyer() throws Exception {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2,
                passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Order order1 = OrderTestObjectFactory.createOrder(1L, store, buyer, PRICE2);
        Order order2 = OrderTestObjectFactory.createOrder(2L, store, buyer, PRICE2);

        List<Order> orders = List.of(order1, order2);

        when(orderService.getOrders(buyer.getId())).thenReturn(orders);

        // when, then
        mockMvc.perform(get("/orders")
                        .with(csrf())
                        .queryParam("userId", BUYER_ID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("주문 ID와 판매자 ID를 입력해 주문을 조회할 수 있다.")
    @Test
    @WithMockUser(username = SELLER_ID1, roles = {"SELLER"})
    void getOrderToSeller() throws Exception {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2,
                passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Order order = OrderTestObjectFactory.createOrder(1L, store, buyer, PRICE2);

        when(orderService.getOrder(anyLong())).thenReturn(order);

        // when, then
        mockMvc.perform(get("/orders/{userId}", buyer.getId())
                        .with(csrf())
                        .queryParam("userId", SELLER_ID1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("주문 ID와 구매자 ID를 입력해 주문을 조회할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void getOrderToBuyer() throws Exception {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2,
                passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Order order = OrderTestObjectFactory.createOrder(1L, store, buyer, PRICE2);

        when(orderService.getOrder(anyLong())).thenReturn(order);

        // when, then
        mockMvc.perform(get("/orders/{orderId}", order.getId())
                        .with(csrf())
                        .queryParam("userId", BUYER_ID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("주문 ID와 구매자 ID를 입력해 주문을 삭제할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void deleteOrder() throws Exception {
        // given
        Store store = StoreTestObjectFactory.createStore(
                Long.parseLong(SELLER_ID1), STORE_NAME1, DESCRIPTION1, IMAGE_URL1
        );
        User buyer = UserTestObjectFactory.createUser(
                Long.parseLong(BUYER_ID), EMAIL2, USERNAME2, PASSWORD2,
                passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.toString())
        );

        Order order = OrderTestObjectFactory.createOrder(1L, store, buyer, PRICE2);

        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(buyer.getId().toString());

        // when, then
        mockMvc.perform(delete("/orders/{orderId}", order.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(userIdRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
