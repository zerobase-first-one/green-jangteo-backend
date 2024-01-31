package com.firstone.greenjangteo.reserve.exception.serious;

import com.firstone.greenjangteo.exception.AbstractSeriousException;
import org.springframework.http.HttpStatus;

public class InsufficientReserveException extends AbstractSeriousException {
    public InsufficientReserveException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNPROCESSABLE_ENTITY.value();
    }
}
