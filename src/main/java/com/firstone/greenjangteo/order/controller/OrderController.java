package com.firstone.greenjangteo.order.controller;

import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
import com.firstone.greenjangteo.order.dto.response.OrderResponseDto;
import com.firstone.greenjangteo.order.dto.response.OrdersResponseDto;
import com.firstone.greenjangteo.order.model.entity.Order;
import com.firstone.greenjangteo.order.service.OrderService;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import com.firstone.greenjangteo.utility.RoleValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.USER_ID_VALUE;

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

        RoleValidator.checkAdminOrPrincipalAuthentication(orderRequestDto.getBuyerId());

        Order order = orderService.createOrder(orderRequestDto);

        return buildResponse(OrderResponseDto.from(order));
    }

    @ApiOperation(value = CART_ORDER_REQUEST, notes = CART_ORDER_REQUEST_DESCRIPTION)
    @PostMapping("/cart-order")
    public ResponseEntity<OrderResponseDto> requestOrderFromCart
            (@RequestBody @ApiParam(value = CART_ORDER_REQUEST_FORM) CartOrderRequestDto cartOrderRequestDto) {
        InputFormatValidator.validateId(cartOrderRequestDto.getBuyerId());
        InputFormatValidator.validateId(cartOrderRequestDto.getCartId());

        RoleValidator.checkAdminOrPrincipalAuthentication(cartOrderRequestDto.getBuyerId());

        Order order = orderService.createOrderFromCart(cartOrderRequestDto);

        return buildResponse(OrderResponseDto.from(order));
    }

    @ApiOperation(value = GET_ORDERS, notes = GET_ORDERS_DESCRIPTION)
    @GetMapping()
    public ResponseEntity<List<OrdersResponseDto>> getOrders
            (@RequestParam(name = "userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId) {
        InputFormatValidator.validateId(userId);
        RoleValidator.checkAdminOrPrincipalAuthentication(userId);

        List<Order> orders = orderService.getOrders(Long.valueOf(userId));

        return transferToResponseEntity(orders);
    }

    @ApiOperation(value = GET_ORDER, notes = GET_ORDER_DESCRIPTION)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder
            (@PathVariable("orderId") @ApiParam(value = ORDER_ID, example = ID_EXAMPLE) String orderId,
             @RequestParam(name = "userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId) {
        InputFormatValidator.validateId(orderId);
        InputFormatValidator.validateId(userId);

        RoleValidator.checkAdminOrPrincipalAuthentication(userId);

        Order order = orderService.getOrder(Long.parseLong(orderId));

        return ResponseEntity.status(HttpStatus.OK).body(OrderResponseDto.from(order));
    }

    @ApiOperation(value = DELETE_ORDER, notes = DELETE_ORDER_DESCRIPTION)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> deleteOrder
            (@PathVariable("orderId") @ApiParam(value = ORDER_ID, example = ID_EXAMPLE) String orderId,
             @RequestBody @ApiParam(value = USER_ID_VALUE) UserIdRequestDto userIdRequestDto) {
        InputFormatValidator.validateId(orderId);
        InputFormatValidator.validateId(userIdRequestDto.getUserId());

        RoleValidator.checkAdminOrPrincipalAuthentication(userIdRequestDto.getUserId());

        orderService.deleteOrder(Long.parseLong(orderId), userIdRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
