package com.spring.userservice.v1.api.auth.exception;

public class UserNotSavedException extends RuntimeException{
    public UserNotSavedException() {
        super();
    }

    public UserNotSavedException(String message) {
        super(message);
    }

    public UserNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotSavedException(Throwable cause) {
        super(cause);
    }

    protected UserNotSavedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
