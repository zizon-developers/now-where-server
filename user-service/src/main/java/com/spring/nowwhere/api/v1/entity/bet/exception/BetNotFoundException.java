package com.spring.nowwhere.api.v1.entity.bet.exception;

public class BetNotFoundException extends RuntimeException{
    public BetNotFoundException() {
        super();
    }

    public BetNotFoundException(String message) {
        super(message);
    }

    public BetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BetNotFoundException(Throwable cause) {
        super(cause);
    }

    protected BetNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
