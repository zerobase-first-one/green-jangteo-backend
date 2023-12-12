package com.firstone.greenjangteo.order.excpeption.serious;

import com.firstone.greenjangteo.exception.AbstractSeriousException;
import org.springframework.http.HttpStatus;

public class InconsistentSellerIdException extends AbstractSeriousException {
    public InconsistentSellerIdException(String messgage) {
        super(messgage);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
