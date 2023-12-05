package com.firstone.greenjangteo.product.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PRODUCT_IS_EMPTY(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    PRODUCT_IS_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보가 존재하지 않습니다."),
    FILE_IS_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String description;
}
