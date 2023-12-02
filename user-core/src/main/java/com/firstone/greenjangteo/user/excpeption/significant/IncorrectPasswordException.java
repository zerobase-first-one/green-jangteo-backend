package com.firstone.greenjangteo.user.excpeption.significant;

import com.firstone.greenjangteo.common.exception.AbstractSignificantException;
import org.springframework.http.HttpStatus;

import static com.firstone.greenjangteo.user.excpeption.message.IncorrectPasswordExceptionMessage.INCORRECT_PASSWORD_EXCEPTION;

public class IncorrectPasswordException extends AbstractSignificantException {
    public IncorrectPasswordException() {
        super(INCORRECT_PASSWORD_EXCEPTION);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}