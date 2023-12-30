package com.firstone.greenjangteo.product.exception;

import com.firstone.greenjangteo.exception.AbstractGeneralException;
import org.springframework.http.HttpStatus;

public class ProductRelatedException extends AbstractGeneralException {
    public ProductRelatedException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
