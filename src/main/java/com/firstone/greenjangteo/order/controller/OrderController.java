package com.firstone.greenjangteo.order.controller;

import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.dto.response.OrderResponseDto;
import com.firstone.greenjangteo.order.dto.response.OrdersResponseDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.service.OrderService;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ACCESS_DENIED_LOGIN_ID;
import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ACCESS_DENIED_REQUEST_ID;
import static com.firstone.greenjangteo.user.model.Role.ROLE_ADMIN;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private static final String ORDER_REQUEST = "주문 요청";
    private static final String ORDER_REQUEST_DESCRIPTION = "주문 요청 양식을 입력해 상품을 주문할 수 있습니다.";
    private static final String ORDER_REQUEST_FORM = "주문 요청 양식";

    private static final String CART_ORDER_REQUEST = "장바구니 상품 주문 요청";
    private static final String CART_ORDER_REQUEST_DESCRIPTION = "장바구니 ID를 입력해 상품을 주문할 수 있습니다.";
    private static final String CART_ORDER_REQUEST_FORM = "장바구니 상품 주문 요청 양식";

    private static final String GET_ORDERS = "주문 목록 조회";
    private static final String GET_ORDERS_DESCRIPTION = "판매자 또는 구매자 ID를 입력해 주문 목록을 조회할 수 있습니다.";
    private static final String SELLER_OR_BUYER_ID = "판매자 또는 구매자 ID";

    private static final String ORDER_ID = "주문 ID";
    private static final String GET_ORDER = "주문 조회";
    private static final String GET_ORDER_DESCRIPTION = "주문 ID와 판매자 또는 구매자 ID를 입력해 주문을 조회할 수 있습니다.";

    private static final String DELETE_ORDER = "주문 삭제";
    private static final String DELETE_ORDER_DESCRIPTION = "주문 ID와 구매자 ID를 입력해 주문을 삭제할 수 있습니다.";

    @ApiOperation(value = ORDER_REQUEST, notes = ORDER_REQUEST_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<OrderResponseDto> requestOrder
            (@RequestBody @ApiParam(value = ORDER_REQUEST_FORM) OrderRequestDto orderRequestDto) {
        InputFormatValidator.validateId(orderRequestDto.getSellerId());
        InputFormatValidator.validateId(orderRequestDto.getBuyerId());

        checkAuthentication(orderRequestDto.getBuyerId());

        Order order = orderService.createOrder(orderRequestDto);

        return buildResponse(OrderResponseDto.from(order));
    }

    @ApiOperation(value = CART_ORDER_REQUEST, notes = CART_ORDER_REQUEST_DESCRIPTION)
    @PostMapping("/cart-order")
    public ResponseEntity<OrderResponseDto> requestOrderFromCart
            (@RequestBody @ApiParam(value = CART_ORDER_REQUEST_FORM) CartOrderRequestDto cartOrderRequestDto) {
        InputFormatValidator.validateId(cartOrderRequestDto.getBuyerId());
        InputFormatValidator.validateId(cartOrderRequestDto.getCartId());

        checkAuthentication(cartOrderRequestDto.getBuyerId());

        Order order = orderService.createOrderFromCart(cartOrderRequestDto);

        return buildResponse(OrderResponseDto.from(order));
    }

    @ApiOperation(value = GET_ORDERS, notes = GET_ORDERS_DESCRIPTION)
    @GetMapping()
    public ResponseEntity<List<OrdersResponseDto>> getOrders
            (@RequestBody @ApiParam(value = SELLER_OR_BUYER_ID)
             UserIdRequestDto userIdRequestDto) {
        InputFormatValidator.validateId(userIdRequestDto.getUserId());

        checkAuthentication(userIdRequestDto.getUserId());

        List<Order> orders = orderService.getOrders(userIdRequestDto);

        return transferToResponseEntity(orders);
    }

    @ApiOperation(value = GET_ORDER, notes = GET_ORDER_DESCRIPTION)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder
            (@PathVariable("orderId") @ApiParam(value = ORDER_ID, example = ID_EXAMPLE) String orderId,
             @RequestBody @ApiParam(value = SELLER_OR_BUYER_ID) UserIdRequestDto userIdRequestDto) {
        InputFormatValidator.validateId(orderId);
        InputFormatValidator.validateId(userIdRequestDto.getUserId());

        checkAuthentication(userIdRequestDto.getUserId());

        Order order = orderService.getOrder(Long.parseLong(orderId));

        return ResponseEntity.status(HttpStatus.OK).body(OrderResponseDto.from(order));
    }

    private void checkAuthentication(String requestedId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (requestedId.equals(currentUsername)
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
            return;
        }

        throw new AccessDeniedException(
                ACCESS_DENIED_REQUEST_ID + requestedId + ACCESS_DENIED_LOGIN_ID + currentUsername
        );
    }

    private ResponseEntity<OrderResponseDto> buildResponse(OrderResponseDto orderResponseDto) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orderId}")
                .buildAndExpand(orderResponseDto.getOrderId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(orderResponseDto);
    }

    private ResponseEntity<List<OrdersResponseDto>> transferToResponseEntity(List<Order> orders) {
        List<OrdersResponseDto> ordersResponseDtos = new ArrayList<>();
        for (Order order : orders) {
            ordersResponseDtos.add(OrdersResponseDto.from(order));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ordersResponseDtos);
    }
}
