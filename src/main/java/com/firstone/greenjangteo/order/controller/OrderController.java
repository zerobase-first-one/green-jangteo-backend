package com.firstone.greenjangteo.order.controller;

import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.dto.response.OrderResponseDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.service.OrderService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ACCESS_DENIED_LOGIN_ID;
import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ACCESS_DENIED_REQUEST_ID;
import static com.firstone.greenjangteo.user.model.Role.ROLE_ADMIN;

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

    @ApiOperation(value = ORDER_REQUEST, notes = ORDER_REQUEST_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<OrderResponseDto> requestOrder
            (@RequestBody @ApiParam(value = ORDER_REQUEST_FORM) OrderRequestDto orderRequestDto) {
        InputFormatValidator.validateId(orderRequestDto.getSellerId());
        InputFormatValidator.validateId(orderRequestDto.getBuyerId());

        checkAuthentication(String.valueOf(orderRequestDto.getBuyerId()));

        Order order = orderService.createOrder(orderRequestDto);

        return buildResponse(OrderResponseDto.from(order));
    }

    @ApiOperation(value = CART_ORDER_REQUEST, notes = CART_ORDER_REQUEST_DESCRIPTION)
    @PostMapping("/cart-order")
    public ResponseEntity<OrderResponseDto> requestOrderFromCart
            (@RequestBody @ApiParam(value = CART_ORDER_REQUEST_FORM) CartOrderRequestDto cartOrderRequestDto) {
        InputFormatValidator.validateId(cartOrderRequestDto.getBuyerId());
        InputFormatValidator.validateId(cartOrderRequestDto.getCartId());

        checkAuthentication(String.valueOf(cartOrderRequestDto.getBuyerId()));

        Order order = orderService.createOrderFromCart(cartOrderRequestDto);

        return buildResponse(OrderResponseDto.from(order));
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
}
