package com.spring.nowwhere.api.v1.entity.bet.exception;

public class TimeValidationException extends RuntimeException {
    public TimeValidationException() {
        super();
    }

    public TimeValidationException(String message) {
        super(message);
    }

    public TimeValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeValidationException(Throwable cause) {
        super(cause);
    }

    protected TimeValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
