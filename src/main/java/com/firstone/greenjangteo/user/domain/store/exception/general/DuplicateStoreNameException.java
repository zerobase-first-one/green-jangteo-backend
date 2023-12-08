package com.firstone.greenjangteo.user.domain.store.exception.general;

import com.firstone.greenjangteo.exception.AbstractGeneralException;
import org.springframework.http.HttpStatus;

public class DuplicateStoreNameException extends AbstractGeneralException {
    public DuplicateStoreNameException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
