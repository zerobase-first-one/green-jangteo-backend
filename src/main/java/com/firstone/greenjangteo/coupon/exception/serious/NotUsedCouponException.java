package com.firstone.greenjangteo.coupon.exception.serious;

import com.firstone.greenjangteo.exception.AbstractSeriousException;
import org.springframework.http.HttpStatus;

public class NotUsedCouponException extends AbstractSeriousException {
    public NotUsedCouponException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
