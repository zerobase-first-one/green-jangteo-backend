package com.firstone.greenjangteo.post.domain.comment.exception.serious;

import com.firstone.greenjangteo.exception.AbstractSeriousException;
import org.springframework.http.HttpStatus;

public class InconsistentCommentException extends AbstractSeriousException {
    public InconsistentCommentException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
