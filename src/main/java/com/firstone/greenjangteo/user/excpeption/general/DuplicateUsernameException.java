package com.firstone.greenjangteo.user.excpeption.general;

import com.firstone.greenjangteo.exception.AbstractGeneralException;
import org.springframework.http.HttpStatus;

public class DuplicateUsernameException extends AbstractGeneralException {
    public DuplicateUsernameException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
