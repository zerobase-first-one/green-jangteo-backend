package com.firstone.greenjangteo.common.exception;

public abstract class AbstractGeneralException extends RuntimeException {
    public AbstractGeneralException(String message) {
        super(message);
    }

    abstract public int getStatusCode();
}
