package com.firstone.greenjangteo.cart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    CART_PRODUCT_IS_NOT_EMPTY(HttpStatus.NOT_FOUND, "장바구니에 상품이 존재합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String description;
}
