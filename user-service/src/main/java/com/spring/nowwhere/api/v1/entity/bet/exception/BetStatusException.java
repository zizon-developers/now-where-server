package com.spring.nowwhere.api.v1.entity.bet.exception;

public class BetStatusException extends RuntimeException{
    public BetStatusException() {
        super();
    }

    public BetStatusException(String message) {
        super(message);
    }

    public BetStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public BetStatusException(Throwable cause) {
        super(cause);
    }

    protected BetStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
