package com.spring.nowwhere.api.v1.entity.user.exception;

public class DuplicateRemittanceIdException extends RuntimeException{
    public DuplicateRemittanceIdException() {
        super();
    }

    public DuplicateRemittanceIdException(String message) {
        super(message);
    }

    public DuplicateRemittanceIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRemittanceIdException(Throwable cause) {
        super(cause);
    }

    protected DuplicateRemittanceIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
