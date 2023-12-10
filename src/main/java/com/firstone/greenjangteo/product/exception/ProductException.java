package com.firstone.greenjangteo.product.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public ProductException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}