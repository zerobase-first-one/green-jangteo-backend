package com.firstone.greenjangteo.common.exception;

public abstract class AbstractSignificantException extends RuntimeException {
    public AbstractSignificantException(String message) {
        super(message);
    }

    abstract public int getStatusCode();
}
