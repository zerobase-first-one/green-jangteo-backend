package com.firstone.greenjangteo.coupon.excpeption.serious;

import com.firstone.greenjangteo.exception.AbstractSeriousException;
import org.springframework.http.HttpStatus;

public class AlreadyProvidedCouponException extends AbstractSeriousException {
    public AlreadyProvidedCouponException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
