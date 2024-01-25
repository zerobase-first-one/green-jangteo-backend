package com.firstone.greenjangteo.coupon.excpeption.serious;

import com.firstone.greenjangteo.exception.AbstractSeriousException;
import org.springframework.http.HttpStatus;

public class AlreadyUsedCouponException extends AbstractSeriousException {
    public AlreadyUsedCouponException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
