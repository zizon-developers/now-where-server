package com.spring.nowwhere.api.v1.security.exception;

public class LogoutTokenException extends RuntimeException{
    public LogoutTokenException() {
    }

    public LogoutTokenException(String message) {
        super(message);
    }

    public LogoutTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogoutTokenException(Throwable cause) {
        super(cause);
    }

    public LogoutTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
