package com.firstone.greenjangteo.user.excpeption.general;

import com.firstone.greenjangteo.common.exception.AbstractGeneralException;
import org.springframework.http.HttpStatus;

public class DuplicateUserException extends AbstractGeneralException {
    public DuplicateUserException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
