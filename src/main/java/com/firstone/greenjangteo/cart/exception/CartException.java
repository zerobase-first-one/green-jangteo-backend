package com.firstone.greenjangteo.cart.exception;

import com.firstone.greenjangteo.exception.AbstractGeneralException;
import org.springframework.http.HttpStatus;

public class CartException extends AbstractGeneralException {
    public CartException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
