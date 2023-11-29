package com.firstone.greenjangteo.common.exception;

public abstract class AbstractSeriousException extends RuntimeException {
    public AbstractSeriousException(String message) {
        super(message);
    }

    abstract public int getStatusCode();
}
